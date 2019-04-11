package com.iisigroup.colabase.common.monitor.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author AndyChen
 * @version <ul>
 * <li>2019/4/9 AndyChen,new
 * </ul>
 * @since 2019/4/9
 */
public final class SqlStatementFormatUtil {

    private SqlStatementFormatUtil() {}

    /**
     * 將SQL語句中沒有no lock的，加上with(nolock)
     * @param origin 原始SQL語句
     * @return 加上with(nolock)的statement
     */
    public static String addNoLockStatement(String origin) {
        String patternStr = "^select.* with\\s*\\(\\s*nolock\\s*\\)\\s*where.*$"; // 有加nolock
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(origin);
        if (!matcher.find()) {
            String patternStr1 = "select.*?from\\s+(.*?)\\s+where.*?"; // 僅適用簡單查詢語句
            Pattern pattern1 = Pattern.compile(patternStr1);
            Matcher matcher1 = pattern1.matcher(origin);
            while (matcher1.find()) {
                String tableName = matcher1.group(1);
                origin = origin.replace(tableName, tableName + " with(nolock) ");
            }
        }
        return origin;
    }

}
