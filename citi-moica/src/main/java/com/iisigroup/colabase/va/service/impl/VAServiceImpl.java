package com.iisigroup.colabase.va.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.TBSCertList.CRLEntry;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iisigroup.cap.base.CapSystemProperties;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.cap.utils.CapSystemConfig;
import com.iisigroup.colabase.va.crypto.CryptoLibrary;
import com.iisigroup.colabase.va.crypto.ICSCChecker;
import com.iisigroup.colabase.va.crypto.PKCS7Verify;
import com.iisigroup.colabase.va.dao.ICAInfoDao;
import com.iisigroup.colabase.va.dao.ICrlCertDao;
import com.iisigroup.colabase.va.dao.IVerPathDao;
import com.iisigroup.colabase.va.model.CAInfo;
import com.iisigroup.colabase.va.model.CrlCert;
import com.iisigroup.colabase.va.service.VAService;
import com.iisigroup.colabase.va.util.CommonCryptUtils;

@Service
public class VAServiceImpl implements VAService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VAServiceImpl.class);

    /**
     *  verify PKCS7 success code
     */
    private static final String SUCCESS_CODE = "0000";

    @Autowired
    private IVerPathDao verPathDao;

    @Autowired
    private ICAInfoDao caInfoDao;

    @Autowired
    private CapSystemConfig config;

    @Autowired
    private ICrlCertDao crlCertDao;

    public String doVerifyPKCS7(String personalId, String... p7bDatas) {
        String rc = SUCCESS_CODE;
        for (String p7b : p7bDatas) {
            boolean ret = false;
            PKCS7Verify verifier = new PKCS7Verify();
//            try {
//                p7b = URLDecoder.decode(p7b, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                throw new CapMessageException(e, getClass());
//            }
            ret = verifier.verify(CryptoLibrary.base64Decode(p7b));
            if (!ret) {
                rc = "E001, PKCS7格式錯誤";
                break;
            }
            Certificate signerCert = verifier.getSignerCert();
            ret = CryptoLibrary.verifyCertChain(signerCert);
            if (!ret) {
                rc = "E002, 驗證憑證鏈錯誤";
                break;
            }
            int ret1 = CryptoLibrary.checkCertDateValid(signerCert, null);
            if (ret1 != 0) {
                rc = "E003, 憑證已經失效";
                break;
            }
            try {
                checkICSC(signerCert, personalId);
            } catch (Exception e) {
                LOGGER.error("checkICSC fail : " + personalId, e);
                rc = e.getMessage();
                break;
            }
            try {
                checkOCSP(signerCert);
            } catch (Exception e) {
                LOGGER.error("checkOCSP fail : " + personalId, e);
                ret1 = verifyCertCRL(signerCert);
                if (ret1 != 0) {
                    LOGGER.error("verifyCertCRL fail : " + personalId);
                    rc = "E006, 憑證已經廢止";
                    break;
                }
            }
        }
        return rc;
    }

    private void checkICSC(Certificate ee, String personalId) {
        String ip = verPathDao.findByVerPathId("ICSC_IP").getParmValue();
        String port = verPathDao.findByVerPathId("ICSC_PORT").getParmValue();
        String uri = verPathDao.findByVerPathId("ICSC_URI").getParmValue();
        String certPath = verPathDao.findByVerPathId("ICSC_CERT").getParmValue();
        String rsaKey = verPathDao.findByVerPathId("ICSC_RSA_KEY").getParmValue();
        String rsaKeyPwd = verPathDao.findByVerPathId("ICSC_RSA_KEY_PWD").getParmValue();
        ICSCChecker check = new ICSCChecker();
        boolean ret = false;
        try {
            rsaKeyPwd = CommonCryptUtils.decrypt(rsaKeyPwd);
            check.setClientCertPath(certPath);
            check.setURL(ip, port, uri);
            ret = check.loadRSAkey(rsaKey, rsaKeyPwd);
        } catch (Exception e) {
            LOGGER.error("rsaKeyPwd decrypt fail.");
            throw new SecurityException("E004");
        }
        if (!ret) {
            LOGGER.error("loadRSAkey fail.");
            throw new SecurityException("E004");
        }
        int ret1 = check.checkMOICAICSC(ee, personalId, getProxy());
        if (ret1 != 0) {
            LOGGER.error("checkMOICAICSC fail. rc = {}", ret1);
            throw new SecurityException("E005");
        }
    }

    private void checkOCSP(Certificate signerCert) {
        OCSPReq req = CryptoLibrary.generateOCSPRequest(CryptoLibrary.getCACert(signerCert), signerCert);
        if (req == null) {
            LOGGER.error("generateOCSPRequest fail.");
            throw new SecurityException("W007");
        }
        String aia = CryptoLibrary.getAIALocation(signerCert);
        OCSPResp resp = CryptoLibrary.SendOCSP(req, aia, getProxy());
        if (resp == null) {
            LOGGER.error("SendOCSP fail.");
            throw new SecurityException("W008");
        }
        int ret1 = CryptoLibrary.analyseOCSPResponse(resp, req);
        if (ret1 != 0) {
            LOGGER.error("analyseOCSPResponse fail. rc = {}", ret1);
            throw new SecurityException("W009");
        }
        Certificate signOCSP = CryptoLibrary.verifyOCSPResp(resp);
        boolean ret = CryptoLibrary.verifyCertChain(signOCSP);
        if (!ret) {
            LOGGER.error("verifyCertChain fail.");
            throw new SecurityException("W010");
        }
    }

    private int verifyCertCRL(Certificate signerCert) {
        int crlType = getCrlType(signerCert);
        if (crlCertDao.findCrlCountsByCertType(crlType) == 0) {
            return CryptoLibrary.ERROR_CERT_CRL_VERIFY_NOCRL;
        }
        String serialNo = signerCert.getSerialNumber().getValue().toString();
        CrlCert crlCert = crlCertDao.findBySerialNoAndCertType(serialNo, crlType);
        if (crlCert != null) {
            return CryptoLibrary.ERROR_CERT_CRL_VERIFY_REVOKE;
        }
        return 0;
    }

    /**
     * @param signerCert
     * @return
     */
    private int getCrlType(Certificate signerCert) {
        String algorithm = String.valueOf(signerCert.getSignatureAlgorithm().getAlgorithm());
        if (algorithm.equals("1.2.840.113549.1.1.11")) {    // SHA256withRSA
            return 2;
        } else if (algorithm.equals("1.2.840.113549.1.1.5")) {  // SHA1withRSA
            return 1;
        }
        throw new CapException("Unknown algorithm id:" + algorithm, getClass());
    }

    public boolean verifyPKCS7Signature(String p7b) {
        PKCS7Verify verifier = new PKCS7Verify();
        return verifier.verify(CryptoLibrary.base64Decode(p7b));
    }

    public int loadAllCaCertAndCRL() {
        SearchSetting search = caInfoDao.createSearchTemplete();
        List<CAInfo> list = caInfoDao.find(search);
        CryptoLibrary.clearCACert();
        for (CAInfo info : list) {
            String certData = info.getCertData();
            // 載入 CA 憑證到記憶體
            Certificate caCert = CryptoLibrary.loadCaCerts(CryptoLibrary.base64Decode(certData));
            if (caCert == null) {
                LOGGER.error("LoadCaCerts fail : {}", info.getCaName());
            }
            // 載入已下載的CRL
            String crlUrl = info.getCrlUrl();
            File crlPathInfo = getCrlLocalPath(crlUrl, true);
            try {
                byte[] crl = FileUtils.readFileToByteArray(crlPathInfo);
                if (crl == null) {
                    LOGGER.error("CRL file is empty : {}", crlPathInfo);
                } else {
                    int rc = loadCRL(crl);
                    if (rc != 0) {
                        LOGGER.error("Load CRL file {} fail. rc = {}", crlPathInfo, rc);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Read CRL file fail : {}", crlPathInfo, e);
            }
        }
        CryptoLibrary.validCaCerts();
        return 0;
    }

    public void loadAllCaCert() {
        SearchSetting search = caInfoDao.createSearchTemplete();
        List<CAInfo> list = caInfoDao.find(search);
        CryptoLibrary.clearCACert();
        for (CAInfo info : list) {
            String certData = info.getCertData();
            // 載入 CA 憑證到記憶體
            Certificate caCert = CryptoLibrary.loadCaCerts(CryptoLibrary.base64Decode(certData));
            if (caCert == null) {
                LOGGER.error("LoadCaCerts fail : " + info.getCaName());
            }
        }
        CryptoLibrary.validCaCerts();
    }

    private File getCrlLocalPath(String crlUrl, boolean success) {
        String path = config.getProperty("crlLocalDir") + "/" + md5(crlUrl.getBytes()) + (success ? "/" : "_fail/");
        String fileName = crlUrl.substring(crlUrl.lastIndexOf('/') + 1);
        return new File(path, fileName);
    }

    public String md5(final byte[] data) {
        String result = null;
        try {
            byte[] hash = null;
            MessageDigest md;
            md = MessageDigest.getInstance("md5");
            md.update(data);
            hash = md.digest();
            result = new String(Hex.encodeHex(hash));
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return result;
    }

    public void downloadAllCRL() {
        SearchSetting search = caInfoDao.createSearchTemplete();
        List<CAInfo> list = caInfoDao.find(search);
        for (CAInfo info : list) {
            String crlUrl = info.getCrlUrl();
            byte[] crl = CryptoLibrary.getCRL(crlUrl, getProxy());
            boolean loadStatus = false;
            if (loadCRL(crl) == 0) {
                loadStatus = true;
                LOGGER.debug("Download CRL success : {}", info.getCaName());
            } else {
                LOGGER.error("Download CRL fail : {}", info.getCaName());
            }
            if (crl != null && crl.length > 0) {
                File crlPathInfo = getCrlLocalPath(crlUrl, loadStatus);
                try {
                    FileUtils.writeByteArrayToFile(crlPathInfo, crl);
                    LOGGER.debug("Save CRL success : {}", info.getCaName());
                } catch (IOException e) {
                    LOGGER.error("Save CRL file fail : " + info.getCaName(), e);
                }
            }
            info.setDlCrlResult(loadStatus);
            caInfoDao.save(info);
        }
    }

    private int loadCRL(byte[] crl) {
        CertificateList crlList = CryptoLibrary.parseCRL(crl);
        if (crlList == null) {
            LOGGER.error("parseCRL fail.");
            return -1;
        }
        boolean ret = CryptoLibrary.verifyCRLChain(crlList);
        if (!ret) {
            LOGGER.error("verifyCRLChain fail.");
            return -2;
        }
        return CryptoLibrary.verifyCRLEffectiveDate(crlList);
    }

    public void saveAllCRL() {
        SearchSetting search = caInfoDao.createSearchTemplete();
        List<CAInfo> list = caInfoDao.find(search);
        for (CAInfo info : list) {
            // 載入已下載的CRL
            String crlUrl = info.getCrlUrl();
            File crlPathInfo = getCrlLocalPath(crlUrl, info.isDlCrlResult());
            try {
                byte[] crl = FileUtils.readFileToByteArray(crlPathInfo);
                if (crl == null) {
                    LOGGER.error("CRL file is empty : {}", crlPathInfo);
                    continue;
                }
                CertificateList crlList = CryptoLibrary.parseCRL(crl);
                String caName = info.getCaName();
                int crlType = caName.charAt(caName.length() - 1) == '2' ? 2 : 1;
                int rc = saveCrlList(crlList, crlType);
                if (rc != 0) {
                    LOGGER.error("Save CRL file {} fail. rc = {}", caName, rc);
                } else {
                    LOGGER.debug("Save CRL file {} success.", caName);
                }
            } catch (Exception e) {
                LOGGER.error("Save CRL file fail : " + crlPathInfo, e);
            }
        }
    }

    private int saveCrlList(CertificateList crl, final int crlType) {
        int ret = 0;
        if (crl == null) {
            return -1;
        }

        final List<CrlCert> crlCerts = new ArrayList<CrlCert>();
        try {
            Calendar thisUpdate = Calendar.getInstance();
            thisUpdate.setTime(crl.getThisUpdate().getDate());
            // logger.info(func+"憑證廢止清單(CRL)有效日期 : " + sdf.format(thisUpdate.getTime()));
            Calendar nextUpdate = Calendar.getInstance();
            nextUpdate.setTime(crl.getNextUpdate().getDate());
            // logger.info(func+"憑證廢止清單(CRL)下次更新 : " + sdf.format(nextUpdate.getTime()));
            Calendar now = Calendar.getInstance();
            long thisHours = CryptoLibrary.calculateHours(thisUpdate, now);
            long nextHours = CryptoLibrary.calculateHours(nextUpdate, now);
            // logger.info(func+"憑證廢止清單(CRL)有效日期距離現在 : " + thisHours + " 小時");
            // logger.info(func+"憑證廢止清單(CRL)下次更新距離現在 : " + nextHours + " 小時");
            if (thisHours < 0) {
                // logger.error(func+"憑證廢止清單(CRL)尚未生效!!");
                ret = CryptoLibrary.ERROR_CRL_NOT_YET_VALIE;
            }
            if (nextHours > 0) {
                // logger.error(func+"憑證廢止清單(CRL)已經過期!!");
                ret = CryptoLibrary.ERROR_CRL_EXPIRED;
            }
            CRLEntry[] crlCertsEntry = crl.getTBSCertList().getRevokedCertificates();
            // logger.info(func+"由 CA 下載之憑證廢止清單(CRL), 共計發行筆數 : " + crlCerts);
            for (CRLEntry crlEntry : crlCertsEntry) {
                CrlCert crlCert = new CrlCert();
                crlCert.setCrlType(crlType);
                // 憑證序號
                crlCert.setSerialNo(crlEntry.getUserCertificate().getValue().toString());
                // 撤銷時間
                crlCert.setExpireDate(crlEntry.getRevocationDate().getDate());
                // CRL 理由代碼
                Extension reasonCode = crlEntry.getExtensions().getExtension(Extension.reasonCode);
                crlCert.setCertStatus(String.valueOf(reasonCode.getParsedValue().toASN1Primitive().getEncoded()[2]));
                crlCerts.add(crlCert);
            }

            crlCertDao.truncate(crlType);
            Long startTime = System.currentTimeMillis();
            LOGGER.debug(" Begin save MOICA{}", crlType);
            crlCertDao.batchSave(crlCerts);
            LOGGER.debug("Finish save MOICA{} - cost time : {} ms", crlType, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            ret = CryptoLibrary.ERROR_CRL_EXCEPTION;
            LOGGER.error(e.getMessage(), e);
        }
        return ret;
    }

    public void updateAllCaCertStatus() {
        SearchSetting search = caInfoDao.createSearchTemplete();
        List<CAInfo> list = caInfoDao.find(search);
        for (CAInfo info : list) {
            info.setStatus(getCaCertStatus(info.getExpiredDate()));
            caInfoDao.save(info);
        }
    }

    public String getCaCertStatus(Date ExpiredDate) {
        if (CapDate.calculateDays(ExpiredDate, new Date()) < 0) {
            return "1";
        } else {
            return "0";
        }
    }

    public Certificate loadCaCerts(byte[] p7bcertchain) throws CertException {
        Certificate caCert = CryptoLibrary.loadCaCerts(p7bcertchain);
        if (caCert == null) {
            throw new CertException("There is no valid CA cert.");
        } else {
            CryptoLibrary.validCaCerts();
            if (!CryptoLibrary.isCaCertValid(getCertInfoByType(caCert, CertInfoType.SUBJECT_KEY_IDENTIFIER))) {
                throw new CertException("VerifyCertChain CA Cert Chain Fail.");
            }
        }
        return caCert;
    }

    public String getCertInfoByType(Certificate cert, CertInfoType type) {
        String result = null;
        switch (type) {
        case CERT_TYPE:
            result = CryptoLibrary.getCertType(cert);
            break;
        case PERSONAL_ID:
            result = CryptoLibrary.getPersonId(cert);
            break;
        case ENTERPRISE_ID:
            result = CryptoLibrary.getEnterpriseId(cert);
            break;
        case X509_SUBJECT:
            result = CryptoLibrary.getX509Subject(cert);
            break;
        case X509_ISSUER:
            result = CryptoLibrary.getX509Issuer(cert);
            break;
        case X509_CN:
            result = CryptoLibrary.getX509CN(cert);
            break;
        case X509_SERIAL:
            result = CryptoLibrary.getX509Serial(cert);
            break;
        case X509_KEY_USAGE:
            result = CryptoLibrary.hexEncode(CryptoLibrary.getX509KeyUsageBytes(cert));
            break;
        case X509_FINGER:
            result = CryptoLibrary.getX509Finger(cert);
            break;
        case X509_NOTBEFORE:
            result = CryptoLibrary.getX509NotBefore(cert);
            break;
        case X509_NOTAFTER:
            result = CryptoLibrary.getX509NotAfter(cert);
            break;
        case SUBJECT_KEY_IDENTIFIER:
            result = CryptoLibrary.getExtensionsSubjectKeyIdentifier(cert);
            break;
        case AUTHORITY_KEY_IDENTIFIER:
            result = CryptoLibrary.getX509AuthorityKeyIdentifier(cert);
            break;
        default:
        }
        return result;
    }

    public Certificate getSignerCert(String p7b) {
        PKCS7Verify verifier = new PKCS7Verify();
        if (verifier.verify(CryptoLibrary.base64Decode(p7b))) {
            return verifier.getSignerCert();
        } else {
            return null;
        }
    }

    private Proxy getProxy() {
        Proxy proxy = null;
        String proxyEnable = config.getProperty("moica.proxy.enable");
        String proxyHost = config.getProperty("moica.proxy.host");
        String proxyPort = config.getProperty("moica.proxy.port");
        if (proxyEnable != null && proxyHost != null && proxyPort != null) {
            LOGGER.debug("PROXY_ENABLE: " + proxyEnable);
            LOGGER.debug("PROXY_HOST: " + proxyHost);
            LOGGER.debug("PROXY_PORT: " + proxyPort);
            if (Boolean.parseBoolean(proxyEnable)) {
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
            }
        }
        return proxy;
    }

}
