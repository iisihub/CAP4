/*
 * GsonUtilTest.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.Test;

import java.math.BigDecimal;
import com.iisigroup.cap.component.impl.AjaxFormResult;

/**
 * <pre>
 * TestCase for GsonUtil
 * </pre>
 *
 * @since 2016年7月20日
 * @author Sunkist
 * @version
 *          <ul>
 *          <li>2016年7月20日,Sunkist,new
 *          </ul>
 */
public class GsonUtilTest {

    static int count = 1;

    @Test
    public void testJsonToStringList() {
        String title = "[" + count++ + "]" + "testJsonToStringList";
        List<String> testData = new ArrayList<String>();
        testData.add("[]");
        testData.add("[0,1]");
        testData.add("[\"{}\",\"{}\"]");
        testData.add("[\"0\",\"1\"]");
        IntStream.range(0, testData.size()).forEach(i -> System.out.print((i == 0 ? "\n" + title + ": \n" : "\n") + "\t" + i + " => " + GsonUtil.jsonToStringList(testData.get(i))));
    }

    @Test
    public void testJsonToObjectList() {
        String title = "[" + count++ + "]" + "testJsonToObjectList";
        List<String> testData = new ArrayList<String>();
        testData.add("[{a:1, b: 2},{a:10, b:20}]");
        IntStream.range(0, testData.size()).forEach(i -> System.out.print((i == 0 ? "\n" + title + ": \n" : "\n") + "\t" + i + " => " + GsonUtil.jsonToObjectList(testData.get(i))));
    }

    @Test
    public void testJsonToMap() {
        String title = "[" + count++ + "]" + "testJsonToMap";
        List<String> testData = new ArrayList<String>();
        testData.add("{a: \"1\", b: \"2\"}");
        testData.add("{a: 1, b: 2}");
        testData.add("{\"a\": 1, \"b\": 2}");
        IntStream.range(0, testData.size()).forEach(i -> System.out.print((i == 0 ? "\n" + title + ": \n" : "\n") + "\t" + i + " => " + GsonUtil.jsonToMap(testData.get(i))));
    }

    @Test
    public void testObjToJson() {
        String title = "[" + count++ + "]" + "testObjToJson";
        List<Object> testData = new ArrayList<Object>();
        testData.add("{a: \"1\", b: \"2\"}");
        testData.add("{a: 1, b: 2}");
        testData.add("\"{a: \"1\", b: \"2\"}\"");
        testData.add(BigDecimal.ONE);
        IntStream.range(0, testData.size()).forEach(i -> System.out.print((i == 0 ? "\n" + title + ": \n" : "\n") + "\t" + i + " => " + GsonUtil.objToJson(testData.get(i))));
    }

    @Test
    public void testObjToMap() {
        String title = "[" + count++ + "]" + "testObjToMap";
        List<Object> testData = new ArrayList<Object>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("a", "1");
        map.put("b", "2");
        testData.add(map);
        // testData.add(BigDecimal.ONE);
        IntStream.range(0, testData.size()).forEach(i -> System.out.print((i == 0 ? "\n" + title + ": \n" : "\n") + "\t" + i + " => " + GsonUtil.objToMap(testData.get(i))));
    }

    @Test
    public void testMapToJson() {
        String title = "[" + count++ + "]" + "testMapToJson";
        List<Map<String, Object>> testData = new ArrayList<Map<String, Object>>();
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("L1", "2");
        map1.put("L2", "3");
        map1.put("R1", "1");
        map1.put("R2", "4");
        Map<String, Object> map1sorted = new LinkedHashMap<String, Object>();
        map1sorted.put("L1", "2");
        map1sorted.put("L2", "3");
        map1sorted.put("R1", "1");
        map1sorted.put("R2", "4");
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("a", 1);
        map2.put("b", 2);
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("a", BigDecimal.ONE.intValue());
        map3.put("b", BigDecimal.ONE.longValue());
        map3.put("c", String.valueOf(BigDecimal.ONE));
        Map<String, Object> map4 = new HashMap<String, Object>();
        map4.put("a", map1);
        map4.put("b", new AjaxFormResult());
        map4.put("c", new AjaxFormResult().set("a'", ""));
        testData.add(map1);
        testData.add(map1sorted);
        testData.add(map2);
        testData.add(map3);
        testData.add(map4);
        IntStream.range(0, testData.size()).forEach(i -> System.out.print((i == 0 ? "\n" + title + ": \n" : "\n") + "\t" + i + " => " + GsonUtil.mapToJson(testData.get(i))));
    }

    @Test
    public void testJsonToObj() {
        String title = "[" + count++ + "]" + "testJsonToObj";
        List<String> testData = new ArrayList<String>();
        testData.add("{}");
        testData.add("{a: \"1\", b: \"2\"}");
        testData.add("{a: 1, b: 2}");
        testData.add("[]");
        testData.add("[0, 1]");
        testData.add("[\"{}\",\"{}\"]");
        testData.add("[{},{}]");
        IntStream.range(0, testData.size()).forEach(i -> System.out.print((i == 0 ? "\n" + title + ": \n" : "\n") + "\t" + i + " => " + GsonUtil.jsonToObj(testData.get(i))));
    }

    @Test
    public void testObjToObj() {
        String title = "[" + count++ + "]" + "testObjToObj";
        List<Object> testData = new ArrayList<Object>();
        testData.add("{}");
        testData.add("{a: \"1\", b: \"2\"}");
        testData.add("{a: 1, b: 2}");
        testData.add("[]");
        testData.add("[0, 1]");
        testData.add("[\"{}\",\"{}\"]");
        testData.add("[{},{}]");
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("a", "1");
        map1.put("b", "2");
        testData.add(map1);
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("a", BigDecimal.ONE.intValue());
        map2.put("b", BigDecimal.ONE.longValue());
        map2.put("c", String.valueOf(BigDecimal.ONE));
        testData.add(map2);
        testData.add(BigDecimal.ZERO);
        IntStream.range(0, testData.size()).forEach(i -> System.out.print((i == 0 ? "\n" + title + ": \n" : "\n") + "\t" + i + " => " + GsonUtil.objToObj(testData.get(i))));
    }

}
