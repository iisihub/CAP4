/*
 * SpELUtil.java
 *
 * Copyright (c) 2011 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.cap.utils;

import java.util.Map;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * <pre>
 * SpELUtil
 * </pre>
 * 
 * @since 2010/12/29
 * @author RodesChen
 * @version
 *          <ul>
 *          <li>2010/12/29,RodesChen,new
 *          <li>2011/11/1,rodeschen,from cap
 *          </ul>
 */
public class SpELUtil {

    private static final ParserContext DEF_PARSER = new TemplateParserContext();

    private SpELUtil() {

    }

    /**
     * use Spring Expression Language (SpEL) parse
     * 
     * @param expressionStr
     *            expression string
     * @param params
     *            parameters
     * @param parserContext
     *            parserContext
     * @return String
     */
    public static String spelParser(String expressionStr, Map<String, Object> params, ParserContext parserContext) {
        StandardEvaluationContext context = new StandardEvaluationContext(params);
        ExpressionParser spel = new SpelExpressionParser();
        return spel.parseExpression(expressionStr, parserContext).getValue(context, String.class);
    }

    /**
     * use Spring Expression Language (SpEL) parse
     * 
     * @param expressionStr
     *            expression string
     * @param params
     *            parameters
     * @return String
     */
    public static String spelParser(String expressionStr, Map<String, Object> params) {
        return spelParser(expressionStr, params, DEF_PARSER);
    }

}