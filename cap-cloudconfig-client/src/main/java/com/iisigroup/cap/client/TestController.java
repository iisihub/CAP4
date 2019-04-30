/* 
 * RestController.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 * A Hello Controller.
 * </pre>
 * 
 * @since Apr 29, 2019
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>Apr 29, 2019,Sunkist Wang,new
 *          </ul>
 */
@RestController
@RefreshScope
class TestController {

    @Value("${upload.path:}")
    private String uploadPath;

    @Value("${db.jdbc.url:}")
    private String dbJdbcUrl;

    @RequestMapping("/test")
    public String from() {
        return "uploadPath= "+ this.uploadPath+ ",dbJdbcUrl= "+ this.dbJdbcUrl;
    }
}
