package com.iisigroup.colabase.webatm.service;

import java.util.ArrayList;

import com.iisigroup.colabase.webatm.parameter.BankData;


public interface BankParameterService {
    public void init();

    public ArrayList getBankInfoArrayByDB();

    public BankData getBankData(Integer IBankID);

    public BankData getBankData(int iBankID);

    public BankData getBankData(String sBankID);

    public BankData getBankDataByDB(int iBankID);

    public BankData getBankDataByDB(String sBankID);

    public ArrayList getBankInfoArray();
}
