package com.iisigroup.colabase.webatm.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.citi.webatm.rmi.TActionCode;

import com.iisigroup.colabase.webatm.parameter.BankData;;

public interface APSystemService {

    public String getRmiSrvName();

    public String getRmiSrvPort();

    public HashMap getSYS_PRAM_MAP();

    public String getCOMM_KEY_TYPE();

    public String getInitB_KEY();

    public String getWorkB_KEY();

    public String[] getValue(String sCodeItem, String sCodeID);

    public int getReaderType(String reader);

    public int getWaitSleepTime();

    public BankData getBankData(String sBankID);

    public TActionCode getActionCode(String respcode);

    public Integer getPCodeProc(int iReaderType, String trnsCode);

    public ArrayList getBankInfoArray();

    public ArrayList getTSysCodeArray(String sCodeItem);

}
