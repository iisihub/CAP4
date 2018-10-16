/* 
 * RemindService.java
 * 
 * Copyright (c) 2011 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.websocket.service;

import java.util.HashMap;
import java.util.List;

import com.iisigroup.cap.base.model.Remind;
import com.iisigroup.cap.base.model.Reminds;
import com.iisigroup.cap.security.model.CapUserDetails;

public interface RemindService {

    List<Reminds> getRemindItems(String[] styleTyp, String locale);

    void saveReminds(Reminds remind);

    HashMap<String, CapUserDetails> getCurrentUser();

    String getUsrEmail(String usrId);

    Remind findRemind(String pid);

}
