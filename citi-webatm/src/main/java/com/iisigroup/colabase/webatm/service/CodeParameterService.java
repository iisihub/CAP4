package com.iisigroup.colabase.webatm.service;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public interface CodeParameterService {
    public void init();

    public String[] getValueByDB(String sCodeItem, String sCodeID);

    public Enumeration listItemGroup();

    public Enumeration listID(String sCodeItem);

    public String[] getValue(String sCodeItem, String sCodeID);

    public ArrayList getTSysCodeArray(String sCode_Item);

    public Hashtable getIDValueMap(String sCodeItem);

    public ArrayList getTSysCodeArrayByDB(String sCodeItem);

    public Hashtable getIDValueMapByDB(String sCodeItem);
}
