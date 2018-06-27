
package com.iisigroup.colabase.webatm.service.impl;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citi.webatm.rmi.TBankInfo;
import com.citi.webatm.rmi.TBankInfoMgr;
import com.iisigroup.colabase.webatm.service.APSystemService;
import com.iisigroup.colabase.webatm.service.BankParameterService;
import com.iisigroup.colabase.webatm.service.ConstMember;

import com.iisigroup.colabase.webatm.parameter.BankData;

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
@Service
public class BankParameterServiceImpl implements BankParameterService, ConstMember {
    protected final Logger Log = LoggerFactory.getLogger(getClass());

    private Hashtable BankInfoMap = new Hashtable();
    private ArrayList BankInfoArray = null;
    private final static String SP_Cmd_01 = "SP_BankInfo_sel_all";
    private final static String SP_Cmd_02 = "SP_BankInfo_sel_one";
    @Autowired
    private APSystemService hsmService;

    @PostConstruct
    public void init() {

        TBankInfo bankinfo;
        ArrayList alRtnData = null;
        ArrayList alCondition = new ArrayList();
        alRtnData = getRmiBankData(SP_Cmd_01, alCondition);

        synchronized (BankInfoMap) {
            BankInfoMap.clear();
            for (int i = 0; i < alRtnData.size(); i++) {
                bankinfo = (TBankInfo) alRtnData.get(i);
                putBankData(bankinfo.getBankCode(), bankinfo.getBankName(), bankinfo.getBankType(), bankinfo.getBankStatus());
            }
        }
        System.gc();
        Log.info("BankInfo Map Finished.【" + alRtnData.size() + "】inserted.");

        // BankInfoArray = new ArrayList();

        try {
            TreeMap typeMap = parseTypeMap(alRtnData);
            BankInfoArray = addBankInfoArray(typeMap);

        } catch (Exception e) {
            Log.error("<ERR> 當在產製 BankInfoArray 時，發生不可能發生的例外！", e);
        }
    }

    private void putBankData(String sBankID, String sBankName, String sBankType, String sBankStatus) {
        try {
            BankData bankTemp = new BankData(Integer.parseInt(sBankID), sBankName.trim(), Integer.parseInt(sBankType), Integer.parseInt(sBankStatus));
            if (BankInfoMap.put(Integer.valueOf(sBankID), bankTemp) != null) {
                Log.error("<ERR> 資料庫 BankInfo + TBankInfo 所得的銀行代碼 " + sBankID + " 有重覆資料出現");
            }
            bankTemp = null;
        } catch (NumberFormatException en) {
            Log.error("<ERR> 資料庫 BankInfo + TBankInfo 資料：" + "Bank_ID <" + sBankID + ">, Bank_Name <" + sBankName + ">, Bank_Type <" + sBankType + ">, Bank_STTS <" + sBankStatus + "> 發生錯誤！");
        }
    }

    private ArrayList getRmiBankData(String sSPName, ArrayList alCondition) {
        TBankInfoMgr bankinfoMgr;
        ArrayList alRtnData = null;
        try {
            bankinfoMgr = (TBankInfoMgr) Naming.lookup("rmi://" + hsmService.getRmiSrvName() + ":" + hsmService.getRmiSrvPort() + "/TBankInfo");
            alRtnData = bankinfoMgr.getArrayList_SP_BankInfo(sSPName, alCondition);
        } catch (RemoteException e) {
            Log.error("<ERR> 存取遠端 RMI 錯誤！", e);
        } catch (Exception e) {
            Log.error("<ERR> 錯誤！", e);
        }
        return alRtnData;
    }

    private TreeMap parseTypeMap(ArrayList alRtnData) {
        TreeMap typeMap = new TreeMap();
        TreeMap groupMap = null;
        TBankInfo bankTemp = null;
        String sType = null;

        for (int i = 0; i < alRtnData.size(); i++) {
            bankTemp = (TBankInfo) alRtnData.get(i);

            sType = bankTemp.getBankType();
            if (typeMap.containsKey(sType)) {
                groupMap = (TreeMap) typeMap.get(sType);
            } else {
                groupMap = new TreeMap();
                typeMap.put(sType, groupMap);
            }
            if (groupMap.put(bankTemp.getBankCode(), bankTemp) != null) {
                Log.error("<ERR> Bank_ID " + bankTemp.getBankCode() + " 有重覆的值出現！");
                continue;
            }
        }
        return typeMap;
    }

    private ArrayList addBankInfoArray(TreeMap typeMap) {
        Iterator itType = typeMap.values().iterator();
        Iterator itBank = null;
        ArrayList typeArray = null;
        ArrayList bankInfoArray = new ArrayList();
        while (itType.hasNext()) {
            itBank = ((TreeMap) itType.next()).values().iterator();
            typeArray = new ArrayList();

            while (itBank.hasNext()) {
                typeArray.add((TBankInfo) itBank.next());
            }
            bankInfoArray.add(typeArray);
        }
        return bankInfoArray;
    }

    public BankData getBankData(Integer IBankID) {

        return (BankData) BankInfoMap.get(IBankID);
    }

    public BankData getBankData(int iBankID) {

        return getBankData(new Integer(iBankID));
    }

    public BankData getBankData(String sBankID) {
        // 20090727 modify if haven't match bankcode,then return new bankdata with blank data
        // return (BankData )BankInfoMap.get(Integer.valueOf(sBankID));
        BankData oBankData = (BankData) BankInfoMap.get(Integer.valueOf(sBankID));
        if (oBankData == null)
            return new BankData(Integer.valueOf(sBankID).intValue(), "", 0);
        else
            return (BankData) BankInfoMap.get(Integer.valueOf(sBankID));
    }

    public BankData getBankDataByDB(int iBankID) {

        return getBankDataByDB(String.valueOf(iBankID));
    }

    public BankData getBankDataByDB(String sBankID) {
        ArrayList alRtnData = null;
        ArrayList alCondition = new ArrayList();
        TBankInfo bankinfo = null;
        BankData bd = null;
        alCondition.add(sBankID);
        alRtnData = getRmiBankData(SP_Cmd_02, alCondition);
        if (alRtnData != null && alRtnData.size() > 0) {
            bankinfo = (TBankInfo) alRtnData.get(0);
            bd = new BankData(Integer.parseInt(bankinfo.getBankCode()), bankinfo.getBankName(), Integer.parseInt(bankinfo.getBankType()), Integer.parseInt(bankinfo.getBankStatus()));

            return bd;
        }
        return null;
    }

    public ArrayList getBankInfoArray() {

        return BankInfoArray;
    }

    public ArrayList getBankInfoArrayByDB() {
        ArrayList alRtnData = null;
        ArrayList alCondition = new ArrayList();
        alRtnData = getRmiBankData(SP_Cmd_01, alCondition);

        try {
            TreeMap typeMap = parseTypeMap(alRtnData);
            return addBankInfoArray(typeMap);
        } catch (Exception e) {
            Log.error("<ERR> 當在產製 BankInfoArray 時，發生不可能發生的例外！", e);
            return null;
        }
    }

    public Enumeration getBankList() {
        return BankInfoMap.keys();
    }

}
