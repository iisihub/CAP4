
var ports="00000";
var PostRet="0";
var mSessionID ="";

function XOR_hex(a1, b1) {
    var res = "";
	
    for (i=0;i< a1.length;i++) {
		var c1=a1.charCodeAt(i);
		var d1=b1.charCodeAt(i);		
		var xval=(c1 ^ d1).toString(16);
		if (xval.length<2) 
			xval ="0"+xval;
        res =  res+xval;
	}
    return res;
}
function XOR_hex1(a, b) {
    var res = "";
    for (i=0;i< a.length;i++) {
		var xval=parseInt(a.charAt(i), 16) ^ parseInt(b.charAt(i), 16);
        res =  res+xval.toString(16);
	}
    return res;
}
// checkMoica
var $PIN_CDOE = $('#pinCode');
function post(str)
{
	var resp="";
	var isOK=false;
	var URL="";
	var tport="";
	var sess=false;
	// var portList = new Array("58433","58434","58435","58436","58437");
	var portList = new Array("58433","58434","58435");	
	var echoStr="a1=get&a2=AtmEcho";	
 	try
	{
		try
		{
			var xhr = new XMLHttpRequest();
			// var isTrue =xhr.withCredentials ;
			// var totalURL = document.URL.substring(0,
			// document.URL.indexOf("?", document.URL.indexOf("?") + 1));
			var urlLocation = 	 document.URL.substring(0, document.URL.indexOf("/", document.URL.indexOf("/") + 2));
			var a0Val =	urlLocation +";;"+mSessionID;					
			if (ports=="00000") {
				// try port
				for (var i = 0; i < portList.length; i++) {					
					try{	
						URL="https://localhost:"+portList[i]+"/CTBMOICA?a0="+a0Val+"&"+echoStr;	
						// URL="https://localhost:"+portList[i]+"/CTBMOICA?a0="+urlLocation+"&"+echoStr;
						xhr.open("POST", URL, false); 
						xhr.send(null);
						resp=xhr.responseText;		
						if (resp != "") {

							var sp=resp.split(";;");
							if (sp[0]!="CTBMOICA2057932336") {
								continue;
							}
							else {
								// alert("echo="+resp);
								ports=portList[i];
								// if(SessionID == "")
								{
									mSessionID=sp[1];
								}
								break;							
							}						
						}
						else {
							continue;				
						}						
					}catch(err){
						continue;
					}
				}
				if (ports == "00000" ) {
					return "-2";
				}	
			}
			// alert("succes, port="+ports);
			// URL="https://localhost:"+ports+"/CTBMOICA?a0="+urlLocation+"&"+str;
			URL="https://localhost:"+ports+"/CTBMOICA?a0="+a0Val+"&"+str;	
			xhr.open("POST", URL, false);  
			xhr.send(null);
			resp=xhr.responseText;
			xhr=null;
			isOK=true;
			PostRet="0";		
		}
		catch(err) {
			// resp=err.message;
			resp="-3%%"+err.message;
			PostRet="-3";
			ports="00000";		
		}

		if(isOK==true)
		{
		   resp="0%%"+resp
		}
		else
		{
			resp="-1%%"+resp
			ports="00000";			
		}
		return resp;	
	}
	
	catch(err) {
	}
}
function send(str)
{
	var ret="";
	try
	{
		ret=post(str);
		
		var sp=ret.split("%%");
		if(sp[0]!="0")
		{
			if ((verOffset=navigator.userAgent.indexOf("Chrome"))!=-1) {
				// alert("try chorme again");
				if (str=="a1=get&a2=GetVersion" || str=="a1=get&a2=AtmEcho" ) {
					ret1=post("a1=get&a2=AtmEcho");
					var sp=ret1.split("%%");
					ret1=post(str);
					sp=ret1.split("%%");					
				}
			}
			if (sp[0]!="0") 
				showFancyBox("請確認您的網路連線或是自然人憑證元件服務是否已開啟，再進行後續辦卡事宜，謝謝。");
				console.log("請確認憑證服務是否已安裝並且已啟動,或重啟憑證服務rc="+sp[0]);
				// alert(" port="+ports+" 請確認憑證服務是否已安裝並且已啟動,或重啟憑證服務rc="+sp[0]);
			PostRet=sp[0];
		}
		return sp[1];	
	}
	
	catch(err) {
		// alert( err.message);
	}
}
function VerifyCode()
{
	if(PostRet=="-2")
	{
		showFancyBox("請確認您的網路連線或是自然人憑證元件服務是否已開啟，再進行後續辦卡事宜，謝謝。");
		console.log("請確認憑證服務是否已安裝rc="+PostRet);
// alert("請確認憑證服務是否已安裝rc="+PostRet);
		ATLActiveXControl.Logout()
		// PostRet=sp[0];
	}	
	else 	if(PostRet=="-1")
	{
		showFancyBox("請確認您的網路連線或是自然人憑證元件服務是否已開啟，再進行後續辦卡事宜，謝謝。");
		conole.log("請確認憑證服務是否已安裝並且已啟動,或重啟憑證服務rc="+PostRet);
// alert("請確認憑證服務是否已安裝並且已啟動,或重啟憑證服務rc="+PostRet);
		ATLActiveXControl.Logout();
		// PostRet=sp[0];
	}
	else 	if(PostRet!="0")
	{
		showFancyBox("請確認您的網路連線或是自然人憑證元件服務是否已開啟，再進行後續辦卡事宜，謝謝。");
		console.log("請確認憑證服務是否已安裝並且已啟動,或重啟憑證服務rc="+PostRet);
// alert("請確認憑證服務是否已安裝並且已啟動,或重啟憑證服務rc="+PostRet);
		ATLActiveXControl.Logout();
		// PostRet=sp[0];
	}		
}
var ATLActiveXControl = 
{
	/** ***************************** */
	/* mth Method : return RC */ 
	/** ***************************** */	
	GetLastError :function(){
		var str="a1=mth&a2=GetLastError";
		var result = send(str);
		return result;// parseInt(result, 10);
	},	// 1
	SetTokenType :function(s1){
		var str="a1=mth&a2=SetTokenType&a3="+encodeURIComponent(s1);
		// return send(str);
		var rc=send(str);	
		// VerifyCode();
		return 	parseInt(rc, 10);
	},// 2
	GetCardStatus :function(){
		var str="a1=mth&a2=GetCardStatus";
		var result = send(str);
		// VerifyCode();
		return parseInt(result, 10);
	},	// 3
	GetCardType :function(){
		var str="a1=mth&a2=GetCardType";
		var result = send(str);
		// VerifyCode();
		return parseInt(result, 10);
	},	// 4
	Login :function(s1,s2){
		var str=""
		if (mSessionID !=""){
			var xStr1= XOR_hex(s1, mSessionID);			
			str="a1=mth&a2=Login&a3="+encodeURIComponent(xStr1)+"&a4="+encodeURIComponent(mSessionID);
		}
		else
			str="a1=mth&a2=Login&a3="+encodeURIComponent(s1)+"&a4="+encodeURIComponent(s2);
		var rc=send(str);	
		return 	parseInt(rc, 10);
	},// 5
	Logout :function(){
		var str="a1=mth&a2=Logout";
		var result = send(str);
		return parseInt(result, 10);
	},	// 6
	ChangePIN :function(s1,s2){
		var str="a1=mth&a2=ChangePIN&a3="+encodeURIComponent(s1)+"&a4="+encodeURIComponent(s2);
		// return send(str);
		var rc=send(str);	
		return 	parseInt(rc, 10);
	},// 7
	SelectCert :function(s1){
		var str="a1=mth&a2=SelectCert&a3="+encodeURIComponent(s1);
		// return send(str);
		var rc=send(str);	
		return 	parseInt(rc, 10);
	},// 8
	SignPKCS7 :function(s1){
		var str="a1=mth&a2=SignPKCS7&a3="+encodeURIComponent(s1);
		var rc=send(str);	
		return 	parseInt(rc, 10);
	},// 9
	VerifyPKCS7 :function(s1){
		var str="a1=mth&a2=VerifyPKCS7&a3="+encodeURIComponent(s1);
		var rc=send(str);	
		return 	parseInt(rc, 10);
	},// 10
	SignPKCS1 :function(s1){
		var str="a1=mth&a2=SignPKCS1&a3="+encodeURIComponent(s1);
		var rc=send(str);	
		return 	parseInt(rc, 10);
	},// 11
	/** ***************************** */
	/* Get Property, return string */ 
	/** ***************************** */	
	// GetErrorCode :function(){
	get GetErrorCode(){	
		var str="a1=mth&a2=GetErrorCode";
		var result = send(str);
		return result;// parseInt(result, 10);
	},	// 1
	GetAtmEcho :function(){
		var str="a1=get&a2=AtmEcho";
		var result= send(str);	
		// VerifyCode();
		return result;
	},// 1
	// GetErrorMsg :function(){
	get GetErrorMsg(){
		var str="a1=get&a2=GetErrorMsg";
		return send(str);
	},	// 2
	// GetVersion : function(){
	get	GetVersion() {
       // alert("mSessionID="+mSessionID);
		var str="a1=get&a2=GetVersion";
		var result= send(str);	
		// VerifyCode();
		return result;
	},	// 3
	// GetGPKICardNo :function() {
	get GetGPKICardNo() {		
		var str="a1=get&a2=GetGPKICardNo";
		var result= send(str);	
		// VerifyCode();
		return result;
	},// 4
	// GetSelectedCertVersion :function() {
	get GetSelectedCertVersion() {		
		var str="a1=get&a2=GetSelectedCertVersion";
		var result= send(str);
		return result;
	},// 5
	get GetSelectedCertKeySize() {
		var str="a1=get&a2=GetSelectedCertKeySize";
		var result= send(str);
		return result;
	},// 6
	get GetSelectedCertKeyUsage(){
		var str="a1=get&a2=GetSelectedCertKeyUsage";
		var result = send(str);
		return result;
	},// 7
	// GetSelectedCertSignAlg :function(){
	get GetSelectedCertSignAlg(){
		var str="a1=get&a2=GetSelectedCertSignAlg";
		var result = send(str);
		return result;
	},// 8
	// GetCertFinger :function(){
	get GetCertFinger(){
		var str="a1=get&a2=GetCertFinger";
		var result = send(str);
		return result;
	},// 9
	// GetSelectedCertB64 :function(){
	get GetSelectedCertB64(){
		var str="a1=get&a2=GetSelectedCertB64";
		var result = send(str);
		return result;
	},// 10
	// GetSelectedCertSerial :function(){
	get GetSelectedCertSerial(){
		var str="a1=get&a2=GetSelectedCertSerial";
		var result = send(str);
		return result;
	},// 11
	// GetSelectedCertSubject :function(){
	get GetSelectedCertSubject(){
		var str="a1=get&a2=GetSelectedCertSubject";
		var result = send(str);
		return result;
	},// 12
	// GetSelectedCertIssuer :function(){
	get GetSelectedCertIssuer(){
		var str="a1=get&a2=GetSelectedCertIssuer";
		var result = send(str);
		return result;
	},// 13
	// GetSelectedCertNotBefore :function(){
	get GetSelectedCertNotBefore(){
		var str="a1=get&a2=GetSelectedCertNotBefore";
		var result = send(str);
		return result;
	},// 14
	// GetSelectedCertNotAfter :function(){
	get GetSelectedCertNotAfter(){
		var str="a1=get&a2=GetSelectedCertNotAfter";
		var result = send(str);
		return result;
	},// 15
	// GetHiCertType :function(){
	get GetHiCertType(){
		var str="a1=get&a2=GetHiCertType";
		return send(str);
	},	// 16
	get GetHiCertTailCitizenID(){
		var str="a1=get&a2=GetHiCertTailCitizenID";
		// return send(str);
		var result= send(str);
		return result;
	},	// 17
	// GetHiCertUniformOrganizationID :function() {
	get GetHiCertUniformOrganizationID() {
		var str="a1=get&a2=GetHiCertUniformOrganizationID";
		var result= send(str);
		return result;
	},// 18
	// GetCardPriSec :function() {
	get GetCardPriSec() {
		var str="a1=get&a2=GetCardPriSec";
		var result= send(str);
		return result;
	},// 19
	// GetPKCS7Data :function() {
	get GetPKCS7Data() {
		var str="a1=get&a2=GetPKCS7Data";
		var result= send(str);
		return result;
	},// 20
	// GetPKCS1Data :function() {
	get GetPKCS1Data() {
		var str="a1=get&a2=GetPKCS1Data";
		var result= send(str);
		return result;
	},// 21
	// GetVerifiedContent :function() {
	get GetVerifiedContent() {
		var str="a1=get&a2=GetVerifiedContent";
		var result= send(str);
		return result;
	},// 22
	// GetVerifiedCertB64 :function() {
	 get GetVerifiedCertB64() {
		var str="a1=get&a2=GetVerifiedCertB64";
		var result= send(str);
		return result;
	},// 23
	// GetVerifiedCertSerial :function() {
	get GetVerifiedCertSerial() {
		var str="a1=get&a2=GetVerifiedCertSerial";
		var result= send(str);
		return result;
	},// 24
	// GetVerifiedCertSubject :function() {
	get GetVerifiedCertSubject() {
		var str="a1=get&a2=GetVerifiedCertSubject";
		var result= send(str);
		return result;
	},// 25
	// GetVerifiedCertIssuer :function() {
	get GetVerifiedCertIssuer() {
		var str="a1=get&a2=GetVerifiedCertIssuer";
		var result= send(str);
		return result;
	},// 26
	// GetVerifiedCertNotBefore :function() {
	get GetVerifiedCertNotBefore() {
		var str="a1=get&a2=GetVerifiedCertNotBefore";
		var result= send(str);
		return result;
	},// 27
	 // GetVerifiedCertNotAfter :function() {
	 get GetVerifiedCertNotAfter() {
		var str="a1=get&a2=GetVerifiedCertNotAfter";
		var result= send(str);
		return result;
	},// 28
	 // service Sign
	signCert: function(signData, id, pwd) {
		debugger;
	     if (ATLActiveXControl.GetVersion && pwd) { // 取得元件版本 && 判斷密碼是否有輸入
	         var _tokenStatus = ATLActiveXControl.SetTokenType('2');
	         // set token
	         if (_tokenStatus == '0') { // 設定token 1:PFX, 2卡片載具
	             var cardStatus = ATLActiveXControl.GetCardStatus();
	             var cardType = ATLActiveXControl.GetCardType();
	             // 檢查讀卡機中是否有卡片
	             if (cardStatus == '0' && cardType == '1') {
	                 if (ATLActiveXControl.Login(pwd, '') == '0') {
	                     // 選擇簽章憑證 1:簽章 2:加密
	                     if (ATLActiveXControl.SelectCert('1') == '0') {
	                         // 取得身份證後四碼做比對
	                         // logDebug('Tail Citizen ID is '+
								// ATLActiveXControl.GetHiCertTailCitizenID);
	                         if (id.substr(6, 4) != ATLActiveXControl.GetHiCertTailCitizenID) {
	                             showFancyBox(_i18n['UTB.006']);
	                             $PIN_CDOE.val('');
	                         } else {
	                             // signing...簽章中
	                             if (ATLActiveXControl.SignPKCS7(signData) == '0') {
	                                 var PKCS7Data = ATLActiveXControl.GetPKCS7Data;
	                                 ATLActiveXControl.Logout();
	                                 return encodeURIComponent(encodeURIComponent(PKCS7Data))
	                             } else {
	                                 ATLActiveXControl.Logout();
	                                 showFancyBox(_i18n['UTB.007']);
	                                 $PIN_CDOE.val('');
	                             }
	                         }
	                     }
// else {
// //service在select的時候就已經出錯，目前先判定為廢止憑證
// showFancyBox(_i18n['VA.ERROR.E003']);
// $PIN_CDOE.val('');
// }
	                     ATLActiveXControl.Logout(); // 登出
	                 }
	                 $PIN_CDOE.val('');
	             } else if (cardStatus == '1') {
	                 showFancyBox(_i18n['UTB.008']);
	                 $PIN_CDOE.val('');
	             } else if (cardStatus == '2') {
	                 showFancyBox(_i18n['UTB.009']);
	                 $PIN_CDOE.val('');
	             } else {
	                 showFancyBox(_i18n['UTB.008']);
	                 $PIN_CDOE.val('');
	             }
	         } else {
	             showFancyBox(_i18n['UTB.007']);
	             $PIN_CDOE.val('');
	         }
	     } else {
	           // showFancyBox(_i18n['UTB.003']);
	           showPopMsgWithNoX(_i18n['UTB.003'],1,function(){
	             window.setCloseConfirm(false);
	               window.location.replace(prop.NTB_LEAD_PAGE);
	           });
	       }
	     var F_GetErrorMsg = function F_GetErrorMsg() {
	         if (ATLActiveXControl.GetErrorCode != '0') {
	             if (_i18n['PKCS.EC.' + ATLActiveXControl.GetErrorCode] != undefined) {
	                 showFancyBox(_i18n['PKCS.EC.' + ATLActiveXControl.GetErrorCode]);
	                 if (ATLActiveXControl.GetErrorCode == '36897') {
	                     $PIN_CDOE.val('');
	                 }
	             } else {
	                 logDebug("_i18n['PKCS.EC.'+ATLActiveXControl.GetErrorCode]]" + _i18n['PKCS.EC.' + ATLActiveXControl.GetErrorCode]);
	             }
	         }
	     }();
	     ATLActiveXControl.Logout(); // 登出
	     return '';
	 
	},
	login : function(id, pwd) {
	       if (ATLActiveXControl.GetVersion && pwd) { // 取得元件版本 && 判斷密碼是否有輸入
	           var _tokenStatus = ATLActiveXControl.SetTokenType('2');
	           // set token
	           if (_tokenStatus == '0') { // 設定token 1:PFX, 2卡片載具
	               var cardStatus = ATLActiveXControl.GetCardStatus();
	               var cardType = ATLActiveXControl.GetCardType();
	               // 檢查讀卡機中是否有卡片
	               if (cardStatus == '0' && cardType == '1') {
	                   if (ATLActiveXControl.Login(pwd, '') == '0') {
	                       // 選擇簽章憑證 1:簽章 2:加密
	                       if (ATLActiveXControl.SelectCert('1') == '0') {
	                           // 取得身份證後四碼做比對
	                           // logDebug('Tail Citizen ID is '+
								// ATLActiveXControl.GetHiCertTailCitizenID);
	                           if (id.substr(6, 4) != ATLActiveXControl.GetHiCertTailCitizenID) {
	                               showFancyBox(_i18n['UTB.006']);
	                               $PIN_CDOE.val('');
	                           } else {
	                             console.log("Login Successful!")
	                             return true;
	                           }
	                       }
	                   }
	                   $PIN_CDOE.val('');
	               } else if (cardStatus == '1') {
	                   showFancyBox(_i18n['UTB.008']);
	                   $PIN_CDOE.val('');
	               } else if (cardStatus == '2') {
	                   showFancyBox(_i18n['UTB.009']);
	                   $PIN_CDOE.val('');
	               } else {
	                   showFancyBox(_i18n['UTB.008']);
	                   $PIN_CDOE.val('');
	               }
	           } else {
	               showFancyBox(_i18n['UTB.007']);
	               $PIN_CDOE.val('');
	           }
	       } else {
	           // showFancyBox(_i18n['UTB.003']);
	           showPopMsgWithNoX(_i18n['UTB.003'],1,function(){
	             window.setCloseConfirm(false);
	               window.location.replace(prop.CLM_API_LEAD_PAGE);
	           });
	       }
	       var F_GetErrorMsg = function F_GetErrorMsg() {
	           if (ATLActiveXControl.GetErrorCode != '0') {
	               if (_i18n['PKCS.EC.' + ATLActiveXControl.GetErrorCode] != undefined) {
	                   showFancyBox(_i18n['PKCS.EC.' + ATLActiveXControl.GetErrorCode]);
	                   if (ATLActiveXControl.GetErrorCode == '36897') {
	                       $PIN_CDOE.val('');
	                   }
	               } else {
	                   logDebug("_i18n['PKCS.EC.'+ATLActiveXControl.GetErrorCode]]" + _i18n['PKCS.EC.' + ATLActiveXControl.GetErrorCode]);
	               }
	           }
	       }();
	       return '';
	  }
}