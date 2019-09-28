/* 
 * SecConstants.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.security.constants;

import com.iisigroup.cap.constants.Constants;

public interface SecConstants extends Constants {
    public enum PwdPolicyKeys {
        PWD_EXPIRED_DAY,
        PWD_MIN_LENGTH,
        PWD_RULE,
        PWD_MAX_HISTORY,
        PWD_CAPTCHA_ENABLE,
        PWD_ACCOUNT_LOCK,
        PWD_FORCE_CHANGE_PWD,
        PWD_CHANGE_INTERVAL,
        PWD_NOTIFY_DAY,
        PWD_ACCOUNT_DISABLE,
        PWD_ACCOUNT_DELETE
    }
}
