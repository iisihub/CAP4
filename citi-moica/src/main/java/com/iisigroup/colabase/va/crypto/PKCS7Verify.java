package com.iisigroup.colabase.va.crypto;

import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;

import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.util.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 提供PKCS7/PKCS1/CMS 格式驗簽章值功能
 * 
 */
@SuppressWarnings({ "rawtypes", "deprecation" })
public class PKCS7Verify {
    private static final Logger LOGGER = LoggerFactory.getLogger(PKCS7Verify.class);
    private byte[] SignedContent = null;
    private SignerInformation signer = null;
    private X509Certificate signercert = null;

    /**
     * 取得簽章值內容
     * 
     * @return
     */
    public byte[] getSignature() {
        byte[] ret = null;
        if (signer != null)
            ret = signer.getSignature();
        return ret;
    }

    /**
     * 取得Digest演算法名字
     * 
     * @return
     */
    public String getDigestName() {
        String ret = null;
        if (signer != null)
            ret = CMSSignedHelper.INSTANCE.getDigestAlgName(signer.getDigestAlgOID());
        return ret;
    }

    /**
     * 取得加密演算法名字
     * 
     * @return
     */
    public String getEncName() {
        String ret = null;
        if (signer != null)
            ret = CMSSignedHelper.INSTANCE.getEncryptionAlgName(signer.getEncryptionAlgOID());
        return ret;
    }

    /**
     * 取得簽章演算法名字
     * 
     * @return
     */
    public String getSignatureName() {
        String ret = null;
        if (signer != null)
            ret = getDigestName() + "with" + getEncName();
        return ret;
    }

    /**
     * 取得簽章者憑證
     * 
     * @return
     */
    public Certificate getSignerCert() {
        Certificate ret = null;
        if (signercert != null) {
            ret = CryptoLibrary.getX509Cert(signercert);
        }
        return ret;
    }

    /**
     * PKCS7格式，帶一張憑證且不帶本文之驗簽，本文由外部傳入
     * 
     * @param Sig_Bytes
     *            PKCS7 格式之簽章值
     * @param Data_Bytes
     *            本文資料
     * @return 驗章結果
     */

    public boolean verify_Detached(byte[] Sig_Bytes, byte[] Data_Bytes) {
        boolean ret = false;
        do {
            try {
                CryptoLibrary.checkProvider();
                CMSSignedData cms = new CMSSignedData(new CMSProcessableByteArray(Data_Bytes), Sig_Bytes);
                Store<X509CertificateHolder> certStore = cms.getCertificates();
                SignedContent = Data_Bytes;
                SignerInformationStore signers = cms.getSignerInfos();
                Collection c = signers.getSigners();
                Iterator it = c.iterator();
                while (it.hasNext()) {
                    signer = (SignerInformation) it.next();
                    // TODO check if work
                    Collection<X509CertificateHolder> certCollection = certStore.getMatches(signer.getSID());
                    Iterator<X509CertificateHolder> certIt = certCollection.iterator();
                    X509CertificateHolder cert = (X509CertificateHolder) certIt.next();
                    ret = signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert));
                    if (ret != true) {
                        continue;
                    } else
                        break;
                }
            } catch (Exception e) {
                LOGGER.error("PKCS7 VerifyDetachedError" + e.getLocalizedMessage(), e);
                ret = false;
            }
        } while (false);
        return ret;
    }

    /**
     * PKCS7格式，帶一張憑證且帶本文之驗簽
     * 
     * @param SignedData
     *            PKCS7 格式之簽章值
     * @return 驗章結果
     */
    public boolean verify(byte[] SignedData) {
        boolean ret = false;
        do {
            CMSSignedData aSignedData = null;
            try {
                CryptoLibrary.checkProvider();
                aSignedData = new CMSSignedData(SignedData);
                Store<X509CertificateHolder> certStore = aSignedData.getCertificates();
                SignerInformationStore signers = aSignedData.getSignerInfos();
                SignedContent = (byte[]) aSignedData.getSignedContent().getContent();
                Collection c = signers.getSigners();
                Iterator it = c.iterator();
                while (it.hasNext()) {
                    signer = (SignerInformation) it.next();
                    SignerId signerId = signer.getSID();
                    Collection<X509CertificateHolder> certCollection = certStore.getMatches(signer.getSID());
                    Iterator<X509CertificateHolder> certIt = certCollection.iterator();
                    X509CertificateHolder cert = (X509CertificateHolder) certIt.next();
                    signercert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(cert);
                    ret = signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert));
                    if (ret != true) {
                        continue;
                    } else
                        break;

                }

            } catch (Exception e) {
                LOGGER.error("PKCS7 VerifyError" + e.getLocalizedMessage(), e);
                ret = false;
            }

        } while (false);
        return ret;
    }

    /**
     * 取得簽章明文資料
     * 
     * @return 簽章明文
     */
    public byte[] getSignedContent() {
        return SignedContent;
    }

    /**
     * PKCS1驗章功能
     * 
     * @param alg
     *            驗章演算法(ex:SHA1WithRSA)
     * @param signature
     *            PKCS1 簽章值
     * @param data
     *            本文資料
     * @param key
     *            公鑰值
     * @return 驗章結果
     */
    public boolean verifyPKCS1(String alg, byte[] signature, byte[] data, PublicKey key) {
        boolean ret = false;
        do {
            try {
                CryptoLibrary.checkProvider();
                Signature verify = Signature.getInstance(alg, "BC");
                verify.initVerify(key);
                verify.update(data);
                ret = verify.verify(signature);
            } catch (Exception ex) {
                LOGGER.error("PKCS1 VerifyError" + ex.getLocalizedMessage(), ex);
            }
        } while (false);
        return ret;
    }

}
