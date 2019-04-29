/* 
 * EdmSetting.java
 * 
 * Copyright (c) 2009-2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.edm.model;

import java.util.Map;

/**
 * EDM相關設定參數
 * 
 * @since 2019年3月12日
 * @author Johnson Ho
 * @version
 *          <ul>
 *          <li>2019年3月12日,Johnson Ho,new
 *          </ul>
 */
public class EdmSetting {

    /** FTL的檔案路徑 */
    private String edmFtlPath;
    /** 收件人email */
    private String mailAddress;
    /** 寄件者email */
    private String fromAddress;
    /** 寄件者名稱 */
    private String fromPerson;
    /** SMTP host */
    private String edmHost;
    /** 帳號 */
    private String edmUsr;
    /** 密碼 */
    private String edmPwd;
    /** 信件主旨 */
    private String edmSubject;
    /** 附件的檔案路徑 */
    private String edmAttachedFilePath;
    /** 對應FTL內的image"資料夾"路徑 */
    private String edmImageFileFolder;
    /** 對應FTL內的變數Map */
    private Map<String, Object> mappingFtlVar;

    /**
     * EdmSetting empty constructor
     */
    public EdmSetting() {
    }

    /**
     * @param edmFtlPath
     *            FTL的檔案路徑
     * @param mailAddress
     *            收件人email
     * @param fromAddress
     *            寄件者email
     * @param fromPerson
     *            寄件者名稱
     * @param edmHost
     *            SMTP host
     * @param edmUsr
     *            帳號
     * @param edmPwd
     *            密碼
     * @param edmSubject
     *            信件主旨
     * @param edmAttachedFilePath
     *            附件的檔案路徑
     * @param edmImageFileFolder
     *            對應FTL內的image"資料夾"路徑
     * @param mappingFtlVar
     *            對應FTL內的變數Map
     */
    public EdmSetting(String edmFtlPath, String mailAddress, String fromAddress, String fromPerson, String edmHost, String edmUsr, String edmPwd, String edmSubject, String edmAttachedFilePath,
            String edmImageFileFolder, Map<String, Object> mappingFtlVar) {
        this.edmFtlPath = edmFtlPath;
        this.mailAddress = mailAddress;
        this.fromAddress = fromAddress;
        this.fromPerson = fromPerson;
        this.edmHost = edmHost;
        this.edmUsr = edmUsr;
        this.edmPwd = edmPwd;
        this.edmSubject = edmSubject;
        this.edmAttachedFilePath = edmAttachedFilePath;
        this.edmImageFileFolder = edmImageFileFolder;
        this.mappingFtlVar = mappingFtlVar;
    }

    /**
     * Get the edmFtlPath
     * 
     * @return the edmFtlPath
     */
    public String getEdmFtlPath() {
        return edmFtlPath;
    }

    /**
     * Set the edmFtlPath
     * 
     * @param edmFtlPath
     *            the edmFtlPath to set
     */
    public void setEdmFtlPath(String edmFtlPath) {
        this.edmFtlPath = edmFtlPath;
    }

    /**
     * Get the mailAddress
     * 
     * @return the mailAddress
     */
    public String getMailAddress() {
        return mailAddress;
    }

    /**
     * Set the mailAddress
     * 
     * @param mailAddress
     *            the mailAddress to set
     */
    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    /**
     * Get the fromAddress
     * 
     * @return the fromAddress
     */
    public String getFromAddress() {
        return fromAddress;
    }

    /**
     * Set the fromAddress
     * 
     * @param fromAddress
     *            the fromAddress to set
     */
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    /**
     * Get the fromPerson
     * 
     * @return the fromPerson
     */
    public String getFromPerson() {
        return fromPerson;
    }

    /**
     * Set the fromPerson
     * 
     * @param fromPerson
     *            the fromPerson to set
     */
    public void setFromPerson(String fromPerson) {
        this.fromPerson = fromPerson;
    }

    /**
     * Get the edmHost
     * 
     * @return the edmHost
     */
    public String getEdmHost() {
        return edmHost;
    }

    /**
     * Set the edmHost
     * 
     * @param edmHost
     *            the edmHost to set
     */
    public void setEdmHost(String edmHost) {
        this.edmHost = edmHost;
    }

    /**
     * Get the edmUsr
     * 
     * @return the edmUsr
     */
    public String getEdmUsr() {
        return edmUsr;
    }

    /**
     * Set the edmUsr
     * 
     * @param edmUsr
     *            the edmUsr to set
     */
    public void setEdmUsr(String edmUsr) {
        this.edmUsr = edmUsr;
    }

    /**
     * Get the edmPwd
     * 
     * @return the edmPwd
     */
    public String getEdmPwd() {
        return edmPwd;
    }

    /**
     * Set the edmPwd
     * 
     * @param edmPwd
     *            the edmPwd to set
     */
    public void setEdmPwd(String edmPwd) {
        this.edmPwd = edmPwd;
    }

    /**
     * Get the edmSubject
     * 
     * @return the edmSubject
     */
    public String getEdmSubject() {
        return edmSubject;
    }

    /**
     * Set the edmSubject
     * 
     * @param edmSubject
     *            the edmSubject to set
     */
    public void setEdmSubject(String edmSubject) {
        this.edmSubject = edmSubject;
    }

    /**
     * Get the edmAttachedFilePath
     * 
     * @return the edmAttachedFilePath
     */
    public String getEdmAttachedFilePath() {
        return edmAttachedFilePath;
    }

    /**
     * Set the edmAttachedFilePath
     * 
     * @param edmAttachedFilePath
     *            the edmAttachedFilePath to set
     */
    public void setEdmAttachedFilePath(String edmAttachedFilePath) {
        this.edmAttachedFilePath = edmAttachedFilePath;
    }

    /**
     * Get the edmImageFileFolder
     * 
     * @return the edmImageFileFolder
     */
    public String getEdmImageFileFolder() {
        return edmImageFileFolder;
    }

    /**
     * Set the edmImageFileFolder
     * 
     * @param edmImageFileFolder
     *            the edmImageFileFolder to set
     */
    public void setEdmImageFileFolder(String edmImageFileFolder) {
        this.edmImageFileFolder = edmImageFileFolder;
    }

    /**
     * Get the mappingFtlVar
     * 
     * @return the mappingFtlVar
     */
    public Map<String, Object> getMappingFtlVar() {
        return mappingFtlVar;
    }

    /**
     * Set the mappingFtlVar
     * 
     * @param mappingFtlVar
     *            the mappingFtlVar to set
     */
    public void setMappingFtlVar(Map<String, Object> mappingFtlVar) {
        this.mappingFtlVar = mappingFtlVar;
    }

}
