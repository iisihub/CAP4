
package com.iisigroup.colabase.webatm.service.impl;

import static com.iisigroup.colabase.webatm.common.CCConstants.COMM_KEY_TYPE;
import static com.iisigroup.colabase.webatm.common.CCConstants.JVM_VAR_RMI_HOSTNAME;
import static com.iisigroup.colabase.webatm.common.CCConstants.JVM_VAR_RMI_PORT;
import static com.iisigroup.colabase.webatm.common.CCConstants.SP_Cmd_01;
import static com.iisigroup.colabase.webatm.common.CCConstants.SP_Cmd_02;
import static com.iisigroup.colabase.webatm.common.CCConstants.SP_Cmd_03;
import static com.iisigroup.colabase.webatm.common.CCConstants.SP_Cmd_04;
import static com.iisigroup.colabase.webatm.common.CCConstants.SP_Cmd_06;
import static com.iisigroup.colabase.webatm.common.CCConstants.TB_ActCode_NAME;
import static com.iisigroup.colabase.webatm.common.CCConstants.TB_BankInfo_NAME;
import static com.iisigroup.colabase.webatm.common.CCConstants.TB_ReaderProc_NAME;
import static com.iisigroup.colabase.webatm.common.CCConstants.TB_ReaderType_NAME;
import static com.iisigroup.colabase.webatm.common.CCConstants.TB_SysCode_NAME;
import static com.iisigroup.colabase.webatm.common.CCConstants.TB_SysParm_NAME;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Timer;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citi.webatm.rmi.GenericSQLMgr;
import com.citi.webatm.rmi.TActionCode;
import com.citi.webatm.rmi.TSysParmMgr;
import com.citi.webatm.rmi.TSystem;
import com.citi.webatm.rmi.TSystemMgr;
import com.citi.webatm.rmi.TTableAlter;
import com.citi.webatm.rmi.TTableAlterMgr;
import com.citi.webatm.rmi.TTmlInfo;
import com.citi.webatm.rmi.TTmlInfoMgr;
import com.iisigroup.colabase.webatm.service.APSystemService;
import com.iisigroup.colabase.webatm.service.ActCodeMapService;
import com.iisigroup.colabase.webatm.service.BankParameterService;
import com.iisigroup.colabase.webatm.service.CodeParameterService;
import com.iisigroup.colabase.webatm.service.ReaderMapService;
import com.iisigroup.colabase.webatm.common.AWATMSystemDBConfig;
import com.iisigroup.colabase.webatm.toolkit.Misc;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.utils.CapSystemConfig;

import com.iisigroup.colabase.webatm.parameter.BankData;
import tw.com.citi.webatm.txn.TxnTerminal;
import tw.com.citi.webatm.webap.signaler.TcpClient;
import tw.com.citi.webatm.webap.signaler.TerminalPool;

/**
 * <pre>
 * APsystem service.
 * </pre>
 *
 * @author Richard Yeh
 * @version
 *          <ul>
 *          <li>2017/6/15,Richard Yeh,new
 *          <li>2017/8/22,Sunkist,update rmi host/port into config.properties
 *          </ul>
 * @since 2017/6/15
 */
@Service
public class APSystemServiceImpl implements APSystemService {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());
    @Autowired
    private CapSystemConfig sysProp;
    
    @Autowired
    private CodeParameterService CodePARM;
    @Autowired
    private BankParameterService BankPARM;
    @Autowired
    private ReaderMapService ReaderPARM;
    @Autowired
    private ActCodeMapService ActCodePARM;

    public static String SYS_RmiSrvName = null;
    public static String SYS_RmiSrvPort = null;
    /**
     * 系統設定資料於initSysParm()內設定
     */
    public static TSystem SYSTEM_DATA = null;
    /**
     * 不使用預先載入之資料、都會去撈DB
     */
    public static final String PRELOAD_STTS_NO = "0";
    /**
     * 使用預先載入之資料
     */
    public static final String PRELOAD_STTS_YES = "1";
    /**
     * 使用預先載入之資料、如資料有更動、則動態更新資料
     */
    public static final String PRELOAD_STTS_DYN = "2";

    /**
     * APSystem 不需要重LOAD此資料:0
     */
    public static final String ALTER_STTS_unNEED_UPD = "0";
    /**
     * APSystem 需要重LOAD此資料:1
     */
    public static final String ALTER_STTS_NEED_UPD = "1";
    public static String COM_VERSION = null;
    public static String COM_CLASSID = null;
    public static String COM_CABNAME = null;
    public static String KEY_InitialCOM_LABEL = null;
    public static String KEY_WorkCOM_LABEL = null;
    public static Integer waitTime = 0;
    /**
     * 元件KEY,驗MAC,驗Challenge,解密confidential Field
     */
    public static String WSS_3DES_KEY = "3desCom";
    public static String WSS_3DES_TYPE = "3";

    /**
     * 系統參數資料於initSysParm()內設定
     */
    public static HashMap SYS_PRAM_MAP = null;
    /**
     * 定義更新表格資料於initSysParm()內設定
     */
    public static HashMap TABLE_ALTER_MAP = null;

    public String getRmiSrvName() {

        SYS_RmiSrvName = System.getProperty(JVM_VAR_RMI_HOSTNAME, sysProp.getProperty(JVM_VAR_RMI_HOSTNAME, "loclahost"));
        return SYS_RmiSrvName;

    }

    public String getRmiSrvPort() {

        SYS_RmiSrvPort = System.getProperty(JVM_VAR_RMI_PORT, sysProp.getProperty(JVM_VAR_RMI_PORT, "3098"));
        return SYS_RmiSrvPort;

    }

    public String getCOMM_KEY_TYPE() {
        return COMM_KEY_TYPE;
    }

    public String getInitB_KEY() {
        if (KEY_InitialCOM_LABEL == null) {
            KEY_InitialCOM_LABEL = (String) SYS_PRAM_MAP.get("KEY_InitialCOM");
            return KEY_InitialCOM_LABEL;
        } else
            return KEY_InitialCOM_LABEL;
    }

    public String getWorkB_KEY() {
        if (KEY_WorkCOM_LABEL == null) {
            KEY_WorkCOM_LABEL = (String) SYS_PRAM_MAP.get("KEY_WorkCOM");
            return KEY_WorkCOM_LABEL;
        } else
            return KEY_WorkCOM_LABEL;
    }

    public String getCOM_KEY_LABEL() {
        SYS_PRAM_MAP = getSrvTrnsData();
        if (SYS_PRAM_MAP == null) {
            LOG.error("operation: 取得交易參數資料發生異常！");
            throw new CapMessageException("取得交易參數資料發生異常", getClass());
        } else {
            // 設定元件之參數
            WSS_3DES_KEY = (String) SYS_PRAM_MAP.get("COM_KEY_LABEL");
            return WSS_3DES_KEY;

        }
    }

    /**
     * Get Txn Send wait time when lock.
     * 
     * @return the Send Txn if Busy One RetryTimes wait N Sec.
     */
    public int getWaitSleepTime() {
        waitTime = Integer.parseInt("10");
        return waitTime;
    }

    @PostConstruct
    public void init() {
    	try {
    		 initSysParm();
    	        if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_DYN)) {
    	            // 先將TableAlter清成無最新更新之狀態
    	            updateTableAlterStatus(TB_ReaderType_NAME, ALTER_STTS_unNEED_UPD);
    	            updateTableAlterStatus(TB_ReaderProc_NAME, ALTER_STTS_unNEED_UPD);
    	            updateTableAlterStatus(TB_BankInfo_NAME, ALTER_STTS_unNEED_UPD);
    	            updateTableAlterStatus(TB_SysCode_NAME, ALTER_STTS_unNEED_UPD);
    	            updateTableAlterStatus(TB_SysParm_NAME, ALTER_STTS_unNEED_UPD);
    	        }

    	        initTerminalPool();
		} catch (Exception e) {
			
		}
       
    }

    /**
     * 啟始參數資料
     * <p>
     * initSystemData();
     * <p>
     * initTrnsSrvData();
     * <p>
     * initTableAlterData();
     */
    private void initSysParm() {
        initSystemData();
        initTrnsSrvData();
        initTableAlterData();
    }

    /**
     * 啟始TrnsSrvData資料
     * <p>
     * 存入SYSTEM_DATA內供後續使用
     */
    private void initSystemData() {
        ArrayList alSystemDataList = getSystemData();
        if (alSystemDataList == null) {
            LOG.error("operation: 取得系統參數資料發生異常！");
            throw new CapMessageException("取得系統參數資料發生異常", getClass());
        } else {
            SYSTEM_DATA = (TSystem) alSystemDataList.get(0);
        }
        alSystemDataList = null;

        LOG.info("SYSTEM_DATA.getPreLoadPram()=" + SYSTEM_DATA.getPreLoadPram());
        if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_NO))
            LOG.info("不使用預先載入之資料、都會去撈DB");
        else if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_YES))
            LOG.info("使用預先載入之資料");
        else if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_DYN))
            LOG.info("使用預先載入之資料、如資料有更動、則動態更新資料");

    }

    /**
     * 取得交易主機系統資料
     *
     * @param null
     *            使用 SP_SysParm_sel_TcpSrvData
     * @return 回傳 SysParm 的 ArrayList 資料 只有getPramCode 與 getPramValue有值。
     */
    private ArrayList getSystemData() {
        TSystemMgr SystemMgr;
        ArrayList alRtnData = null;
        ArrayList alCondition = new ArrayList();
        LOG.info("method: - Start getSystemData()");
        try {
            alCondition.add("WebATM");
            SystemMgr = (TSystemMgr) Naming.lookup("rmi://" + getRmiSrvName() + ":" + getRmiSrvPort() + "/TSystem");
            alRtnData = SystemMgr.getArrayList_SP_System(SP_Cmd_01, alCondition);
            if (alRtnData.size() != 0) {
                return alRtnData;
            }
        } catch (RemoteException ex) {
            LOG.error(ex.getMessage());
            LOG.error("getTrnsSrvData: 存取遠端 RMI 錯誤！");
            return null;
        } catch (Exception ex) {
            LOG.error("getTrnsSrvData: Other Exception:" + ex.getMessage());
            return null;
        } // try 1
        return null;
    }

    /**
     * 啟始TableAlter資料
     * <p>
     * 存入TABLE_ALTER_MAP內
     */
    private void initTableAlterData() {
        TABLE_ALTER_MAP = getTableAlterHashMap();
        if (TABLE_ALTER_MAP == null) {
            LOG.error("operation: 取得表格異動資料發生異常！");
            throw new CapMessageException("取得表格異動資料發生異常", getClass());
        }
    }

    /**
     * 啟始TrnsSrvData資料
     * <p>
     * 存入SYS_PRAM_MAP內
     * <p>
     * 設定COM_VERSION、COM_CLASSID、COM_CABNAME
     */
    private void initTrnsSrvData() {
        SYS_PRAM_MAP = getSrvTrnsData();
        if (SYS_PRAM_MAP == null) {
            LOG.error("operation: 取得交易參數資料發生異常！");
            throw new CapMessageException("取得交易參數資料發生異常", getClass());
        } else {
            // 設定元件之參數
            COM_VERSION = (String) SYS_PRAM_MAP.get("COM_VERSION");
            COM_CLASSID = (String) SYS_PRAM_MAP.get("COM_CLASSID");
            COM_CABNAME = sysProp.getProperty("objfile") + (String) SYS_PRAM_MAP.get("COM_CABNAME");
            WSS_3DES_KEY = (String) SYS_PRAM_MAP.get("COM_KEY_LABEL");
            WSS_3DES_TYPE = (String) SYS_PRAM_MAP.get("COM_KEY_TYPE");
        }
    }

    /**
     * 取得交易主機資料
     *
     * @param null
     *            使用 SP_SysParm_sel_SrvTrnsData
     * @return 回傳 SysParm 的 HashMap 資料 只有getPramCode 與 getPramValue有值。
     */
    private HashMap getSrvTrnsData() {
        TSysParmMgr SysParmMgr;
        // ArrayList alRtnData = null;
        HashMap hmRtnData = null;
        ArrayList alCondition = new ArrayList();
        alCondition.add(sysProp.getProperty("systemidno"));
        LOG.info("method: - Start getTrnsSrvData()");
        try {
            SysParmMgr = (TSysParmMgr) Naming.lookup("rmi://" + getRmiSrvName() + ":" + getRmiSrvPort() + "/TSysParm");
            hmRtnData = SysParmMgr.getValueMap_SP_SysParm(SP_Cmd_02, alCondition);
            if (hmRtnData.size() != 0) {
                LOG.info("Init SYS_PRAM_MAP Finished.【" + hmRtnData.size() + "】inserted.");
                return hmRtnData;
            }
        } catch (RemoteException ex) {
            LOG.error(ex.getMessage());
            LOG.error("getTrnsSrvData: 存取遠端 RMI 錯誤！");
            return null;
        } catch (Exception ex) {
            LOG.error("getTrnsSrvData: Other Exception:" + ex.getMessage());
            return null;
        } // try 1
        return null;
    }

    /**
     * 更新變動表格記錄.
     * <p>
     * 先至DB取得TableAlterArrayList
     * <p>
     * 再與TABLE_ALTER_MAP比對，當AlterStatus被變為1
     * <p>
     * 和TABLE_ALTER_MAP的AlterStatus為0時
     * <p>
     * 則更新TABLE_ALTER_MAP的AlterStatus為1
     * <p>
     */
    public boolean updateTableAlterMap() {
        LOG.info("method: - Start updateTableAlterMap()");
        ArrayList alRtn = null;
        TTableAlter tempTTA = null;
        TTableAlter apTTA = null;
        alRtn = getTableAlterArrayList();
        if (alRtn == null)
            return false;

        int iSize = alRtn.size();
        for (int i = 0; i < iSize; i++) {
            tempTTA = (TTableAlter) alRtn.get(i);
            apTTA = ((TTableAlter) TABLE_ALTER_MAP.get(tempTTA.getTableName()));
            if (apTTA == null)
                continue;

            if (tempTTA.getAlterStatus().equals("1") && apTTA.getAlterStatus().equals("0")) {
                // LOG.debug(apTTA.getTableName() + " apTTA.getAlterStatus=" +
                // apTTA.getAlterStatus());
                synchronized (TABLE_ALTER_MAP) {
                    TABLE_ALTER_MAP.put(tempTTA.getTableName(), tempTTA);
                }
                apTTA = ((TTableAlter) TABLE_ALTER_MAP.get(tempTTA.getTableName()));
                // LOG.debug(apTTA.getTableName() + "apTTA.getAlterStatus=" +
                // apTTA.getAlterStatus());
            }
        }
        tempTTA = null;
        apTTA = null;
        alRtn = null;
        return true;
    }

    /**
     * 取得變動Table資料
     *
     * @param null
     *            使用 SP_TableAlter_sel_all
     * @return 回傳 TableAlter 的 HashMap 資料
     */
    private HashMap getTableAlterHashMap() {
        TTableAlterMgr TableAlterMgr;
        HashMap hmRtnData = null;
        ArrayList alCondition = new ArrayList();
        alCondition.add(sysProp.getProperty("systemidno"));
        LOG.info("method: - Start getTableAlterHashMap()");
        try {
            TableAlterMgr = (TTableAlterMgr) Naming.lookup("rmi://" + getRmiSrvName() + ":" + getRmiSrvPort() + "/TTableAlter");
            hmRtnData = TableAlterMgr.getBeanMap_SP_TableAlter(SP_Cmd_03, alCondition);
            if (hmRtnData.size() != 0) {
                return hmRtnData;
            }
        } catch (RemoteException ex) {
            LOG.error(ex.getMessage());
            LOG.error("getTableAlterHashMap: 存取遠端 RMI 錯誤！");
            return null;
        } catch (Exception ex) {
            LOG.error("getTableAlterHashMap: Other Exception:" + ex.getMessage());
            return null;
        } // try 1
        return null;
    }

    /**
     * 取得變動Table資料
     *
     * @param null
     *            使用 SP_TableAlter_sel_all
     * @return 回傳 TableAlter 的 ArrayList 資料
     */
    private ArrayList getTableAlterArrayList() {
        TTableAlterMgr TableAlterMgr;
        ArrayList alRtnData = null;
        ArrayList alCondition = new ArrayList();
        alCondition.add(sysProp.getProperty("systemidno"));
        LOG.info("method: - Start getTableAlterArrayList()");
        try {
            TableAlterMgr = (TTableAlterMgr) Naming.lookup("rmi://" + getRmiSrvName() + ":" + getRmiSrvPort() + "/TTableAlter");
            alRtnData = TableAlterMgr.getArrayList_SP_TableAlter(SP_Cmd_03, alCondition);
            if (alRtnData.size() != 0) {
                return alRtnData;
            }
        } catch (RemoteException ex) {
            LOG.error(ex.getMessage());
            LOG.error("getTableAlterArrayList: 存取遠端 RMI 錯誤！");
            return null;
        } catch (Exception ex) {
            LOG.error("getTableAlterArrayList: Other Exception:" + ex.getMessage());
            return null;
        } // try 1
        return null;
    }

    /**
     * 更新TTableAlter狀態資料
     * <p>
     * 使用 SP_TableAlter_sel_all
     *
     * @param sTableName
     * @param sAlterStatus
     */

    private boolean updateTableAlterStatus(String sTableName, String sAlterStatus) {
        TTableAlterMgr TableAlterMgr;
        HashMap hm = null;
        LOG.info("method: - Start updateTableAlterStatus(" + sTableName + ")");
        sAlterStatus = (sAlterStatus.equals("")) ? "0" : sAlterStatus;

        hm = new HashMap();
        hm.put("_ServerID", sysProp.getProperty("systemidno"));
        hm.put("_TableName", sTableName);
        hm.put("AlterStatus", sAlterStatus);
        try {
            TableAlterMgr = (TTableAlterMgr) Naming.lookup("rmi://" + getRmiSrvName() + ":" + getRmiSrvPort() + "/TTableAlter");
            int iRtn = TableAlterMgr.update_SP_TableAlter(SP_Cmd_04, hm);
            if (iRtn == 0) {
                return false;
            }
        } catch (RemoteException ex) {
            LOG.error(ex.getMessage());
            LOG.error("updateTableAlterStatus: 存取遠端 RMI 錯誤！");
            return false;
        } catch (Exception ex) {
            LOG.error("updateTableAlterStatus: Other Exception:" + ex.getMessage());
            return false;
        } // try 1
        return true;
    }

    /**
     * @return Returns the SYS_PRAM_MAP.
     */
    public HashMap getSYS_PRAM_MAP() {
        if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_NO)) {
            HashMap sys_pram_map = getSrvTrnsData();
            if (sys_pram_map == null) {
                LOG.error("operation: 取得交易參數資料發生異常！");
                throw new CapMessageException("取得交易參數資料發生異常", getClass());
            } else {
                return sys_pram_map;
            }
        } else if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_DYN)) {
            TTableAlter tta = null;
            tta = (TTableAlter) TABLE_ALTER_MAP.get(TB_SysParm_NAME);
            if ((tta != null) && (tta.getAlterStatus().equals(ALTER_STTS_NEED_UPD))) {
                initTrnsSrvData();
                updateTableAlterStatus(TB_SysParm_NAME, ALTER_STTS_unNEED_UPD);
                tta.setAlterStatus(ALTER_STTS_unNEED_UPD);
                TABLE_ALTER_MAP.put(TB_SysParm_NAME, tta);
            }
        }
        return SYS_PRAM_MAP;
    }

    /**
     * @return Returns the BankInfoArray.
     */
    public ArrayList getBankInfoArray() {
        if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_NO)) {
            return BankPARM.getBankInfoArrayByDB();
        } else if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_DYN)) {
            TTableAlter tta = null;
            tta = (TTableAlter) TABLE_ALTER_MAP.get(TB_BankInfo_NAME);
            if ((tta != null) && (tta.getAlterStatus().equals(ALTER_STTS_NEED_UPD))) {
                BankPARM.init();
                updateTableAlterStatus(TB_BankInfo_NAME, ALTER_STTS_unNEED_UPD);
                tta.setAlterStatus(ALTER_STTS_unNEED_UPD);
                TABLE_ALTER_MAP.put(TB_BankInfo_NAME, tta);
            }
        }
        return BankPARM.getBankInfoArray();
    }

    /**
     * @return Returns the BankData.
     */
    public BankData getBankData(String sBankID) {
        if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_NO)) {
            return BankPARM.getBankDataByDB(sBankID);
        } else if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_DYN)) {
            TTableAlter tta = null;
            tta = (TTableAlter) TABLE_ALTER_MAP.get(TB_BankInfo_NAME);
            if ((tta != null) && (tta.getAlterStatus().equals(ALTER_STTS_NEED_UPD))) {
                BankPARM.init();
                updateTableAlterStatus(TB_BankInfo_NAME, ALTER_STTS_unNEED_UPD);
                tta.setAlterStatus(ALTER_STTS_unNEED_UPD);
                TABLE_ALTER_MAP.put(TB_BankInfo_NAME, tta);
            }
        }
        return BankPARM.getBankData(sBankID);
    }

    /**
     * @return Returns the ReaderType.
     */
    public int getReaderType(String reader) {
        if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_NO)) {
            return ReaderPARM.getReaderTypeByDB(reader);
        } else if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_DYN)) {
            TTableAlter tta = null;
            tta = (TTableAlter) TABLE_ALTER_MAP.get(TB_ReaderType_NAME);
            if ((tta != null) && (tta.getAlterStatus().equals(ALTER_STTS_NEED_UPD))) {
                ReaderPARM.init();
                updateTableAlterStatus(TB_ReaderType_NAME, ALTER_STTS_unNEED_UPD);
                tta.setAlterStatus(ALTER_STTS_unNEED_UPD);
                TABLE_ALTER_MAP.put(TB_ReaderType_NAME, tta);
            } else {
                tta = (TTableAlter) TABLE_ALTER_MAP.get(TB_ReaderProc_NAME);
                if ((tta != null) && (tta.getAlterStatus().equals(ALTER_STTS_NEED_UPD))) {
                    ReaderPARM.init();
                    updateTableAlterStatus(TB_ReaderProc_NAME, ALTER_STTS_unNEED_UPD);
                    tta.setAlterStatus(ALTER_STTS_unNEED_UPD);
                    TABLE_ALTER_MAP.put(TB_ReaderProc_NAME, tta);
                }
            }
        }
        return ReaderPARM.getReaderType(reader);
    }

    /**
     * @return Returns the ActionCode.
     */
    public TActionCode getActionCode(String respcode) {
        if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_NO)) {
            return ActCodePARM.getActionCodeByDB(respcode);
        } else if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_DYN)) {
            TTableAlter tta = null;
            tta = (TTableAlter) TABLE_ALTER_MAP.get(TB_ActCode_NAME);
            if ((tta != null) && (tta.getAlterStatus().equals(ALTER_STTS_NEED_UPD))) {
                ActCodePARM.init();
                updateTableAlterStatus(TB_ActCode_NAME, ALTER_STTS_unNEED_UPD);
                tta.setAlterStatus(ALTER_STTS_unNEED_UPD);
                TABLE_ALTER_MAP.put(TB_ActCode_NAME, tta);
            } else {
                tta = (TTableAlter) TABLE_ALTER_MAP.get(TB_ReaderProc_NAME);
                if ((tta != null) && (tta.getAlterStatus().equals(ALTER_STTS_NEED_UPD))) {
                    ActCodePARM.init();
                    updateTableAlterStatus(TB_ReaderProc_NAME, ALTER_STTS_unNEED_UPD);
                    tta.setAlterStatus(ALTER_STTS_unNEED_UPD);
                    TABLE_ALTER_MAP.put(TB_ReaderProc_NAME, tta);
                }
            }
        }
        return ActCodePARM.getActionCode(respcode);
    }

    /**
     * @return Returns the PCodeProc.
     */
    public Integer getPCodeProc(int iReaderType, String trnsCode) {
        if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_NO)) {
            return ReaderPARM.getPCodeProcByDB(iReaderType, trnsCode);
        } else if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_DYN)) {
            TTableAlter tta = null;
            tta = (TTableAlter) TABLE_ALTER_MAP.get(TB_ReaderType_NAME);
            if ((tta != null) && (tta.getAlterStatus().equals(ALTER_STTS_NEED_UPD))) {
                ReaderPARM.init();
                updateTableAlterStatus(TB_ReaderType_NAME, ALTER_STTS_unNEED_UPD);
                tta.setAlterStatus(ALTER_STTS_unNEED_UPD);
                TABLE_ALTER_MAP.put(TB_ReaderType_NAME, tta);
            } else {
                tta = (TTableAlter) TABLE_ALTER_MAP.get(TB_ReaderProc_NAME);
                if ((tta != null) && (tta.getAlterStatus().equals(ALTER_STTS_NEED_UPD))) {
                    ReaderPARM.init();
                    updateTableAlterStatus(TB_ReaderProc_NAME, ALTER_STTS_unNEED_UPD);
                    tta.setAlterStatus(ALTER_STTS_unNEED_UPD);
                    TABLE_ALTER_MAP.put(TB_ReaderProc_NAME, tta);
                }
            }
        }
        return ReaderPARM.getPCodeProc(iReaderType, trnsCode);
    }

    /**
     * @return Returns the TSysCode的Value.
     */
    public String[] getValue(String sCodeItem, String sCodeID) {
        if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_NO)) {
            return CodePARM.getValueByDB(sCodeItem, sCodeID);
        } else if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_DYN)) {
            TTableAlter tta = null;
            tta = (TTableAlter) TABLE_ALTER_MAP.get(TB_SysCode_NAME);
            if ((tta != null) && (tta.getAlterStatus().equals(ALTER_STTS_NEED_UPD))) {
                CodePARM.init();
                updateTableAlterStatus(TB_SysCode_NAME, ALTER_STTS_unNEED_UPD);
                tta.setAlterStatus(ALTER_STTS_unNEED_UPD);
                TABLE_ALTER_MAP.put(TB_SysCode_NAME, tta);
            }
        }
        return CodePARM.getValue(sCodeItem, sCodeID);
    }

    public ArrayList getTSysCodeArray(String sCodeItem) {
        if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_NO)) {
            return CodePARM.getTSysCodeArrayByDB(sCodeItem);
        } else if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_DYN)) {
            TTableAlter tta = null;
            tta = (TTableAlter) TABLE_ALTER_MAP.get(TB_SysCode_NAME);
            if ((tta != null) && (tta.getAlterStatus().equals(ALTER_STTS_NEED_UPD))) {
                CodePARM.init();
                updateTableAlterStatus(TB_SysCode_NAME, ALTER_STTS_unNEED_UPD);
                tta.setAlterStatus(ALTER_STTS_unNEED_UPD);
                TABLE_ALTER_MAP.put(TB_SysCode_NAME, tta);
            }
        }
        return CodePARM.getTSysCodeArray(sCodeItem);
    }

    public Hashtable getIDValueMap(String sCodeItem) {
        if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_NO)) {
            return CodePARM.getIDValueMapByDB(sCodeItem);
        } else if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_DYN)) {
            TTableAlter tta = null;
            tta = (TTableAlter) TABLE_ALTER_MAP.get(TB_SysCode_NAME);
            if ((tta != null) && (tta.getAlterStatus().equals(ALTER_STTS_NEED_UPD))) {
                CodePARM.init();
                updateTableAlterStatus(TB_SysCode_NAME, ALTER_STTS_unNEED_UPD);
                tta.setAlterStatus(ALTER_STTS_unNEED_UPD);
                TABLE_ALTER_MAP.put(TB_SysCode_NAME, tta);
            }
        }
        return CodePARM.getIDValueMap(sCodeItem);
    }

    /**
     * @return Returns the cOM_CABNAME.
     */
    public String getCOM_CABNAME() {
        if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_NO)) {
            // return COM_CABNAME;
            return sysProp.getProperty("objfile") + (String) getSYS_PRAM_MAP().get("COM_CABNAME");
        } else if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_DYN)) {
            TTableAlter tta = null;
            tta = (TTableAlter) TABLE_ALTER_MAP.get(TB_SysParm_NAME);
            if ((tta != null) && (tta.getAlterStatus().equals(ALTER_STTS_NEED_UPD))) {
                initTrnsSrvData();
                updateTableAlterStatus(TB_SysParm_NAME, ALTER_STTS_unNEED_UPD);
                tta.setAlterStatus(ALTER_STTS_unNEED_UPD);
                TABLE_ALTER_MAP.put(TB_SysParm_NAME, tta);
            }
        }
        return COM_CABNAME;
    }

    /**
     * @return Returns the cOM_CLASSID.
     */
    public String getCOM_CLASSID() {
        if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_NO)) {
            // return COM_CLASSID;
            return (String) getSYS_PRAM_MAP().get("COM_CLASSID");
        } else if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_DYN)) {
            TTableAlter tta = null;
            tta = (TTableAlter) TABLE_ALTER_MAP.get(TB_SysParm_NAME);
            if ((tta != null) && (tta.getAlterStatus().equals(ALTER_STTS_NEED_UPD))) {
                initTrnsSrvData();
                updateTableAlterStatus(TB_SysParm_NAME, ALTER_STTS_unNEED_UPD);
                tta.setAlterStatus(ALTER_STTS_unNEED_UPD);
                TABLE_ALTER_MAP.put(TB_SysParm_NAME, tta);
            }
        }
        return COM_CLASSID;
    }

    /**
     * @return Returns the cOM_VERSION.
     */
    public String getCOM_VERSION() {
        if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_NO)) {
            // return COM_VERSION;
            return (String) getSYS_PRAM_MAP().get("COM_VERSION");
        } else if (SYSTEM_DATA.getPreLoadPram().equals(PRELOAD_STTS_DYN)) {
            TTableAlter tta = null;
            tta = (TTableAlter) TABLE_ALTER_MAP.get(TB_SysParm_NAME);
            if ((tta != null) && (tta.getAlterStatus().equals(ALTER_STTS_NEED_UPD))) {
                initTrnsSrvData();
                updateTableAlterStatus(TB_SysParm_NAME, ALTER_STTS_unNEED_UPD);
                tta.setAlterStatus(ALTER_STTS_unNEED_UPD);
                TABLE_ALTER_MAP.put(TB_SysParm_NAME, tta);
            }
        }
        return COM_VERSION;
    }

    /**
     * 啟始TerminalPool資料
     * <p>
     * initSystemData();
     * <p>
     * initTrnsSrvData();
     * <p>
     * initTableAlterData();
     */
    private void initTerminalPool() {
        Hashtable htTML_Send_MSG = new Hashtable();
        Hashtable htTML_Receive_MSG = new Hashtable();
        ArrayList alTmlInfo = getTmlData();
        TTmlInfo HandShakingTmlInfo = getHandShaking_TmlInfo(alTmlInfo);
        ArrayList alTrnsTmlID = getArrayList_TmlID(alTmlInfo);

        TmlPool = new TerminalPool(htTML_Send_MSG, htTML_Receive_MSG);
        SocketClient = new TcpClient((String) getSYS_PRAM_MAP().get("TCP_SRV_ADDR"), Integer.parseInt((String) getSYS_PRAM_MAP().get("TCP_SRV_PORT")), htTML_Send_MSG, htTML_Receive_MSG);
        SocketClient.init(); // 設定參數
        SocketClient.starClientThread();
        TmlPool.initTerminalPool(alTrnsTmlID, SocketClient);
        TxnTerminal txn;
        if (HandShakingTmlInfo != null) {
            TmlPool.addHandShaking_Tml(HandShakingTmlInfo, SocketClient);
            txn = TmlPool.getHandShaking_Tml();
            if (txn.doTimeSync()) {
                if (!txn.initKeyExchange()) {
                    throw new CapMessageException("連線主機HandShaking發生異常", getClass());
                } else {
                    TmlPool.starDetectReConnectThread();

                    TTmlInfo tmlinfo = null;
                    for (int index = 0; index < alTmlInfo.size(); index++) {
                        tmlinfo = (TTmlInfo) alTmlInfo.get(index);
                        if (tmlinfo.getTmlStatus().equals(TTmlInfo.TmlStatus_ON)) {
                            if (!tmlinfo.getTmlFunction().equals(TTmlInfo.TmlFunc_KE)) {
                                txn = TmlPool.getTerminal(tmlinfo.getTmlID().trim());

                                // do 4101 for normal terminal depend by
                                // ResetMsgSeqNo SysParm
                                if (((String) SYS_PRAM_MAP.get("ResetMsgSeqNo")).equals("1")) {
                                    txn.setMsgSeqNo(tmlinfo.getMsgSeqNo());
                                    SocketClient.setStopTrns(true);
                                    if (!txn.doTmlSeqReset()) {
                                        throw new CapMessageException("連線主機TmlSeqReset發生異常", getClass());
                                    } else {
                                        SocketClient.setStopTrns(false);
                                    }
                                } else {
                                    SocketClient.setStopTrns(false);
                                }
                                TmlPool.returnTerminal(txn);
                            }
                        }
                    }
                }
            } else {
                throw new CapMessageException("連線主機HandShaking發生異常", getClass());
            }
        }

    }

    private static void testTxn() {
        TxnTerminal txn;
        txn = TmlPool.getTerminal();
        txn.doKeyExchange();
        TmlPool.returnTerminal(txn);
    }

    public TxnTerminal getTerminal() {
        TxnTerminal txn;
        txn = TmlPool.getTerminal();
        return txn;
    }

    /**
     * 取得端末機系統資料
     *
     * @param null
     *            使用 SP_TmlInfo_sel_all
     * @return 回傳 TmlInfo 的 ArrayList 資料
     */
    private ArrayList getTmlData() {
        TTmlInfoMgr TmlInfoMgr;
        ArrayList alRtnData = null;
        ArrayList alCondition = new ArrayList();
        alCondition.add(sysProp.getProperty("systemidno"));
        LOG.info("method: - Start getTmlInfo()");
        try {
            TmlInfoMgr = (TTmlInfoMgr) Naming.lookup("rmi://" + getRmiSrvName() + ":" + getRmiSrvPort() + "/TTmlInfo");
            alRtnData = TmlInfoMgr.getArrayList_SP_TmlInfo(SP_Cmd_06, alCondition);
            if (alRtnData.size() != 0) {
                return alRtnData;
            }
        } catch (RemoteException ex) {
            LOG.error(ex.getMessage());
            LOG.error("getTmlInfoData: 存取遠端 RMI 錯誤！");
            return null;
        } catch (Exception ex) {
            LOG.error("getTmlInfoData: Other Exception:" + ex.getMessage());
            return null;
        } // try 1
        return null;
    }

    public static TTmlInfo getHandShaking_TmlInfo(ArrayList alTmlInfo) {
        if (alTmlInfo == null)
            return null;
        TTmlInfo ti = null;
        for (int i = 0; i < alTmlInfo.size(); i++) {
            ti = (TTmlInfo) alTmlInfo.get(i);
            if (ti.getTmlStatus().equals(TTmlInfo.TmlStatus_ON)) {
                if (ti.getTmlFunction().equals(TTmlInfo.TmlFunc_KE)) {
                    return ti;
                }
            }
        }
        return null;
    }

    public static ArrayList getArrayList_TmlID(ArrayList alTmlInfo) {
        if (alTmlInfo == null)
            return null;
        TTmlInfo ti = null;
        ArrayList alRt = new ArrayList(alTmlInfo.size());
        for (int i = 0; i < alTmlInfo.size(); i++) {
            ti = (TTmlInfo) alTmlInfo.get(i);
            if (ti.getTmlStatus().equals(TTmlInfo.TmlStatus_ON))
                if (!ti.getTmlFunction().equals(TTmlInfo.TmlFunc_KE))
                    alRt.add(ti.getTmlID().trim());
        }
        return alRt;
    }

    private boolean doStopSystemService(String execCmd) {
        Process process = null;
        try {
            Runtime rt = Runtime.getRuntime();
            // Process process = rt.exec("cmd /c " + value);
            // //D:/eATMTrns/01_startTrnsServer.bat

            // process = rt.exec("D:/eATMTrns/01_startTrnsServer.bat" + " >>" +
            // redirectStdout);
            process = rt.exec(execCmd);

            BufferedReader rd = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str = "";
            while ((str = rd.readLine()) != null) {
                LOG.info(str);
            }
            System.out.println("process is run now");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 至指定的URL取得文件，Insert/Update ActionCode,BankInfo,ReaderType Table
     **/
    private boolean doAlterDB_Param(String proxyHost, String proxyPort, final String proxyUser, final String proxyPwd, String QueryURL, String TableName) {
        HttpURLConnection httpConn = null;
        try {
            if (proxyHost != null && proxyPort != null && proxyHost.trim().length() != 0 && proxyPort.trim().length() != 0) {

                System.setProperty("proxySet", "true");
                System.setProperty("https.proxyHost", proxyHost);
                System.setProperty("https.proxyPort", proxyPort);
                System.setProperty("http.proxyHost", proxyHost);
                System.setProperty("http.proxyPort", proxyPort);
            }
            URL url = new URL(QueryURL);
            LOG.info("get Online UpdateDB CSV url:" + url);
            httpConn = (HttpURLConnection) url.openConnection();

            if (proxyUser != null && proxyPwd != null && proxyUser.trim().length() != 0 && proxyPwd.trim().length() != 0) {

                Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(proxyUser, proxyPwd.toCharArray());
                    }
                });
            }
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);

            InputStream is = null;

            is = httpConn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            LineNumberReader LineReader = new LineNumberReader(br);
            String read_line = null;
            ArrayList asLine = new ArrayList();
            while ((read_line = LineReader.readLine()) != null) {
                // LOG.debug(read_line);
                asLine.add(read_line);
            }

            String eachLine = null;
            String[] saData = null;

            boolean validColumnCount = true;
            for (int i = 0; i < asLine.size(); i++) {
                // System.out.println(saRow[i]);
                eachLine = (String) asLine.get(i);
                if (isValidSQL((String) asLine.get(i), "(/{2}.+)|(.+((.*/{2})).+)|(\\s*\\n.+)")) {
                    LOG.debug(i + "-備註");
                    continue;
                }

                if (i == 0) { // check date
                    LOG.debug(i + "-" + eachLine);
                    // if (!Misc.genDate(Misc.DT_DATE).equals(eachLine))
                    if (eachLine.indexOf(Misc.genDate(Misc.DT_DATE)) == -1) {
                        LOG.debug("今天不用做或檔案日期定義錯誤 " + eachLine);
                        validColumnCount = false;
                        break;
                    }
                } else if (i == 1) {
                    LOG.debug(i + "-" + eachLine);
                } else if (i >= 2) {

                    // 基本項目檢核
                    if (isValidSQL(eachLine, ".+-{2}.+")) {
                        LOG.debug("SQL不允許 『-』字元 :" + eachLine);
                        validColumnCount = false;
                        break;
                    }

                    if (isValidSQL(eachLine, ".+'.+")) {
                        LOG.debug("SQL不允許 『'』字元 :" + eachLine);
                        validColumnCount = false;
                        break;
                    }

                    if (isValidSQL(eachLine.toUpperCase(), ".+\\s+((INSERT)|(UPDATE)|(DELETE))\\s+.+")) {
                        LOG.debug("不允許更新非法之操作功能 :" + eachLine);
                        validColumnCount = false;
                        break;
                    }

                    // 確認每筆資料欄位個數
                    saData = eachLine.split(",");

                    if (TableName.equalsIgnoreCase("ActionCode")) {
                        if (saData.length != 4 && saData.length != 5) {
                            validColumnCount = false;
                            break;
                        }
                    } else if (TableName.equalsIgnoreCase("BankInfo")) {
                        if (saData.length != 4) {
                            validColumnCount = false;
                            break;
                        }
                    } else if (TableName.equalsIgnoreCase("ReaderType")) {
                        if (saData.length != 4) {
                            validColumnCount = false;
                            break;
                        }
                    } else {
                        validColumnCount = false;
                        break;
                    }
                }
            }

            String sql = null;
            if (validColumnCount) {
                sql = "Delete " + TableName + ";";
                LOG.debug(sql + " 【result = " + doSQL_EXEC(sql) + "】");
            }

            for (int i = 2; i < asLine.size() && validColumnCount; i++) {
                eachLine = (String) asLine.get(i);
                String values = eachLine.replaceAll(",", "','");
                sql = "insert into " + TableName + " values('" + values + "')";
                LOG.debug(sql + " 【result = " + doSQL_EXEC(sql) + "】");

                /*
                 * saData = saRow[i].split("#"); if (saData.length == 2){ String table = saData[0]; String sql = saData[1]; LOG.debug("TABLE = " + table); LOG.debug("SQL = " + sql); //檢核資料 if
                 * (isValidSQL(sql, ".+-{2}.+")){ LOG.debug("SQL不允許 『-』字元 :" + sql); continue; }
                 * 
                 * if (!isValidSQL(sql.toUpperCase(), ".+\\s+((ACTIONCODE)|(BANKINFO)|(READERTYPE))\\s+.+")){ LOG.debug("不允許更新非法之Table :" + sql); continue; }
                 * 
                 * if (!isValidSQL(sql.toUpperCase(), ".+\\s+" + table.toUpperCase() + "\\s+.+")){ LOG.debug("SQL語法與定義Table Name不符 :" + sql); continue; }
                 * 
                 * if (!isValidSQL(sql)){ LOG.debug("SQL只允許 Insert,Update"); continue; }
                 * 
                 * LOG.debug(sql + " 【result = " + doSQL_EXEC(sql) + "】"); }
                 */
            }

            is.close();
            httpConn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    static private boolean isValidSQL(String sql, String Pattern) {

        boolean retval = false;

        retval = sql.matches(Pattern);

        return retval;
    }

    private boolean isValidSQL(String sql) {

        String Pattern = "\\s*(([iI][nN][sS][eE][rR][tT])|([uU][pP][dD][aA][tT][eE]))\\s+.+";

        return isValidSQL(sql, Pattern);
    }

    private int doSQL_EXEC(String SQL_CMD) {
        GenericSQLMgr SQLMgr;
        LOG.info("method: - Start doSQL_EXEC()");
        try {
            SQLMgr = (GenericSQLMgr) Naming.lookup("rmi://" + getRmiSrvName() + ":" + getRmiSrvPort() + "/GenericSQL");
            int rtn = SQLMgr.EXEC(SQL_CMD);
            return rtn;
        } catch (RemoteException rex) {
            rex.printStackTrace();
            LOG.error(rex.getMessage());
            LOG.error("doSQL_EXEC: 存取遠端 RMI 錯誤！");
            return 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.error("doSQL_EXEC: Other Exception:" + ex.getMessage());
            return 0;
        } // try 1
    }

    // private static Hashtable htTML_Send_MSG = new Hashtable();
    // private static Hashtable htTML_Receive_MSG = new Hashtable();
    public static TerminalPool TmlPool;
    public static Timer KeyExchangeTimer;

    private static TcpClient SocketClient;

    public static void main(String[] args) {

    }

}
