/* 
 * LogContext.java
 * 
 * Copyright (c) 2018 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.web;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * 從原本 LogContextFilter 抽出 LogContext
 * </pre>
 * 
 * @since 2018年10月16日
 * @author Lancelot
 * @version
 *          <ul>
 *          <li>2018年10月16日,Lancelot,new
 *          </ul>
 */
public class LogContext extends InheritableThreadLocal {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogContext.class);

    private static ThreadLocal<Map> context = new InheritableThreadLocal<Map>();

    private static boolean useMDC = false;

    public static final String LOGIN = "login";

    public static final String UUID = "uuid";

    public static final String SESSION_ID = "sessionId";

    public static final String HOST = "host";

    public static final String CLIENT_ADDR = "clientAddr";

    public static final String REQUEST_URI = "reqURI";

    static {
        try {
            Class.forName("org.apache.log4j.MDC");
            useMDC = true;
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("org.apache.log4j.MDC was not found on the classpath, continue without");
            }
        }
    }

    @Override
    protected Object childValue(Object parentValue) {
        return new LinkedHashMap((Map) parentValue);
    }

    /**
     * Get a map containing all the objects held by the current thread.
     */
    private static Map getContext() {
        if (useMDC) {
            return org.apache.log4j.MDC.getContext();
        } else {
            Map m = context.get();
            if (m == null) {
                m = new LinkedHashMap();
                context.set(m);
            }
            return m;
        }
    }

    /**
     * Get the context identified by the key parameter.
     * 
     * @param key
     *            the key
     * @return Object
     */
    public static Object get(String key) {
        if (useMDC) {
            return org.apache.log4j.MDC.get(key);
        } else {
            return getContext().get(key);
        }
    }

    /**
     * Put a context value (the o parameter) as identified with the key parameter into the current thread's context map.
     * 
     * @param key
     *            the Key
     * @param o
     *            Object
     */
    public static void put(String key, Object o) {
        if (useMDC) {
            org.apache.log4j.MDC.put(key, o);
        } else {
            getContext().put(key, o);
        }
    }

    /**
     * Remove the the context identified by the key parameter.
     * 
     * @param key
     *            the Key
     */
    public static void remove(String key) {
        if (useMDC) {
            org.apache.log4j.MDC.remove(key);
        } else {
            getContext().remove(key);
        }
    }

    /**
     * Remove all the object put in this thread context.
     */
    public static void resetLogContext() {
        if (getContext() != null) {
            getContext().clear();
        }
    }

    /**
     * Only used if jdk logging is used.
     * 
     * @return String
     */
    public static String toLogPrefixString() {
        Map m = getContext();
        Iterator i = m.entrySet().iterator();

        StringBuilder sb = new StringBuilder("[");
        while (i.hasNext()) {
            Map.Entry e = (Map.Entry) i.next();
            sb.append((String) e.getKey()).append("=").append(e.getValue().toString());
            if (i.hasNext()) {
                sb.append("&");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * set the given login in the map
     * 
     * @param login
     *            the user Id
     */
    public static void setLogin(String login) {
        put(LOGIN, login);
    }

    /**
     * set the given IP in the map
     * 
     * @param host
     *            the host
     */
    public static void setHost(String host) {
        put(HOST, host);
    }

    /**
     * set the given web session in the map
     * 
     * @param sessionId
     *            the session id
     */
    public static void setSessionId(String sessionId) {
        put(SESSION_ID, sessionId);
    }

    public static void setClientAddr(String addr) {
        put(CLIENT_ADDR, addr);
    }

    public static void setRequestURL(String url) {
        put(REQUEST_URI, url);
    }

    public static void setUUID(String uuid) {
        put(UUID, uuid);
    }

}
