/* 
 * UUIDGenerator.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.utils;

import java.util.UUID;

/**
 * <pre>
 * UUID Generator
 * </pre>
 * 
 * @since 2011/7/8
 * @author iristu
 * @version
 *          <ul>
 *          <li>2011/7/8,iristu,new
 *          </ul>
 */
public class UUIDGenerator {

    /**
     * 產生 32 位的 UUID
     * 
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
