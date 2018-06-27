pageInit(function() {
  var arrAcct = new Array();
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
      //取隨機產生的認證碼(4位數字)

        function getAuthCode(){
        	
        	var sRand="";
        	for (var a=0;a<4;a++){
        		var rand=parseInt(10*Math.random());
        		sRand+=rand;
        		
        	}
        	return sRand;
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
        //偵測是否安裝元件
        function getATMService() {
          if (confirm("偵測到您尚未安裝元件，請先下載元件或者啟用已下載元件，是否進行下載?")) {
            window.setCloseConfirm(false);
            window.location.href = "../../static/CitiWebATMServiceInstall.exe";
          }
        }
        //偵測讀卡機
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
        //連線卡片
        $("#btnConnectCard").on('click', function(e) {
          if (ConnectCardV3(myform.READER_NAME.options[myform.READER_NAME.selectedIndex].text, false) == false) {
        	 
            myform.Status.value = "ConnectCard失敗";
          } else {
        	
            myform.Status.value = "ConnectCard成功";
          }
        });
        //斷開卡片
        $("#btnDisConnectCard").on('click', function(e) {
          DisConnectCard();
        });
        //取得卡片銀行代碼
        $("#btnGetBankID").on('click', function(e) {
          // 取得卡片資訊
          var theResult = getCardInfoV3(myform.READER_NAME.options[myform.READER_NAME.selectedIndex].text);
          if (theResult == null) {
            myform.Status.value = "讀取卡片資訊失敗";
            return false;
          }
          $("#bankid").val(theResult[0]);
        });
        //取得備註欄
        $("#btnGetRemark").on('click', function(e) {
          GetRemark();
        });
        //取得轉出帳號
        $("#btnGetAllAccount").on('click', function(e) {
          GetAllAccount(myform.READER_NAME.options[myform.READER_NAME.selectedIndex].text);
        });
        //取得約定轉入帳號
        $("#btnGetAllInAccount").on('click', function(e) {
          GetAllInAccount();
        });
        // 登入按鈕
        $('#login').on('click', function(event) {
          var jQForm = $("#myform");
          if (ConnectCardV3(myform.READER_NAME.options[myform.READER_NAME.selectedIndex].text, false) != false) {
            processlogin();
          }
        });
        // 登出按鈕
        $('#logout').on('click', function(event) {
          $.ajax({
              url : url('demoportalhandler/logout'),
              type : 'post',
              dataType : 'json',
              success : function(data) {
                if (data.res == 'sucess') {
                  window.setCloseConfirm(false);
                  CommonAPI.formSubmit({
                    url : url(data.nextPageURL),
                  });
                }

              }
            });
        });
        // 餘額查詢
        $('#goBalance').on('click', function(event) {
          var jQForm = $("#myform");
          $('#TRNS_OUT_ACCOUNT_INDEX').prop("value",$('#balanceAcc').find(":selected").val());
          checkReloadCard('balance');
          $("#checkOutInCard").dialog({
        	  autoOpen: true,
        	  modal: true,
        	  dialogClass: "dlg-no-close",
        	  buttons:  {
                  關閉: function() {$(this).dialog("close");}
              },
          });
        });
        
        // 約定帳戶轉帳
        $('#goTransfer').on('click', function(event) {
          var jQForm = $("#myform");
          $('#TRNS_OUT_ACCOUNT_INDEX').prop("value",$('#transferOutAcc').find(":selected").val());
          checkReloadCard('transfer');
          $("#checkOutInCard").dialog({
        	  autoOpen: true,
        	  modal: true,
        	  dialogClass: "dlg-no-close",
        	  buttons:  {
                  關閉: function() {$(this).dialog("close");}
              },
          });
        });
        
        
        
        
        
        
        //登入
        function processlogin() {
          // 取得選取的讀卡機
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
            url : url('demoportalhandler/reader'),
            type : 'post',
            async : true,
            dataType : 'json',
            data : {
              _service : 'login',
              READER_NAME : READER_NAME,
              _random : challenge_random,
            },
            success : function(data) {

              var rc = ATM.VerifyMAC(data.hex_sessionid, data.rand1, data.rand1mac, data.rand2);
              if (rc != 0) {
                $("#pw").val("");
                alert("Hand-Shaking 錯誤!");
                return false;
              }
              myform.randKeypadList.value = data.randKeypadList;
              rc = ATM.VerifyPINEx(pw, data.encmappingtable, data.hex_sessionid);
              if (rc == 26144) {
                $("#pw").val("");
                $("#pw").focus();
                alert("密碼錯誤次數已達限制次數，帳戶已暫停使用！ ");
                return false;
              } else if (rc != 0) {
                $("#pw").val("");
                $("#pw").focus();
                alert("密碼錯誤");
                return false;
              } else {
                login_readername = data.reader_name;
                login_sessionid = data.hex_sessionid;
                encmappingtable = data.encmappingtable;
                ICMAC = ATM.GetHexData();
                $('#login_sessionid').val(login_sessionid);
              }

            },

          }).done(function() {
            var ciphAlgo = 0;// ECB
            // 取得ACCOUNT_LIST
            var theResult = getCardInfoV3(login_readername);
            if (theResult == null) {

              return false;
            }
            var IssuerId = theResult[0];
            var CardMemo = theResult[1];

            var ACCOUNT_LIST =  GetAllAccount(login_readername);
            GetAllInAccount();
            var len = paddingRight(toHexString(ACCOUNT_LIST)).length;

            // 加密ACCOUNT_LIST
            var rc = ATM.DESEncipher(ciphAlgo, len, paddingRight(toHexString(ACCOUNT_LIST)), encmappingtable);
            if (rc != 0) {
              alert("error");
              return false;
            }
            E_ACCOUNT = ATM.GetHexData();
            // console.log("E_ACCOUNT"+E_ACCOUNT);
            // 發卡單位
            ISSUER_BankCode = IssuerId;
            if (ISSUER_BankCode == null) {
              return false;
            }
            // console.log("CardMemo"+CardMemo);
            len = paddingRight(toHexString(CardMemo)).length;
            // 加密CardMemo
            rc = ATM.DESEncipher(ciphAlgo, len, paddingRight(toHexString(CardMemo)), encmappingtable);
            if (rc != 0) {
              alert("error");
              return false;
            }
            E_ICREMARK = ATM.GetHexData();
            // console.log("E_ICREMARK"+E_ICREMARK);
            $.ajax({
              url : url('demoportalhandler/newsession'),
              type : 'post',
              async : true,
              dataType : 'json',
              data : {
                ICMAC : ICMAC,
                E_ACCOUNT : E_ACCOUNT,
                E_ICREMARK : E_ICREMARK,
                ISSUER_BankCode : ISSUER_BankCode,

              },
              success : function(data) {
                if (data.res == 'sucess') {
                	 $.ajax({
                         url : url('demoportalhandler/login'),
                         type : 'post',
                         async : true,
                         dataType : 'json',
                         data : {
                         },
                         success : function(data) {
                           if (data.res == 'sucess') {
                        	 myform.TmlID.value = data.TmlID;
                        	 myform.tNow.value = data.tNow;
                           	 myform.bankid.value = ISSUER_BankCode;
                           	 myform.Status.value = "登入成功!";
                           	 $('.deal').show();
                           	 

                           }

                         }
                       });
                }

              }
            });

          });
        }
        //取得轉出帳號
        function GetAllAccount(readername) {
            var rc;    
            var ACCOUNT_LIST = "";
            var theResult = getCardInfoV3(readername);
            if (theResult == null) {
				 alert("卡片中無任何帳號資料");

			}
            
            for (var i = 2; i < theResult.length; i++) {
                arrAcct[arrAcct.length] = theResult[i];
                if (ACCOUNT_LIST == "") {
                  ACCOUNT_LIST = theResult[i];
                } else {
                  ACCOUNT_LIST = ACCOUNT_LIST + "," + theResult[i];
                }
              }
			if (arrAcct == null) {
				 //showPopMsgWithNoX("卡片中無任何帳號資料",0, cancelAction);
				 alert("卡片中無任何帳號資料");
			}
			for (i=0; i< arrAcct.length; i++) {
				if (arrAcct[i]!="") {
					myform.Ef1001Account.value =arrAcct[i]+",";
					
				}
			}
			for (i=0; i< arrAcct.length; i++) {
				if (arrAcct[i]!="") {
					var oOption = document.createElement("OPTION");
					myform.balanceAcc.options.add(oOption);
					oOption.innerText =  arrAcct[i];
					oOption.value = i;
					var oOption2 = document.createElement("OPTION");
					myform.transferOutAcc.options.add(oOption2);
					oOption2.innerText =  arrAcct[i];
					oOption2.value = i;
					
				}
			}	
			
			return arrAcct[0];
			
      } 

        
        //取得約定帳戶
        function GetAllInAccount() {
          var rc;
      	  var i;
            ATM.baEFID = "1002";
            rc = ATM.SelectEF();
            if(rc != 0) {
               myform.Ef1002Account.value = "SelectEF(1001)失敗"+rc;
               return;
            }
           
            for (i=1;i<=8;i++) {
		        ATM.bRecID = i;
		        ATM.bLen = 255;
		        rc = ATM.ReadRecord();
		        if(rc != 0) {
		        	myform. Ef1002Account.value ="ReadRecord(1002-"+i+ ")失敗,rc="+rc;
		           return false;
		        }         
		        if(ATM.uiSW12 != 0x9000) {
		        	myform. Ef1002Account.value ="ReadRecord(1002-"+i+ ")失敗, SW12 =" + ATM.uiSW12;
		         
		           return false;
		        }      
		       if( HexToStr(ATM.baBuf.substring(4,10))=="000"){
		    	   break;
		       }

		        if(HexToStr(ATM.baBuf.substring(20,53)).trim() == null || ATM.baBuf == undefined || HexToStr(ATM.baBuf.substring(20,53)).trim() ==""){
		        	myform. Ef1002Account.value = "卡片中無任何約定帳號資料";
		        	
		        	return false;
		        }else{
		        	myform. Ef1002Account.value =  myform.Ef1002Account.value + HexToStr(ATM.baBuf.substring(4,10)) + "-" + HexToStr(ATM.baBuf.substring(20,53))+ ",";
		        	var oOption3 = document.createElement("OPTION");
					myform.transferInAcc.options.add(oOption3);
					oOption3.innerText =  HexToStr(ATM.baBuf.substring(20,53));
					oOption3.value = HexToStr(ATM.baBuf.substring(4,10)) + "-" + HexToStr(ATM.baBuf.substring(20,53));
		        	
		        
		        }
		  	 }
      }
        //取得備註欄
        function GetRemark() {
            var rc;      
            ATM.baEFID = "1001";
            rc = ATM.SelectEF();
            if(rc != 0) {
               myform.Status.value = "SelectEF(1001)失敗"+rc;
               return;
            }
            if(ATM.uiSW12 != 0x9000) {
               myform.Status.value = "SelectEF(1001) SW12 = " + ATM.uiSW12;
               return;
            }         
            ATM.bRecID = 2;
            ATM.bLen = 255;
            rc = ATM.ReadRecord();
            if(rc != 0) {
               myform.Status.value = "ReadRecord(1001-2)失敗"+rc;
               return;
            }         
            if(ATM.uiSW12 != 0x9000) {
               myform.Status.value = "ReadRecord(1001-2) SW12 = " + ATM.uiSW12;
               return;
            }      
            myform.remark.value = "(" + ATM.bRLen + ")" + ATM.baBuf;
      }
        
      //倒數檢查是否抽插拔卡片
  	  function checkReloadCard(checkType){
  	      var count = 30;
  	      var isDisConnect = 0;
  	      var isConnect = 1;
  	      var iCardType;
  	      $(function() {
  	    	  stime = setTimeout(BtnCount, 1000); // 1s執行一次BtnCount
  	      });
  	      BtnCount = function() {
  	          if (count == 0) {
  	        	  clearTimeout(stime);           // 可取消由 setTimeout() 方法設置的 timeout

  	        	$("#checkOutInCard").dialog("close");

  	          }
  	          else {
  	              count--;
  	              $("#checkOutInCard").find("nobr").eq(0).text(count);
  	              if(isDisConnect != 0 && isConnect!=0){
  	            	  ATM.CheckCardInsert();
  	            	  isConnect = ATM.cardType;

  	            	  if(isConnect !=0){

  	            		  ATM.CheckCardInsert();
  		            	  isConnect = ATM.cardType;

  						}else{
  							$("#checkOutInCard").dialog("close");
  							$("#checkPassword").dialog({
  				        	  autoOpen: true,
  				        	  modal: true,
  				        	  dialogClass: "dlg-no-close",
  				        	  buttons:  {
  				                 確定: function() {
  				                	 	if(checkType==='balance'){
  				                	 		balanceProcess();
  				                	 	}else if(checkType==='transfer'){
  				                	 		transferProcess();
  				                	 	}
  				                	 	
  				                	 	$("#checkPassword").dialog("close");
  				                	 }
  				              },
  							});
  							

  						}
  	              }else if(isConnect!=0){
  	            	  ATM.CheckCardInsert();
  	            	  isDisConnect = ATM.cardType;
  	              }
  	              setTimeout(BtnCount, 1000);

  	          }
  	      };

  	  }
  	  //餘額查詢方法
	  function balanceProcess(){
		  var checkpw = $("#checkpw").val();
		  var pHexAPDU;
		  var rc;
		  var OutBuf;
		  var HexEncTac;
		  var AuthCode = getAuthCode();
		  var IFD_Chk_Code = AuthCode + AuthCode;
		  $.ajax({
			  type: 'post',
	          async: false,
	          dataType: 'json',
			  url: url('democ0100handler/query'), 
		  }).done(function(data) {
			  //組APDU
			  if (data.TRNS_CODE=="2590"){
				  pHexAPDU = data.TRNS_CODE + data.ACCOUNT + IFD_Chk_Code + "000000000000000000000000000000000000";
				}else{
				  pHexAPDU = data.TRNS_CODE + IFD_Chk_Code +  data.ACCOUNT;
				}	
			  rc = ATM.WriteRecordWithSNUMTACProc(checkpw,data.ENC_MAPPING_TABLE,data.HEX_SESSIONID ,toHexString(pHexAPDU));
			  
			  if (rc == 26144){
	        		alert("密碼錯誤次數已達限制次數，帳戶已暫停使用！ ");
	      			return false;
	      		}else if(rc !=0){
	      			alert("密碼錯誤");
	      			return false;
	      		}else{	
	      			OutBuf = ATM.baOutBuf;
	      			HexEncTac = ATM.vc;
	      			
	      			$.ajax({
	      		        url : url('democ0100handler/inquiry'),
	      		        data : {
	      		          ICACT_INDEX :  $('#TRNS_OUT_ACCOUNT_INDEX').val(),
	      		          TRNS_CODE : data.TRNS_CODE,
	      		          ICSEQ : HexToStr(OutBuf.substring(2, 18)),
	      		          ICTAC : OutBuf.substring(22, 38),
	      		          ICMAC : HexEncTac,
	      		          TXNDT : data.tNow,
	      		          AUTHCODE:AuthCode
	      		        },
	      		      success: function (data) {
	      		    	  $("#balanceDetail tr").eq(0).find("td").text(data.DATE_TIME);
	      				  $("#balanceDetail tr").eq(1).find("td").text(data.TML_ID+"-"+ data.TXTNO+ (data.PCODE) != undefined ? "("+data.PCODE+")" : "");
	      				  $("#balanceDetail tr").eq(2).find("td").text(data.ERRCODE+"-"+ data.ERRMSG_CH);
	      				  $("#balanceDetail tr").eq(3).find("td").text(data.TRNS_OUT_BANK+"-"+data.BANK_NAME+data.TRNS_OUT_ACCOUNT);
	      				  $("#balanceDetail tr").eq(4).find("td").text(data.ERRCODE!="00000"?"":amountFormat(data.WTHBAL));
	      				  $("#balanceDetail tr").eq(5).find("td").text(data.ERRCODE!="00000"?"": amountFormat(data.AVBAL));
	      				  $("#balanceDetail tr").eq(6).find("td").text(strNull(data.STAN1));
	      				  $("#showBalance").show();
	      		        }
	      		      });
	      			
	      			}
  
		  		});
		  
	  }  
  	  //約定帳戶轉帳
	  function transferProcess(){
		  var TRNS_CODE;
		  var Money =  $("#money").val();
		  Money = padLeft(Money.replace(/[^\d]/g, '') ,12)+"00";
		  var TML_ID = $("#TmlID").val();
		  var TXNDT = $("#tNow").val();
		  var Pay_Account ;
		  var pw = $("#checkpw").val();
		  var pHexAPDU;
		  var rc;
		  var OutBuf;
		  var HexEncTac;
		  var AuthCode = getAuthCode();
		  var TML_Chk_Code = AuthCode + AuthCode;
		  var ENC_MAPPING_TABLE;
		  var inAccount = $('#transferInAcc option:selected').val();
		  var outAccountindex= $('#transferOutAcc option:selected').val();
		  
		  var inbankID = inAccount.substring(0,3);
		  var inaccount = inAccount.substring(4,20);
		  var E_ACCOUNT;
		  var len = paddingRight(toHexString(inaccount)).length;
		  var ciphAlgo =0;//ECB
		  var TRNS_OUT_BANK_NAME ;
		  var TRNS_OUT_ACCOUNT;
		  var TRNS_IN_BANK_NAME;
		  var AMOUNT;
		  
		  $.ajax({
			  url : url('democ0200handler/query'),
			  async: false,
			  data:{
				  TRNS_IN_ACCOUNT:inAccount,
				  TRNS_OUT_ACCOUNT:arrAcct[outAccountindex]
			  },
		      success: function (p) {
		    	  var rc= ATM.DESEncipher(ciphAlgo,len, paddingRight(toHexString(inaccount)),p.hex_sessionid); //CBC-NOPAD
		    	  if (rc!=0) {
						alert( "DESEncipher fail");
						return false;
					} 
		    	  E_ACCOUNT = ATM.GetHexData();
		      }
			  }).done(function() {
					  $.ajax({
						  url : url('democ0200handler/warn'),
						  async: false,
						  data:{
							  TRNS_IN_BANK:inbankID,
							  TRNS_IN_ACCOUNT:E_ACCOUNT,
							  TRNS_OUT_ACCOUNT_INDEX: outAccountindex,
							  TRNS_AMOUNT: $("#money").val()
						  },
					      success: function (r) {
					    	  TRNS_CODE = r.TRNS_CODE;
					    	  Pay_Account = r.TRNS_OUT_ACCOUNT;
							  $.ajax({
						             url: url('democ0200handler/EncMappingTable'),
						             type: 'post',
						             async: false,
						             dataType: 'json',
						             success: function (data) {
						            	 ENC_MAPPING_TABLE = data.EncMappingTable;
						            	 if (TRNS_CODE != "2580") {
						       			  pHexAPDU =TRNS_CODE + Money			+ TML_ID
						       													+ TML_Chk_Code	 + TXNDT
						       													+ inAccount + Pay_Account;
						       			}else{
						       				pHexAPDU = TRNS_CODE + Money		+ inAccount
						       													+ TML_Chk_Code	 + TXNDT.substring(8)
						       													+ Pay_Account;
						       			}
						       			  rc = ATM.WriteRecordWithSNUMTACProc(pw,ENC_MAPPING_TABLE,data.HexSessionId ,toHexString(pHexAPDU));
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
							      			return false;
							      		}else{	
							      			OutBuf = ATM.baOutBuf;
							      			HexEncTac = ATM.vc;
							      			
							      			$.ajax({
							      		        url : url('democ0200handler/transfer'),
							      		      async: false,
							      		        data : {
							      		          ICACT_INDEX : $("#TRNS_OUT_ACCOUNT_INDEX").val(),
							      		          AUTHCODE:AuthCode,
							      		          TRNS_CODE : TRNS_CODE,
							      		          ICSEQ : HexToStr(OutBuf.substring(2, 18)),
							      		          ICTAC : OutBuf.substring(22, 38),
							      		          ICMAC : HexEncTac,
							      		          TXNDT : TXNDT ,
							      		          EmailType :1,
							      		          EMAIL :"",
							      		        },
							      		      success: function (datas) {
							      		    	
							      		      $("#transferDetail tr").eq(0).find("td").text(datas.ERRCODE+"-"+datas.ERRMSG_CH);
							      			  $("#transferDetail tr").eq(1).find("td").text(datas.DATE_TIME);
							      			  $("#transferDetail tr").eq(2).find("td").text(datas.TML_ID+"-"+datas.TXTNO+(datas.TAC_ID) != undefined ? "("+datas.TAC_ID+")" : "");
							      			  $("#transferDetail tr").eq(3).find("td").text(datas.TRNS_OUT_BANK+"-"+datas.PAY_BANK_NAME);
							      			  $("#transferDetail tr").eq(4).find("td").text(datas.TRNS_OUT_ACCOUNT);
							      			  $("#transferDetail tr").eq(5).find("td").text(datas.TRNS_IN_BANK+"-"+datas.REMIT_BANK_NAME);
							      			  $("#transferDetail tr").eq(6).find("td").text(datas.TRNS_IN_ACCOUNT);
							      			  $("#transferDetail tr").eq(7).find("td").text(datas.ERRCODE!="00000"?"":amountFormat(datas.TXAMT));
							      			  $("#transferDetail tr").eq(8).find("td").text(datas.ERRCODE!="00000"?"":amountFormat(datas.CHARGE));
							      			  $("#transferDetail tr").eq(9).find("td").text(datas.ERRCODE!="00000"?"":amountFormat(datas.WTHBAL));
							      			  $("#transferDetail tr").eq(10).find("td").text(datas.ERRCODE!="00000"?"": amountFormat(datas.AVBAL));
							      			  $("#transferDetail tr").eq(11).find("td").html(strNull(datas.STAN1));  
							      		      $("#showTransfer").show();
							      		    	  
							      		        }
							      		      });
							      			
							      			}
						             }
						           });
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
          $('.deal').hide();
          if (isNaN(version)) {
            if (isMacLike) {
              myform.Status.value = "當前環境不支援此服務!";
            } else if (_app == 'Microsoft Internet Explorer' && IERV != 10 && IERV != 11) {
              myform.Status.value = "當前環境不支援此服務!";

            } else {

              // getATMService();
            }
          } else {
            ATM.GetAPIVersion();
            myform.Version.value = ATM.cVer;
            if (ListReader(myform.READER_NAME) == false) {
              alert('讀卡機連線失敗，請確認讀卡機是否已安裝妥當!');

            }

          }

        })();

      });
});