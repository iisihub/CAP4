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

    public List<Reminds> getRemindItems(String[] styleTyp, String locale);

    public void saveReminds(Reminds remind);

    public HashMap<String, CapUserDetails> getCurrentUser();

    public String getUsrEmail(String usrId);

    public Remind findRemind(String pid);

}
