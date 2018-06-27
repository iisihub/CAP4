package com.iisigroup.colabase.webatm.toolkit;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 雜七雜八的公用程式
 *
 * @author axl
 * @version 1.0
 */

public class Misc {
    private static Random rand = null;
    private static int cnt;
    /**
     * {@link #genDate} 使用的類別-IFX標準格式-日期, 格式為 YYYY-m-d
     */
    public static final int DT_IFX_DATE = 100;
    /**
     * {@link #genDate} 使用的類別-IFX標準格式-日期時間, 格式為 YYYY-m-dTh:n:s+08:00
     */
    public static final int DT_IFX_FULL = 101;
    /**
     * {@link #genDate} 使用的類別-可閱讀格式-日期, 格式為 YYYY-MM-DD
     */
    public static final int DT_FMT_DATE = 200;
    /**
     * {@link #genDate} 使用的類別-可閱讀格式-日期時間, 格式為 YYYY-MM-DD HH:NN:SS.ZZZ
     */
    public static final int DT_FMT_FULL = 201;
    /**
     * {@link #genDate} 使用的類別-日期, 格式為 YYYYMMDD
     */
    public static final int DT_DATE = 0;
    /**
     * {@link #genDate} 使用的類別-日期時間, 格式為 YYYYMMDDHHNNSS
     */
    public static final int DT_DATETIME = 1;
    /**
     * {@link #genDate} 使用的類別-時間, 格式為 HHNNSS
     */
    public static final int DT_TIME = 2;
    /**
     * {@link #genDate} 使用的類別-時間(至毫秒), 格式為 HHNNSSZZZ
     */
    public static final int DT_LTIME = 3;
    /**
     * {@link #genDate} 使用的類別-完整(日期時間至毫秒), 格式為 YYYYMMDDHHNNSSZZZ
     */
    public static final int DT_FULL = 4;

    public static final int SUCCESS = 0;
    public static final int FAILURE = -1;
    public static final int FILE_NOT_FOUND = -2;
    private static final int STND_BUFFER_SIZE = 1024;

    /**
     * 產生唯一的 UID (全宇宙不重複)<br>
     * todo : 加上以網路卡ID
     *
     * @return Random UID
     */
    public static String genUUID() {
        if (rand == null) {
            rand = new Random(System.currentTimeMillis());
        }
        return new ServiceID(rand.nextLong(), rand.nextLong()).toString();
    }

    /**
     * 檢查 UUID 格式 (hhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh)
     *
     * @return 格式正確否
     */
    public static boolean validateUUID(String sUUID) {
        boolean bRtn = false;
        try {
            // 總長度不對就甭看了
            if (sUUID.length() < 36)
                return bRtn;
            // 一段段抓出來
            int lenUUID[] = { 8, 4, 4, 4, 12 };
            String stUUID;
            StringTokenizer st = new StringTokenizer(sUUID, "-");
            for (int i = 0; st.hasMoreTokens(); i++) {
                stUUID = st.nextToken();
                if (stUUID.length() != lenUUID[i])
                    return bRtn;
                Long.valueOf(stUUID, 16); // 只看能不能轉就好了
            }
            bRtn = true;
        } catch (Exception e) {
            // whatever, return false;
        }
        return bRtn;
    }

    /**
     * 產生亂數, 以 int 格式回傳
     *
     * @param max
     *            亂數的最大值
     */
    public static int genRandom(int max) {
        if (rand == null) {
            rand = new Random(System.currentTimeMillis());
        }
        return rand.nextInt(max);
    }

    /**
     * 產生亂數, 以 String 格式回傳, 前面不足位數補零, 總位數以傳入的最大值為準
     *
     * @param max
     *            亂數的最大值
     */
    public static String genRandomStr(int max) {
        if (rand == null) {
            rand = new Random(System.currentTimeMillis());
        }
        return padZero(rand.nextInt(max), Integer.toString(max).length());
    }

    /**
     * 取得系統時間, IFX 格式
     */
    public static String genDate() {
        Calendar now = Calendar.getInstance();
        return (now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE) + "T" + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":"
                + now.get(Calendar.SECOND));
    }

    /**
     * 取得系統時間, 格式依傳入參數決定
     *
     * @param fmt
     *            傳入格式類別
     * @return 就系統時間咩
     * @see #DT_DATE
     * @see #DT_DATETIME
     * @see #DT_TIME
     * @see #DT_LTIME
     * @see #DT_FULL
     */
    public static String genDate(int fmt) {
        String strFmt;
        switch (fmt) {
        case DT_IFX_DATE:
            strFmt = "yyyy-MM-dd";
            break;
        case DT_IFX_FULL:
            strFmt = "yyyy-MM-dd'T'HH:mm:ss+480";
            break;
        case DT_FMT_DATE:
            strFmt = "yyyy-MM-dd";
            break;
        case DT_FMT_FULL:
            strFmt = "yyyy-MM-dd HH:mm:ss.SSS";
            break;
        case DT_DATE:
            strFmt = "yyyyMMdd";
            break;
        case DT_DATETIME:
            strFmt = "yyyyMMddHHmmss";
            break;
        case DT_TIME:
            strFmt = "HHmmss";
            break;
        case DT_LTIME:
            strFmt = "HHmmssSSS";
            break;
        case DT_FULL:
            strFmt = "yyyyMMddHHmmssSSS";
            break;
        default:
            strFmt = "yyyyMMddHHmmssSSS";
        }
        return ((new SimpleDateFormat(strFmt, Locale.getDefault())).format(new Date()));
    }

    /**
     * 傳入指定日期, 傳回該日期之前（後）N 天
     *
     * @param String
     *            sDate 格式 yyyyMMdd
     * @param int
     *            iAddDay 前（後）天數
     */
    public static String getAddDate(String sDate, int iAddDay) {
        Calendar date = Calendar.getInstance();
        date.set(Integer.parseInt(sDate.substring(0, 4)), Integer.parseInt(sDate.substring(4, 6)) - 1, Integer.parseInt(sDate.substring(6, 8)));
        date.add(Calendar.DATE, iAddDay);
        return ((new SimpleDateFormat("yyyyMMdd", Locale.getDefault())).format(date.getTime()));
    }

    /**
     * 傳入指定日期, 傳回該日期之前（後）N 月
     *
     * @param String
     *            sDate 格式 yyyyMM[dd]
     * @param int
     *            iAddMonth 前（後）月數
     */
    public static String getAddMonth(String sDate, int iAddMonth) {
        Calendar date = Calendar.getInstance();
        date.set(Integer.parseInt(sDate.substring(0, 4)), Integer.parseInt(sDate.substring(4, 6)) - 1, 1);
        date.add(Calendar.MONTH, iAddMonth);
        return ((new SimpleDateFormat("yyyyMM", Locale.getDefault())).format(date.getTime()));
    }

    /**
     * 取得系統時間, 格式依傳入參數決定
     *
     * @param fmt
     *            傳入格式類別
     * @param int
     *            iAddDay 前（後）天數
     * @see #DT_DATE
     * @see #DT_DATETIME
     * @see #DT_TIME
     * @see #DT_LTIME
     * @see #DT_FULL
     */
    public static String genAddDate(int fmt, int iAddDay) {
        String strFmt;
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, iAddDay);

        switch (fmt) {
        case DT_IFX_DATE:
            strFmt = "yyyy-MM-dd";
            break;
        case DT_IFX_FULL:
            strFmt = "yyyy-MM-dd'T'HH:mm:ss+480";
            break;
        case DT_FMT_DATE:
            strFmt = "yyyy-MM-dd";
            break;
        case DT_FMT_FULL:
            strFmt = "yyyy-MM-dd HH:mm:ss.SSS";
            break;
        case DT_DATE:
            strFmt = "yyyyMMdd";
            break;
        case DT_DATETIME:
            strFmt = "yyyyMMddHHmmss";
            break;
        case DT_TIME:
            strFmt = "HHmmss";
            break;
        case DT_LTIME:
            strFmt = "HHmmssSSS";
            break;
        case DT_FULL:
            strFmt = "yyyyMMddHHmmssSSS";
            break;
        default:
            strFmt = "yyyyMMddHHmmssSSS";
        }
        return ((new SimpleDateFormat(strFmt, Locale.getDefault())).format(date.getTime()));
    }

    public static String genAddDate(int iAddDay) {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, iAddDay);
        return (date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.DATE) + "T" + date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE) + ":"
                + date.get(Calendar.SECOND));
    }

    /**
     * 取得該週的第一天, 格式依傳入參數決定
     *
     * @param c
     *            日期
     * @param first
     *            0:第一天為星期天, 1:第一天為星期一
     * @param fmt
     *            傳入格式類別
     * @return 就系統時間咩
     * @see #DT_DATE
     * @see #DT_DATETIME
     * @see #DT_TIME
     * @see #DT_LTIME
     * @see #DT_FULL
     */
    public static String genFirstDate(Calendar cc, int first, int fmt) {
        String strFmt;
        Calendar c = (Calendar) cc.clone();
        int iDayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        if (first == 0) { // 第一天為星期天
            c.add(Calendar.DATE, -(iDayOfWeek - 1));
        } else { // 第一天為星期一
            if (iDayOfWeek == 1)
                iDayOfWeek += 7;
            c.add(Calendar.DATE, -(iDayOfWeek - 2));
        }

        switch (fmt) {
        case DT_IFX_DATE:
            strFmt = "yyyy-MM-dd";
            break;
        case DT_IFX_FULL:
            strFmt = "yyyy-MM-dd'T'HH:mm:ss+480";
            break;
        case DT_FMT_DATE:
            strFmt = "yyyy-MM-dd";
            break;
        case DT_FMT_FULL:
            strFmt = "yyyy-MM-dd HH:mm:ss.SSS";
            break;
        case DT_DATE:
            strFmt = "yyyyMMdd";
            break;
        case DT_DATETIME:
            strFmt = "yyyyMMddHHmmss";
            break;
        case DT_TIME:
            strFmt = "HHmmss";
            break;
        case DT_LTIME:
            strFmt = "HHmmssSSS";
            break;
        case DT_FULL:
            strFmt = "yyyyMMddHHmmssSSS";
            break;
        default:
            strFmt = "yyyyMMddHHmmssSSS";
        }
        return ((new SimpleDateFormat(strFmt, Locale.getDefault())).format(c.getTime()));
    }

    /**
     * 取得該週的最後一天, 格式依傳入參數決定
     *
     * @param cc
     *            日期
     * @param first
     *            0:第一天為星期天, 1:第一天為星期一
     * @param fmt
     *            傳入格式類別
     * @return 就系統時間咩
     * @see #DT_DATE
     * @see #DT_DATETIME
     * @see #DT_TIME
     * @see #DT_LTIME
     * @see #DT_FULL
     */
    public static String genLastDate(Calendar cc, int first, int fmt) {
        String strFmt;
        Calendar c = (Calendar) cc.clone();
        int iDayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        if (first == 0) { // 第一天為星期天
            c.add(Calendar.DATE, 7 - iDayOfWeek);
        } else { // 第一天為星期一
            if (iDayOfWeek == 1)
                iDayOfWeek += 7;
            c.add(Calendar.DATE, 8 - iDayOfWeek);
        }

        switch (fmt) {
        case DT_IFX_DATE:
            strFmt = "yyyy-MM-dd";
            break;
        case DT_IFX_FULL:
            strFmt = "yyyy-MM-dd'T'HH:mm:ss+480";
            break;
        case DT_FMT_DATE:
            strFmt = "yyyy-MM-dd";
            break;
        case DT_FMT_FULL:
            strFmt = "yyyy-MM-dd HH:mm:ss.SSS";
            break;
        case DT_DATE:
            strFmt = "yyyyMMdd";
            break;
        case DT_DATETIME:
            strFmt = "yyyyMMddHHmmss";
            break;
        case DT_TIME:
            strFmt = "HHmmss";
            break;
        case DT_LTIME:
            strFmt = "HHmmssSSS";
            break;
        case DT_FULL:
            strFmt = "yyyyMMddHHmmssSSS";
            break;
        default:
            strFmt = "yyyyMMddHHmmssSSS";
        }
        return ((new SimpleDateFormat(strFmt, Locale.getDefault())).format(c.getTime()));
    }

    /**
     * 取得系統時間, 格式=yyyyMMddhhmmssSSS
     */
    public static String genDateStr() {
        return (new SimpleDateFormat("yyyyMMddhhmmssSSS", Locale.getDefault())).format(new Date());
    }

    /**
     * 計算兩個時間差, 以傳入的時間格式回傳 (only support to ddhhmmssSSS)
     *
     * @param d1
     *            第一個時間點
     * @param d2
     *            第二個時間點
     * @param fmt
     *            時間格式, 參考 java.text.SimpleDateFormat
     */
    public static String diffDate(Date d1, Date d2, String fmt) {
        // 原寫法有bug ~~ 小時部分不對
        // return (new SimpleDateFormat(fmt)).format(new Date(d2.getTime() - d1.getTime()));

        long diff = d2.getTime() - d1.getTime();
        long SSS = 0, ss = 0, mm = 0, hh = 0, dd = 0;
        if (fmt.indexOf("ss") < 0) {
            SSS = diff;
        } else {
            SSS = diff % 1000;
            if (fmt.indexOf("mm") < 0) {
                ss = (diff / 1000);
            } else {
                ss = (diff / 1000) % 60;
                if (fmt.indexOf("hh") < 0) {
                    mm = (diff / (1000 * 60));
                } else {
                    mm = (diff / (1000 * 60)) % 60;
                    if (fmt.indexOf("dd") < 0) {
                        hh = (diff / (1000 * 60 * 60));
                    } else {
                        hh = (diff / (1000 * 60 * 60)) % 60;
                        dd = (diff / (1000 * 60 * 60 * 24)); // 最多到天就好
                    }
                }
            }
        }

        fmt = replace(fmt, "SSS", padZero((int) SSS, 3));
        fmt = replace(fmt, "ss", padZero((int) ss, 2));
        fmt = replace(fmt, "mm", padZero((int) mm, 2));
        fmt = replace(fmt, "hh", padZero((int) hh, 3));
        fmt = replace(fmt, "dd", padZero((int) dd, 3));
        return fmt;
    }

    public static String replace(String strSrc, String strTag, String strNew) {
        int idx = strSrc.indexOf(strTag);
        if (idx < 0)
            return strSrc;
        return strSrc.substring(0, idx) + strNew + strSrc.substring(idx + strTag.length());
    }

    /**
     * 固定長度前補零
     *
     * @deprecated 拼字拼錯了 ... >_<
     */
    public static String pendZero(String str, int len) {
        return padZero(str, len);
    }

    /**
     * 固定長度前補零
     *
     * @param str
     *            以 String 的方式傳入
     * @param len
     *            回傳字串長度
     */
    public static String padZero(String str, int len) {
        if (str.length() > len)
            return str; // 超過時還是不能給人家截掉呀 ... 2005.3.21
        // return "000000000000000000000000000000".substring(0, len - str.length()) + str;
        byte[] b = new byte[Math.max(0, len - str.getBytes().length)];
        Arrays.fill(b, "0".getBytes()[0]);
        return new String(b) + str;
    }

    /**
     * 固定長度前補零
     *
     * @deprecated 拼字拼錯了 ... >_<
     */
    public static String pendZero(int val, int len) {
        return padZero(val, len);
    }

    /**
     * 固定長度前補零
     *
     * @param val
     *            以 int 的方式傳入
     * @param len
     *            回傳字串長度 (max=30)
     */
    public static String padZero(int val, int len) {
        return padZero(Integer.toString(val), len);
    }

    public static String padding(String strSource, int len) {
        return padding(strSource, " ", len);
    }

    public static String padding(String strSource, String padding, int len) {
        byte[] b = new byte[Math.max(0, len - strSource.getBytes().length)];
        Arrays.fill(b, padding.getBytes()[0]);
        return strSource + new String(b);
    }

    /**
     * @deprecated 內容是空白 -_-
     */
    public static String encode(String s, String enc) {
        try {
            /*
             * InputStream in; OutputStream out; EncodingConverter ec = new EncodingConverter("Big5", enc); in = new ByteArrayInputStream(s.getBytes()); out = new ByteArrayOutputStream();
             * ec.convert(in, out);
             */
            /*
             * ByteArrayOutputStream out = new ByteArrayOutputStream(); OutputStreamWriter outSW = new OutputStreamWriter(new BufferedOutputStream(out), enc); outSW.write(s); outSW.flush();
             * System.out.println("outSW.getEncoding()=" + outSW.getEncoding()); //System.out.println("out.toString(" + enc + ")=" + out.toString(enc));
             */
            // return out.toString();
            // 上面這樣寫太囉嗦了 ...

            /*
             * 先不要理它嘿 ... 有點小問題 ... 91/1/7 return (new String(s.getBytes(enc)));
             */
            return s;
        } catch (Exception e) {
            // System.out.println(e.getMessage());
            return "[Error] Encode String Exception";
        }
    }

    /**
     * 執行傳入的字串指令.<br>
     * todo : 還要加上任何傳回值的功能
     *
     * @param strEval
     *            要執行的指令
     * @throws Exception
     *             whatever ...
     * @return String of evaluate result
     */
    public static String evaluate(String strEval) throws Exception {
        int MAX_PARAM = 10; // 暫定

        // System.out.println("[Util.evaluate()] strEval=" + strEval);
        // 1. Parsing method name n' parameters
        int idxBracketL = strEval.indexOf('(');
        int idxBracketR = strEval.lastIndexOf(')');
        String strMethod = strEval.substring(0, idxBracketL).trim();
        String strParam = strEval.substring(idxBracketL + 1, idxBracketR).trim();
        String[] strParams = new String[MAX_PARAM];
        int cntParam = 0;

        // System.out.println("[Util.evaluate()] strMethod=" + strMethod);
        // System.out.println("[Util.evaluate()] strParam=" + strParam);
        // 2. Parsing parameters to param[]
        for (cntParam = 0;; cntParam++) {
            if (strParam.length() == 0)
                break;
            int idxComma = strParam.indexOf(',');
            if (idxComma == (-1)) {
                // System.out.println("cntParam=" + cntParam + ", strParams.length=" + strParams.length);
                strParams[cntParam] = strParam.trim();
                strParam = "";
            } else {
                strParams[cntParam] = strParam.substring(0, idxComma).trim();
                strParam = strParam.substring(idxComma + 1).trim();
            }
            // System.out.println("[Util.evaluate()] strParams[" + cntParam + "]=[" + strParams[cntParam] + "]");
        }

        // 它始終沒有那麼聰明, 如果沒有參數 array[] 還是要設成 null
        Class classCustom = Class.forName("tw.com.citi.utils.Misc");
        Class[] classParam = (cntParam > 0) ? (new Class[cntParam]) : null;
        Object[] objParam = (cntParam > 0) ? (new Object[cntParam]) : null;
        for (int i = 0; i < cntParam; i++) {
            try {
                // 用參數陣列內的 name 取得 instance (Object)
                objParam[i] = classCustom.getField(strParams[i]).get(classCustom);
                // 用參數陣列內的 type 取得 Class
                classParam[i] = classCustom.getField(strParams[i]).getType();
            } catch (Exception e1) {
                try {
                    objParam[i] = Integer.getInteger(strParams[i]);
                    classParam[i] = Class.forName("java.lang.Integer");
                } catch (Exception e2) {
                    objParam[i] = strParams[i].substring(1, strParams[i].length() - 2);
                    classParam[i] = Class.forName("java.lang.String");
                }
            }
        }
        Method methodCustom = classCustom.getMethod(strMethod, classParam);

        return ((String) methodCustom.invoke(classCustom, objParam));
    }

    public static InputStream post(URL url, String str, Properties param, String encoding) throws IOException {
        URLConnection uc = url.openConnection();
        // 有加上 Request 參數
        if (param != null) {
            Enumeration reqParam = param.propertyNames();
            String reqName, reqValue;
            while (reqParam.hasMoreElements()) {
                reqName = reqParam.nextElement().toString();
                reqValue = param.getProperty(reqName);
                uc.setRequestProperty(reqName, reqValue);
                // System.out.println("[Post] add RequestProperty: "+reqName+"="+reqValue);
            }
        }
        uc.setDoOutput(true);
        uc.setDoInput(true);
        // String enc = (encoding==null) ? "UTF-8" : encoding;
        // OutputStreamWriter out = new OutputStreamWriter(uc.getOutputStream(), enc);
        OutputStream os = uc.getOutputStream();

        os.write(str.getBytes());
        os.close();
        return uc.getInputStream();
    }

    public static InputStream post(URL url, String str, Properties param) throws IOException {
        return post(url, str, param, null);
    }

    public static InputStream post(URL url, String str) throws IOException {
        return post(url, str, null);
    }

    public static InputStream post(URL url, StringBuffer buf) throws IOException {
        return post(url, buf.toString());
    }

    /**
     * 用檔名 Post URL, 回傳也是檔名(Response 存檔)
     *
     * @param url
     * @param fileName
     * @return response file name from POST
     * @throws Exception
     */
    public static String postFile(URL url, String fileName) throws Exception {
        // System.out.println("\n[Util.postFile.init] postFile(url:"+url+", fileName:"+fileName+")");
        BufferedReader brf = new BufferedReader(new FileReader(fileName));
        StringWriter sw = new StringWriter();
        String str1;
        while ((str1 = brf.readLine()) != null) {
            sw.write(str1);
        }
        brf.close();
        sw.flush();
        InputStream is = post(url, sw.getBuffer());
        sw.close();

        // Start geting response XML ...
        String tmpFileResponse = "/demo/tmp/" + genDateStr() + ".xml";
        PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(tmpFileResponse)));
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String str2;
        while ((str2 = br.readLine()) != null) {
            // System.out.println("[Util.postFile.writeForRecv] str2.trim().length()=" + str2.trim().length());
            if (str2.trim().length() != 0) {
                ps.println(str2);
            }
        }
        ps.flush();
        ps.close();
        br.close();
        is.close();

        return tmpFileResponse;
    }

    /**
     * 用 Stream Post URL, 回傳是字串, steal form Kevin
     *
     * @param urlTarget
     * @param isInput
     *            是 InputStream 喔 ...
     * @param pParams
     * @return response String from POST
     * @throws Exception
     */
    public static String post(URL urlTarget, InputStream isInput, Properties pParams) throws Exception {
        StringBuffer sbResult = new StringBuffer();
        // System.out.println("utils.Misc.post(): url="+urlTarget);

        HttpURLConnection urlcChannel = (HttpURLConnection) urlTarget.openConnection();
        // 有加上 Request 參數
        if (pParams != null) {
            Enumeration reqParam = pParams.propertyNames();
            String reqName, reqValue;
            while (reqParam.hasMoreElements()) {
                reqName = reqParam.nextElement().toString();
                reqValue = pParams.getProperty(reqName);
                urlcChannel.setRequestProperty(reqName, reqValue);
            }
        }
        // add for no cache (3 lines)
        urlcChannel.setRequestProperty("Pragma", "no-cache");
        urlcChannel.setRequestProperty("Cache-Control", "no-cache");
        urlcChannel.setUseCaches(false);

        urlcChannel.setDoOutput(true);
        urlcChannel.setDoInput(true);

        OutputStream osTo = urlcChannel.getOutputStream();
        byte barr[] = new byte[1460];
        int iRead = 0;

        while ((iRead = isInput.read(barr)) > 0) {
            osTo.write(barr, 0, iRead);
            // System.out.println("utils.Misc.post(): write Rq "+iRead+" bytes");
            osTo.flush();
        }
        osTo.close();

        InputStream isFrom = urlcChannel.getInputStream();
        while ((iRead = isFrom.read(barr)) > 0) {
            // System.out.println("utils.Misc.post(): read Rs "+iRead+" bytes");
            sbResult.append(new String(barr, 0, iRead));
        }

        return sbResult.toString();
    }

    public static String postString(URL urlTarget, String strPost, Properties param, String enc) throws Exception {
        if (enc == null) {
            return post(urlTarget, new ByteArrayInputStream(strPost.getBytes()), param);
        } else {
            return post(urlTarget, new ByteArrayInputStream(strPost.getBytes(enc)), param);
        }
    }

    public static String postString(URL urlTarget, String strPost, Properties pParams) throws Exception {
        return postString(urlTarget, strPost, pParams, null);
    }

    public static String postString(URL urlTarget, String strPost) throws Exception {
        return postString(urlTarget, strPost, null);
    }

    /**
     * 把一個檔案內容全部讀出來輸出成字串 update : 2004.11.30, add close() x 2, AXL
     *
     * @param fileName
     *            檔案名稱
     * @return 檔案內容
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static String readStringFromFile(String fileName) throws IOException, FileNotFoundException {
        BufferedReader in;
        String strLine, strAllFile = "";
        FileReader fr = new FileReader(fileName);
        in = new BufferedReader(fr);
        while ((strLine = in.readLine()) != null) {
            strAllFile = strAllFile + strLine;
        }
        in.close();
        fr.close();
        return strAllFile;
    }

    public static boolean isEmpty(String s) {
        return ((s == null) || (s.trim().length() == 0));
    }

    /**
     * 在一個字串中, 置換掉固定的子字串
     *
     * @param dataSrc
     *            來源字串
     * @param stringSpec
     *            要換掉的子字串
     * @param stringRepl
     *            要換成的子字串
     * @return 置換後的結果
     */
    public static String replSpecString(String dataSrc, String stringSpec, String stringRepl) {
        boolean proceeded = true;
        while (proceeded) {
            int itemPos = dataSrc.indexOf(stringSpec, 0);
            if (itemPos != -1)
                dataSrc = dataSrc.substring(0, itemPos) + stringRepl + dataSrc.substring(itemPos + stringSpec.length());
            else
                proceeded = false;
        }
        return dataSrc;
    }

    /**
     * 複製檔案, steal from Kevin
     *
     * @param sSrc
     *            來源檔名
     * @param sDest
     *            目的檔名
     * @return {@link #SUCCESS}, {@link #FAILURE}, {@link #FILE_NOT_FOUND}
     */
    public static int copyFile(String sSrc, String sDest) {
        int iResult = FAILURE;

        // Create dir if not exists, add by AXL 2002.9.27
        File fDest = new File(sDest);
        File fDestDir = new File(fDest.getParent());
        if (!(fDestDir.exists())) {
            fDestDir.mkdirs();
        }

        FileInputStream fisSrc = null;
        FileOutputStream fosDest = null;
        try {
            fisSrc = new FileInputStream(sSrc);
            fosDest = new FileOutputStream(sDest);

            byte[] barrBuf = new byte[STND_BUFFER_SIZE];
            int iReadLen = 0;

            while ((iReadLen = fisSrc.read(barrBuf)) > 0)
                fosDest.write(barrBuf, 0, iReadLen);

            iResult = SUCCESS;
        } catch (FileNotFoundException e) {
            iResult = FILE_NOT_FOUND;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fisSrc != null)
                    fisSrc.close();
                if (fosDest != null)
                    fosDest.close();
            } catch (IOException e) {
            }
        }
        return iResult;
    }

    /**
     * 刪除檔案, steal from Kevin
     *
     * @param sFileName
     *            檔名, of course
     * @return {@link #SUCCESS}, {@link #FAILURE}, {@link #FILE_NOT_FOUND}
     */
    public static int deleteFile(String sFileName) {
        int iResult = FILE_NOT_FOUND;
        File f = new File(sFileName);

        if (f.exists()) {
            if (f.delete())
                iResult = SUCCESS;
            else
                iResult = FAILURE;
        }
        return iResult;
    }

    /**
     * 變更檔案名稱, steal from Kevin
     *
     * @param sSrc
     *            原始檔名
     * @param sDest
     *            新檔名
     * @return {@link #SUCCESS}, {@link #FAILURE}, {@link #FILE_NOT_FOUND}
     */
    public static int renameFile(String sSrc, String sDest) {
        int iResult = FILE_NOT_FOUND;
        if ((iResult = copyFile(sSrc, sDest)) == SUCCESS) {
            iResult = deleteFile(sSrc);
        }
        return iResult;
    }

    /**
     * 以某個路徑為起點, 刪除含子目錄下的所有檔案, 可以加 Fillter
     *
     * @param sPath
     *            開始路徑
     * @param fillter
     *            過濾副檔名, {@link DirList}
     */
    public static void deleteFiles(String sPath, String fillter) {
        Vector vDelList = new DirList(sPath, fillter).getAllFiles();
        for (int i = 0; i < vDelList.size(); i++) {
            deleteFile((String) vDelList.get(i));
        }
    }

    /**
     * 西元年轉中國年 YYYYMMDD -> YYMMDD
     *
     * @param strDateDC
     */
    public static String transDCDateToCC(String strDateDC) {
        String strDateCC = "";

        try {
            if (strDateDC.length() < 4)
                return strDateCC; // 你亂傳我亂回 !!
            int year = Integer.parseInt(strDateDC.substring(0, 4));
            strDateCC = Integer.toString(year - 1911) + strDateDC.substring(4);
        } catch (Exception e) {
        }
        return strDateCC;
    }

    /**
     * Read file content into byte[]
     *
     * @param fileName
     *            - file name to be read
     */
    public static byte[] readFile(String fileName) throws IOException {
        // Read file content
        FileInputStream in = new FileInputStream(fileName);
        byte[] keyData = new byte[in.available()];
        in.read(keyData);
        in.close();
        return keyData;
    }

    /**
     * Write data byte[] into file
     *
     * @param fileName
     *            - file name to be read data - data byte[] for writ to
     */
    public static void writeFile(String fileName, byte[] data) throws IOException {
        // Write byte[] to the file.
        FileOutputStream out = new FileOutputStream(fileName);
        out.write(data);
        out.close();
    }

    /**
     * Binary byte[] to Heximal byte[]
     *
     * @param bin
     *            - data in binary for convert
     */
    public static byte[] bin2Hex(byte[] bin) {
        // Allocate byte[] for return
        byte[] hex = new byte[2 * bin.length];

        // Convert one binary byte to two heximal bytes
        for (int i = 0, j = 0; i < bin.length; i++, j += 2) {
            int iByte = bin[i];
            if (iByte < 0)
                iByte += 256;
            // First byte
            int i4Bit = iByte >> 4;
            if (i4Bit < 10)
                hex[j] = (byte) ('0' + i4Bit);
            else
                hex[j] = (byte) ('A' + i4Bit - 10);
            // Second byte
            i4Bit = iByte & 0x000F;
            if (i4Bit < 10)
                hex[j + 1] = (byte) ('0' + i4Bit);
            else
                hex[j + 1] = (byte) ('A' + i4Bit - 10);
        }

        // Return the heximal byte[]
        return hex;
    }

    public static byte[] bin2Hex(byte[] bin, int offset, int length) {
        // Allocate byte[] for return
        byte[] hex = new byte[2 * length];

        // Convert one binary byte to two heximal bytes
        for (int i = 0, j = 0; i < length; i++, j += 2) {
            int iByte = bin[offset + i];
            if (iByte < 0)
                iByte += 256;
            // First byte
            int i4Bit = iByte >> 4;
            if (i4Bit < 10)
                hex[j] = (byte) ('0' + i4Bit);
            else
                hex[j] = (byte) ('A' + i4Bit - 10);
            // Second byte
            i4Bit = iByte & 0x000F;
            if (i4Bit < 10)
                hex[j + 1] = (byte) ('0' + i4Bit);
            else
                hex[j + 1] = (byte) ('A' + i4Bit - 10);
        }

        // Return the heximal byte[]
        return hex;
    }

    /**
     * Array Copy with Little-Endian
     *
     * @param src
     *            - the source array.
     * @param srcPos
     *            - starting position in the source array.
     * @param dest
     *            - the destination array.
     * @param destPos
     *            - starting position in the destination data.
     * @param length
     *            - the number of array elements to be copied.
     */
    public static void arraycopyLE(byte[] src, int srcPos, byte[] dest, int destPos, int length) {
        for (int i = 0; i < length; i++) {
            dest[destPos + i] = src[srcPos + length - 1 - i];
        }
        return;
    }

    /**
     * Returns a byte array representation of the integer argument as an unsigned integer in base 16
     *
     * @param value
     *            - the unsigned integer.
     * @param length
     *            - byte array length.
     */
    public static byte[] int2HexByteArray(int value, int length) {
        return int2HexByteArray(Integer.toHexString(value), length);
    }

    public static byte[] int2HexByteArray(long value, int length) {
        return int2HexByteArray(Long.toHexString(value), length);
    }

    private static byte[] int2HexByteArray(String sHexString, int length) {
        char[] caZero = new char[2 * length - sHexString.length()];
        Arrays.fill(caZero, '0');
        return hex2Bin((new String(caZero) + sHexString).toUpperCase().getBytes());
    }

    /**
     * Heximal byte[] to binary byte[]
     *
     * @param hex
     *            - data in heximal for convert
     */
    public static byte[] hex2Bin(byte[] hex) {
        // Allocate byte[] for return
        byte[] bin = new byte[hex.length / 2];

        // Convert two heximal bytes to one binary byte
        for (int i = 0, j = 0; i < bin.length; i++, j += 2) {
            // First byte
            int iL = hex[j] - '0';
            if (iL > 9)
                iL -= 7;
            iL <<= 4;
            // Second byte
            int iR = hex[j + 1] - '0';
            if (iR > 9)
                iR -= 7;
            bin[i] = (byte) (iL | iR);
        }

        // Return the binary byte[]
        return bin;
    }

    /**
     * Read Dir files to String[]
     *
     * @param dir
     *            - data path for read file
     */
    public static ArrayList dirList(String dir, String filter) throws Exception {
        System.out.println("filter=" + filter);
        ArrayList list = new ArrayList();
        File f = new File(dir);
        if (f.isDirectory()) {
            Pattern pattern = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);
            String[] s = f.list();
            for (int i = 0; i < s.length; i++) {
                File f1 = new File(dir + "/" + s[i]);
                // System.out.println(s[i] + ", " + f1.isFile());
                if (f1.isFile()) {
                    // 低級寫法....
                    // if(s[i].endsWith(filter)) list.add(s[i]);

                    // 高級寫法, user regular expression (正規表示式)
                    Matcher m = pattern.matcher(s[i]);
                    if (m.find()) {
                        // System.out.println("m.find one=" + m.group(0));
                        list.add(s[i]);
                    }
                }
            }
        }
        return list;
    }

    public static String divDot(String name) {

        int i = name.indexOf(".");
        return name.substring(0, i);
    }

    /**
     * @param string
     *            dir (done dir)
     * @param string
     *            move file name(done file)
     */
    public static void moveFile(String sourceDir, String targetDir, String file) throws Exception {

        File tDir = new File(targetDir);
        if (!tDir.exists()) {
            if (!tDir.mkdir())
                return; // create folder fail , do nothing...
        }
        if (tDir.isDirectory()) {

            FileInputStream fi = new FileInputStream(sourceDir + "/" + file);
            FileOutputStream fo = new FileOutputStream(targetDir + "/" + file);
            byte[] readFile = new byte[fi.available()];
            fi.read(readFile);
            fo.write(readFile);
            fi.close();
            fo.close();
            File delFile = new File(sourceDir + "/" + file);
            if (delFile.exists()) {
                delFile.delete();
                // System.out.println("del ok !!");
            }
        }
    }

    /**
     * @param string
     *            dir (done dir)
     * @param string
     *            move file name(done file)
     */
    public static void moveFile(String sourceDir, String targetDir, String file, String targetFile) throws Exception {

        File tDir = new File(targetDir);
        if (!tDir.exists()) {
            if (!tDir.mkdir())
                return; // create folder fail , do nothing...
        }
        if (tDir.isDirectory()) {

            FileInputStream fi = new FileInputStream(sourceDir + "/" + file);
            FileOutputStream fo = new FileOutputStream(targetDir + "/" + targetFile);
            byte[] readFile = new byte[fi.available()];
            fi.read(readFile);
            fo.write(readFile);
            fi.close();
            fo.close();
            File delFile = new File(sourceDir + "/" + file);
            if (delFile.exists()) {
                delFile.delete();
                // System.out.println("del ok !!");
            }
        }
    }

    /**
     * check if file exist ...
     *
     * @param filename
     * @return
     */
    public static boolean fileExist(String filename) {
        File f = new File(filename);
        return f.exists();
    }

    /**
     * 將字串依照轉換表重新排過
     *
     * @param table
     *            轉換表
     * @param data
     *            欲轉換的資料
     */
    public static String transfer(byte[] table, String data) throws Exception {
        byte[] in = data.getBytes();
        byte[] out = data.getBytes();

        for (int i = 0; i < table.length; i++) {
            out[table[i] - 1] = in[i];
        }

        return (new String(out));
    }

    /**
     * 將字串依照轉換表轉回原字串
     *
     * @param table
     *            轉換表
     * @param data
     *            欲轉換的資料
     */
    public static String recover(byte[] table, String data) throws Exception {
        byte[] in = data.getBytes();
        byte[] out = data.getBytes();

        for (int i = 0; i < table.length; i++) {
            out[i] = in[table[i] - 1];
        }

        return (new String(out));
    }
}
