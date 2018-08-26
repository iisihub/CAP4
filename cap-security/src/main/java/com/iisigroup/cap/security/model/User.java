/* 
 * User.java
 * 
 * Copyright (c) 2011 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.security.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

/**
 * <p>
 * 使用者資料.
 * </p>
 * 
 * @author iristu
 * @version
 *          <ul>
 *          <li>2010/7/26,iristu,new
 *          <li>2011/11/1,rodeschen,from cap
 *          </ul>
 */
public interface User extends Serializable {

    String getCode();

    String getName();

    String getDepCode();

    String getStatusDesc();

    String getUpdater();

    Timestamp getUpdateTime();

    List<? extends Role> getRoles();

    Locale getLocale();

    String getPassword();

    String getStatus();

    String getEmail();
}
