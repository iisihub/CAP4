package com.iisigroup.colabase.va.service;

import java.util.Date;

import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.cert.CertException;

public interface VAService {

    enum CertInfoType {
        CERT_TYPE, PERSONAL_ID, ENTERPRISE_ID, X509_SUBJECT, X509_ISSUER, X509_CN, X509_SERIAL, X509_KEY_USAGE, X509_FINGER, X509_NOTBEFORE, X509_NOTAFTER, SUBJECT_KEY_IDENTIFIER, AUTHORITY_KEY_IDENTIFIER
    }

    /**
     * 驗 PKCS7 的簽章。
     *
     * @param p7b
     *            Base64格式的PKCS7
     * @return
     */
    boolean verifyPKCS7Signature(String p7b);

    /**
     * 取得簽章者憑證
     *
     * @param p7b
     * @return
     */
    Certificate getSignerCert(String p7b);
    
    /**
     * 
     */
    String doVerifyPKCS7(String personalId, String... p7bDatas);
//    /**
//     *
//     * @param request
//     * @return
//     * @throws SecurityException
//     */
//
//    String verifyPKCS7ForRe(Request request) throws SecurityException;

//    /**
//     * 驗證 PKCS7 的有效性，包含驗章、憑證鏈、ICSC、OCSP、CRL
//     *
//     * @param Request
//     *            request
//     * @return 若回傳的 return code 第一位為 E 代表是錯誤，第一位為 W 代表 WARNING。0000 為成功。
//     * @throws SecurityException
//     *             exception 其 message 代表 i18n 的 key
//     */
//    String verifyPKCS7(Request request) throws SecurityException;

    /**
     * 載入所有 CA 憑證和憑證廢止清單
     *
     * @return
     */
    int loadAllCaCertAndCRL();

    /**
     * 載入所有 CA 憑證
     */
    void loadAllCaCert();

    /**
     * CRL 下載
     */
    void downloadAllCRL();

    /**
     * CRL 灌檔
     */
    void saveAllCRL();

    /**
     * 更新所有 CA 憑證狀態
     */
    void updateAllCaCertStatus();

    /**
     * 判斷 CA 憑證狀態
     *
     * @param ExpiredDate
     *            到期日
     * @return 0:ACTIVE 1:EXPIRED
     */
    String getCaCertStatus(Date ExpiredDate);

    /**
     * 載入 CA 憑證並驗證
     *
     * @param p7bcertchain
     * @return
     */
    Certificate loadCaCerts(byte[] p7bcertchain) throws CertException;

    /**
     * 根據憑證資訊種類取得對應資訊
     *
     * @param cert
     *            憑證
     * @param type
     *            CertInfoType
     * @return
     */
    String getCertInfoByType(Certificate cert, CertInfoType type);

    /**
     * hash
     *
     * @param data
     * @return
     */
    String md5(final byte[] data);
}
