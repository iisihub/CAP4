
package com.iisigroup.colabase.webatm.service.impl;

import static com.iisigroup.colabase.webatm.common.CCConstants.READER_TXN_DEFAULT;
import static com.iisigroup.colabase.webatm.common.CCConstants.READER_TXN_RANGE_MAX;
import static com.iisigroup.colabase.webatm.common.CCConstants.READER_TXN_RANGE_MIN;
import static com.iisigroup.colabase.webatm.common.CCConstants.READER_TYPE_DEFAULT;
import static com.iisigroup.colabase.webatm.common.CCConstants.READER_TYPE_RANGE_MAX;
import static com.iisigroup.colabase.webatm.common.CCConstants.READER_TYPE_RANGE_MIN;
import static com.iisigroup.colabase.webatm.common.CCConstants.TB_READERTYPE_NAME_LEN;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citi.webatm.rmi.TReaderProc;
import com.citi.webatm.rmi.TReaderProcMgr;
import com.citi.webatm.rmi.TReaderType;
import com.citi.webatm.rmi.TReaderTypeMgr;
import com.iisigroup.colabase.webatm.service.APSystemService;
import com.iisigroup.colabase.webatm.service.ReaderMapService;

/**
 * <pre>
 * Hsm service.
 * </pre>
 *
 * @author Richard Yeh
 * @version
 *          <ul>
 *          <li>2017/6/15,Richard Yeh,new
 *          </ul>
 * @since 2017/6/15
 */
/*
 * Frank：由於使用 Hashtable，故不考慮 synchronize 的問題 當同時又在 init 又在 getType 時，getType 先跑的話 會拿到舊的資料，而 init 先跑以後，getType 得等他跑完
 *
 * 20060613 新增：更改原先的 Mapping 為四種 1.ReaderTypeMap：讀卡機名稱 vs 真正的讀卡機型態(ReaderType) 2.ATMTypeMap ：讀卡機名稱 vs 送給前端的讀卡機型態(ATMType) 3.TypeLinkMap ：真正的讀卡機型態 vs 該讀卡機的 procMap 4.PCodeProcMap ：每個讀卡機型態都有自己的
 * procMap，內容為 交易代碼 P-Code vs 特殊處理型式
 */
@Service
public class ReaderMapServiceImpl implements ReaderMapService {

    protected final Logger Log = LoggerFactory.getLogger(getClass());
    private Hashtable ReaderTypeMap = new Hashtable();
    private Hashtable TmlTypeMap = new Hashtable();
    private Hashtable TypeLinkMap = new Hashtable();

    private final static String SP_Cmd_01 = "SP_ReaderType_sel_all";
    private final static String SP_Cmd_02 = "SP_ReaderProc_sel_all";
    private final static String SP_Cmd_03 = "SP_ReaderType_sel_one";
    private final static String SP_Cmd_04 = "SP_ReaderProc_sel_one";
    private final static String SP_Cmd_05 = "SP_ReaderType_ins";
    @Autowired
    private APSystemService hsmService;

    @PostConstruct
    public void init() {

        String sTypeTemp = null;
        Hashtable PCodeTemp = null;

        TReaderType readertype;
        TReaderProc readerproc;

        ArrayList alRtnData = null;
        ArrayList alCondition = new ArrayList();
        alRtnData = getRmiReaderTypeData(SP_Cmd_01, alCondition);

        // ReaderTypeMap = new Hashtable();
        // ATMTypeMap = new Hashtable();
        // TypeLinkMap = new Hashtable();

        synchronized (ReaderTypeMap) {
            // ReaderTypeMap = null;
            // System.gc();

            for (int i = 0; i < alRtnData.size(); i++) {
                readertype = (TReaderType) alRtnData.get(i);
                putReaderType(readertype.getReaderName().trim(), Integer.valueOf(readertype.getReaderType()));
            }
        }
        System.gc();
        Log.info("Init Reader-Type Map Finished.【" + alRtnData.size() + "】inserted.");
        synchronized (TmlTypeMap) {
            // ATMTypeMap = null;
            // System.gc();
            // ATMTypeMap = new Hashtable();
            for (int i = 0; i < alRtnData.size(); i++) {
                readertype = (TReaderType) alRtnData.get(i);
                putTmlType(readertype.getReaderName().trim(), Integer.valueOf(readertype.getTmlType()));
            }
        }
        System.gc();
        Log.info("Init ATM-Type Map Finished.【" + alRtnData.size() + "】inserted.");

        alRtnData.clear();

        alRtnData = getRmiReaderProcData(SP_Cmd_02, alCondition);

        synchronized (TypeLinkMap) {
            // TypeLinkMap = null;
            // System.gc();
            // TypeLinkMap = new Hashtable();
            for (int i = 0; i < alRtnData.size(); i++) {
                readerproc = (TReaderProc) alRtnData.get(i);
                if (readerproc.getReaderType().trim().equals(sTypeTemp) == false) {
                    sTypeTemp = readerproc.getReaderType().trim();
                    PCodeTemp = new Hashtable();
                    if (TypeLinkMap.put(Integer.valueOf(sTypeTemp), PCodeTemp) != null) {
                        Log.error("<ERR> TypeLinkMap 放入重覆的物件，" + "請檢查系統或 SQL Command。");
                    }
                }

                if (PCodeTemp.put(readerproc.getPCode().trim(), Integer.valueOf(readerproc.getTxnType())) != null) {
                    Log.error("<ERR> 讀卡機型態 " + sTypeTemp + " 放入重覆" + " PCode，請檢查系統或 SQL Command。");
                }
            }
        }
        System.gc();
        Log.info("Init PCode Map Finished.【" + alRtnData.size() + "】inserted.");
    }

    private ArrayList getRmiReaderTypeData(String sSPName, ArrayList alCondition) {
        TReaderTypeMgr readertypeMgr;
        ArrayList alRtnData = null;
        try {
            readertypeMgr = (TReaderTypeMgr) Naming.lookup("rmi://" + hsmService.getRmiSrvName() + ":" + hsmService.getRmiSrvPort() + "/TReaderType");
            alRtnData = readertypeMgr.getArrayList_SP_ReaderType(sSPName, alCondition);
        } catch (RemoteException e) {
            Log.error("<ERR> 存取遠端 RMI 錯誤！", e);
        } catch (Exception e) {
            Log.error("<ERR> 錯誤！", e);
        }
        return alRtnData;
    }

    private ArrayList getRmiReaderProcData(String sSPName, ArrayList alCondition) {
        TReaderProcMgr readerprocMgr;
        ArrayList alRtnData = null;
        try {
            readerprocMgr = (TReaderProcMgr) Naming.lookup("rmi://" + hsmService.getRmiSrvName() + ":" + hsmService.getRmiSrvPort() + "/TReaderProc");
            alRtnData = readerprocMgr.getArrayList_SP_ReaderProc(sSPName, alCondition);
        } catch (RemoteException e) {
            Log.error("<ERR> 存取遠端 RMI 錯誤！", e);
        } catch (Exception e) {
            Log.error("<ERR> 錯誤！", e);
        }
        return alRtnData;
    }

    private int doInsertReaderTypeData(String sSPName, String ReaderName) {
        TReaderTypeMgr readertypeMgr;
        ArrayList alData = new ArrayList();
        alData.add(ReaderName);
        try {
            readertypeMgr = (TReaderTypeMgr) Naming.lookup("rmi://" + hsmService.getRmiSrvName() + ":" + hsmService.getRmiSrvPort() + "/TReaderType");
            int iRtn = readertypeMgr.insert_SP_ReaderType(sSPName, alData);
            return iRtn;
        } catch (RemoteException e) {
            Log.error("<ERR> 存取遠端 RMI 錯誤！", e);
        } catch (Exception e) {
            Log.error("<ERR> 錯誤！", e);
        }
        return 0;
    }

    public int getReaderType(String sInputReader) {
        int iValue = READER_TYPE_DEFAULT;
        Integer value = null;
        String sReaderName = sInputReader.substring(0, (sInputReader.lastIndexOf(' ') > 0 ? sInputReader.lastIndexOf(' ') : sInputReader.length()));

        if ((value = (Integer) ReaderTypeMap.get(sReaderName)) == null) {
            Log.error("<ERR> ReaderName " + sReaderName + " doesn't Found!");

            Log.info("Insert Reader Use Default Type, Result = " + doInsertReaderTypeData(SP_Cmd_05, sReaderName));

            return READER_TYPE_DEFAULT;
        }
        iValue = value.intValue();
        if ((iValue < READER_TYPE_RANGE_MIN) || (iValue > READER_TYPE_RANGE_MAX)) {
            Log.error("【ERR】Reader " + sReaderName + " value " + iValue + " out of Range !!!");
            return READER_TYPE_DEFAULT;
        }
        return iValue;
    }

    public Integer getPCodeProc(int iReaderType, String sPCodeInput) {
        int iValue = READER_TYPE_DEFAULT;
        Integer value = null;
        String sPCode = sPCodeInput.trim();
        Hashtable mapTemp = null;

        if ((mapTemp = (Hashtable) TypeLinkMap.get(new Integer(iReaderType))) == null) {
            Log.error("<ERR> ReaderType " + iReaderType + " doesn't Found!");
            return new Integer(READER_TXN_DEFAULT);
        }

        if ((value = (Integer) mapTemp.get(sPCode)) == null) {
            Log.error("<ERR> ReaderType " + iReaderType + " with PCode " + sPCode + " doesn't Found!");
            return new Integer(READER_TXN_DEFAULT);
        }

        iValue = value.intValue();
        if ((iValue < READER_TXN_RANGE_MIN) || (iValue > READER_TXN_RANGE_MAX)) {
            Log.error("【ERR】ReaderType " + iReaderType + " with PCode " + sPCode + " TXN_ProceType" + iValue + " out of range !");
            return new Integer(READER_TXN_DEFAULT);
        }
        return value;
    }

    public int getReaderTypeByDB(String sInputReader) {
        ArrayList alRtnData = null;
        ArrayList alCondition = new ArrayList();
        TReaderType readertype = null;
        String sReaderName = sInputReader.substring(0, (sInputReader.lastIndexOf(' ') > 0 ? sInputReader.lastIndexOf(' ') : sInputReader.length()));

        alCondition.add(sReaderName);
        alRtnData = getRmiReaderTypeData(SP_Cmd_03, alCondition);
        if (alRtnData != null && alRtnData.size() > 0) {
            readertype = (TReaderType) alRtnData.get(0);
            return Integer.parseInt(readertype.getReaderType());
        }
        return READER_TYPE_DEFAULT;
    }

    public Integer getPCodeProcByDB(int iReaderType, String sPCodeInput) {
        ArrayList alRtnData = null;
        ArrayList alCondition = new ArrayList();
        TReaderProc readerproc = null;

        alCondition.add(String.valueOf(iReaderType));
        alCondition.add(sPCodeInput);
        alRtnData = getRmiReaderProcData(SP_Cmd_04, alCondition);
        if (alRtnData != null && alRtnData.size() > 0) {
            readerproc = (TReaderProc) alRtnData.get(0);
            return new Integer(readerproc.getTxnType());
        }
        return new Integer(READER_TXN_DEFAULT);
    }

    protected void putReaderType(String sReaderName, Integer IType) {
        Integer origin = null;

        if (sReaderName.length() > TB_READERTYPE_NAME_LEN) {
            Log.error("<ERR> ReaderName 【" + sReaderName + "】from DB too long.");
            return;
        }

        if ((origin = (Integer) ReaderTypeMap.put(sReaderName, IType)) != null) {
            Log.error("Update 【" + sReaderName + "】 from " + origin.toString() + " to " + IType);
        } else {
            Log.debug("Insert 【" + sReaderName + "】 values " + IType);
        }
    }

    protected void putTmlType(String sReaderName, Integer IType) {
        Integer origin = null;

        if (sReaderName.length() > TB_READERTYPE_NAME_LEN) {
            Log.error("<ERR> ReaderName 【" + sReaderName + "】from DB too long.");
            return;
        }

        if ((origin = (Integer) TmlTypeMap.put(sReaderName, IType)) != null) {
            Log.error("Update 【" + sReaderName + "】 from " + origin.toString() + " to " + IType);
        } else {
            Log.debug("Insert 【" + sReaderName + "】 values " + IType);
        }
    }

    private Hashtable showReaderMap() {
        return ReaderTypeMap;
    }

    public static void main(String[] saArgs) {
        String sReaderName = null;
        ReaderMapServiceImpl myTest = new ReaderMapServiceImpl();
        myTest.init();
        Enumeration names = myTest.showReaderMap().keys();
        while (names.hasMoreElements() == true) {
            sReaderName = (String) names.nextElement();
            System.out.println(sReaderName + "\t-->\t" + myTest.getReaderType(sReaderName));
        }
    }

}
