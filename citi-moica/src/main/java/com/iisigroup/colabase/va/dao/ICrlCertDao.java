/*
 * ICrlCertDao.java
 *
 * Copyright (c) 2009-2016 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.va.dao;

import java.util.List;

import com.iisigroup.colabase.va.model.CrlCert;
import com.iisigroup.cap.db.dao.GenericDao;

/**
 * <pre>
 * {@link CrlCert} DAO
 * </pre>
 *
 * @since 2016-04-25
 * @author Bo-Xuan Fan
 * @version
 *          <ul>
 *          <li>2016-04-25,Bo-Xuan Fan,new
 *          </ul>
 */
public interface ICrlCertDao extends GenericDao<CrlCert> {

    /**
     * 憑證序號 & 憑證種類查詢
     * @param serialNo 憑證序號
     * @param crlType 憑證種類, 1=SHA1, 2=SHA256
     * @return
     */
    CrlCert findBySerialNoAndCertType(String serialNo, int crlType);

    /**
     * 清空該憑證資料
     * @param crlType 憑證種類, 1=SHA1, 2=SHA256
     */
    void truncate(int crlType);

    /**
     * 批次匯入 CRL
     * @param crlCerts
     */
    void batchSave(List<CrlCert> crlCerts);

    /**
     * 計算 CRL 憑證筆數
     * @param crlType 憑證種類, 1=SHA1, 2=SHA256
     * @return
     */
    int findCrlCountsByCertType(int crlType);
}
