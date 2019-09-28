/* 
 * HandlerType.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * Handler Type
 * </pre>
 * 
 * @since 2011/11/30
 * @author rodeschen
 * @version
 *          <ul>
 *          <li>2011/11/30,rodeschen,new
 *          </ul>
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HandlerType {
    HandlerTypeEnum value() default HandlerTypeEnum.FORM;

    String name() default "";

    /**
     * <pre>
     * Handler Type Enum
     * </pre>
     * 
     * @since 2011/11/30
     * @author rodeschen
     * @version
     *          <ul>
     *          <li>2011/11/30,rodeschen,new
     *          </ul>
     */
    public enum HandlerTypeEnum {
        FORM,
        GRID,
        FILE_UPLOAD,
        FILE_DOWNLOAD;
    }
}
