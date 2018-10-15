/*
 * RespMsgHelper.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.base.response;

import java.text.MessageFormat;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import com.iisigroup.cap.base.model.ErrorCode;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.operation.simple.SimpleContextHolder;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.cap.utils.CapWebUtil;

/**
 * <pre>
 * 格式化回傳訊息工具程式
 * </pre>
 *
 * @since 2011/07/29
 * @author UFO
 * @version
 *          <ul>
 *          <li>2011/07/29,UFO,new
 *          <li>2011/09/07,Fantasy,新增getMainMessage method
 *          </ul>
 */
public class RespMsgHelper {
    public static final int EX_MAX_LEN = 1000;
    public static final int EX_MAX_LINE_LEN = 100;

    public static final int FLD_CODE = 0;
    public static final int FLD_SEVERTY = 1;
    public static final int FLD_MESSAGE = 2;
    public static final int FLD_SUGGESTION = 3;

    public static final String SEVERTY_INFO = "INFO";
    public static final String SEVERTY_WARN = "WARN";
    public static final String SEVERTY_ERROR = "ERROR";

    private static final String SEPARATOR = "|";
    private static final String SPLIT_CHAR = "\\|";
    private static final String OUT_SEPARATOR = "-";

    private static final String OUT_SUG_SEPARATOR = "|<BR>";

    private static final boolean DB_SOURCE = true;

    private RespMsgHelper() {

    }

    /**
     * 格式化回應訊息
     *
     * @param plugin
     *            AjaxHandlerPlugin
     * @param key
     *            i18n key
     * @return 回應訊息
     */
    public static String getMessage(String key) {
        return getMessage(key, (Object[]) null);
    }

    /**
     * 格式化回應訊息
     *
     * @param parent
     *            IRequest
     * @param key
     *            i18n key
     * @return 回應訊息
     */
    public static String getMessage(Request request, String key) {
        return getMessage(request, key, (Object[]) null);
    }

    /**
     * 格式化回應訊息
     *
     * @param key
     *            i18n key
     * @param params
     *            自訂的參數組(沒有設null)
     * @return 回應訊息
     */
    public static String getMessage(String key, Object[] params) {
        String value = getMsgString(key, null, params);
        return format(key, value, null);

    }

    /**
     * @param plugin
     *            AjaxHandlerPlugin
     * @param key
     *            i18n key
     * @param custMessage
     *            !=null則使用本參數當作回傳的錯誤訊息
     * @return 回應訊息
     */
    public static String getMessage(String key, String custMessage) {
        String value = CapAppContext.getMessage(key);
        return format(key, value, custMessage);

    }

    /**
     * @param comp
     *            IRequest
     * @param key
     *            i18n key
     * @param params
     *            自訂的參數組(沒有設null)
     * @return 回應訊息
     */
    public static String getMessage(Request request, String key, Object[] params) {

        String value = getMsgString(key, request, params);
        return format(key, value, null);
    }

    /**
     * @param comp
     *            IRequest
     * @param key
     *            i18n key
     * @param custMessage
     *            !=null則使用本參數當作回傳的錯誤訊息
     * @return 回應訊息
     */
    public static String getMessage(Request request, String key, String custMessage) {

        String value = getMsgString(key, request, null);
        return format(key, value, custMessage);
    }

    /**
     * 取得訊息字串
     *
     * @param key
     *            訊息代碼
     * @param workComp
     *            來源物件
     * @param params
     *            自訂參數
     * @param defaultValue
     *            預設值
     * @return
     */
    protected static String getMsgString(String key, Object workComp, Object[] params) {
        String msgstr = null;
        if (DB_SOURCE) {
            String localeStr = SimpleContextHolder.get(CapWebUtil.LOCALE_KEY).toString();
            ErrorCode errorCode = RespMsgFactory.getInstance().getErrorCode(key, localeStr);

            if (errorCode == null) {
                msgstr = CapString.concat("WARN", SEPARATOR, key, SEPARATOR, "");
            } else {
                String msg = errorCode.getMessage();
                if (params != null) {
                    msg = msg.replace("$\\{", "${").replace("\\}", "}");
                    msg = MessageFormat.format(msg, params);
                }
                msgstr = CapString.concat(errorCode.getSeverity(), SEPARATOR, msg, SEPARATOR, errorCode.getSuggestion());
            }

        } else {
            if (workComp instanceof Request) {
                if (params == null) {
                    msgstr = CapAppContext.getMessage(key);
                } else {
                    msgstr = CapAppContext.getMessage(key, params);
                }
            }
        }
        return msgstr;
    }

    /**
     * 格式化回應訊息
     *
     * @param key
     *            i18n key
     * @param value
     * @param custMessage
     *            !=null則使用本參數當作回傳的錯誤訊息
     * @return
     */
    private static String format(String key, String value, String custMessage) {
        if (value != null) {
            if (value.indexOf(SEPARATOR) == -1) {
                value = "WARN" + SEPARATOR + value;
            }

            // 等級|訊息內容|建議處理作法
            value = key + SEPARATOR + value;
            String[] cols = value.split(SPLIT_CHAR);

            // 設定自定義訊息
            if (custMessage != null) {
                cols[FLD_MESSAGE] = custMessage;
            }

            return getMessage(cols);
        } else {
            return key;
        }

    }

    /**
     * 格式化回應訊息
     *
     * @param params
     *            自定義訊息欄位陣列,size=4,[0]=錯誤代碼, [1]=等級,[2]=訊息內容,[3]=建議處理作法
     * @return 回應訊息
     */
    public static String getMessage(String[] params) {
        if (params == null || params.length < FLD_MESSAGE) {
            throw new IllegalArgumentException();
        }
        return getMessage(params[FLD_CODE], params[FLD_SEVERTY], params[FLD_MESSAGE], params.length > FLD_SUGGESTION ? params[FLD_SUGGESTION] : null);
    }

    /**
     * 格式化回應訊息
     *
     * @param severty
     *            等級
     * @param code
     *            錯誤代碼
     * @param msg
     *            訊息內容
     * @param suggestion
     *            建議處理作法
     * @return 回應訊息
     */
    public static String getMessage(String code, String severty, String msg, String suggestion) {

        StringBuilder errmsg = new StringBuilder();
        errmsg.append("[").append(severty).append("]");
        errmsg.append("").append(code).append(OUT_SEPARATOR);
        errmsg.append(msg);
        if (!StringUtils.isEmpty(suggestion)) {
            errmsg.append(OUT_SUG_SEPARATOR).append(suggestion).append("");
        }
        return errmsg.toString();
    }

    /**
     * 格式化回應訊息
     *
     * @param errorCode
     *            {@link com.mega.eloan.common.model.ErrorCode}
     * @return 回應訊息
     */
    public static String getMessage(ErrorCode errorCode) {
        if (errorCode != null) {
            return getMessage(errorCode.getCode(), errorCode.getSeverity(), errorCode.getMessage(), errorCode.getSuggestion());
        } else {
            return "[RespMsgHelper][getMessage]ErrorCode is null!!";
        }
    }

    /**
     * <pre>
     * 格式化回應訊息(取得主要訊息)
     *
     *  EX: [INFO]EFD0025-執行有誤 -> 執行有誤
     *
     * </pre>
     *
     * @param parent
     *            IRequest
     * @param key
     *            i18n key
     * @return 回應訊息
     */
    public static String getMainMessage(Request request, String key) {
        String temp = getMessage(request, key);
        if (!StringUtils.isEmpty(temp)) {
            String[] values = temp.split(OUT_SEPARATOR);
            if (values.length >= 1) {
                return values[1];
            }
        }
        return null;
    }

    /**
     * 將Exception stack trace指定長度輸出
     *
     * @param ex
     *            Exception
     * @param maxLen
     *            總長度
     * @param lineLen
     *            每列長度
     * @return 錯誤訊息
     */
    public static String formatDesc(Object obj) {
        if (obj == null) {
            return "";
        }

        String exTrace = (obj instanceof Throwable) ? StringEscapeUtils.escapeHtml(ExceptionUtils.getStackTrace((Throwable) obj)) : obj.toString();
        exTrace = exTrace.getBytes().length > EX_MAX_LEN ? (new String(exTrace.getBytes(), 0, EX_MAX_LEN) + "...") : exTrace;
        return CapString.addStrByFixedLength(exTrace, EX_MAX_LINE_LEN, "<BR>");
    }
}
