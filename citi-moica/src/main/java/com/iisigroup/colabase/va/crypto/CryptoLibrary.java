package com.iisigroup.colabase.va.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.Security;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CRLObject;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.Streams;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 提供各項PKI作業功能
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
public final class CryptoLibrary {

    /**
     * 執行CRL檔分解發生錯誤
     */
    public static int ERROR_CRL_Exception = 0x2000;
    /**
     * CRL 檔尚未生效
     */
    public static int ERROR_CRL_NOT_YET_VALIE = 0x2001;
    /**
     * CRL 檔已過期
     */
    public static int ERROR_CRL_EXPIRED = 0x2002;

    /**
     * 執行OCSP發生錯誤
     */
    public static int ERROR_OCSP_EXP = 0x3000;
    /**
     * OCSP 之Req與Resp的None內容不一致
     */
    public static int ERROR_OCSP_Nonce = 0x3001;
    /**
     * 該憑證已廢止或狀態未明
     */
    public static int ERROR_OCSP_REVOKE_OR_UNKNOW = 0x3002;

    /**
     * 驗章憑證發生錯誤
     */
    public static int ERROR_CERT_Exception = 0x4000;
    /**
     * 憑證尚未生效
     */
    public static int ERROR_CERT_NOT_YET_VALIE = 0x4001;
    /**
     * 憑證已過期
     */
    public static int ERROR_CERT_EXPIRED = 0x4002;

    /**
     * CRL 驗證發生錯誤
     */
    public static int ERROR_CERT_CRL_VERIFY_Exception = 0x5000;
    /**
     * 憑證已廢止
     */
    public static int ERROR_CERT_CRL_VERIFY_REVOKE = 0x5001;
    /**
     * 找不到驗證之CRL檔
     */
    public static int ERROR_CERT_CRL_VERIFY_NOCRL = 0x5002;

    /**
     * MOICA 憑證有問題
     */
    public static int ERROR_MOICAICSC_CERT = 0x6001;

    /**
     * 使用中華電信API錯誤
     */
    public static int Error_CALL_HiSecureAPI = 0x7001;
    /**
     * 執行驗證身份錯誤
     */
    public static int Error_CALL_HiSecure_Exception = 0x7002;

    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoLibrary.class);

    private static Hashtable jdField_if;
    private static Hashtable rootCertsList = new Hashtable();
    private static Hashtable caCertsList = new Hashtable();
    private static Hashtable crlList = new Hashtable();

    static {
        jdField_if = new Hashtable();
        jdField_if.put("OU=工商憑證管理中心", "MOEACA");
        jdField_if.put("OU=內政部憑證管理中心", "MOICA");
        jdField_if.put("OU=政府憑證管理中心", "GCA");
        jdField_if.put("OU=組織及團體憑證管理中心", "XCA");
        jdField_if.put("OU=政府測試憑證管理中心", "GTESTCA");
    }

    private CryptoLibrary() {
    }

    /**
     * 載入BouncyCastle Provide
     *
     */
    public static void checkProvider() {
        if (Security.getProvider("BC") == null) {
            BouncyCastleProvider bcp = new BouncyCastleProvider();
            Security.addProvider(bcp);
        }
    }

    /**
     * 取得政府憑證類別:MOEACA、MOICA、GCA、XCA、GTESTCA
     *
     * @param cert
     *            憑證
     * @return 若有在定義內(MOEACA、MOICA、GCA、XCA、GTESTCA)或輸出 NULL
     */
    public static String getCertType(Certificate cert) {
        String ret = null;
        do {

            if (cert == null) {
                break;
            }
            String str1 = cert.getTBSCertificate().getIssuer().toString();
            Enumeration e = jdField_if.keys();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                if (str1.indexOf(key) > 0) {
                    ret = (String) jdField_if.get(key);
                }
            }
        } while (false);
        return ret;
    }

    /**
     * SHA1 演算法計算
     *
     * @param toHash
     * @return 20 bytes byte[] 計算結果
     */
    public static byte[] sha1Hash(byte[] toHash) {
        SHA1Digest digest = new SHA1Digest();
        digest.reset();
        digest.update(toHash, 0, toHash.length);
        byte[] hash = new byte[digest.getDigestSize()];
        digest.doFinal(hash, 0);
        return hash;
    }

    /**
     * SHA256 演算法計算
     *
     * @param toHash
     * @return 32 bytes byte[] 計算結果
     */
    public static byte[] sha256Hash(byte[] toHash) {
        SHA256Digest digest = new SHA256Digest();
        digest.reset();
        digest.update(toHash, 0, toHash.length);
        byte[] hash = new byte[digest.getDigestSize()];
        digest.doFinal(hash, 0);
        return hash;
    }

    /**
     * byte[] 轉字串
     *
     * @param data
     * @return 字串格式
     */
    public static String hexEncode(byte[] data) {
        return new String(Hex.encode(data));
    }

    /**
     * 16進位字串轉16進位byte []
     *
     * @param data
     * @return
     */
    public static byte[] hexDecode(String data) {
        return Hex.decode(data);
    }

    /*
     * hexDump 16進位byte []轉16進位字串 輸入： byte[] 輸出： String data
     */
    /**
     * byte [] Dump
     *
     * @param in
     * @return
     */
    public static String hexDump(byte[] in) {
        return hexDump(in, in.length);
    }

    /**
     * byte [] Dump
     *
     * @param in
     * @param len
     * @return
     */
    public static String hexDump(byte[] in, int len) {
        char[] hexTable = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        String retR = "";
        StringBuffer sb = new StringBuffer();
        int iLen = len;
        int step = 0;
        if (iLen <= 0) {
            iLen = in.length;
        }
        sb.append("\t");
        for (int i = 0; i < iLen; i++) {
            char ch = (char) in[i];
            sb.append(hexTable[(ch >> 4) & 0x0f]);
            sb.append(hexTable[ch & 0x0f]);
            if ((ch >= 32) && (ch < 127)) {
                retR += Character.toString(ch);
            } else {
                retR += ".";
            }
            step++;
            if (step % 32 == 0) {
                sb.append(" - ");
                sb.append(retR);
                retR = "";
                sb.append("\n\t");
                step = 0;
            }
        }
        if (step > 0) {
            for (int i = step; i < 32; i++) {
                sb.append("  ");
            }
            sb.append(" - ");
            sb.append(retR);
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * BASE64 編碼
     *
     * @param data
     * @param length
     * @return
     */
    public static String base64Encode(byte[] data, int length) {
        String ret = "";
        String crlf = "\r\n";
        byte[] b64 = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        do {
            if ((data == null) || (data.length == 0)) {
                break;
            }

            b64 = Base64.encode(data);
            if ((length < 0) || ((length % 4) > 0)) {
                ret = new String(b64);
                break;
            }
            int i = 0;
            while (true) {
                if (i + 80 > b64.length) {
                    baos.write(b64, i, b64.length - i);
                    ret = baos.toString();
                    break;
                }
                baos.write(b64, i, length);
                baos.write(crlf.getBytes(), 0, crlf.length());
                i += length;
            }
        } while (false);
        return ret;
    }

    /**
     * BASE64 編碼
     *
     * @param data
     * @return
     */
    public static String base64Encode(byte[] data) {
        return CryptoLibrary.base64Encode(data, 80);
    }

    /**
     * BASE64 解碼
     *
     * @param data
     * @return
     */
    public static byte[] base64Decode(String data) {
        byte[] ret = null;
        do {
            if ((data == null) || (data.length() == 0)) {
                break;
            }
            if (data.startsWith("-----")) {
                data = data.replaceAll("BEGIN ", "");
                data = data.replaceAll("END ", "");
                data = data.replaceAll("-----", "");
                data = data.replaceAll("CERTIFICATE", "");
            }
            data = data.replaceAll("\r", "");
            data = data.replaceAll("\n", "");
            data = data.replaceAll(" ", "");
            ret = Base64.decode(data);
        } while (false);
        return ret;
    }

    /**
     * PublicKey 由 byte [] 轉成 RSAKeyParameters
     *
     * @param data
     * @return
     */
    public static RSAKeyParameters getRSAPubKey(byte[] data) {
        RSAKeyParameters ret = null;
        ASN1InputStream ais = null;
        try {
            ais = new ASN1InputStream(data);
            RSAPublicKey pubKS = RSAPublicKey.getInstance(ais.readObject());
            ret = new RSAKeyParameters(false, pubKS.getModulus(), pubKS.getPublicExponent());
        } catch (Exception e) {
        } finally {
            if (ais != null) {
                try {
                    ais.close();
                } catch (IOException e) {
                }
            }
        }
        return ret;
    }

    /**
     * PrivateKey 由 byte [] 轉成 RSAKeyParameters
     *
     * @param data
     * @return
     */
    public static RSAKeyParameters getRSAPriKey(byte[] data) {
        RSAKeyParameters ret = null;
        ASN1InputStream ais = null;
        try {
            ais = new ASN1InputStream(data);
            RSAPrivateKey priKS = RSAPrivateKey.getInstance(ais.readObject());
            ret = new RSAPrivateCrtKeyParameters(priKS.getModulus(), priKS.getPublicExponent(), priKS.getPrivateExponent(), priKS.getPrime1(), priKS.getPrime2(), priKS.getExponent1(),
                    priKS.getExponent2(), priKS.getCoefficient());
        } catch (Exception e) {
        } finally {
            if (ais != null) {
                try {
                    ais.close();
                } catch (IOException e) {
                }
            }
        }
        return ret;
    }

    /**
     * PublicKey 由 RSAKeyParameters 轉成 byte []
     *
     * @param rsaKey
     * @return
     */
    public static byte[] getRSAPubKey(RSAKeyParameters rsaKey) throws Exception {
        RSAPublicKey pubKS = null;
        byte[] ret = null;
        try {
            if (rsaKey.isPrivate()) {
                pubKS = new RSAPublicKey(rsaKey.getModulus(), ((RSAPrivateCrtKeyParameters) rsaKey).getPublicExponent());
            } else {
                pubKS = new RSAPublicKey(rsaKey.getModulus(), rsaKey.getExponent());
            }
            ret = pubKS.getEncoded();
        } catch (Exception e) {
        }
        return ret;
    }

    /**
     * PrivateKey 由 RSAKeyParameters 轉成 byte []
     *
     * @param rsaKey
     * @return
     */
    public static byte[] getRSAPriKey(RSAKeyParameters rsaKey) {
        byte[] ret = null;
        RSAPrivateKey priKS = null;
        try {
            if (rsaKey.isPrivate()) {
                RSAPrivateCrtKeyParameters rsaPri = (RSAPrivateCrtKeyParameters) rsaKey;
                priKS = new RSAPrivateKey(rsaPri.getModulus(), rsaPri.getPublicExponent(), rsaPri.getExponent(), rsaPri.getP(), rsaPri.getQ(), rsaPri.getDP(), rsaPri.getDQ(),
                        rsaPri.getQInv());
                ret = priKS.getEncoded();
            }
        } catch (Exception e) {
        }
        return ret;
    }

    private static String getDT(int i) {
        String ret = "0";
        String out = Integer.toString(i);
        if (out.length() == 1) {
            ret += out;
        } else {
            ret = out;
        }
        return ret;
    }

    /**
     * 取得台灣目前時間
     *
     * @return
     */
    public static String getCurrentDateTime() {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        return CryptoLibrary.getDateTimeStr(now);
    }

    /**
     * 取得目前時間,格式為 YYYYMMDD
     *
     * @param dt
     * @return
     */
    public static String getDateTimeYYYYMMDD(Calendar dt) {
        String ret = CryptoLibrary.getDT(dt.get(Calendar.YEAR));
        ret += CryptoLibrary.getDT(dt.get(Calendar.MONTH) + 1);
        ret += CryptoLibrary.getDT(dt.get(Calendar.DATE));
        return ret;
    }

    /**
     * 取得目前時間,格式為 HHNNSS
     *
     * @param dt
     * @return
     */
    public static String getDateTimeHHNNSS(Calendar dt) {
        String ret = CryptoLibrary.getDT(dt.get(Calendar.HOUR_OF_DAY));
        ret += CryptoLibrary.getDT(dt.get(Calendar.MINUTE));
        ret += CryptoLibrary.getDT(dt.get(Calendar.SECOND));
        return ret;
    }

    /**
     * 取得目前時間,格式為 YYYYMMDDHHNNSS
     *
     * @param dt
     * @return
     */
    public static String getDateTimeStr(Calendar dt) {
        String ret = CryptoLibrary.getDT(dt.get(Calendar.YEAR));
        ret += CryptoLibrary.getDT(dt.get(Calendar.MONTH) + 1);
        ret += CryptoLibrary.getDT(dt.get(Calendar.DATE));
        ret += CryptoLibrary.getDT(dt.get(Calendar.HOUR_OF_DAY));
        ret += CryptoLibrary.getDT(dt.get(Calendar.MINUTE));
        ret += CryptoLibrary.getDT(dt.get(Calendar.SECOND));
        return ret;
    }

    /**
     * 轉換成 GMT 時區
     *
     * @param certDate
     * @return
     */
    public static String certDateToGMT8(String certDate) {
        Calendar cc = Calendar.getInstance();
        cc.set(Calendar.YEAR, Integer.parseInt(certDate.substring(0, 4)));
        cc.set(Calendar.MONTH, Integer.parseInt(certDate.substring(4, 6)) - 1);
        cc.set(Calendar.DATE, Integer.parseInt(certDate.substring(6, 8)));
        cc.set(Calendar.HOUR_OF_DAY, Integer.parseInt(certDate.substring(8, 10)));
        cc.set(Calendar.MINUTE, Integer.parseInt(certDate.substring(10, 12)));
        cc.set(Calendar.SECOND, Integer.parseInt(certDate.substring(12)));
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        now.setTime(cc.getTime());
        return CryptoLibrary.getDateTimeStr(now);
    }

    /**
     * 取得憑證時間轉成Calendar格式
     *
     * @param certDate
     *            (YYYYMMDDHHNNSS)
     * @return
     */
    public static Calendar getCertCalendar(String certDate) {
        Calendar cc = Calendar.getInstance();
        cc.set(Calendar.YEAR, Integer.parseInt(certDate.substring(0, 4)));
        cc.set(Calendar.MONTH, Integer.parseInt(certDate.substring(4, 6)) - 1);
        cc.set(Calendar.DATE, Integer.parseInt(certDate.substring(6, 8)));
        cc.set(Calendar.HOUR_OF_DAY, Integer.parseInt(certDate.substring(8, 10)));
        cc.set(Calendar.MINUTE, Integer.parseInt(certDate.substring(10, 12)));
        cc.set(Calendar.SECOND, Integer.parseInt(certDate.substring(12)));
        return cc;
    }

    /**
     * 計算dateLater-dateEarly 天數
     *
     * @param dateEarly
     * @param dateLater
     * @return
     */
    public static long calculateDays(Calendar dateEarly, Calendar dateLater) {
        return (dateLater.getTimeInMillis() - dateEarly.getTimeInMillis()) / (24 * 60 * 60 * 1000);
    }

    /**
     * 計算dateLater-dateEarly 小時數
     *
     * @param dateEarly
     * @param dateLater
     * @return
     */
    public static long calculateHours(Calendar dateEarly, Calendar dateLater) {
        return (dateLater.getTimeInMillis() - dateEarly.getTimeInMillis()) / (60 * 60 * 1000);
    }

    /**
     * 計算dateLater-dateEarly 分鐘數
     *
     * @param dateEarly
     * @param dateLater
     * @return
     */
    public static long calculateMinutes(Calendar dateEarly, Calendar dateLater) {
        return (dateLater.getTimeInMillis() - dateEarly.getTimeInMillis()) / (60 * 1000);
    }

    /**
     * 計算dateLater-dateEarly 秒數
     *
     * @param dateEarly
     * @param dateLater
     * @return
     */
    public static long calculateSeconds(Calendar dateEarly, Calendar dateLater) {
        return (dateLater.getTimeInMillis() - dateEarly.getTimeInMillis()) / (1000);
    }

    /**
     * PKCS7 憑證鏈分解成憑證 array
     *
     * @param p7bDer
     *            (DER格式之PKCS7)
     * @return
     */
    public static Certificate[] getP7bCerts(byte[] p7bDer) {
        Certificate[] ret = null;
        do {
            ASN1InputStream ais = null;
            try {
                if (p7bDer == null) {
                    break;
                }

                if (p7bDer[0] == 0x30) {
                    ais = new ASN1InputStream(p7bDer);
                } else {
                    ais = new ASN1InputStream(base64Decode(new String(p7bDer)));
                }
                ContentInfo p7bContent = new ContentInfo((ASN1Sequence) ais.readObject());
                if (!p7bContent.getContentType().equals(PKCSObjectIdentifiers.signedData)) {
                    break;
                }
                SignedData p7b = SignedData.getInstance(p7bContent.getContent());
                int certs = p7b.getCertificates().size();
                ret = new Certificate[certs];
                for (int i = 0; i < certs; i++) {
                    ret[i] = Certificate.getInstance(p7b.getCertificates().getObjectAt(i));
                }
            } catch (Exception e) {
                ret = null;
            } finally {
                if (ais != null) {
                    try {
                        ais.close();
                    } catch (IOException e) {
                    }
                }
            }
        } while (false);
        return ret;
    }

    /**
     * byte [] 轉成憑證物件
     *
     * @param x509der
     *            (DER格式之X509憑證)
     * @return
     */
    public static Certificate getX509Cert(byte[] x509der) {
        Certificate ret = null;
        do {
            ASN1InputStream ais = null;
            try {
                if (x509der == null) {
                    break;
                }
                if (x509der[0] == 0x30) {
                    ais = new ASN1InputStream(x509der);
                } else {
                    byte[] der = base64Decode(new String(x509der));
                    ais = new ASN1InputStream(der);
                }
                ret = Certificate.getInstance((ASN1Sequence) ais.readObject());
            } catch (Exception e) {
                ret = null;
            } finally {
                if (ais != null) {
                    try {
                        ais.close();
                    } catch (IOException e) {
                    }
                }
            }
        } while (false);
        return ret;
    }

    /**
     * 憑證物件轉成Base64格式字串
     *
     * @param x509der
     * @return
     */
    public static String getX509CertString(Certificate x509der) {
        String ret = null;
        do {
            try {
                ret = CryptoLibrary.base64Encode(x509der.getEncoded());

            } catch (Exception e) {
                ret = null;
            }
        } while (false);
        return ret;
    }

    /**
     * 驗證憑證鏈，這裡只會拿ee憑證上一層來驗ee
     *
     * @param eeCert
     * @return
     */
    public static boolean verifyCertChain(Certificate eeCert) {
        boolean ret = false;
        do {

            String certAKI = CryptoLibrary.getX509AuthorityKeyIdentifier(eeCert);
            if (caCertsList.containsKey(certAKI)) {
                Certificate caCert = (Certificate) caCertsList.get(certAKI);
                if (CryptoLibrary.verifyCert(eeCert, caCert)) {
                    ret = true;
                } else {
                    ret = false;
                }
            } else if (rootCertsList.containsKey(certAKI)) {
                Certificate caCert = (Certificate) rootCertsList.get(certAKI);
                if (!CryptoLibrary.verifyCert(eeCert, caCert)) {
                    ret = true;
                } else {
                    ret = false;
                }
            } else {
                ret = false;
            }

        } while (false);
        return ret;
    }

    /**
     * 取得 ee 憑證之上層憑證物件
     *
     * @param eeCert
     * @return
     */
    public static Certificate getCACert(Certificate eeCert) {
        Certificate ret = null;
        do {

            String certAKI = CryptoLibrary.getX509AuthorityKeyIdentifier(eeCert);
            if (caCertsList.containsKey(certAKI)) {
                ret = (Certificate) caCertsList.get(certAKI);

            } else if (rootCertsList.containsKey(certAKI)) {
                ret = (Certificate) rootCertsList.get(certAKI);

            } else {
                ret = null;
            }

        } while (false);
        return ret;
    }

    /**
     * byte[] CRL 資料轉成CertificateList
     *
     * @param crlData
     * @return
     */
    public static CertificateList parseCRL(byte[] crlData) {
        CertificateList ret = null;
        ASN1InputStream ais = null;
        do {
            try {
                if (crlData == null) {
                    break;
                }
                ais = new ASN1InputStream(crlData);
                ret = new CertificateList((ASN1Sequence) ais.readObject());
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                if (ais != null) {
                    try {
                        ais.close();
                    } catch (IOException e) {
                    }
                }
            }
        } while (false);
        return ret;
    }

    /**
     * 驗證CRL簽章者憑證鍵
     *
     * @param crl
     * @return
     */
    public static boolean verifyCRLChain(CertificateList crl) {
        boolean ret = false;
        do {
            try {
                if (crl == null) {
                    break;
                }
                String issuerCN = "";
                X509CRLObject crlObj = new X509CRLObject(crl);
                Extensions crlExt = crl.getTBSCertList().getExtensions();
                issuerCN = CryptoLibrary.getExtensionsAuthorityKeyIdentifier(crlExt);
                if (caCertsList.containsKey(issuerCN)) {
                    Certificate caCert = (Certificate) caCertsList.get(issuerCN);
                    if (CryptoLibrary.verifyCRL(crlObj, caCert)) {
                        ret = true;
                    } else {
                        ret = false;
                    }
                } else if (rootCertsList.containsKey(issuerCN)) {
                    Certificate caCert = (Certificate) rootCertsList.get(issuerCN);
                    if (!CryptoLibrary.verifyCRL(crlObj, caCert)) {
                        ret = true;
                    } else {
                        ret = false;
                    }
                } else {
                    ret = false;
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        } while (false);
        return ret;
    }

    /**
     * 檢驗 CRL 有效性
     * @param crl
     * @return
     */
    public static int verifyCRLEffectiveDate(CertificateList crl) {
        int ret = -1;
        Calendar now = Calendar.getInstance();
        try {
            if (crl != null) {
                Calendar thisUpdate = Calendar.getInstance();
                thisUpdate.setTime(crl.getThisUpdate().getDate());
                // logger.info(func+"憑證廢止清單(CRL)有效日期 : " + sdf.format(thisUpdate.getTime()));
                Calendar nextUpdate = Calendar.getInstance();
                nextUpdate.setTime(crl.getNextUpdate().getDate());
                // logger.info(func+"憑證廢止清單(CRL)下次更新 : " + sdf.format(nextUpdate.getTime()));
                long thisHours = CryptoLibrary.calculateHours(thisUpdate, now);
                long nextHours = CryptoLibrary.calculateHours(nextUpdate, now);
                // logger.info(func+"憑證廢止清單(CRL)有效日期距離現在 : " + thisHours + " 小時");
                // logger.info(func+"憑證廢止清單(CRL)下次更新距離現在 : " + nextHours + " 小時");
                if (thisHours < 0) {
                    // logger.error(func+"憑證廢止清單(CRL)尚未生效!!");
                    ret = ERROR_CRL_NOT_YET_VALIE;
                }
                if (nextHours > 0) {
                    // logger.error(func+"憑證廢止清單(CRL)已經過期!!");
                    ret = ERROR_CRL_EXPIRED;
                }
                ret = 0;
            }
        } catch (Exception e) {
            ret = ERROR_CRL_Exception;
            LOGGER.error(e.getMessage(), e);
        }
        return ret;
    }

    /**
     * 驗證憑證CRL檔
     *
     * @param eeCert
     *            要驗CRL之憑證物件
     * @return
     */
    public static int verifyCertCRL(Certificate eeCert) {
        int ret = 0;
        do {

            String certAKI = CryptoLibrary.getX509AuthorityKeyIdentifier(eeCert);
            if (crlList.containsKey(certAKI)) {
                java.math.BigInteger thisSerial = eeCert.getSerialNumber().getValue();
                Vector crlSerialList = (Vector) crlList.get(certAKI);
                if (crlSerialList == null) {
                    ret = ERROR_CERT_CRL_VERIFY_NOCRL;
                    break;
                }
                if (crlSerialList.contains(thisSerial)) {
                    ret = ERROR_CERT_CRL_VERIFY_REVOKE;
                } else {
                    ret = 0;
                }
            } else {
                ret = ERROR_CERT_CRL_VERIFY_NOCRL;
            }
        } while (false);
        return ret;
    }

    /**
     * 憑證物件轉換格式
     *
     * @param cert
     * @return
     */
    public static X509Certificate getX509Cert(Certificate cert) {
        X509Certificate ret = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
            ASN1OutputStream out = new ASN1OutputStream(baos);
            out.writeObject(cert);
            out.close();
            byte[] x509 = baos.toByteArray();
            InputStream in = new ByteArrayInputStream(x509);
            ret = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(in);
        } catch (Exception e) {
        }
        return ret;
    }

    /**
     * 憑證物件轉換格式
     *
     * @param cert
     * @return
     */
    public static Certificate getX509Cert(X509Certificate cert) {
        Certificate ret = null;
        try {
            ret = Certificate.getInstance(cert.getEncoded());
        } catch (Exception e) {
        }
        return ret;
    }

    /**
     * 檢查憑證效期
     *
     * @param cert
     * @param date
     *            (若為null則與目前時間比較)
     * @return
     */
    public static int checkCertDateValid(Certificate cert, Date date) {
        int ret = 0;
        try {
            X509Certificate cert1 = CryptoLibrary.getX509Cert(cert);
            if (date == null) {
                cert1.checkValidity();
            } else {
                cert1.checkValidity(date);
            }
        } catch (CertificateExpiredException ex) {
            ret = CryptoLibrary.ERROR_CRL_EXPIRED;
        } catch (CertificateNotYetValidException ex) {
            ret = CryptoLibrary.ERROR_CERT_NOT_YET_VALIE;
        } catch (Exception ex) {
            ret = CryptoLibrary.ERROR_CERT_Exception;
        }
        return ret;
    }

    /**
     * 取得憑證序號(16進位)
     *
     * @param x509Cert
     * @return
     */
    public static String getX509Serial(Certificate x509Cert) {
        return x509Cert.getTBSCertificate().getSerialNumber().getPositiveValue().toString(16).toUpperCase();
    }

    /**
     * 取得憑證序號(10進位)
     *
     * @param x509Cert
     * @return
     */
    public static int getX509Serial_10(Certificate x509Cert) {
        return Integer.parseInt(x509Cert.getTBSCertificate().getSerialNumber().getPositiveValue().toString(10));
    }

    /**
     * 取得憑證CN
     *
     * @param x509Cert
     * @return
     */
    public static String getX509CN(Certificate x509Cert) {
        String ret = null;
        X500Name x500name = x509Cert.getSubject();
        RDN[] rdns = x500name.getRDNs(BCStyle.CN);
        if(rdns.length > 0) {
            RDN rdn = x500name.getRDNs(BCStyle.CN)[0];
            // TODO check if work
            ret = rdn.toString();
            LOGGER.debug("CN String (toString) vs. (IETFUtils)" + ret + " vs. " + IETFUtils.valueToString(rdn.getFirst().getValue()));
        }
        if (ret != null && ret.startsWith("[")) {
            ret = ret.substring(1, ret.length() - 1);
        }
        return ret;
    }

    /**
     * 取得憑證主旨
     *
     * @param x509Cert
     * @return
     */
    public static String getX509Subject(Certificate x509Cert) {
        return x509Cert.getSubject().toString();
    }

    /**
     * 取得憑證發行者
     *
     * @param x509Cert
     * @return
     */
    public static String getX509Issuer(Certificate x509Cert) {
        return x509Cert.getIssuer().toString();
    }

    /**
     * 取得憑證起始日
     *
     * @param x509Cert
     * @return
     */
    public static String getX509NotBefore(Certificate x509Cert) {
        return CryptoLibrary.certDateToGMT8(x509Cert.getStartDate().getTime().substring(0, 14));
    }

    /**
     * 取得憑證到期日
     *
     * @param x509Cert
     * @return
     */
    public static String getX509NotAfter(Certificate x509Cert) {
        return CryptoLibrary.certDateToGMT8(x509Cert.getEndDate().getTime().substring(0, 14));
    }

    /**
     * 取得憑證拇指紋
     *
     * @param x509Cert
     * @return
     */
    public static String getX509Finger(Certificate x509Cert) {
        String ret = null;
        try {
            byte[] x509 = x509Cert.getEncoded();
            ret = new String(Hex.encode(CryptoLibrary.sha1Hash(x509))).toUpperCase();
        } catch (Exception e) {
        }
        return ret;
    }

    /**
     * 取得憑證金鑰使用方式
     *
     * @param x509Cert
     * @return
     */
    public static DERBitString getX509KeyUsage(Certificate x509Cert) {
        DERBitString ret = null;
        do {
            TBSCertificate tbsCert = x509Cert.getTBSCertificate();
            if (tbsCert.getVersion().getValue().intValue() != 3) {
                break;
            }
            Extensions ext = tbsCert.getExtensions();
            if (ext == null) {
                break;
            }

            java.util.Enumeration en = ext.oids();
            while (en.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) en.nextElement();
                Extension extVal = ext.getExtension(oid);

                ASN1OctetString oct = extVal.getExtnValue();
                ASN1InputStream extIn = null;

                if (oid.equals(Extension.keyUsage)) {
                    try {
                        extIn = new ASN1InputStream(new ByteArrayInputStream(oct.getOctets()));
                        ret = new DERBitString(extIn.readObject());
                    } catch (Exception e) {
                    } finally {
                        if (extIn != null) {
                            try {
                                extIn.close();
                            } catch (IOException e) {
                            }
                        }
                    }
                }

            }
        } while (false);
        return ret;
    }

    /**
     * 取得憑證金鑰使用方式
     *
     * @param x509Cert
     * @return
     */
    public static byte[] getX509KeyUsageBytes(Certificate x509Cert) {
        byte[] ret = null;
        DERBitString bitstr = getX509KeyUsage(x509Cert);
        if (bitstr != null) {
            ret = bitstr.getBytes();
        }
        return ret;
    }

    public static List<URL> getURLs(Certificate cert1) {
        X509Certificate cert = CryptoLibrary.getX509Cert(cert1);

        List<URL> urls = new LinkedList<URL>();

        // Retrieves the raw ASN1 data of the CRL Dist Points X509 extension
        byte[] cdp = cert.getExtensionValue(Extension.cRLDistributionPoints.getId());
        if (cdp != null) {
            try {
                // Wraps the raw data in a container class
                CRLDistPoint crldp = CRLDistPoint.getInstance(X509ExtensionUtil.fromExtensionValue(cdp));

                DistributionPoint[] distPoints = crldp.getDistributionPoints();

                for (DistributionPoint dp : distPoints) {
                    // Only use the "General name" data in the distribution
                    // point entry.
                    GeneralNames gns = (GeneralNames) dp.getDistributionPoint().getName();

                    for (GeneralName name : gns.getNames()) {
                        // Only retrieve URLs
                        if (name.getTagNo() == GeneralName.uniformResourceIdentifier) {
                            ASN1String s = (ASN1String) name.getName();
                            urls.add(new URL(s.getString()));
                        }
                    }
                }
            } catch (IOException e) {
                // Could not retrieve the CRLDistPoint object. Just return empty
                // url list.
            }
        }

        return urls;
    }

    /**
     * 取得憑證CRL下載點
     *
     * @param exts
     * @return
     */
    public static String getExtensionsCrlDistributionPoint(Extensions exts) {
        String ret = "";
        CRLDistPoint dp = null;
        DistributionPoint[] dps = null;
        DistributionPointName dpn = null;
        GeneralNames names = null;
        GeneralName[] name = null;

        do {
            if (exts == null) {
                break;
            }

            java.util.Enumeration en = exts.oids();
            while (en.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) en.nextElement();
                Extension extVal = exts.getExtension(oid);

                ASN1OctetString oct = extVal.getExtnValue();

                if (oid.equals(Extension.cRLDistributionPoints)) {
                    ASN1InputStream extIn = null;
                    try {
                        extIn = new ASN1InputStream(new ByteArrayInputStream(oct.getOctets()));
                        dp = CRLDistPoint.getInstance(extIn.readObject());
                        dps = dp.getDistributionPoints();
                        dpn = dps[0].getDistributionPoint();
                        names = GeneralNames.getInstance(dpn.getName());
                        name = names.getNames();

                        switch (name[0].getTagNo()) {
                        case 1: // rfc822Name:
                        case 2: // dNSName:
                        case 6: // uniformResourceIdentifier:
                            ret = DERIA5String.getInstance(name[0].getName()).getString();
                            break;
                        case 4: // directoryName:
                            ret = X509Name.getInstance(name[0].getName()).toString();
                            break;
                        default:
                            ret = (name[0].getName()).toString();
                        }
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    } finally {
                        if (extIn != null) {
                            try {
                                extIn.close();
                            } catch (IOException e) {
                            }
                        }
                    }
                    break;
                }

            }
        } while (false);
        return ret;
    }

    /**
     * 取得憑證CRL下載點
     *
     * @param x509Cert
     * @return
     */
    public static String getX509CrlDistributionPoint(Certificate x509Cert) {
        String ret = "";
        do {
            TBSCertificate tbsCert = x509Cert.getTBSCertificate();
            if (tbsCert.getVersion().getValue().intValue() != 3) {
                break;
            }
            ret = CryptoLibrary.getExtensionsCrlDistributionPoint(tbsCert.getExtensions());
        } while (false);
        return ret;
    }

    /**
     * 取得憑證AKI
     *
     * @param exts
     * @return
     */
    public static String getExtensionsAuthorityKeyIdentifier(Extensions exts) {
        String ret = "";

        do {
            if (exts == null) {
                break;
            }

            java.util.Enumeration en = exts.oids();
            while (en.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) en.nextElement();
                Extension extVal = exts.getExtension(oid);

                ASN1OctetString oct = extVal.getExtnValue();

                if (oid.equals(Extension.authorityKeyIdentifier)) {
                    ASN1InputStream extIn = null;
                    try {
                        extIn = new ASN1InputStream(new ByteArrayInputStream(oct.getOctets()));
                        AuthorityKeyIdentifier aki = AuthorityKeyIdentifier.getInstance(extIn.readObject());
                        ret = CryptoLibrary.hexEncode(aki.getKeyIdentifier()).toUpperCase();
                    } catch (Exception e) {
                    } finally {
                        if (extIn != null) {
                            try {
                                extIn.close();
                            } catch (IOException e) {
                            }
                        }
                    }
                }

            }
        } while (false);
        return ret;
    }

    /**
     * 取得憑證SKI
     *
     * @param exts
     * @return
     */
    public static String getExtensionsSubjectKeyIdentifier(Extensions exts) {
        String ret = "";

        do {
            if (exts == null) {
                break;
            }

            java.util.Enumeration en = exts.oids();
            while (en.hasMoreElements()) {
                ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) en.nextElement();
                Extension extVal = exts.getExtension(oid);

                ASN1OctetString oct = extVal.getExtnValue();

                if (oid.equals(Extension.subjectKeyIdentifier)) {
                    ASN1InputStream extIn = null;
                    try {
                        extIn = new ASN1InputStream(new ByteArrayInputStream(oct.getOctets()));
                        SubjectKeyIdentifier ski = SubjectKeyIdentifier.getInstance(extIn.readObject());
                        ret = CryptoLibrary.hexEncode(ski.getKeyIdentifier()).toUpperCase();
                    } catch (Exception e) {
                    } finally {
                        if (extIn != null) {
                            try {
                                extIn.close();
                            } catch (IOException e) {
                            }
                        }
                    }
                }

            }
        } while (false);
        return ret;
    }

    /**
     * 取得憑證AKI
     *
     * @param x509Cert
     * @return
     */
    public static String getX509AuthorityKeyIdentifier(Certificate x509Cert) {
        String ret = "";
        do {
            TBSCertificate tbsCert = x509Cert.getTBSCertificate();
            if (tbsCert.getVersion().getValue().intValue() != 3) {
                break;
            }
            ret = CryptoLibrary.getExtensionsAuthorityKeyIdentifier(tbsCert.getExtensions());
        } while (false);
        return ret;
    }

    /**
     * 取得憑證SKI
     *
     * @param x509Cert
     * @return
     */
    public static String getExtensionsSubjectKeyIdentifier(Certificate x509Cert) {
        String ret = "";
        do {
            TBSCertificate tbsCert = x509Cert.getTBSCertificate();
            if (tbsCert.getVersion().getValue().intValue() != 3) {
                break;
            }
            ret = CryptoLibrary.getExtensionsSubjectKeyIdentifier(tbsCert.getExtensions());
        } while (false);
        return ret;
    }

    /**
     * 取得憑證公鑰物件
     *
     * @param x509Cert
     * @return
     */
    public static RSAPublicKey getX509PublicKey(Certificate x509Cert) {
        RSAPublicKey ret = null;
        try {
            ret = RSAPublicKey.getInstance(x509Cert.getSubjectPublicKeyInfo().getPublicKey());
        } catch (Exception e) {
        }
        return ret;
    }

    /**
     * 憑證驗章(caCert驗x509Cert之簽章值)
     *
     * @param x509Cert
     * @param caCert
     * @return
     */
    public static boolean verifyCert(Certificate x509Cert, Certificate caCert) {
        boolean ret = false;
        java.security.PublicKey caKey = null;
        X509CertificateObject ca = null;
        X509CertificateObject x509 = null;

        try {
            CryptoLibrary.checkProvider();
            ca = new X509CertificateObject(caCert);
            caKey = ca.getPublicKey();
            x509 = new X509CertificateObject(x509Cert);
            x509.verify(caKey, "BC");
            ret = true;
        } catch (Exception e) {
        }
        return ret;
    }

    /**
     * CRL 驗章(caCert驗證crl之簽章)
     *
     * @param crl
     * @param caCert
     * @return
     */
    public static boolean verifyCRL(X509CRLObject crl, Certificate caCert) {
        boolean ret = false;
        java.security.PublicKey caKey = null;
        X509CertificateObject ca = null;

        try {
            CryptoLibrary.checkProvider();
            ca = new X509CertificateObject(caCert);
            caKey = ca.getPublicKey();
            crl.verify(caKey, "BC");
            ret = true;
        } catch (Exception e) {
        }
        return ret;
    }

    /**
     * 清除目前CRL Hashtable 所以CA憑證資料
     */
    public static void clearCACert() {
        rootCertsList.clear();
        caCertsList.clear();

    }

    /**
     * 載入憑證鏈到記憶体
     *
     * @param p7bcertchain
     * @return
     */
    public static Certificate loadCaCerts(byte[] p7bcertchain) {
        Certificate caCert = null;
        Certificate[] certs = null;
        do {
            try {
                certs = CryptoLibrary.getP7bCerts(p7bcertchain);
            } catch (Exception e) {
                break;
            }
            for (int j = 0; j < certs.length; j++) {
                Certificate thisCert = certs[j];
                String certSubject = CryptoLibrary.getX509Subject(thisCert);
                String certIssuer = CryptoLibrary.getX509Issuer(thisCert);
                String certSKI = CryptoLibrary.getExtensionsSubjectKeyIdentifier(thisCert.getTBSCertificate().getExtensions());
                if (certSubject.compareTo(certIssuer) == 0) {
                    if (!rootCertsList.containsKey(certSKI)) {
                        rootCertsList.put(certSKI, thisCert);
                    }
                } else {
                    DERBitString ku = CryptoLibrary.getX509KeyUsage(thisCert);
                    if ((ku.getBytes()[0] & (byte) KeyUsage.keyCertSign) == (byte) KeyUsage.keyCertSign) {
                        if (!caCertsList.containsKey(certSKI)) {
                            caCertsList.put(certSKI, thisCert);
                        }
                        Enumeration e = jdField_if.keys();
                        while (e.hasMoreElements()) {
                            String key = (String) e.nextElement();
                            if (getX509Subject(thisCert).indexOf(key) > 0) {
                                caCert = thisCert;
                                break;
                            }
                        }
                    } else {
                        continue;
                    }
                }
            }
        } while (false);
        return caCert;
    }

    /**
     * 驗章目前記憶体內之憑證鏈正確產，若驗證不過會被移除記憶体
     */
    public static void validCaCerts() {
        Enumeration elms = caCertsList.elements();
        boolean done = true;
        while (elms.hasMoreElements()) {
            Certificate caCert = null;
            Certificate thisCert = (Certificate) elms.nextElement();
            String certSKI = CryptoLibrary.getExtensionsSubjectKeyIdentifier(thisCert.getTBSCertificate().getExtensions());
            String certAKI = CryptoLibrary.getExtensionsAuthorityKeyIdentifier(thisCert.getTBSCertificate().getExtensions());
            if (caCertsList.containsKey(certAKI)) {
                caCert = (Certificate) caCertsList.get(certAKI);
            } else if (rootCertsList.containsKey(certAKI)) {
                caCert = (Certificate) rootCertsList.get(certAKI);
            } else if (!rootCertsList.containsKey(certAKI)) {
                caCertsList.remove(certSKI);
                continue;
            }
            done = CryptoLibrary.verifyCert(thisCert, caCert);
            if (!done) {
                caCertsList.remove(certSKI);
                continue;
            }

            X509CertificateObject x509 = null;
            try {
                x509 = new X509CertificateObject(thisCert);
                x509.checkValidity();
            } catch (java.security.cert.CertificateExpiredException eExp) {
                caCertsList.remove(certSKI);
            } catch (java.security.cert.CertificateNotYetValidException eExp) {
                caCertsList.remove(certSKI);
            } catch (Exception eExp) {
                caCertsList.remove(certSKI);
            }
        }
    }

    /**
     * 載入CRL檔到記憶体
     *
     * @param crl
     * @return
     */
    public static int storeAndReloadCRL(CertificateList crl) {
        int ret = 0;
        Calendar now = Calendar.getInstance();

        do {

            if (crl == null) {
                break;
            }
            String issuerCN = "";
            Vector crlSerialList = new Vector();
            boolean crlFalure = false;
            try {
                Extensions crlExt = crl.getTBSCertList().getExtensions();
                String keyIdentifier = CryptoLibrary.getExtensionsAuthorityKeyIdentifier(crlExt);
                if (keyIdentifier.length() > 0) {
                    issuerCN = keyIdentifier;
                }
                Calendar thisUpdate = Calendar.getInstance();
                thisUpdate.setTime(crl.getThisUpdate().getDate());
                // logger.info(func+"憑證廢止清單(CRL)有效日期 : " + sdf.format(thisUpdate.getTime()));
                Calendar nextUpdate = Calendar.getInstance();
                nextUpdate.setTime(crl.getNextUpdate().getDate());
                // logger.info(func+"憑證廢止清單(CRL)下次更新 : " + sdf.format(nextUpdate.getTime()));
                long thisHours = CryptoLibrary.calculateHours(thisUpdate, now);
                long nextHours = CryptoLibrary.calculateHours(nextUpdate, now);
                // logger.info(func+"憑證廢止清單(CRL)有效日期距離現在 : " + thisHours + " 小時");
                // logger.info(func+"憑證廢止清單(CRL)下次更新距離現在 : " + nextHours + " 小時");
                if (thisHours < 0) {
                    // logger.error(func+"憑證廢止清單(CRL)尚未生效!!");
                    ret = ERROR_CRL_NOT_YET_VALIE;
                    crlFalure = true;
                }
                if (nextHours > 0) {
                    // logger.error(func+"憑證廢止清單(CRL)已經過期!!");
                    ret = ERROR_CRL_EXPIRED;
                    crlFalure = true;
                }
                org.bouncycastle.asn1.x509.TBSCertList.CRLEntry[] crlCertsEntry = crl.getTBSCertList().getRevokedCertificates();
                int crlCerts = crlCertsEntry.length;
                // logger.info(func+"由 CA 下載之憑證廢止清單(CRL), 共計發行筆數 : " + crlCerts);
                for (int i = 0; i < crlCerts; i++) {
                    crlSerialList.add(crlCertsEntry[i].getUserCertificate().getValue()); // 放 BigInteger
                }
            } catch (Exception e) {
                ret = ERROR_CRL_Exception;
                LOGGER.error(e.getMessage(), e);
                break;
            }
            if (crlFalure) {
                // 尚未生效 或 已經過期
                if (crlList.containsKey(issuerCN)) {
                    crlList.remove(issuerCN);
                }
                crlList.put(issuerCN, crlSerialList);
                break;
            }

            // byte[] crlBytes = crl.getDEREncoded();
            // logger.info(func+"由 CA 下載之憑證廢止清單(CRL) length : " + crlBytes.length);

            // 載入記憶體
            if (crlList.containsKey(issuerCN)) {
                crlList.remove(issuerCN);
                // logger.info(func+"刪除原有 憑證廢止清單(CRL) 註記為 : " + issuerCN + " 的內容");
            }
            crlList.put(issuerCN, crlSerialList);
            // logger.info(func+"由 CA 下載之憑證廢止清單(CRL), 註記為 : " + issuerCN + ", 已經載入");
        } while (false);
        return ret;
    }

    /**
     * 取得CRL檔
     *
     * @param urlAddr
     *            位置
     * @return
     */
    public static byte[] getCRL(String urlAddr, Proxy proxy) {
        byte[] ret = null;
        URL url = null;
        URLConnection urlConnection = null;
        InputStream is = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        do {
            try {
                url = new URL(urlAddr);
                if (proxy == null) {
                    urlConnection = url.openConnection();
                } else {
                    urlConnection = url.openConnection(proxy);
                }
                urlConnection.setDefaultUseCaches(false);
                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                // urlConnection.setConnectTimeout(1000*5);
                // urlConnection.setReadTimeout(1000*3);

                urlConnection.setDoOutput(false);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                break;
            }
            // long begintime = System.currentTimeMillis();
            byte[] buffer = new byte[256];
            try {
                is = urlConnection.getInputStream();
                int len;
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                    // long endtinme=System.currentTimeMillis();
                    // if( (endtinme-begintime)/1000 > 5)
                    // throw new Exception();
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                break;
            } finally {
                try {
                    is.close();
                } catch (IOException ex) {
                }
            }
            ret = baos.toByteArray();
            // String dump = hexDump(ret);
            // logger.debug(func+"URL 接收資料 :\r\n" + dump);
        } while (false);
        return ret;
    }

    /**
     * 取得憑證之OCSP下載位置
     *
     * @param certst
     * @return
     */
    public static String getAIALocation(Certificate certst) {
        AuthorityInformationAccess authorityInformationAccess = null;
        String OCSPUrl = null;
        do {
            ASN1InputStream asn1InOctets = null;
            try {
                DEROctetString aiaDEROctetString = (DEROctetString) certst.getTBSCertificate().getExtensions().getExtension(Extension.authorityInfoAccess).getExtnValue();
                asn1InOctets = new ASN1InputStream(aiaDEROctetString.getOctets());
                ASN1Sequence aiaASN1Sequence = (ASN1Sequence) asn1InOctets.readObject();
                authorityInformationAccess = AuthorityInformationAccess.getInstance(aiaASN1Sequence);

                AccessDescription[] accessDescriptions = authorityInformationAccess.getAccessDescriptions();
                for (AccessDescription accessDescription : accessDescriptions) {
                    GeneralName gn = accessDescription.getAccessLocation();
                    if (gn.getTagNo() == GeneralName.uniformResourceIdentifier) {
                        DERIA5String str = DERIA5String.getInstance(gn.getName());
                        OCSPUrl = str.getString();
                    }
                }
            } catch (IOException e) {
                break;
            } finally {
                if (asn1InOctets != null) {
                    try {
                        asn1InOctets.close();
                    } catch (IOException e) {
                    }
                }
            }
        } while (false);
        return OCSPUrl;
    }

    private static int a(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
        int j = 0;
        for (int i = 0; i < paramArrayOfByte1.length - paramArrayOfByte2.length; i++) {
            for (j = 0; (j < paramArrayOfByte2.length) && (paramArrayOfByte1[(i + j)] == paramArrayOfByte2[j]); j++) {
            }
            if (j == paramArrayOfByte2.length) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 取得憑證之統編(只有工商憑證才能取出值)
     *
     * @param cert
     * @return
     */
    public static String getEnterpriseId(Certificate cert) {
        String ret = null;
        do {
            if (cert == null) {
                break;
            }

            try {
                ASN1ObjectIdentifier xx = new ASN1ObjectIdentifier("2.5.29.9");
                DEROctetString aiaDEROctetString = (DEROctetString) cert.getTBSCertificate().getExtensions().getExtension(xx).getExtnValue();
                byte[] arrayOfByte1 = aiaDEROctetString.getEncoded();
                if (arrayOfByte1 == null) {
                    break;
                }
                byte[] arrayOfByte2 = new ASN1ObjectIdentifier("2.16.886.1.100.2.101").getEncoded();
                int i = a(arrayOfByte1, arrayOfByte2);
                if (i == -1) {
                    return null;
                }
                i = i + arrayOfByte2.length + 4;
                int j = arrayOfByte1[(i - 1)];
                ret = new String(arrayOfByte1, i, j);
            } catch (Exception e) {
                break;
            }
        } while (false);
        return ret;
    }

    /**
     * 取得憑證之身份證後4碼(只有自然人憑證才能取出值)
     *
     * @param cert
     * @return
     */
    public static String getPersonId(Certificate cert) {
        String ret = null;
        do {
            if (cert == null) {
                break;
            }
            try {
                ASN1ObjectIdentifier xx = new ASN1ObjectIdentifier("2.5.29.9");
                DEROctetString aiaDEROctetString = (DEROctetString) cert.getTBSCertificate().getExtensions().getExtension(xx).getExtnValue();

                byte[] arrayOfByte1 = aiaDEROctetString.getEncoded();
                if (arrayOfByte1 == null) {
                    break;
                }
                byte[] arrayOfByte2 = new ASN1ObjectIdentifier("2.16.886.1.100.2.51").getEncoded();
                int i = a(arrayOfByte1, arrayOfByte2);
                if (i == -1) {
                    break;
                }
                i = i + arrayOfByte2.length + 4;
                int j = arrayOfByte1[(i - 1)];
                ret = new String(arrayOfByte1, i, j);
            } catch (Exception e) {
                break;
            }
        } while (false);
        return ret;
    }

    /**
     * 產生OCSP請求資料
     *
     * @param uca
     *            簽發ee憑證之UCA
     * @param ee
     *            要驗證之憑證
     * @return
     */
    public static OCSPReq generateOCSPRequest(Certificate uca, Certificate ee) {
        OCSPReq Req = null;
        do {
            try {
                X509Certificate issuerCert = CryptoLibrary.getX509Cert(uca);
                BigInteger serialNumber = CryptoLibrary.getX509Cert(ee).getSerialNumber();
                CertificateID id = new CertificateID(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build().get(CertificateID.HASH_SHA1),
                        new X509CertificateHolder(issuerCert.getEncoded()), serialNumber);
                OCSPReqBuilder ocspGen = new OCSPReqBuilder();
                ocspGen.addRequest(id);

                // create a nonce to avoid replay attack
                BigInteger nonce = BigInteger.valueOf(System.currentTimeMillis());

                Extension ex = new Extension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, false, new DEROctetString(nonce.toByteArray()));
                ocspGen.setRequestExtensions(new Extensions (ex));
                Req = ocspGen.build();
            } catch (Exception e) {
                break;
            }
        } while (false);
        return Req;
    }

    /**
     * 發送OCSP請求到CA
     *
     * @param req
     *            OCSP 請求物件
     * @param sOCSPURL
     *            OCSP CA位置
     * @return
     */
    public static OCSPResp SendOCSP(OCSPReq req, String sOCSPURL, Proxy proxy) {
        OCSPResp Resp = null;
        do {
            HttpURLConnection conn = null;
            try {
                if (proxy == null) {
                    conn = (HttpURLConnection) new URL(sOCSPURL).openConnection();
                } else {
                    conn = (HttpURLConnection) new URL(sOCSPURL).openConnection(proxy);
                }
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/ocsp-request");
                conn.setDoOutput(true);
                conn.connect();
                OutputStream os = conn.getOutputStream();
                os.write(req.getEncoded());
                os.flush();
                byte[] resp = Streams.readAll(conn.getInputStream());
                conn.disconnect();
                Resp = new OCSPResp(resp);
            } catch (Exception e) {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        } while (false);
        return Resp;
    }

    /**
     * OCSP 回覆分析
     *
     * @param response
     * @param request
     * @return
     */
    public static int analyseOCSPResponse(OCSPResp response, OCSPReq request) {
        int ret = 0;
        do {
            try {
                BasicOCSPResp basicResponse = (BasicOCSPResp) response.getResponseObject();
                SingleResp[] responses = basicResponse.getResponses();
                byte[] reqNonce = request.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce).getExtnValue().getOctets();
                byte[] respNonce = basicResponse.getExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce).getExtnValue().getOctets();

                if (reqNonce == null || Arrays.equals(reqNonce, respNonce)) {
                    for (int i = 0; i != responses.length; i++) {
                        if (responses[i].getCertStatus() == CertificateStatus.GOOD) {
                            ret = 0;
                        } else {
                            ret = ERROR_OCSP_REVOKE_OR_UNKNOW;
                        }
                    }
                } else {
                    ret = ERROR_OCSP_Nonce;
                }

            } catch (Exception e) {
                ret = ERROR_OCSP_EXP;
                break;
            }
        } while (false);
        return ret;
    }

    /**
     * 驗證OCSP回覆之簽章值
     *
     * @param resp
     * @return
     */
    public static Certificate verifyOCSPResp(OCSPResp resp) {
        Certificate cert = null;
        do {
            try {
                BasicOCSPResp basic = (BasicOCSPResp) resp.getResponseObject();
                for (X509CertificateHolder holder : basic.getCerts()) {
                    try {
                        if (basic.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider("BC").build(holder))) {
                            cert = CryptoLibrary.getX509Cert(holder.getEncoded());
                            break;
                        }
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        } while (false);
        return cert;
    }

    /**
     * 取得CRL本次更新時間
     *
     * @param crl
     * @return
     */
    public static Date getCRLThisUpdate(CertificateList crl) {
        Date ret = null;
        try {
            if (crl != null) {
                ret = crl.getThisUpdate().getDate();
            }
        } catch (Exception e) {
        }
        return ret;
    }

    /**
     * 取得CRL下次更新時間
     *
     * @param crl
     * @return
     */
    public static Date getCRLNextUpdate(CertificateList crl) {
        Date ret = null;
        try {
            if (crl != null) {
                ret = crl.getNextUpdate().getDate();
            }
        } catch (Exception e) {
        }
        return ret;
    }

    /**
     * validCaCerts 之後呼叫。validCaCerts 會將驗不過的移除，但是無從得知那些被移除了。
     *
     * @param ski
     *            憑證的 SubjectKeyIdentifier
     * @return
     */
    public static boolean isCaCertValid(String ski) {
        return caCertsList.keySet().contains(ski);
    }
}
