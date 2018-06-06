package com.iisigroup.colabase.webatm.parameter;

import java.io.Serializable;

public class BankData implements Serializable {
    private static final long serialVersionUID = -3415052304154324440L;

    private int iBankID;
    private String sBankName;
    private int iBankType;
    private int iBankStatus = 1;

    public BankData(int iBankID, String sBankName, int iBankType) {
        this.iBankID = iBankID;
        this.sBankName = sBankName;
        this.iBankType = iBankType;
    }

    public BankData(int iBankID, String sBankName, int iBankType, int iBankStatus) {
        this.iBankID = iBankID;
        this.sBankName = sBankName;
        this.iBankType = iBankType;
        this.iBankStatus = iBankStatus;
    }

    public int getID() {
        return iBankID;
    }

    public String getName() {
        return sBankName;
    }

    public int getType() {
        return iBankType;
    }

    public int getStatus() {
        return iBankStatus;
    }

    public String toString() {
        return new String("" + iBankID + " " + sBankName + "; Type:" + iBankType + " Status:" + iBankStatus);
    }

    public static final int DEFAULT_BANK_STATUS = 1;
    public static final int DEFAULT_BANK_TYPE = 9;
}
