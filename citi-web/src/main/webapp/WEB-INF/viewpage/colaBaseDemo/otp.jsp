<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content=cola>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>OTP身份認證</title>
</head>
<body>
	<script>
		loadScript('js/colaBaseDemo/otp');
  	</script>
	<div style="height: 560px;">
		<form id="otpForm" autocomplete="off" method="post">
			<h3>OTP身份認證</h3>
			<input type="text" id="mobilePhone" maxlength="10" placeholder="請輸入發送OTP簡訊手機號碼" />
			<button id="sendbtn" class="sendbtn" type="button">發送OTP簡訊</button><br/>
			<input type="password" id="otp" name="otp" maxlength="6" placeholder="請輸入簡訊動態密碼OTP" />
			<button id="reSendOTPbtn" class="sendbtn" type="button">重新發送簡訊動態密碼OTP</button><br/>
			<input id="otpSeconds" type="text" name="ss" readOnly> 秒失效
		</form>
	</div>
</body>
</html>