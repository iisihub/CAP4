/* 
 * GsonUtil.java
 * 
 * Copyright (c) 2016 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.utils;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * <pre>
 * 以 Gson 實作的 CapJsonUtil
 * </pre>
 * 
 * @since 2016年7月20日
 * @author Sunkist
 * @version
 *          <ul>
 *          <li>2016年7月20日,Sunkist,new
 *          </ul>
 */
public class GsonUtil {

    /**
     * <pre>
     * test data:
     * "[0,1]" 
     * "[\"{}\",\"{}\"]"
     * "[\"0\",\"1\"]"
     * </pre>
     * 
     * @param jsonStringArray
     * @return List of String
     */
    public static List<String> jsonToStringList(String jsonStringArray) {
        return jsonToObj(jsonStringArray);
    }

    /**
     * <pre>
     * test data:
     * [{a:1, b: 2},{a:10, b:20}]
     * </pre>
     * 
     * @param jsonObjectArray
     * @return List of Object
     */
    public static List<Object> jsonToObjectList(Object jsonObjectArray) {
        return objToObj(jsonObjectArray);
    }

    /**
     * <pre>
     * test data:
     * "{a: \"1\", b: \"2\"}"
     * "{a: 1, b: 2}"
     * </pre>
     * 
     * @param jsonString
     * @return Map
     */
    public static Map<String, Object> jsonToMap(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static String mapToJson(Map<String, Object> map) {
        Gson gson = new Gson();
        return gson.toJson(map, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    /**
     * <pre>
     * test data:
     * "{}"
     * "{a: \"1\", b: \"2\"}"
     * "{a: 1, b: 2}"
     * "[]"
     * "[0,1]"
     * "[\"{}\",\"{}\"]"
     * "[{},{}]"
     * </pre>
     * 
     * @param jsonString
     * @return <T>
     */
    public static <T> T jsonToObj(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, new TypeToken<T>() {
        }.getType());
    }

    public static String objToJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static Map<String, Object> objToMap(Object obj) {
        if (obj instanceof String) {
            return jsonToMap((String) obj);
        }
        return jsonToMap(objToJson(obj));
    }

    public static <T> T objToObj(Object obj) {
        if (obj instanceof String) {
            return jsonToObj((String) obj);
        }
        return jsonToObj(objToJson(obj));
    }
}
