/*
 * COLAConstatants.java
 *
 * Copyright (c) 2009-2014 International Integrated System, Inc.
 * All Rights Reserved.
 *
 * Licensed Materials - Property of International Integrated System, Inc.
 *
 * This software is confidential and proprietary information of
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.iisigroup.colabase.webatm.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iisigroup.cap.constants.Constants;

/**
 * <pre>
 * For COLA Constants.
 * </pre>
 *
 * @author Sunkist Wang
 * @version <ul>
 *          <li>2014/1/17,Sunkist Wang,new
 *          <li>2014/2/6,Sunkist Wang,add CC_VALUES_SEPARATOR
 *          <li>2014/6/4,Sunkist Wang,add VA_BASE_URL,VA_TIMEOUT_IN_MS
 *          <li>2016/2/23,TimChiang,change TIME_OUT & MAX_FILE_COUNT >> XSL_TIME_OUT & XSL_MAX_FILE_COUNT
 *          <li>2016/7/16,AndyChen,change TIME_OUT >> CLM_TIME_OUT
 *          </ul>
 * @since 2014/1/17
 */
public interface CCConstants extends Constants {
  public static final String P_FORM_ACTION = "formAction";
  public static final String C_AUDITLOG_START_TS = "_AuditLogTS";
  public static final String S_ATTR_ERR_MSG = "ERRMSG";
  public static final String GZIP = "gzip";
  public static final String FULL_COMMA = "，";
  public static final String CC_VALUES_SEPARATOR = COMMA;
  public static final String XSL_DUMMY_AGENTCODE_GENERAL = "generalCode";
  public static final String XSL_DUMMY_AGENTCODE_BUS = "BUScode";
  public static final String XSL_DUMMY_AGENTCODE_DS = "DScode";
  public static final String XSL_DUMMY_AGENTCODE_TM = "TMcode";
  public static final String XSL_BARCODE_FOLDER = "Barcode_Temp";

  public static final String GENERAL_SCRIPT = "GENERAL_SCRIPT";
  public static final String POP_UPS = "POP_UPS";
  public static final String CBOL_HOST = "AWATM_CBOL_HOST";
  public static final String UPLOAD_FILE_LOCATION = "uploadFileLocation";
  public static final String IMAGE_FILE_LOCATION = "imageFileLocation";
  public static final String CSS_FILE_LOCATION = "cssFileLocation";
  public static final String TEMP_FILE_LOCATION = "tempFileLocation";
  public static final String MOICA_TEMP_FILE_LOCATION = "moicaTempFileLocation";
  public static final String EMAIL_FILE_LOCATION = "emailFileLocation";
  public static final String TIME_OUT = "TIME_OUT";
  public static final String MAX_FILE_COUNT = "DUL_MAX_FILE_COUNT";
  public static final String MAX_FILE_SIZE = "DUL_MAX_FILE_SIZE";
  public static final String VA_BASE_URL = "VA_BASE_URL";
  public static final String VA_TIMEOUT_IN_MS = "VA_TIMEOUT_IN_MS";
  public static final String DATA_IMAGE_BASE64 = "data:image;base64,";
  public static final String ATTR_REDIRECT = "_ar";
  public static final String MODIFY_COL_PREFIX = "diff_";
  public static final String COLLAPSE_THREE_STEP = "COLLAPSE_THREE_STEP";
  public static final String MOICA_ENABLE = "MOICA_ENABLE";
  public static final String SHORT_FORM_PATH = "SHORT_FORM_PATH";
  public static final String ORG_REQ = "ORG_REQ";
  public static final String REF_NO = "REF_NO";
  public static final String APPL_REMIND_PHONE = "A_Remind_Ph";
  public static final String APPL_REMIND_FEE = "A_Remind_Fe";
  public static final String FOOTER_IKEY = "FooterKey";
  public final static String SESSION_UPLOAD_FILE_PATH = "zipFilePath";
  public final static String SESSION_ZIPFILE_NAME = "zipFileName";
  public static final String ATTR_VERIFYCUST_FLAG = "ATTR_VERIFYCUST_FLAG";
  public static final String HAS_COMPLETE_UPLOAD = "HAS_COMPLETE_UPLOAD";
  public static final String SESSION_FOLDER_BATCHTIME = "SESSION_FOLDER_BATCHTIME";
  public static final String SESSION_FOLDER_WORKINGDAY = "SESSION_FOLDER_WORKINGDAY";
  public static final String SESSION_PDF_FILE_PATH = "SESSION_PDF_FILE_PATH";
  public static final String SESSION_WORKINGDAY_BARCODE = "SESSION_WORKINGDAY_BARCODE";
  public static final String COMPLETE_FOLDER_NAME = "completedApp/MOICA";
  public static final String INCOMPLETE_FOLDER_NAME = "Sales_support/MOICA";
  public static final String NTCHASUPLOAD = "new_to_card_has_upload";
  public static final String SESSION_IDNO = "ino";
  public static final String FILES_UUID = "FILES_UUID";


  public static final String SESSION_BOD = "bod";
  public static final String SESSION_MEDIACODE = "MEDIA_CODE";
  public static final String SESSION_PAGE_NO = "pageNo";
  public static final String SESSION_ISRESELECT = "isReselect";
  public static final String SESSION_ALLOWLOGPAGE = "SESSION_ALLOWLOGPAGE";
  public static final String SESSION_HASPROCESS_PDF = "SESSION_HASPROCESS_PDF";
  public static final String SESSION_PDF_HASH = "SESSION_PDF_HASH";
  public static final String SESSION_VA_HAS_PASS = "SESSION_VA_HAS_PASS";
  public static final String SESSION_CHECKMOICA_HAS_PASS = "SESSION_CHECKMOICA_HAS_PASS";
  public static final String SESSION_FILE_UUID = "SESSION_FILE_UUID";
  public static final String SESSION_TEMP_FILE_FOLDER = "SESSION_TEMP_FILE_FOLDER";
  public static final String SESSION_EDM_ATTACHMENT_PATH = "SESSION_EDM_ATTACHMENT_PATH";
  /**
   * 後端資料要塞給前端 reqJSON 的 map
   */
  public static final String SESSION_TO_REQJSON_MAP = "toReqJsonMap";
  
  /** 是否允許訪問下一頁 flag */
  public static final String SESSION_CAN_ACCESS_TO_NEXTPAGE = "canAccessToNextPage";
  /** 是否為重導訪問頁面 flag */
  public static final String SESSION_IS_REDIRECT_TO_PAGE = "isRedirectToPage";
  /** 目前訪問的頁面代碼 */
  public static final String SESSION_LAST_ACCESS_PAGENO = "lastAccessPageNo";

  /**
   * AWATM Eblank flag
   */

  public static final String SEARCH_TYPE_TWD = "TWD";
  public static final String SEARCH_TYPE_USD = "USD";
  public static final String SEARCH_TYPE_XAU = "XAU";

  public static final String SELECTED_INDEX = "selectedIndex";
  public static final String ACTION_TYPE = "actionType";
  public static final String INIT_DETAIL_TYPE = "type";
 

  /**
   * AWATM ATM flag
   */
  // General Constants
  public static String LOCAL_BANKID = "010";
  // SESSION_ATTRIB 的內容『lib.login』將被所有 jsp 使用，不可輕換！
  public static final String SESSION_ATTRIB = "lib.login"; // APLogin seeion attribute name
  public static final String SESSION_LOG_OUT = "lib.logout"; // APLogout seeion attribute name

  /**
   * 與HSM WebService 交易之參數
   */
  public static String WSS_SYS_ID = "eATM";
  public static String WSS_CHK_CODE = "eATMCheckCode";
  public static String WSS_CHK_USR = "eatmuser";


  public static String RAND_KEYPAD_LIST = "randKeypadList";
  public static String SELECT_TYPE  = "select_type";

  /**
   * 與BAFES 交易通訊KEY
   */
  public static String COMM_KEY_TYPE = "2";


  public final static String SP_Cmd_01 = "SP_System_sel";
  public final static String SP_Cmd_02 = "SP_SysParm_sel_SrvTranData";
  public final static String SP_Cmd_03 = "SP_TableAlter_sel_all";
  public final static String SP_Cmd_04 = "SP_TableAlter_upd_status";
  public final static String SP_Cmd_05 = "SP_ActionCode_sel_all";
  public final static String SP_Cmd_06 = "SP_TmlInfo_sel_all";


  /**
   * 如果是如果是IBM的Websphere 則收中文時要用URLDecoder
   * 0:IBM Webphere
   * 1:Other AP
   **/
  public static final String CONST_AP_TYPE_WEBSPHERE = "0";
  public static final String CONST_AP_TYPE_OTHER = "1";

  // Private properities for service
  public static String sysDir = null;

  public static String sysName = null;
  public static int sendRetry = 0;
  public static String domainName = null;
  public static String ap_type = null;
  public static String images = null;
  public static String image_root = null;
  public static String jsfile = null;
  public static String jsfile_root = null;
  public static String system_root = null;
  public static String server_no = null;
  public static String mail_path = null;
  public static String objfile = null;
  /**
   * READER_TYPE
   */
  public static final int READER_TYPE_DEFAULT = 1;
  public static final int READER_TYPE_RANGE_MIN = -1;
  public static final int READER_TYPE_RANGE_MAX = 99;
  public static final int READER_TYPE_ALARM = 2;
  public static final int READER_TYPE_KEYPAD = 3;
  public static final int READER_TYPE_KEYPAD_II = 4;

  public static final int READER_TXN_DEFAULT = 0;
  public static final int READER_TXN_RANGE_MIN = -1;
  public static final int READER_TXN_RANGE_MAX = 99;

  public static final int READER_TXN_SET_DUMMY = 1;

  /**
   * This is used in various environmental settings.
   */
  public static final String JVM_VAR_RMI_HOSTNAME = "rmi.config.trns.hostname";
  public static final String JVM_VAR_RMI_PORT = "rmi.config.trns.port";
  //DBSchema
  // 全域常數：就是任何 TABLE  都一樣的啦
  public static int GL_BANKID_LEN = 3;
  public static int GL_READERNAME_LEN = 50;
  public static int GL_READERTYPE_LEN = 4;

  // 定義各 TABLE 的相關資訊（程式有用到的才填.. After V2.0）
  public static int TB_BANKINFO_BANKID_LEN = GL_BANKID_LEN;
  public static int TB_BANKINFO_BANKNAME_LEN = 100;
  public static int TB_BANKINFO_BANKSTTS_LEN = 1;

  public static int TB_BANKTYPE_BANKID_LEN = GL_BANKID_LEN;
  public static int TB_BANKTYPE_BANKTYPE_LEN = 1;

  public static int TB_READERTYPE_NAME_LEN = GL_READERNAME_LEN;
  public static int TB_READERTYPE_READERTYPE_LEN = GL_READERTYPE_LEN;
  public static int TB_READERTYPE_ATMTYPE_LEN = GL_READERTYPE_LEN;

  public static int TB_READERPROC_READERTYPE_LEN = GL_READERTYPE_LEN;
  public static int TB_READERPROC_PCODE_LEN = 6;
  public static int TB_READERPROC_TXNTYPE = 4;

  public static String TB_ReaderType_NAME = "ReaderType";
  public static String TB_ReaderProc_NAME = "ReaderProc";
  public static String TB_BankInfo_NAME = "BankInfo";
  public static String TB_SysCode_NAME = "SysCode";
  public static String TB_SysParm_NAME = "SysParm";
  public static String TB_ActCode_NAME = "ActionCode";

  public final static String GetHostTime_MsgCode = Integer.toHexString(31077);//"7965"; //31077 -> hex
  public final static String HostIntroductory_MsgCode = Integer.toHexString(4101);//"4101";
  public final static String HostEncryptionKeyLoad_MsgCode = Integer.toHexString(4801);//"4801";
  public final static String HostEndKeyLoad_MsgCode = Integer.toHexString(4802);//"4802";
  public final static String EncryptionKeyRequest_MsgCode = Integer.toHexString(4105);//"4105";
  public final static String StartKeyExchangeRequest_MsgCode = Integer.toHexString(4805);//"4805";
  public final static String BalanceInquiry_MsgCode = Integer.toHexString(4501);//="4501";
  public final static String InterCitiPaymentTransfer_MsgCode = Integer.toHexString(4411);//="4411";
  public final static String InterCitiPaymentTransferReversal_MsgCode = Integer.toHexString(4412);//="4412";
  public final static String PaymentExternalTransfer_MsgCode = Integer.toHexString(4404);//="4404";

  public final static String InterCitiPinChange_MsgCode = Integer.toHexString(4113);//="4113";
  public final static String InterCitiPinChangeReversal_MsgCode = Integer.toHexString(4118);//="4118";


  /**
   * <pre>
   * CheckPageFilter 管理頁面
   * </pre>
   *
   * @author Tse-Hsien Chiang
   * @version <ul>
   *          <li>2016年10月25日, Tse-Hsien Chiang: 新建
   *          </ul>
   * @since 2016年10月25日
   */
  public static enum AWATMControlPages {
    index("index", "index", new String[]{}), 
    //匯率查詢
    rate_exchange("rate_exchange", "exc1", new String[]{}), 
    show_exchange("show_exchange", "exc2", new String[]{"exc1","exc2"}), 
    //利率查詢
    rate_interest("rate_interest", "interest1", new String[]{}), 
    init_interest("init_interest", "interest2", new String[]{"interest1","interest2","interest3"}), 
    productdetail("productdetail", "interest3", new String[]{"interest2","interest3"}), 
    //登入頁
    login("login", "login", new String[]{}), 
    signon("signon", "signon", new String[]{"login","signon"}),
    //網站導覽
    sitemap("sitemap","map", new String[]{}),
    //餘額查詢
    balance("balance", "bal1", new String[]{"signon","tran1","tran2","tran3","tran4","tran5","bal1","bal2","bal3","bal4"}), 
    balance01("balance01", "bal2", new String[]{"bal1","bal2"}), 
    balance02("balance02", "bal3", new String[]{"bal2","bal3"}), 
    balance03("balance03", "bal4", new String[]{"bal3","bal4"}), 
    //轉帳
    transfer("transfer", "tran1", new String[]{"signon","tran1","tran2","tran3","tran4","tran5","bal1","bal2","bal3","bal4"}),
    transfer01("transfer01", "tran2", new String[]{"tran1","tran2"}),
    transfer02("transfer02", "tran3", new String[]{"tran2","tran3"}),
    transfer03("transfer03", "tran4", new String[]{"tran3","tran4"}),
    transfer04("transfer04", "tran5", new String[]{"tran4","tran5"});
    private final String pagePathName;
    private final String pageNo;
    private final List<String> allowAccessFromPageNos;

    private AWATMControlPages(String pagePathName, String pageNo, String[] allowAccessFromPageNos) {
      this.pagePathName = pagePathName;
      this.pageNo = pageNo;
      this.allowAccessFromPageNos = Arrays.asList(allowAccessFromPageNos);
    }

    /**
     * 頁面路徑名稱是否相同
     *
     * @param pagePathName 頁面路徑名稱
     * @return 布林
     */
    public boolean checkPagePathNameMatch(String pagePathName) {
      return this.pagePathName.equalsIgnoreCase(pagePathName);
    }

    /**
     * 頁面代碼是否相同
     *
     * @param pageNo 頁面代碼
     * @return 布林
     */
    public boolean checkPageNoMatch(String pageNo) {
      return this.pageNo.equalsIgnoreCase(pageNo);
    }

    /**
     * 取得頁面代碼
     *
     * @return 頁面代碼
     */
    public String getPageNo() {
      return pageNo;
    }

    /**
     * 是否允許來自任何頁面訪問
     *
     * @return 布林
     */
    public boolean isAnyFromPageNoAllowToAccess() {
      return allowAccessFromPageNos.isEmpty();
    }

    /**
     * 檢查來自頁面代碼是否允許訪問本頁
     *
     * @param fromPageNo 來自頁面代碼
     * @return 布林
     */
    public boolean checkFromPageNoForAllowAccess(String fromPageNo) {
      for (String pageNo : allowAccessFromPageNos) {
        if (pageNo.equalsIgnoreCase(fromPageNo)) {
          return true;
        }
      }
      return false;
    }

  }

  /**
   * 側邊欄 Logo & gift 版型
   */
  public static final String SIDEBAR_LOGO = "SideBar_Logo";
  public static final String SIDEBAR_GIFT = "SideBar_Gift";

  /**
   * 2016/1/25,Tim,update for PCL2VA,M3(get method change to post method) use POST method would create another session after submit to (CrossDomain)Step1 page, so send OTP at step1 page
   */
  public static final String CORS_DOMAIN_URL = "CORS_DOMAIN_URL";

  /**
   * develop test
   */
  public static final String DEVELOP_TEST = "DEVELOP_TEST";
  public static final String DEVELOP_OTP_TEST = "DEVELOP_OTP_TEST";
  public static final String DEVELOP_MOICA_TEST = "DEVELOP_MOICA_TEST";

  /**
   * <pre>
   * TODO Write a short description on the purpose of the program
   * </pre>
   *
   * @author Roger Lin
   * @version <ul>
   *          <li>2016-02-18,Roger Lin,new
   *          <li>2016-04-01,Bo-Xaun Fan,add ReturnDoc Type
   *          </ul>
   * @since 2016-02-18
   */
  public enum SystemType {
    MOICA_OPEN_ACCOUNT("MOM"), MOICA_RETURN_DOC("MOM_RETURN_DOC"), COLA("COLA"), NEWMOICACARD("MOICACARD");

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

  public enum PCLchannelCode {
    //2016,01,11 Andy 新增ACT channelCode
    //2016,12,28 Richard 新增CFTI channelCode
    PA("PA"), AM("AM"), BUS("BUS"), CTP("CTP"), ML("MORTGAGE"), ACT("ACT"), CFTI("CFTI");

    private String code;

    private PCLchannelCode(String code) {
      this.code = code;
    }

    public String getCode() {
      return this.code;
    }
  }

  public enum salesChannel {
    DS, BUS, TM, STP
  }

  public enum MediaCode {

    PullMarketing("001", CCConstants.XSL_DUMMY_AGENTCODE_GENERAL), Internet("002", CCConstants.XSL_DUMMY_AGENTCODE_GENERAL), /* 客戶自行從網站連結進來，內頁判斷邏輯和Pull Marketing一樣 */
    TM("003", CCConstants.XSL_DUMMY_AGENTCODE_TM), /* 內頁判斷邏輯和BUS、S&D一樣 */
    BUS("004", CCConstants.XSL_DUMMY_AGENTCODE_BUS), DS("005", CCConstants.XSL_DUMMY_AGENTCODE_DS);

    private String code;
    private String dummyAgentCode;

    MediaCode(String code, String dummyCode) {
      this.code = code;
      this.dummyAgentCode = dummyCode;
    }

    public String getCode() {
      return this.code;
    }

    public String getDummyAgentCode() {
      return this.dummyAgentCode;
    }
  }

  public enum CardTypeIDEnum {
    /**
     * HappyGo卡
     */
    HPG("HAPPYGO"),
    /**
     * 環旅卡
     */
    PM("PM"),
    /**
     * 超級紅利回饋卡
     */
    REWARDS("REWARDS"),
    /**
     * 饗樂卡
     */
    TNE("TNE"),
    /**
     * prestige
     */
    PRESTIGE("PRESTIGE");

    private String code;

    private CardTypeIDEnum(String code) {
      this.code = code;
    }

    public String getCode() {
      return this.code;
    }
  }

  public enum CardTypeArEnum {
    /**
     * 饗樂卡
     */
    TNE("TNE_GPP"),
    /**
     * 環旅卡
     */
    PM("ASI_TYPE", "ASI_NO", "PM_GPP"),
    /**
     * 超級紅利回饋卡
     */
    REWARDS("SPENDING_TYPE"),
    /**
     * HappyGo卡
     */
    HPG("ALO_BAL"),
    /**
     * 饗樂+環旅+超紅+HappyGo 卡
     */
    ALL("TNE_GPP", "ASI_TYPE", "ASI_NO", "PM_GPP", "SPENDING_TYPE", "ALO_BAL");

    private String[] code;

    public String[] getCode() {
      return code;
    }

    private CardTypeArEnum(String... code) {
      this.code = code;
    }

  }

  public enum CCActionTypeEnum {
    Add, Update, Delete, Query, Export, Import;
  }

  public enum ContextTypeEnum {
    text("text/html"),
    pdf("application/pdf"),
    doc("application/msword"),
    UNKNOW("application/octet-stream"),
    xls("application/vnd.ms-excel"),
    jpg("image/jpeg"),
    tif("image/tiff"),
    gif("image/gif"),
    png("image/png"),
    IMAGE("image/.*");

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

  public enum CCLogStateEnum {
    MODIFY, COMPLETE;

    public int getCode() {
      return this.ordinal();
    }
  }

  public enum SSOStatusEnum {
    STATUS_0("0"), STATUS_2("2"), STATUS_A_PLUS("A+"), STATUS_U_PLUS("U+"), STATUS_D_PLUS("D+"), STATUS_9("9");

    private String code;

    private SSOStatusEnum(String code) {
      this.code = code;
    }

    public String getCode() {
      return this.code;
    }
  }
}
