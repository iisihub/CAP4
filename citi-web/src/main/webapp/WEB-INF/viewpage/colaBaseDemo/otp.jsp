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
    <div style="height: 460px;">
        <form id="otpForm" autocomplete="off" method="post">
            <h3>OTP身份認證</h3>
            <input type="text" id="otpTimoutSec" maxlength="4" placeholder="請輸入OTP TIME OUT秒數"/>
            <br /> 
            <span class="otpMaxRetry" style="display:none">
                <input type="text" id="otpMaxRetry" maxlength="2" placeholder="請輸入OTP重送最多次數" value=""/>
                <br /> 
            </span>
            <input type="text" id="mobilePhone" maxlength="10" placeholder="請輸入發送OTP簡訊手機號碼" />
            <button id="sendBtn" class="sendbtn" type="button">發送OTP簡訊</button>
            <button id="reSendOtpBtn" class="sendbtn" type="button" style="display:none">重新發送OTP簡訊動態密碼</button>
            <br /> 
            <hr>
            <input type="password" id="otp" name="otp" maxlength="6" placeholder="請輸入6碼OTP簡訊動態密碼" />
            <button id="subimtBtn" class="sendbtn" type="button" style="background: red;">認證OTP簡訊動態密碼</button>
            <br />
            <fieldset>
                <legend>
                    <h3>Message</h3>
                </legend>
                 <span id="otpResultMsg"></span><br />
                 <span id="otpSmsMsg"></span><br /> 
                 <span id="retryMsg"></span>
            </fieldset>
        </form>
    </div>
</body>
</html>