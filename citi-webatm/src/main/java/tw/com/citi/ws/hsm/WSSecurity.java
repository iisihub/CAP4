package tw.com.citi.ws.hsm;

/*
import tw.com.citi.ws.hsm.DoDESRequest;
import tw.com.citi.ws.hsm.DoDESResponse;
import tw.com.citi.ws.hsm.DoUnDESRequest;
import tw.com.citi.ws.hsm.DoUnDESResponse;
import tw.com.citi.ws.hsm.HsmService_PortType;
import tw.com.citi.ws.hsm.HsmService_ServiceLocator;
import tw.com.citi.ws.hsm.ImportKeyRequest;
import tw.com.citi.ws.hsm.ImportKeyResponse;
*/

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.UnknownHostException;

import com.citi.rmi.hsm.HsmServiceSOAPMgr;
import com.iisigroup.colabase.webatm.toolkit.CommonCryptoUtils;
import com.iisigroup.colabase.webatm.toolkit.Misc;

import tw.com.iisi.desserver.execption.SAPIException;

public class WSSecurity {
    private String WS_ADDR;
    private String SysID;
    private String CheckCode;
    private String Slot;
    private String User;
    private String Passwd;
    private String KeyName;
    private String KeyType;
    private String Data1;
    private String Data2;
    private String KeyNameCD;
    private String KeyTypeCD;
    private String KeyValue;
    private String Kcv;

    HsmServiceSOAPMgr Remote_mgr;

    private String TraceNo;
    private String Rv;
    private String RvString;

    public WSSecurity() {

    }

    public WSSecurity(String ws_addr, String slot, String sysid, String checkcode, String user, String passwd) {
        // 因為怕別處有設Proxy存取，在此先停掉透過Proxy機制
        System.setProperty("proxySet", "false");
        System.setProperty("https.proxyHost", "");
        System.setProperty("https.proxyPort", "");
        System.setProperty("http.proxyHost", "");
        System.setProperty("http.proxyPort", "");
        Authenticator.setDefault(null);

        WS_ADDR = ws_addr;
        SysID = slot;
        CheckCode = sysid;
        Slot = slot;
        User = user;
        Passwd = passwd;

        try {
            Remote_mgr = (HsmServiceSOAPMgr) Naming.lookup(WS_ADDR + "/HsmServiceSOAP");
        } catch (UnknownHostException uhe) {
            System.out.println("The host computer name you have specified, 127.0.0.1 does not match your real computer name.");
        } catch (RemoteException re) {
            System.out.println("A Remote Exception was thrown when requesting the RemoteModelManager Server");
            System.out.println("" + re);
        } catch (MalformedURLException mURLe) {
            System.out.println("There is a problem with the rmi: URL you are using");
            System.out.println("" + mURLe);
        } catch (NotBoundException nbe) {
            nbe.printStackTrace();
            System.out.println("" + nbe);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("" + e);
        }
    }

    public String getSync(String keytype, String keyname, String data) {
        return getComponentMAC(keytype, keyname, data, "");
    }

    public String getMAC(String keytype, String keyname, String inData1, String inData2) {
        byte[] data1 = Misc.hex2Bin(inData1.getBytes());
        byte[] data2 = Misc.hex2Bin(inData2.getBytes());
        byte[] data = new byte[8];
        for (int i = 0; i < 8; i++)
            data[i] = (byte) (data1[i] ^ data2[i]);

        return getComponentMAC(keytype, keyname, new String(Misc.bin2Hex(data)), "");
    }

    public String getChallengeMAC(String keytype, String keyname, String data1, String data2) {
        return getComponentMAC(keytype, keyname, data1, data2);
    }

    public String getComponentMAC(String keytype, String keyname, String data1, String data2) {
        // HsmService_PortType service;
        try {
            /*
             * HsmService_ServiceLocator hssLocator = new HsmService_ServiceLocator(); hssLocator.setHsmServiceSOAPEndpointAddress(this.WS_ADDR); service = hssLocator.getHsmServiceSOAP();
             */

            com.citi.rmi.hsm.DoDESRequest desRequest = new com.citi.rmi.hsm.DoDESRequest();
            desRequest.setSlot(this.Slot);
            desRequest.setSysID(this.SysID);
            desRequest.setCheckCode(this.CheckCode);
            desRequest.setUser(this.User);
            desRequest.setPasswd(this.Passwd);
            desRequest.setKeyType(keytype);
            desRequest.setKeyName(keyname);
            desRequest.setData1(data1);
            desRequest.setData2(data2);

            com.citi.rmi.hsm.DoDESResponse desResponse = Remote_mgr.doDES(desRequest);
            // DoDESResponse desResponse = service.doDES(desRequest);
            System.out.println("==========================");
            System.out.println(desResponse.getData1());
            System.out.println(desResponse.getData2());
            System.out.println(desResponse.getTraceno());
            System.out.println(desResponse.getReturnData());
            System.out.println(desResponse.getRv());
            System.out.println(desResponse.getRvString());
            this.setTraceNo(desResponse.getTraceno());
            this.setRv(desResponse.getRv());
            this.setRvString(desResponse.getRvString());
            System.out.println("==========================");
            if (desResponse.getRv() == null || !desResponse.getRv().equals("0")) {
                return null;
            }
            return desResponse.getReturnData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String do3DESDecrypt(String KeyType, String KeyName, String Data1, String Data2) {
        // HsmService_ServiceLocator ServiceLocator;
        // HsmService_PortType service;

        try {
            /*
             * ServiceLocator = new HsmService_ServiceLocator(); ServiceLocator.setHsmServiceSOAPEndpointAddress(this.WS_ADDR); service =ServiceLocator.getHsmServiceSOAP();
             */

            com.citi.rmi.hsm.DoUnDESRequest desRequest = new com.citi.rmi.hsm.DoUnDESRequest();
            desRequest.setSlot(this.Slot);
            desRequest.setSysID(this.SysID);
            desRequest.setCheckCode(this.CheckCode);
            desRequest.setUser(this.User);
            desRequest.setPasswd(this.Passwd);
            desRequest.setKeyType(KeyType);
            desRequest.setKeyName(KeyName);
            desRequest.setData1(Data1);
            desRequest.setData2(Data2);

            com.citi.rmi.hsm.DoUnDESResponse desResponse = Remote_mgr.doUnDES(desRequest);
            // DoUnDESResponse desResponse = service.doUnDES(desRequest);
            String rtString = new String(Misc.hex2Bin(desResponse.getReturnData().getBytes()));
            rtString = rtString.trim();
            /*
             * System.out.println(desResponse.getData1()); System.out.println(desResponse.getData2()); System.out.println(desResponse.getTraceno()); System.out.println(desResponse.getReturnData());
             * System.out.println(rtString); System.out.println(desResponse.getRv()); System.out.println(desResponse.getRvString());
             */
            return rtString;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Decrypt long hex value.
     * 
     * @param keyType
     * @param keyName
     * @param hexData
     * @return
     */
    public String do3DESDecrypt(String keyType, String keyName, String hexData) {
        byte[] data1 = Misc.hex2Bin((hexData).getBytes());
        StringBuilder result = new StringBuilder();
        for (int i = 0; (i < data1.length / 24) && (data1.length % 24 == 0); i++) {
            byte[] data = new byte[24];
            System.arraycopy(data1, 0 + i * 24, data, 0, 24);
            result.append(do3DESDecrypt(keyType, keyName, new String(Misc.bin2Hex(data)), null));
        }
        return result.toString();
    }

    /**
     * Decrypt long hex value by keyValue
     * 
     * @param keyValue
     * @param hexData
     * @return
     * @throws SAPIException
     */
    public String do3DESDecryptByValue(byte[] keyValue, String hexData) throws SAPIException {
        byte[] data1 = Misc.hex2Bin((hexData).getBytes());
        StringBuilder result = new StringBuilder();
        for (int i = 0; (i < data1.length / 24) && (data1.length % 24 == 0); i++) {
            byte[] data = new byte[24];
            System.arraycopy(data1, 0 + i * 24, data, 0, 24);
            result.append(CommonCryptoUtils.decryptByKeyValueForAWATM(new String(Misc.bin2Hex(data)), keyValue));
        }
        return result.toString();
    }

    public String do3DESEncrypt(String KeyType, String KeyName, String InData) {
        // HsmService_ServiceLocator ServiceLocator;
        // HsmService_PortType service;

        try {
            /*
             * ServiceLocator = new HsmService_ServiceLocator(); ServiceLocator.setHsmServiceSOAPEndpointAddress(this.WS_ADDR); service =ServiceLocator.getHsmServiceSOAP();
             */
            com.citi.rmi.hsm.DoDESRequest desRequest = new com.citi.rmi.hsm.DoDESRequest();
            desRequest.setSlot(this.Slot);
            desRequest.setSysID(this.SysID);
            desRequest.setCheckCode(this.CheckCode);
            desRequest.setUser(this.User);
            desRequest.setPasswd(this.Passwd);
            desRequest.setKeyType(KeyType);
            desRequest.setKeyName(KeyName);
            desRequest.setData1(InData);
            desRequest.setData2("");

            com.citi.rmi.hsm.DoDESResponse desResponse = Remote_mgr.doDES(desRequest);
            // DoDESResponse desResponse = service.doDES(desRequest);

            System.out.println(desResponse.getData1());
            System.out.println(desResponse.getTraceno());
            System.out.println(desResponse.getReturnData());
            System.out.println(desResponse.getRv());
            System.out.println(desResponse.getRvString());
            if (desResponse.getRv() == null || !desResponse.getRv().equals("0")) {
                return null;
            }
            return desResponse.getReturnData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public boolean importKey(String keytype, String keyname, String keytypecd, String keynamecd, String keyvalue, String kcv) {
        // HsmService_PortType service;
        try {
            /*
             * HsmService_ServiceLocator hssLocator = new HsmService_ServiceLocator(); hssLocator.setHsmServiceSOAPEndpointAddress(this.WS_ADDR); service = hssLocator.getHsmServiceSOAP();
             */
            com.citi.rmi.hsm.ImportKeyRequest importkeyRequest = new com.citi.rmi.hsm.ImportKeyRequest();
            importkeyRequest.setSlot(this.Slot);
            importkeyRequest.setSysID(this.SysID);
            importkeyRequest.setCheckCode(this.CheckCode);
            importkeyRequest.setUser(this.User);
            importkeyRequest.setPasswd(this.Passwd);
            importkeyRequest.setKeyType(keytype);
            importkeyRequest.setKeyName(keyname);
            importkeyRequest.setKeyTypeCD(keytypecd);
            importkeyRequest.setKeyNameCD(keynamecd);
            importkeyRequest.setKeyValue(keyvalue);
            importkeyRequest.setKcv(kcv);

            com.citi.rmi.hsm.ImportKeyResponse importkeyResponse = Remote_mgr.importKey(importkeyRequest);
            // ImportKeyResponse importkeyResponse = service.importKey(importkeyRequest);
            /*
             * System.out.println(importkeyResponse.getTraceno()); System.out.println(importkeyResponse.isResult()); System.out.println(importkeyResponse.getRv());
             * System.out.println(importkeyResponse.getRvString());
             */
            this.setTraceNo(importkeyResponse.getTraceno());
            this.setRv(importkeyResponse.getRv());
            this.setRvString(importkeyResponse.getRvString());
            if (importkeyResponse.getRv() == null || !importkeyResponse.getRv().equals("0")) {
                return false;
            }
            return importkeyResponse.isResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @return Returns the rv.
     */
    public String getRv() {
        return Rv;
    }

    /**
     * @param rv
     *            The rv to set.
     */
    public void setRv(String rv) {
        Rv = rv;
    }

    /**
     * @return Returns the rvString.
     */
    public String getRvString() {
        return RvString;
    }

    /**
     * @param rvString
     *            The rvString to set.
     */
    public void setRvString(String rvString) {
        RvString = rvString;
    }

    /**
     * @return Returns the traceNo.
     */
    public String getTraceNo() {
        return TraceNo;
    }

    /**
     * @param traceNo
     *            The traceNo to set.
     */
    public void setTraceNo(String traceNo) {
        TraceNo = traceNo;
    }
}
