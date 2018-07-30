/* 
 * CommonCryptoUtils.java
 * 
 * Copyright (c) 2009-2017 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.webatm.toolkit;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
//import org.junit.Assert;
//import org.junit.Test;

import tw.com.iisi.desserver.execption.SAPIException;
import tw.com.iisi.desserver.security.entry.IISISecurityEntry;
import tw.com.iisi.desserver.security.impl.iisijcecrypto.EntryType;
import tw.com.iisi.desserver.security.impl.iisijcecrypto.IISIJCEDESOp;
import tw.com.iisi.desserver.utils.Util;

/**
 * <pre>
 * 加解密工具(IISIJCECrypto)
 * </pre>
 * 
 * @since 2017年7月13日
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>2017年7月13日,Sunkist Wang,copy from cola and Add encryptByKeyValueForAWATM()
 *          <li>2017年7月17日,Sunkist Wang,encryptByKeyValueForAWATM(plainTextInHex, sEntry)
 *          <li>2017年7月18日,Sunkist Wang,decryptByKeyValueForAWATM(cipherTextInHex, sEntry)
 *          </ul>
 */
public class CommonCryptoUtils {

    public static final byte[] randomByte = new byte[] { 115, 90, -128, 75, -65, 113, 118, -31, -123, -59, -102, -110, -70, 54, -12, -119, 9, -33, 5, -20, 41, -105, 51, -12 };
    public static final byte[] checkSumByte = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    static IISIJCEDESOp desOp = new IISIJCEDESOp();

    private static byte[] encryptForAWATM(byte[] data, IISISecurityEntry entry) throws SAPIException {
        SecretKey key = (SecretKey) entry.getAttribute("SecretKey");
        byte[] cipher = null;
        try {
            Cipher desCipher = Cipher.getInstance(key.getAlgorithm() + "/ECB/NoPadding");
            desCipher.init(1, key);
            cipher = desCipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SAPIException(2002, "Encrypt error.");
        }
        return cipher;
    }

    private static byte[] decryptForAWATM(byte[] data, IISISecurityEntry entry) throws SAPIException {
        SecretKey key = (SecretKey) entry.getAttribute("SecretKey");
        byte[] cipher = null;
        try {
            Cipher desCipher = Cipher.getInstance(key.getAlgorithm() + "/ECB/NoPadding");
            desCipher.init(2, key);
            cipher = desCipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SAPIException(2002, "Decrypt error.");
        }
        return cipher;
    }

    /**
     * Encrypt plainText by keyValue
     * 
     * @param plainTextInHex
     *            String
     * @param keyValue
     *            byte[]
     * @return cipherTextInHex
     * @throws SAPIException
     */
    public static String encryptByKeyValueForAWATM(String plainTextInHex, byte[] keyValue) throws SAPIException {
        String cryptResult = "";
        byte[] dataByte = Util.hexStringToByte(plainTextInHex);
        IISISecurityEntry sentry = desOp.generate3DESSecretKey(EntryType.DB_SECRET_KEY, keyValue);
        byte[] checkSumResult = encryptForAWATM(dataByte, sentry);
        if (checkSumResult != null) {
            String s2 = new String(Util.byteToHexChar(checkSumResult));
            cryptResult = (s2);
        }
        return cryptResult;
    }

    /**
     * Decrypt cipherText by keyValue
     * 
     * @param cipherTextInHex
     * @param keyValue
     * @return plainTextInHex
     * @throws SAPIException
     */
    public static String decryptByKeyValueForAWATM(String cipherTextInHex, byte[] keyValue) throws SAPIException {
        String cryptResult = "";
        byte[] dataByte = Util.hexStringToByte(cipherTextInHex);
        IISISecurityEntry sentry = desOp.generate3DESSecretKey(EntryType.DB_SECRET_KEY, keyValue);
        byte[] checkSumResult = decryptForAWATM(dataByte, sentry);
        if (checkSumResult != null) {
            String s2 = new String(Util.byteToHexChar(checkSumResult));
            cryptResult = (s2);
        }
        return cryptResult;
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
            // byte[] randomByte = cryptoOp.generateRandom(24);
            IISISecurityEntry sentry = desOp.generate3DESSecretKey(EntryType.DB_SECRET_KEY, randomByte);
            byte[] checkSumResult = desOp.encrypt("CBC", "PKCS5Padding", checkSumByte, dataByte, sentry);
            if (checkSumResult != null) {
                String s2 = new String(Util.byteToHexChar(checkSumResult));
                cryptResult = (s2);
                // cryptOp.hash("SHA-256", checkSumResult);
            }
            // System.out.println("checkSum = >>" + cryptResult);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return decryptResult;
    }

    public static byte[] decodeHex(String hexString) throws SAPIException {
        byte[] result = null;
        try {
            result = Hex.decodeHex(hexString.toCharArray());
        } catch (DecoderException e) {
            e.printStackTrace();
            throw new SAPIException(4, "CommonUtil.hexToBinary: " + hexString);
        }
        return result;
    }

    public static String encodeHex(byte[] binary) {
        return String.valueOf(Hex.encodeHex(binary));
    }

//    @Test
//    public void testEncryptECB() throws SAPIException {
//        System.out.println(
//                CommonCryptoUtils.encryptByKeyValueForAWATM(CommonCryptoUtils.encodeHex("0476183925xxxxxx".getBytes()), Misc.hex2Bin("D9ACF32065C8918DF80D9940F3795C445CF238C41B89AF95".getBytes())));
//        Assert.assertTrue("69CAB5BDA070116719BB9AA16FEE4D2B".equals(
//                CommonCryptoUtils.encryptByKeyValueForAWATM(new String(Misc.bin2Hex("0476183925xxxxxx".getBytes())), Misc.hex2Bin("D9ACF32065C8918DF80D9940F3795C445CF238C41B89AF95".getBytes()))));
//    }
//
//    @Test
//    public void testDecryptECB() throws SAPIException {
//        System.out.println(CommonCryptoUtils.decryptByKeyValueForAWATM("69CAB5BDA070116719BB9AA16FEE4D2B", Misc.hex2Bin("D9ACF32065C8918DF80D9940F3795C445CF238C41B89AF95".getBytes())));
//        Assert.assertTrue("0476183925xxxxxx".equals(new String(CommonCryptoUtils
//                .decodeHex(CommonCryptoUtils.decryptByKeyValueForAWATM("69CAB5BDA070116719BB9AA16FEE4D2B", Misc.hex2Bin("D9ACF32065C8918DF80D9940F3795C445CF238C41B89AF95".getBytes()))))));
//    }
}
