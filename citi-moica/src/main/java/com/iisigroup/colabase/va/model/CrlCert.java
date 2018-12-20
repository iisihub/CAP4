/*
 * CrlCert.java
 *
 * Copyright (c) 2009-2016 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.va.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.iisigroup.cap.db.model.DataObject;
import com.iisigroup.cap.model.GenericBean;

/**
 * <pre>
 * CrlCert
 * </pre>
 *
 * @since 2016-04-25
 * @author Bo-Xuan Fan
 * @version <ul>
 *          <li>2016-04-25,Bo-Xuan Fan,new
 *          </ul>
 */
@Entity
@SuppressWarnings("serial")
@Table(name = "CO_CRL_CERT")
public class CrlCert extends GenericBean implements DataObject {

    @EmbeddedId
    private CrlCertPK crlCertPK = new CrlCertPK();

    /** CRL 理由代碼 */
    @Column(name = "CERT_STATUS", length = 2, nullable = false)
    private String certStatus;

    /** 撤銷時間 */
    @Column(name = "EXPIRE_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expireDate;

    public CrlCertPK getCrlCertPK() {
        return crlCertPK;
    }

    public void setCrlCertPK(CrlCertPK crlCertPK) {
        this.crlCertPK = crlCertPK;
    }

    public int getCrlType() {
        return crlCertPK.getCrlType();
    }

    public void setCrlType(int crlType) {
        this.crlCertPK.setCrlType(crlType);
    }

    public String getSerialNo() {
        return this.crlCertPK.getSerialNo();
    }

    public void setSerialNo(String serialNo) {
        this.crlCertPK.setSerialNo(serialNo);
    }

    public String getCertStatus() {
        return certStatus;
    }

    public void setCertStatus(String certStatus) {
        this.certStatus = certStatus;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public String getOid() {
        return this.getSerialNo();
    }

    public void setOid(String oid) {
        this.setSerialNo(oid);
    }
}
