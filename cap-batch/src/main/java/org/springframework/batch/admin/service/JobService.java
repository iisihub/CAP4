/*
 * Copyright 2009-2010 the original author or authors.
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

import java.util.Collection;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.tasklet.Tasklet;

/**
 * Interface for general purpose monitoring and management of Batch jobs. The features here can generally be composed from existing Spring Batch interfaces (although for performance reasons,
 * implementations might choose special-purpose optimisations via a relation database, for instance).
 * 
 * @author Dave Syer
 * 
 */
public interface JobService {

    /**
     * Launch a job with the parameters provided. If an instance with the parameters provided has already failed (and is not abandoned) it will be restarted.
     * 
     * @param jobName
     *            the job name
     * @param params
     *            the {@link JobParameters}
     * @return the resulting {@link JobExecution} if successful
     * 
     * @throws NoSuchJobException
     * @throws JobExecutionAlreadyRunningException
     * @throws JobRestartException
     * @throws JobInstanceAlreadyCompleteException
     * @throws JobParametersInvalidException
     */
    JobExecution launch(String jobName, JobParameters params)
            throws NoSuchJobException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException;

    /**
     * Get the last {@link JobParameters} used to execute a job successfully.
     * 
     * @param jobName
     *            the name of the job
     * @return the last parameters used to execute this job or empty if there are none
     * 
     * @throws NoSuchJobException
     */
    JobParameters getLastJobParameters(String jobName) throws NoSuchJobException;

    /**
     * Launch a job with the parameters provided.
     * 
     * @param jobExecutionId
     *            the job execution to restart
     * @return the resulting {@link JobExecution} if successful
     * 
     * @throws NoSuchJobExecutionException
     * @throws JobExecutionAlreadyRunningException
     * @throws JobRestartException
     * @throws JobInstanceAlreadyCompleteException
     * @throws NoSuchJobException
     * @throws JobParametersInvalidException
     */
    JobExecution restart(Long jobExecutionId)
            throws NoSuchJobExecutionException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, NoSuchJobException, JobParametersInvalidException;

    /**
     * Send a signal to a job execution to stop processing. This method does not guarantee that the processing will stop, only that the signal will be delivered. It is up to the individual {@link Job}
     * and {@link Step} implementations to ensure that the signal is obeyed. In particular, if users provide a custom {@link Tasklet} to a {@link Step} it must check the signal in the
     * {@link JobExecution} itself.
     * 
     * @param jobExecutionId
     *            the job execution id to stop
     * @return the {@link JobExecution} that was stopped
     * @throws NoSuchJobExecutionException
     * @throws JobExecutionNotRunningException
     */
    JobExecution stop(Long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException;

    /**
     * Get all the job executions for a given job instance. On a sunny day there would be only one. If there have been failures and restarts there may be many, and they will be listed in reverse order
     * of primary key.
     * 
     * @param jobName
     *            the name of the job
     * @param jobInstanceId
     *            the id of the job instance
     * @return all the job executions
     * @throws NoSuchJobException
     */
    Collection<JobExecution> getJobExecutionsForJobInstance(String jobName, Long jobInstanceId) throws NoSuchJobException;

    /**
     * Get a {@link JobExecution} by id.
     * 
     * @param jobExecutionId
     *            the job execution id
     * @return the {@link JobExecution}
     * 
     * @throws NoSuchJobExecutionException
     */
    JobExecution getJobExecution(Long jobExecutionId) throws NoSuchJobExecutionException;

}
