package com.iisigroup.colabase.util;

import com.iisigroup.cap.utils.CapString;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author AndyChen
 * @version
 *          <ul>
 *          <li>2018/5/30 AndyChen,new
 *          </ul>
 * @since 2018/5/30
 */
public class NumberUtil {

    public enum Type {
        HALF,
        FULL,
        CHINESE
    }

    public enum Numbers {
        ZERO("0", "０", "零"),
        ONE("1", "１", "一"),
        TWO("2", "２", "二"),
        THREE("3", "３", "三"),
        FOUR("4", "４", "四"),
        FIVE("5", "５", "五"),
        SIX("6", "６", "六"),
        SEVEN("7", "７", "七"),
        EIGHT("8", "８", "八"),
        NINE("9", "９", "九");

        private String half;
        private String full;
        private String chinese;

        Numbers(String half, String full, String chinese) {
            this.half = half;
            this.full = full;
            this.chinese = chinese;
        }

        public static Numbers numToNums(String number) {
            for (Numbers numbers : Numbers.values()) {
                if (numbers.half.equals(number))
                    return numbers;
                if (numbers.full.equals(number))
                    return numbers;
                if (numbers.chinese.equals(number))
                    return numbers;
            }
            return null;
        }
    }

    public static String getFirstNumers(String value) {
        String temp = "";
        boolean foundNum = false;
        for (String key : toStringArray(value)) {
            if (isNumber(key)) {
                foundNum = true;
                temp += key;
            } else {
                if (foundNum) { // 之前已經找到過
                    break;
                }
            }
        }
        return temp;
    }

    /**
     * 將一段文字裡的數字轉成指定type
     * 
     * @param value
     *            受檢數字
     * @param type
     *            指定型態
     * @return 轉換後文字
     */
    public static String formatStrNumberByType(String value, Type type) {
        String noneNumTemp = "";
        String temp = "";
        boolean foundNum = false;
        for (String key : toStringArray(value)) {
            if (isNumber(key)) {
                foundNum = true;
                temp += key;
            } else {
                if (foundNum) { // 之前已經找到過
                    break;
                }
                noneNumTemp += key;
            }
        }
        value = value.substring(value.indexOf(temp) + temp.length());
        if ("".equals(temp) || value.length() == 0)
            return noneNumTemp + temp;
        temp = transNumberToAssignType(temp, type);
        return noneNumTemp + temp + formatStrNumberByType(value, type);
    }

    /**
     * 轉換數字(string)成指定型態數字
     * 
     * @param numbers
     *            需轉換數字
     * @param type
     *            型態
     * @return 轉換後數字
     */
    public static String transNumberToAssignType(String numbers, Type type) {
        boolean checkMix = isMixNum(numbers);
        if (checkMix) { // 如果是混合數字就全部先轉成half
            String temp = "";
            for (String s : toStringArray(numbers)) {
                Numbers nums = Numbers.numToNums(s);
                temp += Objects.requireNonNull(nums).half;
            }
            numbers = temp;
        }

        Integer number;
        int length;
        if (isChineseNumber(numbers)) { // 中文數字先轉成半形數字
            if (Type.CHINESE == type)
                return numbers;
            String strToNumber = transStrToNumber(numbers);
            length = strToNumber.length();
            number = Integer.parseInt(strToNumber);
        } else {
            length = numbers.length();
            number = Integer.parseInt(numbers);
        }

        final String zero = "0";
        switch (type) {
        case FULL:
            return StringUtils.leftPad(CapString.halfWidthToFullWidth(String.valueOf(number)), length, zero);
        case HALF:
            return StringUtils.leftPad(number.toString(), length, zero);
        case CHINESE:
            String tenFormat = specialTenFormat(String.valueOf(number));
            if (length <= 2 && !String.valueOf(number).equals(tenFormat))
                return tenFormat;
            StringBuilder result = new StringBuilder();
            for (String key : toStringArray(numbers)) {
                for (Numbers num : Numbers.values()) {
                    if (num.half.equals(key)) {
                        result.append(num.chinese);
                        break;
                    }
                }
            }
            return StringUtils.leftPad(result.toString(), length, zero);
        default:
            return numbers;
        }
    }

    private static boolean isMixNum(String value) {
        Type flagType = null;
        if (value == null)
            return false;
        value = value.trim();
        for (String key : toStringArray(value)) {
            if ("".equals(key))
                continue;
            if (isNumber(key)) {
                for (Numbers number : Numbers.values()) {
                    Type type;
                    if (isChineseNumber(key)) {
                        type = Type.CHINESE;
                    } else if (number.full.equals(key)) {
                        type = Type.FULL;
                    } else {
                        type = Type.HALF;
                    }
                    if (flagType == null)
                        flagType = type;
                    if (flagType != type) {
                        return true;
                    }
                }
            } else {
                throw new IllegalArgumentException("argument must be numbers");
            }
        }
        if (flagType != null) {
            return false;
        }
        throw new IllegalArgumentException("argument must be numbers");
    }

    private static String specialTenFormat(String value) {
        String regExpStr = "^(([2-9])|(1))([0-9])$";
        Pattern pattern = Pattern.compile(regExpStr);
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
            String firstChar = Objects.requireNonNull(Numbers.numToNums(matcher.group(1))).chinese;
            if (!StringUtils.isBlank(matcher.group(3)))
                firstChar = "";
            if ("0".equals(matcher.group(4))) {
                return firstChar + "十";
            }
            return firstChar + "十" + Objects.requireNonNull(Numbers.numToNums(matcher.group(4))).chinese;
        }
        return value;
    }

    public static boolean isNumber(String check) {
        if (isChineseNumber(check))
            return true;
        try {
            Integer.parseInt(check);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isChineseNumber(String check) {
        String[] split = toStringArray(check);
        for (String key : split) {
            boolean found = false;
            for (NumberUtil.Numbers number : NumberUtil.Numbers.values()) {
                if (number.chinese.equals(key) || "十".equals(key)) {
                    found = true;
                    break;
                }
            }
            if (!found)
                return false;
        }
        return true;
    }

    /**
     * 大寫數字轉換小寫 及中文數字辨識 目前支援如下ex: 五十, 五十五, 二九五五 (不含百千等詞)
     * 
     * @param numberStr
     *            chinese numbers
     * @return normal number ex: 123
     */
    public static String transStrToNumber(String numberStr) {
        try {
            Integer.parseInt(numberStr);
            return numberStr;
        } catch (NumberFormatException e) {
            // for 中文數字辨識 目前支援如下ex: 五十, 五十五, 二九五, 八零八
            String regExpChinStr = "([一二三四五六七八九]?)(十+)([一二三四五六七八九]?)$|([一二三四五六七八九零]+)";
            Pattern chinPattern = Pattern.compile(regExpChinStr);
            Matcher chinMatcher = chinPattern.matcher(numberStr);
            if (chinMatcher.find()) {
                if (!StringUtils.isBlank(chinMatcher.group(4))) { // ex: 二九五
                    numberStr = chinMatcher.group(4);
                    String formatNumStr = "";
                    for (int i = 0; i < numberStr.length(); i++) {
                        formatNumStr += matchChinStrToNumber(String.valueOf(numberStr.charAt(i)));
                    }
                    return formatNumStr;
                } else {
                    String temp = "";
                    if (StringUtils.isBlank(chinMatcher.group(1)) && StringUtils.isBlank(chinMatcher.group(3))) { // ex: 十
                        temp = "10";
                    } else if (StringUtils.isBlank(chinMatcher.group(3))) { // ex: 五十
                        temp += matchChinStrToNumber(chinMatcher.group(1));
                        temp += "0";
                    } else { // ex: 五十五
                        temp += matchChinStrToNumber(chinMatcher.group(1));
                        temp += matchChinStrToNumber(chinMatcher.group(3));
                    }
                    return temp;
                }
            }
        }
        throw new NumberFormatException();
    }

    public static String matchChinStrToNumber(String chineseNums) {
        StringBuilder result = new StringBuilder();
        for (String key : toStringArray(chineseNums)) {
            char[] chars = key.toCharArray();
            result.append(matchChinStrToNumber(chars[0]));
        }
        return result.toString();
    }

    /**
     * 中文數字對照阿拉伯數字 ex: 八 -> 8
     * 
     * @param single
     *            number needs to match
     * @return result
     */
    public static String matchChinStrToNumber(char single) {
        String chineseStr = String.valueOf(single);
        switch (chineseStr) {
        case "ㄧ":
            return "1";
        case "二":
            return "2";
        case "三":
            return "3";
        case "四":
            return "4";
        case "五":
            return "5";
        case "六":
            return "6";
        case "七":
            return "7";
        case "八":
            return "8";
        case "九":
            return "9";
        case "十":
            return "0";
        case "零":
            return "0";
        default:
            return "";
        }
    }

    public static String[] toStringArray(String text) {
        char[] chars = text.toCharArray();
        List<String> list = new ArrayList<>();
        String[] result = new String[chars.length];
        for (char aChar : chars) {
            list.add(String.valueOf(aChar));
        }
        list.toArray(result);
        return result;
    }
}
