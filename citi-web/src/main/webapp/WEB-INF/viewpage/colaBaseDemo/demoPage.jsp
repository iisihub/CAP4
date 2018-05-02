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
          loadScript('js/colaBaseDemo/demoPage');
        </script>
    <div>
        <fieldset>
            <legend>
                <h1>Cola Base Module Demo Page</h1>
            </legend>
            <table style="width: 80%; margin: 0 auto;">
                <tr>
                    <td><button id="otpBtn" class="orangeBtn" type="button">OTP</button></td>
                    <td><button id="imageUtilBtn" class="orangeBtn" type="button">ImageUtil</button></td>
                    <td><button id="writingBoardBtn" class="orangeBtn" type="button">SignaturePanel</button></td>
                </tr>
                <tr>
                    <td><button id="zipBtn" class="orangeBtn" type="button">ZIP</button></td>
                    <td><button id="netUseUtilBtn" class="orangeBtn" type="button">NetUseUtil</button></td>
                    <td><button id="sslClientBtn" class="orangeBtn" type="button">SSL Client</button></td>
                </tr>
                <tr>
                    <td><button id="pdfBtn" class="orangeBtn" type="button">PDF</button></td>
                </tr>
            </table>
        </fieldset>
    </div>
</body>
</html>