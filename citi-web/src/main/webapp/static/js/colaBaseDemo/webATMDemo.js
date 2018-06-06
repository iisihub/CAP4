pageInit(function() {
  $(document).ready(
      function() {
        ATM = ATLActiveXControl;// document.getElementById('embed2');
        function toHexString(str) {
          var hex = '';
          for (var i = 0; i < str.length; i++) {
            hex += '' + str.charCodeAt(i).toString(16);
          }
          return hex;
        }
        function XOR_hex2(a1, b1) {
          var res = "";

          for (i = 0; i < a1.length; i++) {
            var c1 = a1.charCodeAt(i);
            var d1 = b1.charCodeAt(i);
            var xval = c1 ^ d1;
            res = res + xval.toString(16);
          }
          return res;
        }
        

        function ConnectCard() {
          var rc;
          var readername = myform.reader.value;// myform.reader.options[reader.selectedIndex].value;
          ATM.baReaderName = readername;// "Generic Usb Smart Card Reader 0";//myform.reader.value;
          rc = ATM.ConnectCard();
          // alert("ConnectCard,rc="+rc);
          if (rc != 0) {
            myform.Status.value = "ConnectCard失敗" + rc;
            return;
          } else {
            myform.Status.value = "ConnectCard成功，ATR = " + ATM.baATR;
            myform.mATR.value = ATM.baATR;
            rc = ATM.SelectAID();
            if (rc != 0) {
              myform.Status.value = "SelectAID失敗" + rc;
              return;
            } else {
              myform.Status.value = "SelectAID成功";
            }
          }
        }

        function DisConnectCard() {
          var rc;
          ATM.iDisConnectType = myform.iDisConnectType.value;// 2;
          rc = ATM.DisConnectCard();
          if (rc != 0) {
            myform.Status.value = "DisConnectCard失敗";
            return;
          } else {
            myform.Status.value = "DisConnectCard成功";
          }
        }
        function DisConnectReader() {
          var rc;
          rc = ATM.DisConnectReader();
          if (rc != 0) {
            myform.Status.value = "DisConnectReader";
            return;
          } else {
            myform.Status.value = "DisConnectReader";
          }
        }

        function bVerifyMAC() {
          var rc;
          var MappingTable;
          var len;
          var HexMappingTable;

          ciphAlgo = 0;// ECB
          HexSessionID = myform.HexSessionID.value;
          len = HexSessionID.length;
          // 1 diversify keyge using DES key encrypt 24 byts session id (ECB)
          rc = ATM.DESEncipher(ciphAlgo, len, HexSessionID, myform.HexDesKey.value); // CBC-NOPAD
          if (rc != 0) {
            myform.Status.value = "DESEncipher sessionID fail,rc=" + rc;
            return;
          }
          myform.diversifyKey.value = ATM.GetHexData();
          alert("diversifyKey=" + myform.diversifyKey.value);
          // 2 using diverisfy key encrypt mapping table (CBC-nopad)
          ciphAlgo = 0;// ECB
          hexRandom1 = myform.Random1.value;
          len = hexRandom1.length;
          rc = ATM.DESEncipher(ciphAlgo, len, hexRandom1, myform.diversifyKey.value); // CBC-NOPAD
          if (rc != 0) {
            myform.Status.value = "DESEncipher random1 fail,rc=" + rc;
            return;
          }
          myform.Random1Mac.value = ATM.GetHexData();
          // 圖形鍵盤
          rc = ATM.VerifyMAC(HexSessionID, myform.Random1.value, myform.Random1Mac.value, myform.Random2.value);// sSessionId, sRand1, sRand1mac, sRand2
          if (rc != 0) {
            myform.Status.value = "VerifyMAC fail, rc=" + rc;
            return;
          }
          myform.Status.value = "VerifyMAC success";
          myform.Random2Mac.value = ATM.GetHexData();
        }

        function VerifyPINEx() {
          var rc;
          var MappingTable;
          var len;
          var HexMappingTable;

          MappingTable = myform.MappingTable.value;
          len = MappingTable.length;
          HexMappingTable = getAscStr(MappingTable);
          ciphAlgo = 0;// ECB
          HexSessionID = myform.HexSessionID.value;
          len = HexSessionID.length;
          // 1 diversify keyge using DES key encrypt 24 byts session id (ECB)
          rc = ATM.DESEncipher(ciphAlgo, len, HexSessionID, myform.HexDesKey.value); // CBC-NOPAD
          if (rc != 0) {
            myform.EncStatus.value = "DESEncipher0 fail,rc=" + rc;
            return;
          }
          myform.diversifyKey.value = ATM.GetHexData();
          alert("diversifyKey=" + myform.diversifyKey.value);
          // 2 using diverisfy key encrypt mapping table (CBC-nopad)
          ciphAlgo = 0;// ECB
          len = HexMappingTable.length;
          rc = ATM.DESEncipher(ciphAlgo, len, HexMappingTable, myform.diversifyKey.value); // CBC-NOPAD
          if (rc != 0) {
            myform.EncStatus.value = "DESEncipher1 fail,rc=" + rc;
            return;
          }
          myform.EncMappingTable.value = ATM.GetHexData();
          // 圖形鍵盤
          rc = ATM.VerifyPINEx(myform.PasswordIndex.value, myform.EncMappingTable.value, HexSessionID);
          if (rc != 0) {
            myform.EncStatus.value = "VerifyPINEx fail, rc=" + rc;
            return;
          }
          myform.EncStatus.value = rc;
          // 一般鍵盤
          rc = ATM.VerifyPINEx(myform.Password0.value, myform.EncMappingTable.value, HexSessionID);
          if (rc != 0) {
            myform.ClearStatus.value = "VerifyPINEx fail, rc=" + rc;
            return;
          }
          myform.ClearStatus.value = rc;
          // 混用
          rc = ATM.VerifyPINEx(myform.MixIndex.value, myform.EncMappingTable.value, HexSessionID);
          if (rc != 0) {
            myform.ClearStatus.value = "VerifyPINEx fail, rc=" + rc;
            return;
          }
          myform.MixStatus.value = rc;
        }
        

        function GetRemark() {
          var rc;
          ATM.baEFID = "1001";
          rc = ATM.SelectEF();
          if (rc != 0) {
            myform.Status.value = "SelectEF(1001)失敗" + rc;
            return;
          }
          if (ATM.uiSW12 != 0x9000) {
            myform.Status.value = "SelectEF(1001) SW12 = " + ATM.uiSW12;
            return;
          }
          ATM.bRecID = 2;
          ATM.bLen = 255;
          rc = ATM.ReadRecord();
          if (rc != 0) {
            myform.Status.value = "ReadRecord(1001-2)失敗" + rc;
            return;
          }
          if (ATM.uiSW12 != 0x9000) {
            myform.Status.value = "ReadRecord(1001-2) SW12 = " + ATM.uiSW12;
            return;
          }
          myform.remark.value = "(" + ATM.bRLen + ")" + ATM.baBuf;
        }

        function GetTAC(s1) {
          var rc;
          var MappingTable;
          var len;
          var HexMappingTable;
          var Apdu;
          var HexApdu;
          var HexSessionID = myform.HexSessionID.value;// "7A7A7A7A7A7A7A7A7A7A7A7A7A7A7A7A7A7A7A7A7A7A7A7A"; //session ID length = 16 *n

          MappingTable = myform.MappingTable.value;
          len = MappingTable.length;
          HexMappingTable = getAscStr(MappingTable);
          ciphAlgo = 0;// ECB
          HexSessionID = myform.HexSessionID.value;
          len = HexSessionID.length;
          // 1 diversify keyge using DES key encrypt 24 byts session id (ECB)
          rc = ATM.DESEncipher(ciphAlgo, len, HexSessionID, myform.HexDesKey.value); // CBC-NOPAD
          if (rc != 0) {
            myform.Status00.value = "DESEncipher fail";
            return;
          }
          myform.diversifyKey.value = ATM.GetHexData();
          // 2 using diverisfy key encrypt mapping table (CBC-nopad)
          ciphAlgo = 0;// ECB-NOPAD
          len = HexMappingTable.length;
          rc = ATM.DESEncipher(ciphAlgo, len, HexMappingTable, myform.diversifyKey.value); // CBC-NOPAD
          if (rc != 0) {
            myform.Status00.value = "DESEncipher fail";
            return;
          }
          myform.EncMappingTable.value = ATM.GetHexData();

          Apdu = myform.Apdu.value;
          len = Apdu.length;
          HexApdu = getAscStr(Apdu);
          myform.HexApdu.value = HexApdu;
          len = HexApdu.length;
          // WriteRecordWithSNUMTACProc :function(s1, s2, s3,s3){//20170103
          if (s1 == 0) {// 圖形鍵盤輸入
            rc = ATM.WriteRecordWithSNUMTACProc(myform.PasswordIndex.value, myform.EncMappingTable.value, HexSessionID, HexApdu);
            if (rc != 0) {
              myform.Status00.value = "WriteRecordWithSNUMTACProc fail, rc=" + rc;
              return;
            }
            myform.Status00.value = rc;
            myform.mOutBuf.value = ATM.baOutBuf;
            myform.HexEncTac.value = ATM.vc;

            Apdu = myform.Apdu2.value;
            len = Apdu.length;
            HexApdu = getAscStr(Apdu);
            myform.HexApdu2.value = HexApdu;
            len = HexApdu.length;
            // WriteRecordWithSNUMTACProc :function(s1, s2, s3,s3){//20170103
            rc = ATM.WriteRecordWithSNUMTACProc(myform.PasswordIndex.value, myform.EncMappingTable.value, HexSessionID, HexApdu);
            if (rc != 0) {
              myform.Status01.value = "WriteRecordWithSNUMTACProc fail, rc=" + rc;
              return;
            }
            myform.Status01.value = rc;
            myform.mOutBuf2.value = ATM.baOutBuf;
            myform.HexEncTac2.value = ATM.vc;
          } else if (s1 == 1) {// 一般鍵盤輸入
            rc = ATM.WriteRecordWithSNUMTACProc(myform.PasswordIndex.value, myform.EncMappingTable.value, HexSessionID, HexApdu);
            if (rc != 0) {
              myform.Status10.value = "WriteRecordWithSNUMTACProc fail, rc=" + rc;
              return;
            }
            myform.Status10.value = rc;

            Apdu = myform.Apdu2.value;
            len = Apdu.length;
            HexApdu = getAscStr(Apdu);
            myform.HexApdu2.value = HexApdu;
            len = HexApdu.length;
            // WriteRecordWithSNUMTACProc :function(s1, s2, s3,s3){//20170103
            rc = ATM.WriteRecordWithSNUMTACProc(myform.PasswordIndex.value, myform.EncMappingTable.value, HexSessionID, HexApdu);
            if (rc != 0) {
              myform.Status11.value = "WriteRecordWithSNUMTACProc fail, rc=" + rc;
              return;
            }
            myform.Status11.value = rc;
          } else if (s1 == 2) {// 圖形鍵盤一般鍵盤混用
            rc = ATM.WriteRecordWithSNUMTACProc(myform.Password0.value, myform.EncMappingTable.value, HexSessionID, HexApdu);
            if (rc != 0) {
              myform.Status20.value = "WriteRecordWithSNUMTACProc fail, rc=" + rc;
              return;
            }
            myform.Status20.value = rc;

            Apdu = myform.Apdu2.value;
            len = Apdu.length;
            HexApdu = getAscStr(Apdu);
            myform.HexApdu2.value = HexApdu;
            len = HexApdu.length;
            // WriteRecordWithSNUMTACProc :function(s1, s2, s3,s3){//20170103
            rc = ATM.WriteRecordWithSNUMTACProc(myform.MixIndex.value, myform.EncMappingTable.value, HexSessionID, HexApdu);
            if (rc != 0) {
              myform.Status21.value = "WriteRecordWithSNUMTACProc fail, rc=" + rc;
              return;
            }
            myform.Status21.value = rc;
          }
        }

        

        var charArray = new Array(' ', '!', '"', '#', '$', '%', '&', "'", '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e',
            'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '', 'C\,', 'u\"', 'e\'', 'a^', 'a\"', 'a\`', 'a*', 'c,',
            'e^', 'e"', 'e\`', 'i\"', 'i^', 'i\`', 'A\"', 'A*', 'A*', 'E\'', '?', '?', 'o^', 'o\"', 'o\`', 'u^', 'u\`', 'y\"', 'O\"', 'U\"', '?', '?', '?', '', 'f', 'a\'', 'i\'', 'o\'', 'u\'', 'n~',
            'N~', '^(a)', '^(o)', '?', '?', '?', '1/2', '1/4', '?', '?', '?', '_', '_', '_', '?', '?', 'A\'', 'A^', 'A`', '(c)', '?', '?', '+', '+', '?', '?', '+', '+', '-', '-', '+', '-', '+', 'a~',
            'A~', '+', '+', '-', '-', '?', '-', '+', '?', '?', '?', 'E^', 'E"', 'E`', 'i', 'I\'', 'I^', 'I"', '+', '+', '_', '_', '?', 'I`', '_', 'O\'', '?', 'O^', 'O`', 'o~', 'O~', '?', '?', '?',
            'U\'', 'U^', 'U`', 'y\'', 'Y\'', '', '?', '?', '', '_', '3/4', '?', '', '', '?', 'X', '', 'P', '^(1)', '^(3)', '^(2)', '_', ' ');
        var hex_digits = new Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F');

        /*
         * ------ 轉換16位資料 ------ 帶入參數:String 回傳參數:String
         */
        function HexToStr(s) {
          var retS = "";
          for (var i = 0; i < s.length; i += 2) {
            retS += String.fromCharCode("0x" + s.substring(i, i + 2));
          }
          return retS;
        }

        function chars_from_hex(hex_str) {
          var char_str = "";
          var num_str = "";
          var i;
          for (i = 0; i < hex_str.length; i += 2)
            char_str += byteToChar(parseInt(hex_str.substring(i, i + 2), 16));
          return char_str;
        }

        function getAscStr(s) {
          ret = '';
          for (i = 0; i < s.length; i++) {
            ret = ret + (Dec2Hex(s.charCodeAt(i)));
          }
          return ret;
        }

        function byteToChar(n) {
          if (n < 32 || n > 255)
            return " ";// Periodly Fixed for kas
          return charArray[n - 32];
        }

        function Dec2Hex(n) {
          var result = "";
          while (n >= 16) {
            result = getHex(n % 16) + result;
            n = Math.floor(n / 16);
          }
          result = getHex(n) + result;
          return result;
        }

        function getHex(n) {
          var result = "";
          switch (n) {
          case 15:
            result = "F";
            break;
          case 14:
            result = "E";
            break;
          case 13:
            result = "D";
            break;
          case 12:
            result = "C";
            break;
          case 11:
            result = "B";
            break;
          case 10:
            result = "A";
            break;
          default:
            result = "" + n;
          }
          return result;
        }

        function getATMService() {
          if (confirm("偵測到您尚未安裝元件，請先下載元件或者啟用已下載元件，是否進行下載?")) {
            window.setCloseConfirm(false);
            window.location.href = "../../static/CitiWebATMServiceInstall.exe";
          }
        }
        function ListReader(objSelect) {
          // ------ 清空選項 ------
          for (i = 0; i < objSelect.length; i++) {
            objSelect.remove(1);
          }
          var rc;
          var ReaderCount;
          rc = ATM.connectReader();
          if (rc != 0) {
            myform.Status.value = "ConnectReader失敗" + rc;
           
          } else {
            myform.Status.value = "ConnectReader成功";
          }
          // ------ 列出讀卡機 ------
          bRdrReady = ATM.connectReader();
          if (bRdrReady != 0) {
            return false;
          }

          bRdrReady = ATM.ListReaders();
          if (bRdrReady != 0) {
            return false;
          } else {
            if (ATM.caReader1 != "") {
              objSelect.options[0].text = ATM.caReader1;
            }
            for (var i = 2; i < 6; i++) {
              var readerName = eval("ATM.caReader" + i);
              if (readerName != "") {
                var oOption = document.createElement("OPTION");
                objSelect.options.add(oOption);
                oOption.innerText = readerName;
                oOption.value = readerName;
              }
            }
            return true;
          }
        }
        
        $("#btnConnectCard").on('click', function(e) {
          if(ConnectCardV3(myform.READER_NAME.options[myform.READER_NAME.selectedIndex].text,false) == false){
            myform.Status.value = "ConnectCard失敗";
          }else{
            myform.Status.value = "ConnectCard成功";
          }
        });
        $("#btnDisConnectCard").on('click', function(e) {
          DisConnectCard();
        });
        $("#btnGetBankID").on('click', function(e) {
          //取得卡片資訊
          var theResult = getCardInfoV3(myform.READER_NAME.options[myform.READER_NAME.selectedIndex].text);
          if (theResult == null) {
            myform.Status.value = "讀取卡片資訊失敗";
            return false;
          }
          $("#bankid").val(theResult[0]);
        });
        $("#btnGetRemark").on('click', function(e) {
          GetRemark();
        });
        $("#btnGetAllAccount").on('click', function(e) {
          GetAllAccount();
        });
        $("#btnGetAllInAccount").on('click', function(e) {
          GetAllInAccount();
        });
        $("#btnVerifyMAC").on('click', function(e) {
          bVerifyMAC();
        });
        $("#btnVerifyPINEx").on('click', function(e) {
          VerifyPINEx();
        });
        $("#btnGetTAC0").on('click', function(e) {
          GetTAC(0);
        });
        $("#btnGetTAC1").on('click', function(e) {
          GetTAC(1);
        });
        $("#btnGetTAC2").on('click', function(e) {
          GetTAC(2);
        });
        
        function processlogin() {
          //取得選取的讀卡機
          if (myform.READER_NAME.options[myform.READER_NAME.selectedIndex].text == "") {
            $("#pw").val("");
            $("#pw").focus();
            alert("請重新安裝讀卡機，安裝完後按下『確定』鍵重新執行");
            return false;
          }
          var READER_NAME = myform.READER_NAME.options[myform.READER_NAME.selectedIndex].text;
          var pw = $("#pw").val();
          var challenge_random = "";
          var login_readername = "";
          var login_sessionid = "";
          var ICMAC = "";
          var E_ACCOUNT = "";
          var E_ICREMARK = "";
          var ISSUER_BankCode = "";
          var encmappingtable = "";
         
         
          for (var i = 0; i < 16; i++) {
            challenge_random += "" + Math.floor(Math.random() * 10);
          }

          if (window.location.protocol.indexOf("https") != -1) {
            document.cookie = "challenge_random=" + escape(challenge_random) + "; path=/; secure; max-age=60;";
          } else {
            document.cookie = "challenge_random=" + escape(challenge_random) + "; path=/; max-age=60;";
          }
          $.ajax({
            url: url('portalhandler/reader'),
            type: 'post',
            async: false,
            dataType: 'json',
            data: {
              _service: 'login',
              READER_NAME:READER_NAME,
              _random: challenge_random,
            },
            success: function (data) {
              
              var rc = ATM.VerifyMAC(data.hex_sessionid,data.rand1,data.rand1mac,data.rand2);
            if(rc!=0){
              $("#pw").val("");
              alert("Hand-Shaking 錯誤!");
              return false;
            }
            rc = ATM.VerifyPINEx(pw,data.encmappingtable,data.hex_sessionid);
              if (rc == 26144){
                $("#pw").val("");
                $("#pw").focus();
                alert("密碼錯誤次數已達限制次數，帳戶已暫停使用！ ");
                return false;
              }else if(rc !=0){
                $("#pw").val("");
                $("#pw").focus();
                alert("密碼錯誤");
                return false;
              }else{  
                login_readername = data.reader_name;
                login_sessionid = data.hex_sessionid;
                encmappingtable = data.encmappingtable;
                ICMAC = ATM.GetHexData();
                reqJSON.hex_sessionid = login_sessionid;
              }
            
            },
            
            }).done(function(){
                var ciphAlgo =0;//ECB
                //取得ACCOUNT_LIST
                var theResult = getCardInfoV3(login_readername);
                if (theResult == null) {
                  
                  return false;
                }
                var IssuerId = theResult[0];
                var CardMemo = theResult[1];
                var arrAcct = new Array();
                var ACCOUNT_LIST = "";
                for (var i=2; i<theResult.length; i++) {
                  arrAcct[arrAcct.length] = theResult[i];
                  if (ACCOUNT_LIST == ""){
                    ACCOUNT_LIST=theResult[i];
                  }else{
                    ACCOUNT_LIST=ACCOUNT_LIST + "," + theResult[i];
                  }
                }
                var len=paddingRight(toHexString(ACCOUNT_LIST)).length;

                //加密ACCOUNT_LIST
                var rc = ATM.DESEncipher(ciphAlgo,len,paddingRight(toHexString(ACCOUNT_LIST)),encmappingtable); 
                if(rc !=0){
                  alert("error");
                  return false;
                }
                E_ACCOUNT =  ATM.GetHexData();  
//                console.log("E_ACCOUNT"+E_ACCOUNT);
                if (arrAcct == null) {
                  
                  return false;
                }
                //發卡單位
                ISSUER_BankCode = IssuerId;
                if (ISSUER_BankCode == null) {  
                  return false;
                }
//                console.log("CardMemo"+CardMemo);
                len=paddingRight(toHexString(CardMemo)).length;
                //加密CardMemo
                rc =  ATM.DESEncipher(ciphAlgo,len,paddingRight(toHexString(CardMemo)),encmappingtable); 
                if(rc !=0){
                  alert("error");
                  return false;
                }
                E_ICREMARK =  ATM.GetHexData(); 
//                console.log("E_ICREMARK"+E_ICREMARK);
                   $.ajax({
                       url: url('portalhandler/newsession'),
                       type: 'post',
                       async: true,
                       dataType: 'json',
                       data: {
                        ICMAC: ICMAC,
                        E_ACCOUNT:E_ACCOUNT,
                        E_ICREMARK:E_ICREMARK,
                        ISSUER_BankCode:ISSUER_BankCode,

                       },
                       success: function (data) {
                          if(data.res=='sucess'){
                            reqJSON.login = data.res;
                            reqJSON.readerName = login_readername;
                            window.setCloseConfirm(false);
                  CommonAPI.formSubmit({
                    url: url(data.nextPageURL),
                    data: reqJSON
                  });
                          }
                      
                       }
                     });
              
              
              
            });
        }
        
        
        
        

        (function init() {
          var version = checkATM();
          var _app = navigator.appName;
          var _agen = navigator.userAgent;
          var isMacLike = navigator.userAgent.match(/(Mac|iPhone|iPod|iPad)/i) ? true : false;
          var IERV = getIEVersion();

          if (isNaN(version)) {
            if (isMacLike) {
              myform.Status.value = "當前環境不支援此服務!";
            } else if (_app == 'Microsoft Internet Explorer' && IERV != 10 && IERV != 11) {
              myform.Status.value = "當前環境不支援此服務!";

            } else {

              //getATMService();
            }
          } else {
            ATM.GetAPIVersion();
            myform.Version.value = ATM.cVer;
            if (ListReader(myform.READER_NAME) == false) {
              alert('讀卡機連線失敗，請確認讀卡機是否已安裝妥當!');

            }
            
            
          }
          
          //登入
          $('#login').on('click', function (event) {
            var jQForm = $("#myform");
          jQForm.validationEngine('attach', jqvSetting);
            if (jQForm.validationEngine('validate')&&ConnectCardV3(myform.READER_NAME.options[myform.READER_NAME.selectedIndex].text,false) != false){
              processlogin();
          }  
          });
          
          

        })();

      });
});