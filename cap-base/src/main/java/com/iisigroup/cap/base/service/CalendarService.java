/* 
 * CalendarService.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.base.service;

import java.util.List;

import com.iisigroup.cap.base.model.Remind;

public interface CalendarService {

    public List<Remind> getCalendarData(String userId, String start, String end);
}
