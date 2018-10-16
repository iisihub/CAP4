/*
 * Sequence.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.base.model;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Sequence {
    private Map<String, Object> thisSeq;

    public Sequence() {
        this.thisSeq = new HashMap<String, Object>();
    }

    public String getSeqNode() {
        return (String) thisSeq.get("seqNode");
    }

    public void setSeqNode(String seqNode) {
        thisSeq.put("seqNode", seqNode);
    }

    public Integer getNextSeq() {
        return (Integer) thisSeq.get("nextSeq");
    }

    public void setNextSeq(Integer nextSeq) {
        thisSeq.put("nextSeq", nextSeq);
    }

    public Integer getRounds() {
        return (Integer) thisSeq.get("rounds");
    }

    public void setRounds(Integer rounds) {
        thisSeq.put("rounds", rounds);
    }

    public void setUpdateTime(Timestamp updateTime) {
        thisSeq.put("updateTime", updateTime);
    }

    public Map<String, Object> getSequence() {
        Map<String, Object> newMap = new HashMap<String, Object>();
        newMap.putAll(thisSeq);
        return newMap;
    }
}
