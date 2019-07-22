/*
 * VerPath.java
 *
 * Copyright (c) 2009-2012 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.va.model;

import com.iisigroup.cap.db.model.DataObject;
import com.iisigroup.cap.model.GenericBean;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * <p>
 * VerPath table 設定參數model
 * </p>
 *
 * @author Roger
 * @version
 *          <ul>
 *          <li>2010/7/22, Roger,new
 *          </ul>
 */
public class VerParameterModel {
    private String icscCert;
    private String icscIp;
    private String icscPort;
    private String icscRSAKey;
    private String icscRSAKeyPwd;
    private String icscURI;

    public String getIcscCert() {
        return icscCert;
    }

    public void setIcscCert(String icscCert) {
        this.icscCert = icscCert;
    }

    public String getIcscIp() {
        return icscIp;
    }

    public void setIcscIp(String icscIp) {
        this.icscIp = icscIp;
    }

    public String getIcscPort() {
        return icscPort;
    }

    public void setIcscPort(String icscPort) {
        this.icscPort = icscPort;
    }

    public String getIcscRSAKey() {
        return icscRSAKey;
    }

    public void setIcscRSAKey(String icscRSAKey) {
        this.icscRSAKey = icscRSAKey;
    }

    public String getIcscRSAKeyPwd() {
        return icscRSAKeyPwd;
    }

    public void setIcscRSAKeyPwd(String icscRSAKeyPwd) {
        this.icscRSAKeyPwd = icscRSAKeyPwd;
    }

    public String getIcscURI() {
        return icscURI;
    }

    public void setIcscURI(String icscURI) {
        this.icscURI = icscURI;
    }
}
