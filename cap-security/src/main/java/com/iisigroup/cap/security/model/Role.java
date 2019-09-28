/* 
 * Role.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.security.model;

import java.io.Serializable;

/**
 * <p>
 * 角色.
 * </p>
 * 
 * @author iristu
 * @version
 *          <ul>
 *          <li>2010/7/26,iristu,new
 *          </ul>
 */
public interface Role extends Serializable {

    String getCode();

    void setCode(String code);

    String getName();

    void setName(String name);
}
