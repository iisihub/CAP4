package com.iisigroup.colabase.webatm.service;

import com.citi.webatm.rmi.TActionCode;

public interface ActCodeMapService {
    public void init();

    public TActionCode getActionCode(String sRespCode);

    public TActionCode getActionCodeByDB(String sRespCode);
}
