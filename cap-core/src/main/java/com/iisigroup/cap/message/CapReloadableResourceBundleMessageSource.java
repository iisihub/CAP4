/*
 * CapReloadableResourceBundleMessageSource.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.message;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;

import com.iisigroup.cap.utils.CapString;

/**
 * <pre>
 * ReloadableResourceBundleMessageSource
 * </pre>
 * 
 * @since 2011/12/23
 * @author rodeschen
 * @version
 *          <ul>
 *          <li>2011/12/23,rodeschen,new
 *          <li>2011/12/29,rodeschen,fix windows reg
 *          <li>2013/1/23,roodeschen,fix weglogic deployment error
 *          </ul>
 */
public class CapReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource implements ApplicationContextAware, InitializingBean {

    private String[] languages;
    private String basePath;
    // 設定預設語系 - 如有使用Spring MVC 可省略
    private Locale defaultLocale;
    private static ApplicationContext applicationContext;

    /**
     * @param defaultLocale
     *            the defaultLocale to set
     */
    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    /**
     * @param language
     *            the language to set
     */
    public void setLanguages(String[] languages) {
        this.languages = languages;
    }

    /**
     * @param basePath
     *            the basePath to set
     */
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        synchronized (this) {
            applicationContext = ctx;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Set<String> l = new HashSet<String>();
        String i18nPattern = basePath + "/**/*.properties";
        String i18nFileREG = "(" + StringUtils.join(languages, '|') + ").properties$";
        if (defaultLocale != null) {
            Locale.setDefault(this.defaultLocale);
        }
        try {
            Resource[] resources = applicationContext.getResources(i18nPattern);
            for (Resource resource : resources) {
                String path = resource.getURI().toString();
                if (CapString.checkRegularMatch(path, i18nFileREG)) {
                    path = path.replaceAll(i18nFileREG, "").replaceAll(".*/i18n/", "classpath:/i18n/")
                            // for windows
                            .replaceAll(".*\\\\i18n\\\\", "classpath:\\\\i18n\\\\").replaceAll("\\\\", "/");
                    if (!l.contains(path)) {
                        l.add(path);
                        logger.debug("set message path:" + path);
                    }
                }

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        super.setBasenames(l.toArray(new String[l.size()]));

    }
}
