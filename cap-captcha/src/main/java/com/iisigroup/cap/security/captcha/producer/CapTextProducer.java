/* 
 * CapTextProducer.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.security.captcha.producer;

import nl.captcha.text.producer.TextProducer;

public class CapTextProducer implements TextProducer {

    private String str;

    public CapTextProducer(String str) {
        this.str = str;
    }

    public String getText() {
        return str;
    }

}
