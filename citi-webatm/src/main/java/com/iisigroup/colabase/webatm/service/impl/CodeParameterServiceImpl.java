
package com.iisigroup.colabase.webatm.service.impl;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citi.webatm.rmi.TSysCode;
import com.citi.webatm.rmi.TSysCodeMgr;
import com.iisigroup.colabase.webatm.service.APSystemService;
import com.iisigroup.colabase.webatm.service.CodeParameterService;
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
@Service
public class CodeParameterServiceImpl implements CodeParameterService, ConstMember {
    protected final Logger Log = LoggerFactory.getLogger(getClass());
    private Hashtable ItemGroupMap = new Hashtable();
    private Hashtable TSysCodeArrMap = null;

    private final static String SP_Cmd_01 = "SP_SysCode_sel_all";
    private final static String SP_Cmd_02 = "SP_SysCode_sel_byItemID";
    private final static String SP_Cmd_03 = "SP_SysCode_sel_byItem";
    @Autowired
    private APSystemService APSystem;
    
    @PostConstruct
    public void init() {

        TSysCode syscode;
        ArrayList alRtnData = null;
        ArrayList alCondition = new ArrayList();

        alRtnData = getRmiSysCodeData(SP_Cmd_01, alCondition);

        // ItemGroupMap = new Hashtable();

        String sItemTemp = "";
        String sIDTemp = null;
        Hashtable IDValueMap = null;
        String[] CodeValue = null;

        synchronized (ItemGroupMap) {
            // ItemGroupMap = null;
            // System.gc();
            // ItemGroupMap = new Hashtable();

            for (int i = 0; i < alRtnData.size(); i++) {
                syscode = (TSysCode) alRtnData.get(i);
                if (sItemTemp.equals(syscode.getCodeItem().trim()) == false) {
                    sItemTemp = syscode.getCodeItem().trim();
                    IDValueMap = new Hashtable();
                    if (ItemGroupMap.put(sItemTemp, IDValueMap) != null) {
                        Log.error("<ERR> Code_Item " + sItemTemp + " DUPLICATED !");
                    }
                }
                sIDTemp = syscode.getCodeID().trim();
                CodeValue = new String[] { syscode.getCodeValue().trim(), syscode.getNote().trim() };
                if (IDValueMap.put(sIDTemp, CodeValue) != null) {
                    Log.error("<ERR> Code_Item " + sItemTemp + " with Code_ID " + sIDTemp + " DUPLICATED !");
                }
            }
        }
        System.gc();
        Log.info("CodeParameter Finished.【" + alRtnData.size() + "】inserted.");

        TSysCodeArrMap = new Hashtable();
        // String sID = null;
        TSysCode CodeTmp = null;
        ArrayList arTemp = null;
        Iterator itID = null;
        Iterator itData = null;
        String[] saData = null;

        try {
            for (int i = 0; i < gsaTSysCodeItem.length; i++) {
                IDValueMap = (Hashtable) ItemGroupMap.get(gsaTSysCodeItem[i]);
                if (IDValueMap == null) {
                    Log.error("<ERR> 在製作 TSysCode Arry 時，輸入的 Code_Item <" + gsaTSysCodeItem[i] + "> 找不到對應的資料");
                    continue;
                }

                arTemp = new ArrayList();
                itID = IDValueMap.keySet().iterator();
                itData = IDValueMap.values().iterator();

                Object[] objID = IDValueMap.keySet().toArray();
                Arrays.sort(objID); // HashTable 的 key 不會照順序所以要將key取出排序

                for (int j = 0; j < objID.length; j++) {
                    CodeTmp = new TSysCode();
                    saData = (String[]) IDValueMap.get(objID[j]);

                    CodeTmp.setCodeItem(gsaTSysCodeItem[i]);
                    CodeTmp.setCodeID((String) objID[j]);
                    CodeTmp.setCodeValue(saData[0]);
                    CodeTmp.setNote(saData[1]);

                    arTemp.add(CodeTmp);
                }
                TSysCodeArrMap.put(gsaTSysCodeItem[i], arTemp);
            }
        } catch (Exception e) {
            Log.error("<ERR> 系統異常，產製 TSysCode Array 發生錯誤！", e);
        }
    }

    public Enumeration listItemGroup() {

        return ItemGroupMap.keys();
    }

    public Enumeration listID(String sCodeItem) {
        Hashtable IDValueMap = (Hashtable) ItemGroupMap.get(sCodeItem.trim());
        return (IDValueMap == null ? null : IDValueMap.keys());
    }

    public String[] getValue(String sCodeItem, String sCodeID) {
        Hashtable IDValueMap = (Hashtable) ItemGroupMap.get(sCodeItem.trim());
        return (IDValueMap == null ? null : (String[]) IDValueMap.get(sCodeID.trim()));
    }

    public ArrayList getTSysCodeArray(String sCode_Item) {

        return (ArrayList) TSysCodeArrMap.get(sCode_Item);
    }

    public Hashtable getIDValueMap(String sCodeItem) {

        return ((Hashtable) ItemGroupMap.get(sCodeItem.trim()));
    }

    private ArrayList getRmiSysCodeData(String sSPName, ArrayList alCondition) {
        TSysCodeMgr syscodeMgr;
        ArrayList alRtnData = null;
        try {
            syscodeMgr = (TSysCodeMgr) Naming.lookup("rmi://" + APSystem.getRmiSrvName() + ":" + APSystem.getRmiSrvPort() + "/TSysCode");
            alRtnData = syscodeMgr.getArrayList_SP_SysCode(sSPName, alCondition);
        } catch (RemoteException e) {
            Log.error("<ERR> 存取遠端 RMI 錯誤！", e);
        } catch (Exception e) {
            Log.error("<ERR> 錯誤！", e);
        }
        return alRtnData;
    }

    public String[] getValueByDB(String sCodeItem, String sCodeID) {
        ArrayList alRtnData = null;
        ArrayList alCondition = new ArrayList();
        TSysCode syscode;
        String[] saRtn = null;
        alCondition.add(sCodeItem);
        alCondition.add(sCodeID);
        alRtnData = getRmiSysCodeData(SP_Cmd_02, alCondition);
        if (alRtnData != null && alRtnData.size() > 0) {
            syscode = (TSysCode) alRtnData.get(0);
            saRtn = new String[2];
            saRtn[0] = syscode.getCodeValue();
            saRtn[1] = syscode.getNote();
            return saRtn;
        }
        return null;
    }

    public ArrayList getTSysCodeArrayByDB(String sCodeItem) {
        ArrayList alRtnData = null;
        ArrayList alCondition = new ArrayList();
        alCondition.add(sCodeItem);
        alRtnData = getRmiSysCodeData(SP_Cmd_03, alCondition);
        if (alRtnData != null && alRtnData.size() > 0) {
            return alRtnData;
        }
        return null;
    }

    public Hashtable getIDValueMapByDB(String sCodeItem) {
        ArrayList alRtnData = null;
        ArrayList alCondition = new ArrayList();
        Hashtable htSysCode = null;
        TSysCode syscode;
        String[] saRtn = new String[2];
        alCondition.add(sCodeItem);
        alRtnData = getRmiSysCodeData(SP_Cmd_03, alCondition);
        if (alRtnData != null && alRtnData.size() > 0) {
            htSysCode = new Hashtable();
            for (int index = 0; index < alRtnData.size(); index++) {
                syscode = (TSysCode) alRtnData.get(index);
                saRtn[0] = syscode.getCodeValue();
                saRtn[1] = syscode.getNote();
                htSysCode.put(syscode.getCodeID(), saRtn);
            }
            return htSysCode;
        }
        return null;
    }

    public static void main(String[] saArgs) {
        CodeParameterServiceImpl sc = new CodeParameterServiceImpl();
        sc.init();
        Enumeration enItem = sc.listItemGroup();
        Enumeration enID = null;
        String sItemTemp = null;
        String sIDTemp = null;
        String[] saValue = null;
        while (enItem.hasMoreElements()) {
            sItemTemp = (String) enItem.nextElement();
            System.out.println("----- " + sItemTemp + " -----");
            enID = sc.listID(sItemTemp);
            while (enID.hasMoreElements()) {
                sIDTemp = (String) enID.nextElement();
                saValue = (String[]) sc.getValue(sItemTemp, sIDTemp);
                System.out.print(sIDTemp);
                for (int i = 0; i < saValue.length; i++) {
                    System.out.print("\t" + saValue[i]);
                }
                System.out.println();
            }
            System.out.println("==========================");
        }
    }

    public static String[] gsaTSysCodeItem = { "BANK_TYPE" };

}
