/*
 * CrlCertPK.java
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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * <pre>
 * CrlCertPK
 * </pre>
 *
 * @since 2016-04-27
 * @author Bo-Xuan Fan
 * @version
 *          <ul>
 *          <li>2016-04-27,Bo-Xuan Fan,new
 *          </ul>
 */
@Embeddable
public class CrlCertPK implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1253136652904891719L;

    /** CRL種類: 1=SHA1, 2=SHA256 */
    @Column(name = "CRL_TYPE", nullable = false)
    private int crlType;

    /** 憑證序號 */
    @Column(name = "SERIAL_NO", length = 50, nullable = false)
    private String serialNo;

    public int getCrlType() {
        return crlType;
    }

    public void setCrlType(int crlType) {
        this.crlType = crlType;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + crlType;
        result = prime * result + ((serialNo == null) ? 0 : serialNo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CrlCertPK other = (CrlCertPK) obj;
        if (crlType != other.crlType)
            return false;
        if (serialNo == null) {
            if (other.serialNo != null)
                return false;
        } else if (!serialNo.equals(other.serialNo))
            return false;
        return true;
    }
}
