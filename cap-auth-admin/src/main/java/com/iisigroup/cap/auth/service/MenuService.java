/*
 * MenuService.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.auth.service;

import java.util.Set;

import com.iisigroup.cap.auth.service.impl.MenuServiceImpl.MenuItem;

public interface MenuService {

    MenuItem getMenuByRoles(Set<String> roles);

}