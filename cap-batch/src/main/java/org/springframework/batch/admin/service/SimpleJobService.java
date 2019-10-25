/*
 * Copyright 2009-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.admin.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.ListableJobLocator;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Implementation of {@link JobService} that delegates most of its work to other
 * off-the-shelf components.
 *
 * @author Dave Syer
 * @author Michael Minella
 *
 */
public class SimpleJobService implements JobService, DisposableBean {

	private static final Log logger = LogFactory.getLog(SimpleJobService.class);

	// 60 seconds
	private static final int DEFAULT_SHUTDOWN_TIMEOUT = 60 * 1000;

	private final JobInstanceDao jobInstanceDao;

	private final JobExecutionDao jobExecutionDao;

	private final JobRepository jobRepository;

	private final JobLauncher jobLauncher;

	private final ListableJobLocator jobLocator;

	private final StepExecutionDao stepExecutionDao;

	private final ExecutionContextDao executionContextDao;

	private Collection<JobExecution> activeExecutions = Collections.synchronizedList(new ArrayList<JobExecution>());

	private int shutdownTimeout = DEFAULT_SHUTDOWN_TIMEOUT;

	/**
	 * Timeout for shutdown waiting for jobs to finish processing.
	 *
	 * @param shutdownTimeout in milliseconds (default 60 secs)
	 */
	public void setShutdownTimeout(int shutdownTimeout) {
		this.shutdownTimeout = shutdownTimeout;
	}

	public SimpleJobService(JobInstanceDao jobInstanceDao, JobExecutionDao jobExecutionDao,
			StepExecutionDao stepExecutionDao, JobRepository jobRepository, JobLauncher jobLauncher,
			ListableJobLocator jobLocator, ExecutionContextDao executionContextDao) {
		super();
		this.jobInstanceDao = jobInstanceDao;
		this.jobExecutionDao = jobExecutionDao;
		this.stepExecutionDao = stepExecutionDao;
		this.jobRepository = jobRepository;
		this.jobLauncher = jobLauncher;
		this.jobLocator = jobLocator;
		this.executionContextDao = executionContextDao;
	}

	@Override
	public JobExecution restart(Long jobExecutionId) throws NoSuchJobExecutionException,
	JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException,
	NoSuchJobException, JobParametersInvalidException {

		JobExecution target = getJobExecution(jobExecutionId);
		JobInstance lastInstance = target.getJobInstance();

		Job job = jobLocator.getJob(lastInstance.getJobName());

		JobExecution jobExecution = jobLauncher.run(job, target.getJobParameters());

		if (jobExecution.isRunning()) {
			activeExecutions.add(jobExecution);
		}
		return jobExecution;
	}

	@Override
	public JobExecution launch(String jobName, JobParameters jobParameters) throws NoSuchJobException,
	JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException,
	JobParametersInvalidException {

		Job job = jobLocator.getJob(jobName);

		JobExecution lastJobExecution = jobRepository.getLastJobExecution(jobName, jobParameters);
		boolean restart = false;
		if (lastJobExecution != null) {
			BatchStatus status = lastJobExecution.getStatus();
			if (status.isUnsuccessful() && status!=BatchStatus.ABANDONED) {
				restart = true;
			}
		}

		if (job.getJobParametersIncrementer() != null && !restart) {
			jobParameters = job.getJobParametersIncrementer().getNext(jobParameters);
		}

		JobExecution jobExecution = jobLauncher.run(job, jobParameters);

		if (jobExecution.isRunning()) {
			activeExecutions.add(jobExecution);
		}
		return jobExecution;

	}

	@Override
	public JobParameters getLastJobParameters(String jobName) throws NoSuchJobException {
		JobExecution lastExecution = jobExecutionDao.getLastJobExecution(jobInstanceDao.getLastJobInstance(jobName));
		JobParameters oldParameters = new JobParameters();
		if (lastExecution != null) {
			oldParameters = lastExecution.getJobParameters();
		}
		return oldParameters;
	}

	@Override
	public JobExecution stop(Long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException {

		JobExecution jobExecution = getJobExecution(jobExecutionId);
		if (!jobExecution.isRunning()) {
			throw new JobExecutionNotRunningException("JobExecution is not running and therefore cannot be stopped");
		}

		logger.info("Stopping job execution: " + jobExecution);
		jobExecution.stop();
		jobRepository.update(jobExecution);
		return jobExecution;

	}

	@Override
	public JobExecution getJobExecution(Long jobExecutionId) throws NoSuchJobExecutionException {
		JobExecution jobExecution = jobExecutionDao.getJobExecution(jobExecutionId);
		if (jobExecution == null) {
			throw new NoSuchJobExecutionException("There is no JobExecution with id=" + jobExecutionId);
		}
		jobExecution.setJobInstance(jobInstanceDao.getJobInstance(jobExecution));
		try {
			jobExecution.setExecutionContext(executionContextDao.getExecutionContext(jobExecution));
		}
		catch (Exception e) {
			logger.info("Cannot load execution context for job execution: " + jobExecution);
		}
		stepExecutionDao.addStepExecutions(jobExecution);
		return jobExecution;
	}

	@Override
	public Collection<JobExecution> getJobExecutionsForJobInstance(String name, Long jobInstanceId)
			throws NoSuchJobException {
		checkJobExists(name);
		List<JobExecution> jobExecutions = jobExecutionDao.findJobExecutions(jobInstanceDao
				.getJobInstance(jobInstanceId));
		for (JobExecution jobExecution : jobExecutions) {
			stepExecutionDao.addStepExecutions(jobExecution);
		}
		return jobExecutions;
	}

	private void checkJobExists(String jobName) throws NoSuchJobException {
		if (jobLocator.getJobNames().contains(jobName)) {
			return;
		}
		if (jobInstanceDao.getJobInstanceCount(jobName) > 0) {
			return;
		}
		throw new NoSuchJobException("No Job with that name either current or historic: [" + jobName + "]");
	}

	/**
	 * Stop all the active jobs and wait for them (up to a time out) to finish
	 * processing.
	 */
	@Override
	public void destroy() throws Exception {

		Exception firstException = null;

		for (JobExecution jobExecution : activeExecutions) {
			try {
				if (jobExecution.isRunning()) {
					stop(jobExecution.getId());
				}
			}
			catch (JobExecutionNotRunningException e) {
				logger.info("JobExecution is not running so it cannot be stopped");
			}
			catch (Exception e) {
				logger.error("Unexpected exception stopping JobExecution", e);
				if (firstException == null) {
					firstException = e;
				}
			}
		}

		int count = 0;
		int maxCount = (shutdownTimeout + 1000) / 1000;
		while (!activeExecutions.isEmpty() && ++count < maxCount) {
			logger.error("Waiting for " + activeExecutions.size() + " active executions to complete");
			removeInactiveExecutions();
			Thread.sleep(1000L);
		}

		if (firstException != null) {
			throw firstException;
		}

	}

	/**
	 * Check all the active executions and see if they are still actually
	 * running. Remove the ones that have completed.
	 */
	@Scheduled(fixedDelay = 60000)
	public void removeInactiveExecutions() {

		for (Iterator<JobExecution> iterator = activeExecutions.iterator(); iterator.hasNext();) {
			JobExecution jobExecution = iterator.next();
			try {
				jobExecution = getJobExecution(jobExecution.getId());
			}
			catch (NoSuchJobExecutionException e) {
				logger.error("Unexpected exception loading JobExecution", e);
			}
			if (!jobExecution.isRunning()) {
				iterator.remove();
			}
		}

	}

}
