/* 
 * CapMathTest.java
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

/**
 * <pre>
 * CapMath unit test program
 * </pre>
 * 
 * @since 2018年10月15日
 * @author Lancelot
 * @version
 *          <ul>
 *          <li>2018年10月15日,Lancelot,new
 *          </ul>
 */
public class CapMathTest {
    @Test
    public void testToChineseUpperAmount() {
        String result = CapMath.toChineseUpperAmount(".123", false);
        Assert.assertTrue("零元壹角貳分參厘".equals(result));
    }
}
