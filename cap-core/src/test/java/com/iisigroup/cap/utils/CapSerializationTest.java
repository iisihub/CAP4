/* 
 * CapSerializationTest.java
 * 
 * Copyright (c) 2018 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.utils;

import org.junit.Assert;
import org.junit.Test;

import com.iisigroup.cap.component.impl.AjaxFormResult;

/**
 * <pre>
 * CapSerialization Unit Test Program
 * </pre>
 * 
 * @since 2018年10月15日
 * @author Lancelot
 * @version
 *          <ul>
 *          <li>2018年10月15日,Lancelot,new
 *          </ul>
 */
public class CapSerializationTest {
    @Test
    public void testLoadData() {
        String data = CapSerialization.newInstance().saveData(new Dummy());
        Object obj = CapSerialization.newInstance().loadData(data, Dummy.class);
        Assert.assertTrue(Dummy.class == obj.getClass());
        Assert.assertTrue(CapSerialization.newInstance().loadData(data, AjaxFormResult.class) == null);
    }
}
