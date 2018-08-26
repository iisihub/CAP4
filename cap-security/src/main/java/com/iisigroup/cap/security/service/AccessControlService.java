/* 
 * AccessControlService.java
 * 
 * Copyright (c) 2011 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.security.service;

import java.util.List;

import com.iisigroup.cap.security.model.Role;

/**
 * <pre>
 * the interface ISecurityService
 * </pre>
 * 
 * @since 2010/11/29
 * @author iristu
 * @version
 *          <ul>
 *          <li>2010/11/29,iristu,new
 *          </ul>
 */
public interface AccessControlService {

    List<Role> getAuthRolesByUrl(String url);

    void lockUserByUserId(String userId);

    void login(String userId);

    boolean checkCaptcha();

}
