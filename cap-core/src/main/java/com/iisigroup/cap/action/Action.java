/* 
 * Action.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.action;

import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;

/**
 * <p>
 * 動作.
 * </p>
 * 
 * @author iristu
 * @version
 *          <ul>
 *          <li>2010/7/20,iristu,new
 *          <li>2011/11/1,rodeschen,from cap
 *          </ul>
 */
public interface Action {

    Result doWork(Request params);

}
