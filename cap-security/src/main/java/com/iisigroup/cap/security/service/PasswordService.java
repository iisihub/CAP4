/* 
 * PasswordService.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.security.service;

import java.util.Map;

public interface PasswordService {
    boolean checkPasswordRule(String userId, String password, String password2, boolean forcePwdChange);

    void changeUserPassword(String userId, String password);

    boolean validatePassword(String userId, String password);

    Map<String, String> getPasswordPolicy();

    int getPasswordChangeNotifyDay(String userId);

}
