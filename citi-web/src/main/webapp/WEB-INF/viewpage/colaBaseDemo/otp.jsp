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
            <button id="sendBtn" class="sendbtn" type="button">發送OTP簡訊</button><br /> 
            <input type="password" id="otp" name="otp" maxlength="6" placeholder="請輸入6碼OTP簡訊動態密碼" />
            <button id="reSendOtpBtn" class="sendbtn" type="button">重新發送OTP簡訊動態密碼</button><br /> 
            密碼於 <span id="otpSeconds" style="color: red;">60</span> 秒後失效，若密碼失效請按『重送OTP簡訊動態密碼』重送，最多可重送 <span id="otpSeconds" style="color: red;">3</span> 次
            <button id="subimtBtn" class="sendbtn" type="button" style="background: red; margin: 30px 200px;">認證OTP簡訊動態密碼</button><br />
            <p style="display: none; color: red; margin: 20px 225px;">
                密碼驗證 <span id="otpResult">成功</span> !!
            <p>
        </form>
    </div>
</body>
</html>