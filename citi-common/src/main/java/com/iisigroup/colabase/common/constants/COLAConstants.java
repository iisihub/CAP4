/*
 * COLAConstants.java
 *
 * Copyright (c) 2009-2015 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.common.constants;

import com.iisigroup.cap.constants.Constants;

public interface COLAConstants extends Constants {

    public static final String S_ATTR_ERR_MSG = "ERRMSG";
    public static final String ATTR_REDIRECT = "_ar";
    public static final String TIME_OUT = "TIME_OUT";

    public enum ContextTypeEnum {
        text("text/html"),
        pdf("application/pdf"),
        doc("application/msword"),
        UNKNOW("application/octet-stream"),
        xls("application/vnd.ms-excel"),
        IMAGE("image/.*"),
        jpg("image/jpeg"),
        tif("image/tiff"),
        gif("image/gif"),
        png("image/png");

        private String code;

        ContextTypeEnum(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return code;
        }

        public static ContextTypeEnum getEnum(String code) {
            if (code.matches(IMAGE.code)) {
                return IMAGE;
            }
            for (ContextTypeEnum enums : ContextTypeEnum.values()) {
                if (enums.isEquals(code)) {
                    return enums;
                }
            }
            return null;
        }

        public boolean isEquals(Object other) {
            if (other instanceof String) {
                return code.equals(other);
            } else {
                return super.equals(other);
            }
        }
    }
}
