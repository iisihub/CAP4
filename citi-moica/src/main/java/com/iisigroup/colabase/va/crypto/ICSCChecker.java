package com.iisigroup.colabase.va.crypto;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Proxy;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bouncycastle.asn1.x509.X509CertificateStructure;

/**
 * 提供自然人憑證(MOICA)身份驗證功能 使用該功能需已成功申請內政部憑證及已開通驗證功能
 * 
 */
@SuppressWarnings("rawtypes")
public class ICSCChecker {

    private String ErrorMsg = "";
    private String sClientCertPath = null;
    private PrivateKey privkey = null;
    private String sIP = "", port = "", URI = "";

    /**
     * 回傳內政部回應訊息
     * 
     * @return
     */
    public String getErrorMsg() {
        return ErrorMsg;
    }

    /**
     * 設定內政部提供之憑證路徑，該憑證需是DER格式之X509憑證
     * 
     * @param path
     *            憑證路徑
     */
    public void setClientCertPath(String path) {
        sClientCertPath = path;
    }

    /**
     * 設定私密金鑰路徑及密碼，該私密金鑰為JKS格式
     * 
     * @param sKeyFilePath
     *            私密金鑰路徑
     * @param sPassword
     *            密碼
     * @return 是否成功
     */
    public boolean loadRSAkey(String sKeyFilePath, String sPassword) {
        boolean ret = false;
        do {
            FileInputStream fis = null;
            try {
                // CryptoLibrary.checkProvider();
                fis = new FileInputStream(sKeyFilePath);
                KeyStore ks = KeyStore.getInstance("PKCS12");
                ks.load(fis, sPassword.toCharArray());
                Enumeration en = ks.aliases();
                // 抓取Alias
                String szAlias = (String) en.nextElement();
                privkey = (PrivateKey) ks.getKey(szAlias, sPassword.toCharArray());
                ret = true;
            } catch (FileNotFoundException ex) {
                // Logger.getLogger(checkICSC.class.getName()).log(Level.SEVERE,
                // null, ex);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(ICSCChecker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } while (false);
        return ret;
    }

    /**
     * 設定連接內政部之IP、Port及URI位置
     * 
     * @param sIP
     *            內政部之IP
     * @param port
     *            內政部之Port
     * @param URI
     *            URI位置
     */
    public void setURL(String sIP, String port, String URI) {
        this.sIP = sIP;
        this.port = port;
        this.URI = URI;

    }

    /**
     * 驗證EE憑證與ID是否為同一身份
     * 
     * @param ee
     *            欲驗證之憑證
     * @param ID
     *            欲驗證之身份證字號
     * @return 0為成功，其他參考ErrorCode
     */
    public int checkMOICAICSC(X509CertificateStructure ee, String ID, Proxy proxy) {
        int ret = 0;
        do {
            String SN = null;
            try {
                CryptoLibrary.checkProvider();
                String SubjectDN = CryptoLibrary.getX509Subject(ee);
                if (SubjectDN.indexOf("SERIALNUMBER=") >= 0) {
                    // split ,
                    String[] split1 = SubjectDN.split(",");
                    for (int i = 0; i < split1.length; i++) {
                        if (split1[i].indexOf("SERIALNUMBER=") >= 0) {
                            String[] split2 = split1[i].split("=");
                            SN = split2[1];
                            break;
                        }

                    }
                } else {
                    ret = CryptoLibrary.ERROR_MOICAICSC_CERT;
                    break;
                }
                ICSCJApi icscapi = new ICSCJApi();

                icscapi.IDSN = new String[1][2];
                icscapi.IDSN[0][0] = SN;
                icscapi.IDSN[0][1] = ID;
                icscapi.iCount = 1;
                byte[] bSignature = null;

                try {
                    // 呼叫 iMake_ToBeSignedData 擷取帶入憑證的姆指紋及先前資待驗資料產生待簽資料封包
                    ret = icscapi.iMake_ToBeSignedData(sClientCertPath);
                    if (ret != 0) {
                        if (ret == -1) {
                            ErrorMsg = "icscapi.iMake_ToBeSignedData 讀取客戶端憑證檔失敗, 可能是找不到該檔或讀取失敗.";

                        }
                        if (ret == -2) {
                            ErrorMsg = "icscapi.iMake_ToBeSignedData 無法產生憑證檔的拇指紋.";
                        }
                        ret = CryptoLibrary.Error_CALL_HiSecureAPI;
                        break;
                    }
                    // Sign process
                    Signature sigEngine = Signature.getInstance("SHA1withRSA", "BC");
                    sigEngine.initSign(privkey);
                    sigEngine.update(icscapi.ToBeSignedData.getBytes());
                    bSignature = sigEngine.sign();
                } catch (Exception ex) {
                    ErrorMsg = ex.toString();
                    ex.printStackTrace();
                    ret = CryptoLibrary.Error_CALL_HiSecure_Exception;
                    break;
                }

                // 發送Query並接s收結果
                ret = icscapi.iQuery(sIP, Integer.parseInt(port), URI, bSignature, proxy);
                if (ret == 0) {
                    ret = icscapi.iQueryResult[0];
                    if (ret == 0) {
                        ErrorMsg = "表示此身份證字號，有對應的已經開卡憑證(OK)";

                    } else if (ret == 1) {
                        ErrorMsg = "表示此身份證字號，沒有對應此SerialNumber的憑證 (NOT OK)";
                    } else if (ret == 2) {
                        ErrorMsg = "表示此身份證字號，只有未開卡的對應憑證 (NOT OK)\n 999: 查詢紀錄時,資料庫端錯誤 (該筆資料必須再送Server查詢一次)";
                    }

                } else if (ret > 0) { // API回應大於0的數值代表 API 執行完畢, 收到Server的回應,
                                      // 但無法完成解析Server回應的封包
                    switch (ret) {
                    case 2001:
                        ErrorMsg = "2001: CERR_ICSResponse_NotFound, 伺服端的回應封包內找不到<ICSResponse>標籤";
                        break;
                    case 2002:
                        ErrorMsg = "2002: CERR_ClientID_NotFound, 伺服端的回應封包內找不到<ClientID>標籤";
                        break;
                    case 2003:
                        ErrorMsg = "2003: CERR_ClientID_Incorrect, 回應封包<Client>標籤內的字串和送出的ClientID不一致";
                        break;
                    case 2004:
                        ErrorMsg = "2004: CERR_Nonce_NotFound, 伺服端的回應封包內找不到<Nonce>標籤";
                        break;
                    case 2005:
                        ErrorMsg = "2005: CERR_Nonce_Incorrect, 回應封包<Nonce>標籤內的字串和送出的Nonce不一致";
                        break;
                    case 2006:
                        ErrorMsg = "2006: CERR_ReqTime_NotFound, 伺服端的回應封包內找不到<ReqTime>標籤";
                        break;
                    case 2007:
                        ErrorMsg = "CERR_TotalCount_NotFound, 伺服端的回應封包內找不到<TotalCount>標籤";
                        break;
                    case 2008:
                        ErrorMsg = "2008: CERR_TotalCount_BadFormat, 回應封包<TotalCount>標籤內不是數值";
                        break;
                    case 2009:
                        ErrorMsg = "2009: CERR_TotalCount_BadValue, 回應封包<TotalCount>標籤內的字串和送出的TotalCount不一致或數值小於0或大於1000";
                        break;
                    case 2011:
                        ErrorMsg = "2011: CERR_Rec_Fail, 伺服端的回應封包內找不到<Rec>標籤";
                        break;
                    case 2013:
                        ErrorMsg = "2013: CERR_ID_Fail, 伺服端的回應封包內找不到<ID>標籤";
                        break;
                    case 2014:
                        ErrorMsg = "2014: CERR_SN_Fail, 伺服端的回應封包內找不到<SN>標籤";
                        break;
                    case 2015:
                        ErrorMsg = "2015: CERR_SSID4_Fail, 伺服端的回應封包內找不到<SSID4>標籤";
                        break;
                    case 2016:
                        ErrorMsg = "2016: CERR_Code_Fail, 伺服端的回應封包內找不到<Code>標籤";
                        break;
                    case 2017:
                        ErrorMsg = "2017: CERR_Code_Incorrect, 回應封包<Code>標籤內不是數值";
                        break;
                    default:
                        ErrorMsg = "不在表單內錯誤+(" + ret + ")";
                        break;
                    }
                } else if (ret == -1) { // API 執行完畢, 收到Server回應之錯誤碼,
                                        // 存在iServerReturn內
                    ret = icscapi.iServerReturn;
                    if (ret == 1001)
                        ErrorMsg = "1001: ERR_NoCGIParam,輸入之CGI參數為空白,沒有值.";
                    else if (ret == 1002)
                        ErrorMsg = "1002: ERR_ICSRequest_NotFound,取出<ICSRequest>參數失敗.";
                    else if (ret == 1003)
                        ErrorMsg = "1003: ERR_ICSReq_NotFound,取出<ICSReq>參數失敗.";
                    else if (ret == 1005)
                        ErrorMsg = "1005: ERR_Nonce_NotFound,取出<Nonce>參數失敗.";
                    else if (ret == 1006)
                        ErrorMsg = "1006: ERR_ReqTime_NotFound,取出<ReqTime>參數失敗.";
                    else if (ret == 1007)
                        ErrorMsg = "1007: ERR_TotalCount_NotFound,取出<TotalCount>參數失敗.";
                    else if (ret == 1008)
                        ErrorMsg = "1008: ERR_TotalCount_BadFormat,參數<TotalCount>格式錯誤:非數值.";
                    else if (ret == 1009)
                        ErrorMsg = "1009: ERR_TotalCount_BadValue,參數<TotalCount>格式錯誤:數值超過範圍.";
                    else if (ret == 1011)
                        ErrorMsg = "1011: ERR_ICSReqLength_NotFound,取出<ICSReqLength>參數失敗.";
                    else if (ret == 1012)
                        ErrorMsg = "1012: ERR_ICSReqLength_BadFormat,參數<ICSReqLength>格式錯誤:非數值.";
                    else if (ret == 1013)
                        ErrorMsg = "1013: ERR_ICSReqLength_NotMatch,參數<ICSReqLength>的值和<ICSReq>的長度不同.";
                    else if (ret == 1014)
                        ErrorMsg = "1014: ERR_Signature_NotFound,取出<Signature>參數失敗.";
                    else if (ret == 1015)
                        ErrorMsg = "1015: ERR_Signature_BadFormat,參數<Signature>格式錯誤,無法從HEX格式轉成binary值.";
                    else if (ret == 1020)
                        ErrorMsg = "1020: ERR_ClientID_Incorrect,系統不認可的ClientID,系統無此ClientID的資訊.";
                    else if (ret == 1021)
                        ErrorMsg = "1021: ERR_Verify_Signature_Fail,檢驗client端所做的簽章失敗.";
                    else if (ret == 1022)
                        ErrorMsg = "1022: ERR_ConnectDB_Fail,連線到資料庫失敗.";
                    else if (ret == 1023)
                        ErrorMsg = "1023: ERR_Verify_Validity_Fail,檢驗client端憑證的效期失敗.";
                    else if (ret >= 10000 && ret <= 19999)
                        ErrorMsg = "ERR_Rec_n_Fail,取出第 xxxx 行<Rec>參數失敗(找不到第XXXX筆資料).";
                    else if (ret >= 20000 && ret <= 29999)
                        ErrorMsg = "ERR_ID_n_Fail,取出第 xxxx 個<ID>參數失敗,或資料錯誤,或不是流水號.";
                    else if (ret >= 30000 && ret <= 39999)
                        ErrorMsg = "ERR_SN_n_Fail,取出第 xxxx 個<SN>參數失敗,或資料錯誤.";
                    else if (ret >= 40000 && ret <= 49999)
                        ErrorMsg = "ERR_SSID_n_Fail,取出第 xxxx 個<SSID>參數失敗,或資料錯誤.";
                    else
                        ErrorMsg = "不在表單內錯誤+(" + ret + ")";
                } else if (ret == -2) { // API 執行完畢, 收到Server回應錯誤訊息,
                                        // 但無法解析Server回應的錯誤碼
                    ErrorMsg = "API 執行完畢, 收到Server回應錯誤訊息, 但無法解析Server回應的錯誤碼";
                } else if (ret == -3) { // 收不到Server的回應
                    ErrorMsg = "收不到Server的回應.";
                } else if (ret == -4) { // SSL通道建立失敗或收送資料有誤
                    ErrorMsg = "SSL通道建立失敗或收送資料有誤";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (false);
        return ret;
    }

}
