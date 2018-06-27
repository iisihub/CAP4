/*
 * CCUtils.java
 *
 * Copyright (c) 2009-2014 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.webatm.toolkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;

/**
 * <pre>
 * Credit Card Utils.
 * </pre>
 *
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>2014/1/28,Sunkist Wang,new
 *          </ul>
 * @since 2014/1/28
 */
public class CCUtils {

    public CCUtils() {
    }

    /**
     * Pattern matched to replace string.
     *
     * @param patterned
     *            String
     * @param input
     *            String
     * @param matched
     *            String
     * @return Replaced String
     */
    public static String replacement(String patterned, String input, String matched) {
        Pattern pattern = Pattern.compile(patterned);
        Matcher matcher = pattern.matcher(input);
        StringBuffer buff = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buff, matched);
        }
        matcher.appendTail(buff);
        return buff.toString();
    }

    public static String stringReplace(String pattern, String str, String replace) {
        int s = 0, e = 0;
        StringBuffer result = new StringBuffer();

        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e + pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }

    /**
     * Encodes the byte array into base64 string
     *
     * @param imageByteArray
     *            - byte array
     * @return String a {@link java.lang.String}
     */
    public static String encodeImage(byte[] imageByteArray) {
        return Base64.encodeBase64String(imageByteArray);
    }

    /**
     * Decodes the base64 string into byte array
     *
     * @param imageDataString
     *            - a {@link java.lang.String}
     * @return byte array
     */
    public static byte[] decodeImage(String imageDataString) {
        return Base64.decodeBase64(imageDataString);
    }

    public static String trim(String str) {
        if (str == null)
            return "";
        return str.trim();
    }
}
