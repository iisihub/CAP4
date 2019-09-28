/* 
 * DataObject.java
 * 
 * Copyright (c) 2019 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.db.model;

import java.io.Serializable;

/**
 * <p>
 * interface IDataObject.
 * </p>
 * 
 * @author iristu
 * @version
 *          <ul>
 *          <li>2010/7/7,iristu,new
 *          <li>2011/11/1,rodeschen,from cap
 *          </ul>
 */
public interface DataObject extends Serializable {

    /**
     * Gets the oid.
     * 
     * @return the oid
     */
    String getOid();

    /**
     * Sets the oid.
     * 
     * @param oid
     *            the new oid
     */
    void setOid(String oid);

}
