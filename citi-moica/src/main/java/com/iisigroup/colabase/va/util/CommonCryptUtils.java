package com.iisigroup.colabase.va.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.com.iisi.desserver.execption.SAPIException;
import tw.com.iisi.desserver.security.entry.IISISecurityEntry;
import tw.com.iisi.desserver.security.impl.iisijcecrypto.EntryType;
import tw.com.iisi.desserver.security.impl.iisijcecrypto.IISIJCEDESOp;
import tw.com.iisi.desserver.utils.Util;

/**
 * 加解密工具
 * 
 * @author TimChiang
 * @since
 *        <li>2014/4/16,Tim,New
 *
 */
public class CommonCryptUtils {

    protected static final byte[] randomByte = new byte[] { 115, 90, -128, 75, -65, 113, 118, -31, -123, -59, -102, -110, -70, 54, -12, -119, 9, -33, 5, -20, 41, -105, 51, -12 };
    protected static final byte[] checkSumByte = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    static IISIJCEDESOp desOp = new IISIJCEDESOp();

    private static Logger logger = LoggerFactory.getLogger(CommonCryptUtils.class);
    private CommonCryptUtils() {
        
    }
    
    /**
     * 加密
     * 
     * @param cryptData
     *            需要加密的字串
     */
    public static String encrypt(String cryptData) throws SAPIException {
        String cryptResult = "";
        try {
            cryptData = encodeHex(cryptData.getBytes("UTF-8"));

            byte[] dataByte = Util.hexStringToByte(cryptData);
            IISISecurityEntry sentry = desOp.generate3DESSecretKey(EntryType.DB_SECRET_KEY, randomByte);
            byte[] checkSumResult = desOp.encrypt("CBC", "PKCS5Padding", checkSumByte, dataByte, sentry);
            if (checkSumResult != null) {
                String s2 = new String(Util.byteToHexChar(checkSumResult));
                cryptResult = (s2);
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        return cryptResult;
    }

    /**
     * 解密
     * 
     * @param decryptData
     *            需要解密的字串
     */
    public static String decrypt(String decryptData) throws SAPIException {
        String decryptResult = "";
        try {
            // IISIJCEDESOp desOp = new IISIJCEDESOp();
            IISISecurityEntry sentry = desOp.generate3DESSecretKey(EntryType.DB_SECRET_KEY, randomByte);
            byte[] checkDecryptResult = desOp.decrypt("CBC", "PKCS5Padding", checkSumByte, Util.hexStringToByte(decryptData), sentry);
            if (checkDecryptResult != null) {
                decryptResult = new String(Util.byteToHexChar(checkDecryptResult));
                decryptResult = new String(decodeHex(decryptResult), "UTF-8");
            }
            // System.out.println("decryptResult = >>" + decryptResult);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        return decryptResult;
    }

    public static byte[] decodeHex(String hexString) throws SAPIException {
        byte[] result = null;
        try {
            result = Hex.decodeHex(hexString.toCharArray());
        } catch (DecoderException e) {
            logger.error(e.getMessage(), e);
            throw new SAPIException(4, "CommonUtil.hexToBinary: " + hexString);
        }
        return result;
    }

    public static String encodeHex(byte[] binary) {
        return String.valueOf(Hex.encodeHex(binary));
    }
}
