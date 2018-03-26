/*
 * LoadAllCaCertAndCRLBean.java
 *
 * Copyright (c) 2009-2014 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.va.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.iisigroup.colabase.va.service.VAService;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapSystemConfig;

/**
 * <pre>
 * AP 啟動時 Load CA 憑證，Load CRL。
 * </pre>
 *
 * @since 2014/5/29
 * @author Sunkist Wang
 * @see com.citibank.cola.admin.service.VAService
 * @version
 *          <ul>
 *          <li>2014/5/29,Sunkist Wang,new
 *          </ul>
 */
public class LoadAllCaCertAndCRLBean implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(LoadAllCaCertAndCRLBean.class);

    @Autowired
    private VAService vaService;

    @Autowired
    private CapSystemConfig config;

    public LoadAllCaCertAndCRLBean() {
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(vaService);
        final String TITLE = "[Init bean]";
        Long startTime = System.currentTimeMillis();
        try {
            Assert.notNull(vaService);
            // 載入 CA 憑證到記憶體
            vaService.loadAllCaCert();
            // 下載 CRL
            if (Boolean.valueOf(config.getProperty("crl.download.on.startup", "false"))) {
                vaService.downloadAllCRL();
            }
            // CRL 灌檔
            if (Boolean.valueOf(config.getProperty("crl.save.on.startup", "false"))) {
                vaService.saveAllCRL();
            }
            if (logger.isInfoEnabled()) {
                logger.info("{} - {}", TITLE, CapAppContext.getMessage("loadAllCaCertAndCRL.success"));
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("{} - {}", TITLE, CapAppContext.getMessage("loadAllCaCertAndCRL.failed"));
            }
            throw e;
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("{} - cost time : {} ms", TITLE, System.currentTimeMillis() - startTime);
            }
        }
    }
}
