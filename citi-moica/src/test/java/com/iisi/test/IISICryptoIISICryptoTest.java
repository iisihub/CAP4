/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iisi.test;

import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPResp;

import com.iisigroup.colabase.va.crypto.CryptoLibrary;
import com.iisigroup.colabase.va.crypto.ICSCChecker;
import com.iisigroup.colabase.va.crypto.PKCS7Verify;

/**
 * 
 * @author TWCArd
 */
public class IISICryptoIISICryptoTest {

    /**
     * @param args
     *            the command line arguments
     */

    public static void main(String[] args) {

        String moica_root = "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";
        String moicap7b = "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";
        String moeacap7b = "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";
        String moeaca_root = "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";
        boolean ret = false;
        int ret1 = 0;
        do {
            try {
                // Load 要trust的憑證鏈到記憶体，可放多組
                CryptoLibrary.clearCACert();
                Certificate ca1 = CryptoLibrary.loadCaCerts(CryptoLibrary.base64Decode(moica_root));
                if (ca1 != null) {
                    System.out.println("getX509NotBefore()=" + CryptoLibrary.getX509NotBefore(ca1));
                    System.out.println("getX509NotAfter()=" + CryptoLibrary.getX509NotAfter(ca1));
                } else {
                    break;
                }
                Certificate ca2 = CryptoLibrary.loadCaCerts(CryptoLibrary.base64Decode(moeaca_root));
                if (ca2 != null) {
                    System.out.println("getX509NotBefore()=" + CryptoLibrary.getX509NotBefore(ca2));
                    System.out.println("getX509NotAfter()=" + CryptoLibrary.getX509NotAfter(ca2));
                } else {
                    break;
                }
                CryptoLibrary.validCaCerts();
                System.out.println("CA1 getExtensionsSubjectKeyIdentifier()="
                        + CryptoLibrary.isCaCertValid(CryptoLibrary.getExtensionsSubjectKeyIdentifier(ca1)));
                System.out.println("CA2 getExtensionsSubjectKeyIdentifier()="
                        + CryptoLibrary.isCaCertValid(CryptoLibrary.getExtensionsSubjectKeyIdentifier(ca2)));

                // pkcs7 驗章測試
                PKCS7Verify a = new PKCS7Verify();
                PKCS7Verify b = new PKCS7Verify();
                ret = a.verify(CryptoLibrary.base64Decode(moicap7b));
                System.out.println("verify ret=" + ret);

                ret = b.verify(CryptoLibrary.base64Decode(moeacap7b));
                System.out.println("verify ret=" + ret);
                if (ret != true)
                    break;

                ret = a.verify_Detached(CryptoLibrary.base64Decode(moicap7b), "test".getBytes("utf-16le"));
                System.out.println("verify_Detached ret=" + ret);
                if (ret != true)
                    break;
                byte[] signature = a.getSignature();
                ret = a.verifyPKCS1("SHA1WithRSA", signature, "test".getBytes("utf-16le"),
                        CryptoLibrary.getX509Cert(a.getSignerCert()).getPublicKey());
                System.out.println("verifyPKCS1 ret=" + ret);
                if (ret != true)
                    break;
                // 驗憑證鏈，所以之前需先有放入記憶体內才行
                ret = CryptoLibrary.verifyCertChain(a.getSignerCert());
                System.out.println("VerifyCertChain ret=" + ret);
                if (ret != true)
                    break;
                // 驗憑證鏈，所以之前需先有放入記憶体內才行
                ret = CryptoLibrary.verifyCertChain(b.getSignerCert());
                System.out.println("VerifyCertChain ret=" + ret);
                if (ret != true)
                    break;

                // 取得PKCS7內各項資料
                System.out.println("getDigestName()=" + a.getDigestName());
                System.out.println("getEncName()=" + a.getEncName());
                System.out.println("getSignatureName()=" + a.getSignatureName());
                System.out.println("getSignedContent()=" + CryptoLibrary.hexEncode(a.getSignedContent()));
                System.out.println("getSignedContent()=" + CryptoLibrary.getX509CertString(a.getSignerCert()));

                // 取得憑證各項資訊
                System.out.println("getCertType()=" + CryptoLibrary.getCertType(a.getSignerCert()));
                System.out.println("getPersonId()=" + CryptoLibrary.getPersonId(a.getSignerCert()));
                System.out.println("getEnterpriseId()=" + CryptoLibrary.getEnterpriseId(a.getSignerCert()));
                System.out.println("getX509Subject()=" + CryptoLibrary.getX509Subject(a.getSignerCert()));
                System.out.println("getX509Issuer()=" + CryptoLibrary.getX509Issuer(a.getSignerCert()));
                System.out.println("getX509CN()=" + CryptoLibrary.getX509CN(a.getSignerCert()));
                System.out.println("getX509Serial()=" + CryptoLibrary.getX509Serial(a.getSignerCert()));
                System.out.println("getX509KeyUsageBytes()="
                        + CryptoLibrary.hexEncode(CryptoLibrary.getX509KeyUsageBytes(a.getSignerCert())));
                System.out.println("getX509Finger()=" + CryptoLibrary.getX509Finger(a.getSignerCert()));
                System.out.println("getX509NotBefore()=" + CryptoLibrary.getX509NotBefore(a.getSignerCert()));
                System.out.println("getX509NotAfter()=" + CryptoLibrary.getX509NotAfter(a.getSignerCert()));
                System.out.println("getExtensionsSubjectKeyIdentifier()="
                        + CryptoLibrary.getExtensionsSubjectKeyIdentifier(a.getSignerCert()));
                System.out.println("getX509AuthorityKeyIdentifier()="
                        + CryptoLibrary.getX509AuthorityKeyIdentifier(a.getSignerCert()));
                System.out.println("getX509Finger()=" + CryptoLibrary.getX509Finger(a.getSignerCert()));

                // 驗憑證效期
                ret1 = CryptoLibrary.checkCertDateValid(a.getSignerCert(), null);
                System.out.println("checkCertDateValid ret=" + ret1);
                if (ret1 != 0)
                    break;
                // MOICA 身份驗證 需要有內政部憑證及key
                ICSCChecker check = new ICSCChecker();
                check.setClientCertPath("D:/COLA/ics/F6E5EDF4350EDF7F5C524EBAFDCB6C4A.cer");
                check.setURL("61.60.9.50", "443", "/cgi-bin/CheckSNPID/CheckSNPID");
                ret = check.loadRSAkey("D:/COLA/ics/Citibank_final_web.pfx", "Citibank123");
                System.out.println("check.loadRSAkey ret=" + ret);
                if (ret != true)
                    break;
                ret1 = check.checkMOICAICSC(a.getSignerCert(), "W100349834", null);
                System.out.println("checkMOICAICSC ret=" + ret1);
                if (ret1 != 0)
                    break;
                System.out.println(check.getErrorMsg());

                String CRLURL = CryptoLibrary.getX509CrlDistributionPoint(a.getSignerCert());
                System.out.println("CRLURL ret=" + CRLURL);
                byte[] crl = CryptoLibrary.getCRL(CRLURL, null);
                if (crl == null)
                    break;
                CertificateList crllist = CryptoLibrary.parseCRL(crl);
                if (crllist == null)
                    break;
                String getCRLThisUpdate = CryptoLibrary.getCRLThisUpdate(crllist).toString();
                System.out.println("getCRLThisUpdate ret=" + getCRLThisUpdate);
                String getCRLNextUpdate = CryptoLibrary.getCRLNextUpdate(crllist).toString();
                System.out.println("getCRLNextUpdate ret=" + getCRLNextUpdate);
                ret = CryptoLibrary.verifyCRLChain(crllist);
                System.out.println("check.VerifyCRLChain ret=" + ret);
                if (ret != true)
                    break;

                // 有關CRL Download 部份可以每日固定download & Load
                ret1 = CryptoLibrary.storeAndReloadCRL(crllist);
                System.out.println("storeAndReloadCRL ret=" + ret1);
                if (ret1 != 0)
                    break;
                ret1 = CryptoLibrary.verifyCertCRL(a.getSignerCert());
                System.out.println("VerifyCertCRL ret=" + ret1);
                if (ret1 != 0)
                    break;

                OCSPReq req = CryptoLibrary.generateOCSPRequest(CryptoLibrary.getCACert(a.getSignerCert()),
                        a.getSignerCert());
                System.out.println("OCSPReq ret=" + req);
                if (req == null)
                    break;
                String aia = CryptoLibrary.getAIALocation(a.getSignerCert());
                System.out.println("OCSP URL ret=" + aia);

                OCSPResp resp = CryptoLibrary.SendOCSP(req, aia, null);
                System.out.println("OCSPResp ret=" + resp);
                if (resp == null)
                    break;

                ret1 = CryptoLibrary.analyseOCSPResponse(resp, req);
                System.out.println("analyseOCSPResponse ret=" + ret1);
                if (ret1 != 0)
                    break;
                Certificate signOCSP = CryptoLibrary.verifyOCSPResp(resp);
                ret = CryptoLibrary.verifyCertChain(signOCSP);
                System.out.println("VerifyCertChain ret=" + ret);
                if (ret != true)
                    break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } while (false);
    }
}
