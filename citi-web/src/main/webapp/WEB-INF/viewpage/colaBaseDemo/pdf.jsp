<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content=cola>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>PDF</title>
</head>
<body>
    <script>
          loadScript('js/colaBaseDemo/pdf');
        </script>
    <div style="height: 460px;">
        <form id="otpForm" autocomplete="off" method="post">
            <h3>PDF</h3>
	        <input id="pdfPath" type="text" maxlength="2" placeholder="請輸入PDF產生路徑" value=""/>
            <button id="genPDF" class="sendbtn" type="button" style="">產生PDF</button>
            <br /> 
            <hr>
            <button id="dwnPDF" class="sendbtn" type="button" style="background: red;">下載PDF</button>
            <br />
            <fieldset>
                <legend>
                    <h3>Message</h3>
                </legend>
                 <span id="pdfResultMsg"></span><br />
            </fieldset>
        </form>
    </div>
</body>
</html>