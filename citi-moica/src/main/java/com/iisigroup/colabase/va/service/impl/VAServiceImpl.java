//package com.iisigroup.colabase.va.service.impl;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.Proxy;
//import java.net.URLDecoder;
//import java.net.UnknownHostException;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//
//import org.apache.commons.codec.binary.Hex;
//import org.apache.commons.io.FileUtils;
//import org.bouncycastle.asn1.x509.CertificateList;
//import org.bouncycastle.asn1.x509.TBSCertList.CRLEntry;
//import org.bouncycastle.asn1.x509.X509CertificateStructure;
//import org.bouncycastle.asn1.x509.X509Extension;
//import org.bouncycastle.cert.CertException;
//import org.bouncycastle.cert.ocsp.OCSPReq;
//import org.bouncycastle.cert.ocsp.OCSPResp;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.citibank.pmom.app.service.ZipFileService;
//import com.citibank.pmom.base.page.util.MOMReportUtils;
//import com.citibank.pmom.common.constants.MOMConstants;
//import com.citibank.pmom.common.jsp.MOMSystemProperties;
//import com.citibank.pmom.va.crypto.CryptoLibrary;
//import com.citibank.pmom.va.crypto.ICSCChecker;
//import com.citibank.pmom.va.crypto.PKCS7Verify;
//import com.citibank.pmom.va.dao.ICAInfoDao;
//import com.citibank.pmom.va.dao.ICrlCertDao;
//import com.citibank.pmom.va.dao.ITransLogDao;
//import com.citibank.pmom.va.dao.IVerPathDao;
//import com.citibank.pmom.va.model.CAInfo;
//import com.citibank.pmom.va.model.CrlCert;
//import com.citibank.pmom.va.model.TransLog;
//import com.citibank.pmom.va.service.VAService;
//import com.citibank.pmom.va.util.CommonCryptUtils;
//import com.iisigroup.cap.component.Request;
//import com.iisigroup.cap.db.dao.SearchSetting;
//import com.iisigroup.cap.exception.CapException;
//import com.iisigroup.cap.exception.CapMessageException;
//import com.iisigroup.cap.utils.CapDate;
//import com.iisigroup.cap.utils.CapSystemConfig;
//
//@Service
//public class VAServiceImpl implements VAService {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(VAServiceImpl.class);
//
//    /**
//     *  verify PKCS7 success code
//     */
//    private static final String SUCCESS_CODE = "0000";
//
//    private static final String ZIP_VA_LOCATION = "pmom.file.zip.va.location";
//    private static final String ZIP_CUS_LOCATION = "pmom.file.zip.cus.location";
//    private static final String ZIP_RETURNDOC_LOCATION = "pmom.file.zip.returndoc.location";
//    private static final String FILES_UUID = "FILES_UUID";
//    private static final String TEMP_FOLDER = "pmom.file.temp.location";
//    private static final String TEMP_UPLOAD_FOLDER = "pmom.file.temp.uploadfiles";
//
//    @Autowired
//    private MOMSystemProperties sysProp;
//
//    @Autowired
//    private IVerPathDao verPathDao;
//
//    @Autowired
//    private ICAInfoDao caInfoDao;
//
//    @Autowired
//    private ITransLogDao transLogDao;
//
//    @Autowired
//    private CapSystemConfig config;
//
//    @Autowired
//    private ZipFileService zipSrv;
//
//    @Autowired
//    private ICrlCertDao crlCertDao;
//
//    public String verifyPKCS7ForRe(Request request) throws SecurityException {
//        String personalId = request.get("ID_NO");
//        String p7b = request.get("PKCS7Data");
//        String rc = doVerifyPKCS7(personalId, p7b);
//        if (SUCCESS_CODE.equals(rc)) {
//            String df = CapDate.getCurrentDate("yyyyMMdd");
//            String zipPwd = personalId.substring(0, 4) + df;
//            File zipLocation = new File(config.getProperty(ZIP_RETURNDOC_LOCATION), df);
//            if (!zipLocation.exists()) {
//                zipLocation.mkdirs();
//            }
//            File destination = new File(zipLocation, "reUpload_Payrollaccount_" + MOMReportUtils.maskId(personalId) + ".zip");
//            HttpSession session = ((HttpServletRequest) request.getServletRequest()).getSession();
//            File pdfTempPath = new File((String) session.getAttribute("pdfPath"));
//            File p7bTempPath = new File((String) session.getAttribute("p7bPath"));
//            File imageZipPath = new File((String) session.getAttribute("imageZipPath"));
//            try {
//                zipSrv.zip(destination, true, zipPwd, new File[] { pdfTempPath, p7bTempPath, imageZipPath });
//                saveTransLog(request, URLDecoder.decode(p7b, "UTF-8"), destination.toString().replaceAll("\\\\", "/"), rc, SystemType.MOICA_RETURN_DOC);
//            } catch (IOException e) {
//                LOGGER.error(e.getMessage(), e);
//            }
//
//        }
//        return rc;
//    }
//
//    public String verifyPKCS7(Request request) throws SecurityException {
//        String p7b = request.get("PKCS7Data");
//        String p7b2 = request.get("PKCS7Data2");
//        String personalId = request.get("ID_NO");
//        String rc = doVerifyPKCS7(personalId, p7b, p7b2);
//        if (SUCCESS_CODE.equals(rc)) {
//            HttpSession session = ((HttpServletRequest) request.getServletRequest()).getSession();
//            String filesUUID = session.getAttribute(FILES_UUID) != null ? (String) session.getAttribute(FILES_UUID) : "";
//            File tempFilePath = new File(config.getProperty(TEMP_FOLDER), filesUUID);
//
//            PDFPacker[] workers = new PDFPacker[] {
//                new VAPDFPacker(p7b, (String) (session.getAttribute(MOMConstants.FILE_NAME)) + ".pdf"),
//                new CUSPDFPacker(p7b2, (String) (session.getAttribute(MOMConstants.CONCAT_PDF_NAME)) + ".pdf")
//            };
//
//            for (PDFPacker worker : workers) {
//                try {
//                    File tempPdf = new File(tempFilePath, worker.getPdfName());
//                    File pdfFolder = new File(config.getProperty(ZIP_VA_LOCATION), filesUUID);
//                    File pdf = new File(pdfFolder, worker.getPdfName());
//                    worker.pack(request);
//                    FileUtils.copyFile(tempPdf, pdf);
//                    saveTransLog(request, URLDecoder.decode(worker.getP7Data(), "UTF-8"), pdf.toString().replaceAll("\\\\", "/"), rc, SystemType.MOICA_OPEN_ACCOUNT);
//                } catch (IOException e) {
//                    LOGGER.error("copy moica pdf file fail : " + personalId + " " + tempFilePath, e);
//                    rc = "E011";
//                    break;
//                }
//            }
//        }
//        return rc;
//    }
//
//    interface PDFPacker {
//        String getUploadFileLocation();
//        String getPdfName();
//        String getP7Data();
//        void pack(Request request) throws IOException;
//    }
//    abstract class AbstractPDFPacker implements PDFPacker {
//        protected String p7Data;
//        protected String pdfName;
//
//        AbstractPDFPacker(String p7Data, String pdfName) {
//            this.p7Data = p7Data;
//            this.pdfName = pdfName;
//        }
//
//        public String getP7Data() {
//            return p7Data;
//        }
//
//        public String getPdfName() {
//            return pdfName;
//        }
//
//        protected File getTempFolder(HttpSession session) {
//            String filesUUID = session.getAttribute(FILES_UUID) != null ? (String) session.getAttribute(FILES_UUID) : "";
//            return new File(config.getProperty(TEMP_FOLDER), filesUUID);
//        }
//    }
//    class CUSPDFPacker extends AbstractPDFPacker implements PDFPacker {
//
//        CUSPDFPacker(String p7Data, String pdfName) {
//            super(p7Data, pdfName);
//        }
//
//        public String getUploadFileLocation() {
//            return config.getProperty(ZIP_CUS_LOCATION);
//        }
//
//        public void pack(Request request) throws IOException {
//            String serNo = "001";
//            //判斷批次時間8:30 ~ 15:29 為 002 ,15:30 ~ 8:29 為001
//            Timestamp ts = CapDate.getCurrentTimestamp();
//            String fhm = new SimpleDateFormat("HHmm").format(ts);
//            if (fhm.compareTo("0830") > 0 && fhm.compareTo("1530") < 0) {
//                serNo = "002";
//            }
//
//            String YYMMDD = null;
//            String YYYYMMDD = null;
//            // 批次為001, 判斷資料夾時間24點前要+1天, 過24點則不用加
//            if (serNo.equals("001") && fhm.compareTo("2359") <= 0 && fhm.compareTo("0830") > 0 ) {
//                YYMMDD = new SimpleDateFormat("yyMMdd").format(ts.getTime() + 86400000);
//                YYYYMMDD = new SimpleDateFormat("yyyyMMdd").format(ts.getTime() + 86400000);
//            } else {
//                YYMMDD = new SimpleDateFormat("yyMMdd").format(ts.getTime());
//                YYYYMMDD = new SimpleDateFormat("yyyyMMdd").format(ts.getTime());
//            }
//
//            String personalId = request.get("ID_NO");
//            String brday = request.get("BIRTH").replaceAll("-", "");
//            String zipName = new StringBuilder()
//                .append("e_payrollaccount_").append(YYMMDD).append(serNo)
//                .append('_').append(MOMReportUtils.maskId(personalId)).append(".zip").toString();
//            // password = <加密邏輯> ID第一碼 + 出生年月日
//            String password = new StringBuilder().append(Character.toUpperCase(personalId.charAt(0))).append(brday).toString();
//            HttpSession session = ((HttpServletRequest) request.getServletRequest()).getSession();
//            File tempFilePath = getTempFolder(session);
//            File destination = new File(tempFilePath, zipName);
//            // 加上上傳的4張圖片zip檔
//            zipSrv.zip(destination, true, password,
//                    new File[] {
//                        new File(tempFilePath, getPdfName()), new File(tempFilePath, getPdfName().replace(".pdf", ".p7b")),
//                        new File(tempFilePath + File.separator + config.getProperty(TEMP_UPLOAD_FOLDER) + File.separator + MOMReportUtils.maskId(personalId) + ".zip") });
//
//            //pdf存放的folder->Main MOICA folder+TCO+日期+ batch number+DL File
//            String tcoPath = getUploadFileLocation() + File.separator + "TCO" + File.separator + YYYYMMDD + serNo
//                    + File.separator + "Application File";
//            FileUtils.copyFile(destination, new File(tcoPath, zipName));
//        }
//    }
//    class VAPDFPacker extends AbstractPDFPacker implements PDFPacker {
//
//        VAPDFPacker(String p7Data, String pdfName) {
//            super(p7Data, pdfName);
//        }
//
//        public String getUploadFileLocation() {
//            return config.getProperty(ZIP_VA_LOCATION);
//        }
//
//        public void pack(Request request) throws IOException {
//            String password = request.get("ID_NO").toUpperCase();
//            String zipFileName = getPdfName().replace(".pdf", ".zip");
//            HttpSession session = ((HttpServletRequest) request.getServletRequest()).getSession();
//            session.setAttribute("USER_ZIP_FILE_NAME", zipFileName);
//            File tempFolder = getTempFolder(session);
//            File destination = new File(tempFolder, zipFileName);
//            String filePath = destination.getName();
//            filePath = filePath.substring(0, filePath.lastIndexOf(".zip"));
//            //只需要打包使用者的 pdf & p7b
//            File[] packFiles = new File[] { new File(tempFolder, getPdfName()), new File(tempFolder, getPdfName().replace(".pdf", ".p7b")) };
//            zipSrv.zip(destination, true, password, packFiles);
//        }
//    }
//
//    private void saveTransLog(Request request, String p7Data, String pdfPath, String rc, SystemType systemType) {
//        TransLog transLog = new TransLog();
//        String personalId = request.get("ID_NO");
//        transLog.setIdHash(md5(personalId.getBytes()));
//        transLog.setP7Data(p7Data);
//        transLog.setPdfPath(pdfPath);
//        transLog.setPrintSeq("");
//        transLog.setSourceIp(request.getServletRequest().getRemoteAddr());
//        transLog.setStatus(rc);
//        transLog.setTransDate(CapDate.getCurrentTimestamp());
//        transLog.setSystemtype(systemType.getCode());
//        try {
//            transLog.setPdfServer(InetAddress.getLocalHost().getHostName());
//        } catch (UnknownHostException e) {
//            LOGGER.error("Can't get hostname for moica pdf server fail : " + personalId, e);
//        }
//        transLogDao.save(transLog);
//    }
//
//    private String doVerifyPKCS7(String personalId, String... p7bDatas) {
//        String rc = SUCCESS_CODE;
//        for (String p7b : p7bDatas) {
//            boolean ret = false;
//            PKCS7Verify verifier = new PKCS7Verify();
//            try {
//                p7b = URLDecoder.decode(p7b, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                throw new CapMessageException(e, getClass());
//            }
//            ret = verifier.verify(CryptoLibrary.base64Decode(p7b));
//            if (!ret) {
//                rc = "E001";
//                break;
//            }
//            X509CertificateStructure signerCert = verifier.getSignerCert();
//            ret = CryptoLibrary.verifyCertChain(signerCert);
//            if (!ret) {
//                rc = "E002";
//                break;
//            }
//            int ret1 = CryptoLibrary.checkCertDateValid(signerCert, null);
//            if (ret1 != 0) {
//                rc = "E003";
//                break;
//            }
//            try {
//                checkICSC(signerCert, personalId);
//            } catch (Exception e) {
//                LOGGER.error("checkICSC fail : " + personalId, e);
//                rc = e.getMessage();
//                break;
//            }
//            try {
//                checkOCSP(signerCert);
//            } catch (Exception e) {
//                LOGGER.error("checkOCSP fail : " + personalId, e);
//                ret1 = verifyCertCRL(signerCert);
//                if (ret1 != 0) {
//                    LOGGER.error("verifyCertCRL fail : " + personalId);
//                    rc = "E006";
//                    break;
//                }
//            }
//        }
//        return rc;
//    }
//
//    private void checkICSC(X509CertificateStructure ee, String personalId) throws SecurityException {
//        String ip = verPathDao.findByVerPathId("ICSC_IP").getParmValue();
//        String port = verPathDao.findByVerPathId("ICSC_PORT").getParmValue();
//        String uri = verPathDao.findByVerPathId("ICSC_URI").getParmValue();
//        String certPath = verPathDao.findByVerPathId("ICSC_CERT").getParmValue();
//        String rsaKey = verPathDao.findByVerPathId("ICSC_RSA_KEY").getParmValue();
//        String rsaKeyPwd = verPathDao.findByVerPathId("ICSC_RSA_KEY_PWD").getParmValue();
//        ICSCChecker check = new ICSCChecker();
//        boolean ret = false;
//        try {
//            rsaKeyPwd = CommonCryptUtils.decrypt(rsaKeyPwd);
//            check.setClientCertPath(certPath);
//            check.setURL(ip, port, uri);
//            ret = check.loadRSAkey(rsaKey, rsaKeyPwd);
//        } catch (Exception e) {
//            LOGGER.error("rsaKeyPwd decrypt fail.");
//            throw new SecurityException("E004");
//        }
//        if (!ret) {
//            LOGGER.error("loadRSAkey fail.");
//            throw new SecurityException("E004");
//        }
//        int ret1 = check.checkMOICAICSC(ee, personalId, getProxy());
//        if (ret1 != 0) {
//            LOGGER.error("checkMOICAICSC fail. rc = " + ret1);
//            throw new SecurityException("E005");
//        }
//    }
//
//    private void checkOCSP(X509CertificateStructure signerCert) {
//        OCSPReq req = CryptoLibrary.generateOCSPRequest(CryptoLibrary.getCACert(signerCert), signerCert);
//        if (req == null) {
//            LOGGER.error("generateOCSPRequest fail.");
//            throw new SecurityException("W007");
//        }
//        String aia = CryptoLibrary.getAIALocation(signerCert);
//        OCSPResp resp = CryptoLibrary.SendOCSP(req, aia, getProxy());
//        if (resp == null) {
//            LOGGER.error("SendOCSP fail.");
//            throw new SecurityException("W008");
//        }
//        int ret1 = CryptoLibrary.analyseOCSPResponse(resp, req);
//        if (ret1 != 0) {
//            LOGGER.error("analyseOCSPResponse fail. rc = " + ret1);
//            throw new SecurityException("W009");
//        }
//        X509CertificateStructure signOCSP = CryptoLibrary.verifyOCSPResp(resp);
//        boolean ret = CryptoLibrary.verifyCertChain(signOCSP);
//        if (!ret) {
//            LOGGER.error("verifyCertChain fail.");
//            throw new SecurityException("W010");
//        }
//    }
//
//    private int verifyCertCRL(X509CertificateStructure signerCert) {
//        int crlType = getCrlType(signerCert);
//        if (crlCertDao.findCrlCountsByCertType(crlType) == 0) {
//            return CryptoLibrary.ERROR_CERT_CRL_VERIFY_NOCRL;
//        }
//        String serialNo = signerCert.getSerialNumber().getValue().toString();
//        CrlCert crlCert = crlCertDao.findBySerialNoAndCertType(serialNo, crlType);
//        if (crlCert != null) {
//            return CryptoLibrary.ERROR_CERT_CRL_VERIFY_REVOKE;
//        }
//        return 0;
//    }
//
//    /**
//     * @param signerCert
//     * @return
//     */
//    private int getCrlType(X509CertificateStructure signerCert) {
//        String algorithm = String.valueOf(signerCert.getSignatureAlgorithm().getAlgorithm());
//        if (algorithm.equals("1.2.840.113549.1.1.11")) {    // SHA256withRSA
//            return 2;
//        } else if (algorithm.equals("1.2.840.113549.1.1.5")) {  // SHA1withRSA
//            return 1;
//        }
//        throw new CapException("Unknown algorithm id:" + algorithm, getClass());
//    }
//
//    public boolean verifyPKCS7Signature(String p7b) {
//        PKCS7Verify verifier = new PKCS7Verify();
//        return verifier.verify(CryptoLibrary.base64Decode(p7b));
//    }
//
//    public int loadAllCaCertAndCRL() {
//        SearchSetting search = caInfoDao.createSearchTemplete();
//        List<CAInfo> list = caInfoDao.find(search);
//        CryptoLibrary.clearCACert();
//        for (CAInfo info : list) {
//            String certData = info.getCertData();
//            // 載入 CA 憑證到記憶體
//            X509CertificateStructure caCert = CryptoLibrary.loadCaCerts(CryptoLibrary.base64Decode(certData));
//            if (caCert == null) {
//                LOGGER.error("LoadCaCerts fail : " + info.getCaName());
//            }
//            // 載入已下載的CRL
//            String crlUrl = info.getCrlUrl();
//            File crlPathInfo = getCrlLocalPath(crlUrl, true);
//            try {
//                byte[] crl = FileUtils.readFileToByteArray(crlPathInfo);
//                if (crl == null) {
//                    LOGGER.error("CRL file is empty : {}", crlPathInfo);
//                } else {
//                    int rc = loadCRL(crl);
//                    if (rc != 0) {
//                        LOGGER.error("Load CRL file {} fail. rc = {}", crlPathInfo, rc);
//                    }
//                }
//            } catch (IOException e) {
//                LOGGER.error("Read CRL file fail : " + crlPathInfo, e);
//            }
//        }
//        CryptoLibrary.validCaCerts();
//        return 0;
//    }
//
//    public void loadAllCaCert() {
//        SearchSetting search = caInfoDao.createSearchTemplete();
//        List<CAInfo> list = caInfoDao.find(search);
//        CryptoLibrary.clearCACert();
//        for (CAInfo info : list) {
//            String certData = info.getCertData();
//            // 載入 CA 憑證到記憶體
//            X509CertificateStructure caCert = CryptoLibrary.loadCaCerts(CryptoLibrary.base64Decode(certData));
//            if (caCert == null) {
//                LOGGER.error("LoadCaCerts fail : " + info.getCaName());
//            }
//        }
//        CryptoLibrary.validCaCerts();
//    }
//
//    private File getCrlLocalPath(String crlUrl, boolean success) {
//        String path = config.getProperty("crlLocalDir") + "/" + md5(crlUrl.getBytes()) + (success ? "/" : "_fail/");
//        String fileName = crlUrl.substring(crlUrl.lastIndexOf("/") + 1);
//        return new File(path, fileName);
//    }
//
//    public String md5(final byte[] data) {
//        String result = null;
//        try {
//            byte[] hash = null;
//            MessageDigest md;
//            md = MessageDigest.getInstance("md5");
//            md.update(data);
//            hash = md.digest();
//            result = new String(Hex.encodeHex(hash));
//        } catch (NoSuchAlgorithmException e) {
//        }
//        return result;
//    }
//
//    public void downloadAllCRL() {
//        SearchSetting search = caInfoDao.createSearchTemplete();
//        List<CAInfo> list = caInfoDao.find(search);
//        for (CAInfo info : list) {
//            String crlUrl = info.getCrlUrl();
//            byte[] crl = CryptoLibrary.getCRL(crlUrl, getProxy());
//            boolean loadStatus = false;
//            if (loadCRL(crl) == 0) {
//                loadStatus = true;
//                LOGGER.debug("Download CRL success : {}", info.getCaName());
//            } else {
//                LOGGER.error("Download CRL fail : {}", info.getCaName());
//            }
//            if (crl != null && crl.length > 0) {
//                File crlPathInfo = getCrlLocalPath(crlUrl, loadStatus);
//                try {
//                    FileUtils.writeByteArrayToFile(crlPathInfo, crl);
//                    LOGGER.debug("Save CRL success : {}", info.getCaName());
//                } catch (IOException e) {
//                    LOGGER.error("Save CRL file fail : " + info.getCaName(), e);
//                }
//            }
//            info.setDlCrlResult(loadStatus);
//            caInfoDao.save(info);
//        }
//    }
//
//    private int loadCRL(byte[] crl) {
//        CertificateList crlList = CryptoLibrary.parseCRL(crl);
//        if (crlList == null) {
//            LOGGER.error("parseCRL fail.");
//            return -1;
//        }
//        boolean ret = CryptoLibrary.verifyCRLChain(crlList);
//        if (!ret) {
//            LOGGER.error("verifyCRLChain fail.");
//            return -2;
//        }
//        return CryptoLibrary.verifyCRLEffectiveDate(crlList);
//    }
//
//    public void saveAllCRL() {
//        SearchSetting search = caInfoDao.createSearchTemplete();
//        List<CAInfo> list = caInfoDao.find(search);
//        for (CAInfo info : list) {
//            // 載入已下載的CRL
//            String crlUrl = info.getCrlUrl();
//            File crlPathInfo = getCrlLocalPath(crlUrl, info.isDlCrlResult());
//            try {
//                byte[] crl = FileUtils.readFileToByteArray(crlPathInfo);
//                if (crl == null) {
//                    LOGGER.error("CRL file is empty : {}", crlPathInfo);
//                    continue;
//                }
//                CertificateList crlList = CryptoLibrary.parseCRL(crl);
//                String caName = info.getCaName();
//                int crlType = caName.charAt(caName.length() - 1) == '2' ? 2 : 1;
//                int rc = saveCrlList(crlList, crlType);
//                if (rc != 0) {
//                    LOGGER.error("Save CRL file {} fail. rc = {}", caName, rc);
//                } else {
//                    LOGGER.debug("Save CRL file {} success.", caName);
//                }
//            } catch (Exception e) {
//                LOGGER.error("Save CRL file fail : " + crlPathInfo, e);
//            }
//        }
//    }
//
//    private int saveCrlList(CertificateList crl, final int crlType) {
//        int ret = 0;
//        if (crl == null) {
//            return -1;
//        }
//
//        final List<CrlCert> crlCerts = new ArrayList<CrlCert>();
//        try {
//            Calendar thisUpdate = Calendar.getInstance();
//            thisUpdate.setTime(crl.getThisUpdate().getDate());
//            // logger.info(func+"憑證廢止清單(CRL)有效日期 : " + sdf.format(thisUpdate.getTime()));
//            Calendar nextUpdate = Calendar.getInstance();
//            nextUpdate.setTime(crl.getNextUpdate().getDate());
//            // logger.info(func+"憑證廢止清單(CRL)下次更新 : " + sdf.format(nextUpdate.getTime()));
//            Calendar now = Calendar.getInstance();
//            long thisHours = CryptoLibrary.calculateHours(thisUpdate, now);
//            long nextHours = CryptoLibrary.calculateHours(nextUpdate, now);
//            // logger.info(func+"憑證廢止清單(CRL)有效日期距離現在 : " + thisHours + " 小時");
//            // logger.info(func+"憑證廢止清單(CRL)下次更新距離現在 : " + nextHours + " 小時");
//            if (thisHours < 0) {
//                // logger.error(func+"憑證廢止清單(CRL)尚未生效!!");
//                ret = CryptoLibrary.ERROR_CRL_NOT_YET_VALIE;
//            }
//            if (nextHours > 0) {
//                // logger.error(func+"憑證廢止清單(CRL)已經過期!!");
//                ret = CryptoLibrary.ERROR_CRL_EXPIRED;
//            }
//            CRLEntry[] crlCertsEntry = crl.getTBSCertList().getRevokedCertificates();
//            // logger.info(func+"由 CA 下載之憑證廢止清單(CRL), 共計發行筆數 : " + crlCerts);
//            for (CRLEntry crlEntry : crlCertsEntry) {
//                CrlCert crlCert = new CrlCert();
//                crlCert.setCrlType(crlType);
//                // 憑證序號
//                crlCert.setSerialNo(crlEntry.getUserCertificate().getValue().toString());
//                // 撤銷時間
//                crlCert.setExpireDate(crlEntry.getRevocationDate().getDate());
//                // CRL 理由代碼
//                X509Extension reasonCode = crlEntry.getExtensions().getExtension(X509Extension.reasonCode);
//                crlCert.setCertStatus(String.valueOf(reasonCode.getParsedValue().getEncoded()[2]));
//                crlCerts.add(crlCert);
//            }
//
//            crlCertDao.truncate(crlType);
//            Long startTime = System.currentTimeMillis();
//            LOGGER.debug(" Begin save MOICA{}", crlType);
//            crlCertDao.batchSave(crlCerts);
//            LOGGER.debug("Finish save MOICA{} - cost time : {} ms", crlType, System.currentTimeMillis() - startTime);
//        } catch (Exception e) {
//            ret = CryptoLibrary.ERROR_CRL_Exception;
//            LOGGER.error(e.getMessage(), e);
//        }
//        return ret;
//    }
//
//    public void updateAllCaCertStatus() {
//        SearchSetting search = caInfoDao.createSearchTemplete();
//        List<CAInfo> list = caInfoDao.find(search);
//        for (CAInfo info : list) {
//            info.setStatus(getCaCertStatus(info.getExpiredDate()));
//            caInfoDao.save(info);
//        }
//    }
//
//    public String getCaCertStatus(Date ExpiredDate) {
//        if (CapDate.calculateDays(ExpiredDate, new Date()) < 0) {
//            return "1";
//        } else {
//            return "0";
//        }
//    }
//
//    public X509CertificateStructure loadCaCerts(byte[] p7bcertchain) throws CertException {
//        X509CertificateStructure caCert = CryptoLibrary.loadCaCerts(p7bcertchain);
//        if (caCert == null) {
//            throw new CertException("There is no valid CA cert.");
//        } else {
//            CryptoLibrary.validCaCerts();
//            if (!CryptoLibrary.isCaCertValid(getCertInfoByType(caCert, CertInfoType.SUBJECT_KEY_IDENTIFIER))) {
//                throw new CertException("VerifyCertChain CA Cert Chain Fail.");
//            }
//        }
//        return caCert;
//    }
//
//    public String getCertInfoByType(X509CertificateStructure cert, CertInfoType type) {
//        String result = null;
//        switch (type) {
//        case CERT_TYPE:
//            result = CryptoLibrary.getCertType(cert);
//            break;
//        case PERSONAL_ID:
//            result = CryptoLibrary.getPersonId(cert);
//            break;
//        case ENTERPRISE_ID:
//            result = CryptoLibrary.getEnterpriseId(cert);
//            break;
//        case X509_SUBJECT:
//            result = CryptoLibrary.getX509Subject(cert);
//            break;
//        case X509_ISSUER:
//            result = CryptoLibrary.getX509Issuer(cert);
//            break;
//        case X509_CN:
//            result = CryptoLibrary.getX509CN(cert);
//            break;
//        case X509_SERIAL:
//            result = CryptoLibrary.getX509Serial(cert);
//            break;
//        case X509_KEY_USAGE:
//            result = CryptoLibrary.hexEncode(CryptoLibrary.getX509KeyUsageBytes(cert));
//            break;
//        case X509_FINGER:
//            result = CryptoLibrary.getX509Finger(cert);
//            break;
//        case X509_NOTBEFORE:
//            result = CryptoLibrary.getX509NotBefore(cert);
//            break;
//        case X509_NOTAFTER:
//            result = CryptoLibrary.getX509NotAfter(cert);
//            break;
//        case SUBJECT_KEY_IDENTIFIER:
//            result = CryptoLibrary.getExtensionsSubjectKeyIdentifier(cert);
//            break;
//        case AUTHORITY_KEY_IDENTIFIER:
//            result = CryptoLibrary.getX509AuthorityKeyIdentifier(cert);
//            break;
//        default:
//        }
//        return result;
//    }
//
//    public X509CertificateStructure getSignerCert(String p7b) {
//        PKCS7Verify verifier = new PKCS7Verify();
//        if (verifier.verify(CryptoLibrary.base64Decode(p7b))) {
//            return verifier.getSignerCert();
//        } else {
//            return null;
//        }
//    }
//
//    private Proxy getProxy() {
//        Proxy proxy = null;
//        String proxyEnable = sysProp.get("PROXY_ENABLE");
//        String proxyHost = sysProp.get("PROXY_HOST");
//        String proxyPort = sysProp.get("PROXY_PORT");
//        if (proxyEnable != null && proxyHost != null && proxyPort != null) {
//            LOGGER.debug("PROXY_ENABLE: " + proxyEnable);
//            LOGGER.debug("PROXY_HOST: " + proxyHost);
//            LOGGER.debug("PROXY_PORT: " + proxyPort);
//            if (Boolean.parseBoolean(proxyEnable)) {
//                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort)));
//            }
//        }
//        return proxy;
//    }
//}
