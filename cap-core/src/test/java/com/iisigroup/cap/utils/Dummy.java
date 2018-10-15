/* 
 * Dummy.java
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

import java.io.Serializable;

/**
 * <pre>
 * TODO Write a short description on the purpose of the program
 * </pre>
 * 
 * @since 2018年10月15日
 * @author Lancelot
 * @version
 *          <ul>
 *          <li>2018年10月15日,Lancelot,new
 *          </ul>
 */
public class Dummy implements Serializable {
    private static final long serialVersionUID = 2969758791830628043L;
    private String a;

    /**
     * @return the a
     */
    public String getA() {
        return a;
    }

    /**
     * @param a
     *            the a to set
     */
    public void setA(String a) {
        this.a = a;
    }

}
