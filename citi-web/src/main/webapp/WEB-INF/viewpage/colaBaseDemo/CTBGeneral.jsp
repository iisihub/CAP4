<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html> 
<head>
<meta name="decorator" content=cola>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<!-- <meta NAME="GENERATOR" Content="Microsoft FrontPage 3.0"> -->
<title>MOICA元件測試</title>
<!-- <script charset="utf-8" src="jquery-1.10.2.js"></script> -->
<!-- <script charset="utf-8" src="CTBMoicaService.js"></script> -->
 </head>    
<body>
    <script>
          loadScript('js/common/CTBMoicaService');
    </script>

	<form id="myform" name="myform">
	<input type="button" class="sendbtn btn1 ui-button ui-corner-all ui-widget" name="mybutton1" value="getVersion" onClick="getVersion()">  
	APIVersion:  <input type="text" name ="ServiceVersion" value="" size=18>  <br> 
	--------------SetTokenType------------------------- <br>  
	<input type="text" name ="tokenType" value="2" size=3>
	<input type="button" class="sendbtn btn1 ui-button ui-corner-all ui-widget" name="mybutton2" value="SetTokenType" onClick="settokentype()"> 
	ret<input name ="retSetTokenType" value=""  size=6> <br> 	
	--------------GetCardStatus------------------------- <br> 
	<input type="button" class="sendbtn btn1 ui-button ui-corner-all ui-widget" name="mybutton3" value="GetCardStatus" onClick="getCardStatus()">
	ret<input name ="retGetCardStatus" value=""  size=6><br>	
	--------------GetCardType--------------------------- <br>
	<input type="button" class="sendbtn btn1 ui-button ui-corner-all ui-widget" name="mybutton4" value="GetCardType" onClick="getCardType()">
	ret<input name ="retGetCardType" value=""  size=6><br>	
	--------------GetGPKICardNo----------------------- <br> 
	<input type="button" class="sendbtn btn1 ui-button ui-corner-all ui-widget" name="mybutton5" value="GetGPKICardNo" onClick="getGPKICardNo()">
	ret<input name ="retGetGPKICardNo" value=""><br>	 
    --------------Login------------------------------------ <br>
	PINCode<input name="Login_PINCode" value="123456" > 
	PfxPath<input name="Login_PfxPath" value="c:/W100349834.p12" > 
    <input type="button" class="sendbtn btn1 ui-button ui-corner-all ui-widget" name="mybutton6" value="Login" onClick="login()">
	ret<input name ="retLogin" value="" size=6><br>	  
	--------------ChangePIN----------------------------- <br>
	OldPIN<input name="ChangePIN_OldPIN" value="123456" > 
	NewPIN<input name="ChangePIN_NewPIN" value="111111" > 
	<input type="button" class="sendbtn btn1 ui-button ui-corner-all ui-widget" name="ChangePIN"  value="ChangePIN" onclick="changePIN()">
	ret<input name ="retChangePIN" value="" size=6 ><br>
	--------------Logout---------------------------------- <br>
	<input type="button" class="sendbtn btn1 ui-button ui-corner-all ui-widget" name="LogoutBtn" value="Logout" onclick="logout()">
	ret<input name ="retLogout" value="" size=6><br>	
	--------------SelectCert------------------------------ <br>
	CertType<input name="SelectCert_CertType" value="1" size=4> 
	<input type="button" class="sendbtn btn1 ui-button ui-corner-all ui-widget" value="SelectCert" name="SelectCert" onclick="selectCert()">
	ret<input name ="retSelectCert" value="" size=6><br>	
			CertFinger<input name ="CertFinger" value="" size=60>
			SelectedCertSerial<input name ="SelectedCertSerial" value="" size=40>	<br>	
			SelectedCertB64<textarea name ="SelectedCertB64" rows="2" cols="116"></textarea><br>
			SelectedCertSubject<input name ="SelectedCertSubject" value="" size=60>	
			SelectedCertIssuer<input name ="SelectedCertIssuer" value="" size=60><br>	
			SelectedCertNotBefore<input name ="SelectedCertNotBefore" value="" size=18>	
			SelectedCertNotAfter<input name ="SelectedCertNotAfter" value="" size=18>			
			SelectedCertKeyUsage<input name ="SelectedCertKeyUsage" value="" size=4>
			SelectedCertSignAlg<input name ="SelectedCertSignAlg" value="" ><br>
			SelectedCertKeySize<input name ="SelectedCertKeySize" value="" size=6>
			SelectedCertVersion<input name ="SelectedCertVersion" value="" size=4>
			HiCertType<input name ="HiCertType" value="" size=18>
			HiCertTailCitizenID<input name ="HiCertTailCitizenID" value="" size=6>
			HiCertUniformOrganizationID<input name ="HiCertUniformOrganizationID" value="" size=8>
			CardPriSec<input name ="CardPriSec" value="" size=8><br>
			--------------SignPKCS7--------------------------<br>
			toSigndata<input name="toSigndata" value="test" size=60> <br>
			<input type="button" class="sendbtn btn1 ui-button ui-corner-all ui-widget" value="SignPKCS7" name="SignPKCS7" onclick="signPKCS7()">
			ret<input name ="retSignPKCS7" value="" size=6>	<br>
			PKCS7Data<textarea name ="PKCS7Data" rows="2" cols="116"></textarea><br>
			--------------VerifyPKCS7-----------------------<br>
			toVerifyData<textarea name ="toVerifyData" rows="2" cols="116">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</textarea><br> 
			<input type="button" class="sendbtn btn1 ui-button ui-corner-all ui-widget" value="VerifyPKCS7" name="VerifyPKCS7" onclick="verifyPKCS7()">
			ret<input name ="retVerifyPKCS7" value="" size=6><br>	
			ErrorMsg<input name ="ErrorMsg" value="" size=60><br>
    <input type="button" class="sendbtn btn1 ui-button ui-corner-all ui-widget" value="產生P7b檔案" name="GenP7bData" onclick="genP7bData()">
        路徑<input name ="p7bPath" value="D:/Test.p7b" size=60><br>
    GenP7bMsg<input name ="GenP7bMsg" value="" size=60><br>
	<input type="button" class="sendbtn btn1 ui-button ui-corner-all ui-widget" value="後端檢測憑證有效性" name="VerifyCRL" onclick="verifyCRL()"><br>
    <textarea name ="VerifyCRLResult" rows="4" cols="116"></textarea><br>
   </form>  
   <script type="text/javascript">
			// alert(navigator.userAgent);
// 			if (navigator.appName == "Netscape") {
// 				var trident = !!navigator.userAgent.match(/Trident\/7.0/);
// 				var net = !!navigator.userAgent.match(/.NET4.0E/);
// 				var IE11 = trident;// && net
// 				var IEold = (navigator.userAgent.match(/MSIE/i) ? true : false);
// 				if (IE11 || IEold) {
// 					ATLActiveXControl = document.getElementById('embed1');
// 				} else {
// 					ATLActiveXControl = ATLActiveXControl;//document.getElementById('embed2'); 	
// 				}
// 			} else
// 				ATLActiveXControl = document.getElementById('embed1');

			function getVersion() {
				var myversion;
				myversion = ATLActiveXControl.GetVersion;
				if (myversion == "") {
					alert("GetAPIVersion error code");
					return;
				}
				myform.ServiceVersion.value = myversion;
			}
			function settokentype() {
				myform.retSetTokenType.value = ATLActiveXControl
						.SetTokenType(myform.tokenType.value);
			}

			function getCardStatus() {
				myform.retGetCardStatus.value = ATLActiveXControl.GetCardStatus();
				if (myform.retGetCardStatus.value != 0)
					myform.ErrorMsg.value = ATLActiveXControl.GetErrorMsg;
				else
					myform.ErrorMsg.value = "";
			}
			function getCardType() {
				myform.retGetCardType.value = ATLActiveXControl.GetCardType();
				myform.ErrorMsg.value = ATLActiveXControl.GetErrorMsg;
			}
			function getGPKICardNo() {
				myform.retGetGPKICardNo.value = ATLActiveXControl.GetGPKICardNo;
				if (myform.retGetGPKICardNo.value == "")
					myform.ErrorMsg.value = ATLActiveXControl.GetErrorMsg;
			}
			function login() {
				myform.retLogin.value = ATLActiveXControl.Login(
						myform.Login_PINCode.value, myform.Login_PfxPath.value);
				if (myform.retLogin.value != 0)
					myform.ErrorMsg.value = ATLActiveXControl.GetErrorMsg;
				else
					myform.ErrorMsg.value = "";

			}
			function changePIN() {
				myform.retChangePIN.value = ATLActiveXControl.ChangePIN(
						myform.ChangePIN_OldPIN.value,
						myform.ChangePIN_NewPIN.value);
				if (myform.retChangePIN.value != 0)
					myform.ErrorMsg.value = ATLActiveXControl.GetErrorMsg;
				else
					myform.ErrorMsg.value = "";
			}
			function logout() {
				myform.retLogout.value = ATLActiveXControl.Logout();
			}
			function selectCert() {
				var ret = ATLActiveXControl.SelectCert(myform.SelectCert_CertType.value);
				myform.retSelectCert.value = ret;
				if (ret == 0) {
					myform.ErrorMsg.value = "";
					myform.CertFinger.value = ATLActiveXControl.GetCertFinger;
					myform.SelectedCertB64.value = ATLActiveXControl.GetSelectedCertB64;
					myform.SelectedCertSerial.value = ATLActiveXControl.GetSelectedCertSerial;
					myform.SelectedCertSubject.value = ATLActiveXControl.GetSelectedCertSubject;
					myform.SelectedCertIssuer.value = ATLActiveXControl.GetSelectedCertIssuer;
					myform.SelectedCertNotBefore.value = ATLActiveXControl.GetSelectedCertNotBefore;
					myform.SelectedCertNotAfter.value = ATLActiveXControl.GetSelectedCertNotAfter;

					myform.SelectedCertKeyUsage.value = ATLActiveXControl.GetSelectedCertKeyUsage;
					myform.SelectedCertSignAlg.value = ATLActiveXControl.GetSelectedCertSignAlg;
					myform.SelectedCertKeySize.value = ATLActiveXControl.GetSelectedCertKeySize;
					myform.SelectedCertVersion.value = ATLActiveXControl.GetSelectedCertVersion;
					myform.HiCertType.value = ATLActiveXControl.GetHiCertType;
					myform.HiCertTailCitizenID.value = ATLActiveXControl.GetHiCertTailCitizenID;
					myform.HiCertUniformOrganizationID.value = ATLActiveXControl.GetHiCertUniformOrganizationID;
					myform.CardPriSec.value = ATLActiveXControl.GetCardPriSec;
				} else
					myform.ErrorMsg.value = ATLActiveXControl.GetErrorMsg;
			}
			function signPKCS7() {
				var ret = ATLActiveXControl.SignPKCS7(myform.toSigndata.value);
				myform.retSignPKCS7.value = ret;
				if (ret == 0) {
					myform.ErrorMsg.value = "";
					myform.PKCS7Data.value = ATLActiveXControl.GetPKCS7Data;
				} else
					myform.ErrorMsg.value = ATLActiveXControl.GetErrorMsg;
			}
			function verifyPKCS7() {
				var ret = ATLActiveXControl.VerifyPKCS7(myform.toVerifyData.value);
				myform.retVerifyPKCS7.value = ret;
				if (ret == 0) {
					myform.ErrorMsg.value = "";
				} else
					myform.ErrorMsg.value = ATLActiveXControl.GetErrorMsg;
			}
			function toHex(d) {
				if (d < 0) {
					d = 0xFFFFFFFF + d + 1;
				}
				var hex = Number(d).toString(16);
				if ((hex.length % 2) != 0) {
					hex = "0" + hex;
				}
				return hex;
			}
			function genP7bData() {
			  	var form = $('#myform');
				var datas = form.serializeData();
			  		$.ajax({
						url : url('demomoicahandler/genP7bData'),
					type : 'post',
					data : datas,
					success : function (d) {
					  myform.GenP7bMsg.value = d.msg
					}
				 });
			}
			function verifyCRL() {
				var form = $('#myform');
				var datas = form.serializeData();
				$.ajax({
					url : url('demomoicahandler/demoMoica'),
					type : 'post',
					data : datas,
					success : function (d) {
					  if (d.msg="0000") {
						  myform.VerifyCRLResult.value = "success"
					  } else {
						  myform.VerifyCRLResult.value = d.msg
					  }
// 						console.log('success');
					}
				})
			}
	</script>
</body>
<Object classid="clsid:74E3DB8C-7E99-438A-9542-B3BBA37AB925" id="embed1" name="embed1" height="30" width="26">
  <param name="_ExtentX" value="661">
  <param name="_ExtentY" value="714">
</object>
</html>



