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

/**<pre>
 * EDM相關設定參數
 * </pre>
 * @since  2019年3月12日
 * @author Johnson Ho
 * @version <ul>
 *           <li>2019年3月12日,Johnson Ho,new
 *          </ul>
 */
public class EdmSetting {
    
    private String edmFtlPath;
    private String mailAddress;
    private String fromAddress;
    private String fromPerson;
    private String edmHost;
    private String edmUsr;
    private String edmPwd;
    private String edmSubject;
    private String edmAttachedFilePath;
    private String edmImageFileFolder;
    private Map<String, Object> mappingFtlVar;
    
    public EdmSetting() {
    }
    /**
     * @param edmFtlPath FTL的檔案路徑
     * @param mailAddress 收件人email
     * @param fromAddress 寄件者email
     * @param fromPerson 寄件者名稱
     * @param edmHost SMTP host name
     * @param edmUsr 帳號
     * @param edmPwd 密碼
     * @param edmSubject 信件主旨
     * @param edmAttachedFilePath 附件的檔案路徑
     * @param edmImageFileFolder 對應FTL內的image"資料夾"路徑
     * @param mappingFtlVar 對應FTL內的變數Map
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
     * @return the edmFtlPath
     */
    public String getEdmFtlPath() {
        return edmFtlPath;
    }
    /**
     * @param edmFtlPath the edmFtlPath to set
     */
    public void setEdmFtlPath(String edmFtlPath) {
        this.edmFtlPath = edmFtlPath;
    }
    /**
     * @return the mailAddress
     */
    public String getMailAddress() {
        return mailAddress;
    }
    /**
     * @param mailAddress the mailAddress to set
     */
    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }
    /**
     * @return the fromAddress
     */
    public String getFromAddress() {
        return fromAddress;
    }
    /**
     * @param fromAddress the fromAddress to set
     */
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }
    /**
     * @return the fromPerson
     */
    public String getFromPerson() {
        return fromPerson;
    }
    /**
     * @param fromPerson the fromPerson to set
     */
    public void setFromPerson(String fromPerson) {
        this.fromPerson = fromPerson;
    }
    /**
     * @return the edmHost
     */
    public String getEdmHost() {
        return edmHost;
    }
    /**
     * @param edmHost the edmHost to set
     */
    public void setEdmHost(String edmHost) {
        this.edmHost = edmHost;
    }
    /**
     * @return the edmUsr
     */
    public String getEdmUsr() {
        return edmUsr;
    }
    /**
     * @param edmUsr the edmUsr to set
     */
    public void setEdmUsr(String edmUsr) {
        this.edmUsr = edmUsr;
    }
    /**
     * @return the edmPwd
     */
    public String getEdmPwd() {
        return edmPwd;
    }
    /**
     * @param edmPwd the edmPwd to set
     */
    public void setEdmPwd(String edmPwd) {
        this.edmPwd = edmPwd;
    }
    /**
     * @return the edmSubject
     */
    public String getEdmSubject() {
        return edmSubject;
    }
    /**
     * @param edmSubject the edmSubject to set
     */
    public void setEdmSubject(String edmSubject) {
        this.edmSubject = edmSubject;
    }
    /**
     * @return the edmAttachedFilePath
     */
    public String getEdmAttachedFilePath() {
        return edmAttachedFilePath;
    }
    /**
     * @param edmAttachedFilePath the edmAttachedFilePath to set
     */
    public void setEdmAttachedFilePath(String edmAttachedFilePath) {
        this.edmAttachedFilePath = edmAttachedFilePath;
    }
    /**
     * @return the edmImageFileFolder
     */
    public String getEdmImageFileFolder() {
        return edmImageFileFolder;
    }
    /**
     * @param edmImageFileFolder the edmImageFileFolder to set
     */
    public void setEdmImageFileFolder(String edmImageFileFolder) {
        this.edmImageFileFolder = edmImageFileFolder;
    }
    /**
     * @return the mappingFtlVar
     */
    public Map<String, Object> getMappingFtlVar() {
        return mappingFtlVar;
    }
    /**
     * @param mappingFtlVar the mappingFtlVar to set
     */
    public void setMappingFtlVar(Map<String, Object> mappingFtlVar) {
        this.mappingFtlVar = mappingFtlVar;
    }
    
}
