
package com.iisigroup.colabase.webatm.service.impl;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citi.webatm.rmi.TActionCode;
import com.citi.webatm.rmi.TActionCodeMgr;
import com.iisigroup.colabase.webatm.service.APSystemService;
import com.iisigroup.colabase.webatm.service.ActCodeMapService;
import com.iisigroup.colabase.webatm.service.ConstMember;

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
 * 20060613 新增：更改原先的 Mapping 為四種 1.ActionCodeMap：讀卡機名稱 vs 真正的讀卡機型態(ActionCode) 2.ATMTypeMap ：讀卡機名稱 vs 送給前端的讀卡機型態(ATMType) 3.TypeLinkMap ：真正的讀卡機型態 vs 該讀卡機的 procMap 4.PCodeProcMap ：每個讀卡機型態都有自己的
 * procMap，內容為 交易代碼 P-Code vs 特殊處理型式
 */
@Service
public class ActCodeMapServiceImpl implements ActCodeMapService, ConstMember {

    protected final Logger Log = LoggerFactory.getLogger(getClass());

    private HashMap ActionCodeMap = new HashMap();

    private final static String SP_Cmd_01 = "SP_ActionCode_sel_all";
    private final static String SP_Cmd_02 = "SP_ActionCode_sel_one";

    @Autowired
    private APSystemService hsmService;

    @PostConstruct
    public void init() {
        ArrayList alCondition = new ArrayList();

        synchronized (ActionCodeMap) {
            ActionCodeMap = getRmiActionCodeData(SP_Cmd_01, alCondition);
        }

        System.gc();
        Log.info("Init Action-Code Map Finished.【" + ActionCodeMap.size() + "】inserted.");
    }

    private HashMap getRmiActionCodeData(String sSPName, ArrayList alCondition) {
        TActionCodeMgr ActCodeMgr;
        HashMap hmRtnData = null;
        try {
            ActCodeMgr = (TActionCodeMgr) Naming.lookup("rmi://" + hsmService.getRmiSrvName() + ":" + hsmService.getRmiSrvPort() + "/TActionCode");
            hmRtnData = ActCodeMgr.getBeanMap_SP_ActionCode(sSPName, alCondition, "getRespCode");
        } catch (RemoteException e) {
            Log.error("<ERR> 存取遠端 RMI 錯誤！", e);
        } catch (Exception e) {
            Log.error("<ERR> 錯誤！", e);
        }
        return hmRtnData;
    }

    public TActionCode getActionCode(String sRespCode) {

        return (TActionCode) ActionCodeMap.get(sRespCode);
    }

    public TActionCode getActionCodeByDB(String sRespCode) {

        ArrayList alRtnData = null;
        ArrayList alCondition = new ArrayList();
        alCondition.add(sRespCode);

        TActionCodeMgr ActCodeMgr;
        try {
            ActCodeMgr = (TActionCodeMgr) Naming.lookup("rmi://" + hsmService.getRmiSrvName() + ":" + hsmService.getRmiSrvPort() + "/TActionCode");
            alRtnData = ActCodeMgr.getArrayList_SP_ActionCode(SP_Cmd_02, alCondition);
            if (alRtnData != null && alRtnData.size() == 1) {
                return (TActionCode) alRtnData.get(0);
            }
        } catch (RemoteException e) {
            Log.error("<ERR> 存取遠端 RMI 錯誤！", e);
        } catch (Exception e) {
            Log.error("<ERR> 錯誤！", e);
        }
        return null;
    }

    public static void main(String[] saArgs) {
        ActCodeMapServiceImpl myTest = new ActCodeMapServiceImpl();
        myTest.init();
        System.out.println(myTest.getActionCode("00000").getDscpEN());
        System.out.println(myTest.getActionCodeByDB("00000").getDscpEN());
    }

}
