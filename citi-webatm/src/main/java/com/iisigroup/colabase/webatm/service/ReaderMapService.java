package com.iisigroup.colabase.webatm.service;

public interface ReaderMapService {
    public void init();

    public int getReaderTypeByDB(String sInputReader);

    public int getReaderType(String sInputReader);

    public Integer getPCodeProc(int iReaderType, String sPCodeInput);

    public Integer getPCodeProcByDB(int iReaderType, String sPCodeInput);
}
