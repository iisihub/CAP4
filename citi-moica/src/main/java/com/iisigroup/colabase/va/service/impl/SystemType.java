/*
 * SystemType.java
 *
 * Copyright (c) 2009-2016 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.va.service.impl;

/**<pre>
 * </pre>
 * @since  2016-02-18
 * @author Roger Lin
 * @version <ul>
 *           <li>2016-02-18,Roger Lin,new
 *           <li>2016-04-01,Bo-Xaun Fan,add ReturnDoc Type
 *          </ul>
 */
public enum SystemType {
    MOICA_OPEN_ACCOUNT("MOM"), MOICA_RETURN_DOC("MOM_RETURN_DOC"), COLA("COLA");

    private String rcode;

    SystemType(String code) {
        this.rcode = code;
    }

    public String getCode() {
        return rcode;
    }

    public boolean isEquals(Object other) {
        if (other instanceof String) {
            return rcode.equals(other);
        } else {
            return super.equals(other);
        }
    }
}

