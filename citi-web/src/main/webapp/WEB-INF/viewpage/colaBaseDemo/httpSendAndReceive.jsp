<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content=cola>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>搬檔</title>
</head>
<body>
    <script>
        loadScript('js/colaBaseDemo/httpSendAndReceive');
    </script>
    <div style="height: 700px;">
        <form id="httpForm" autocomplete="off" method="post" onsubmit="return false;">
            <h3>HTTP</h3>
            <input type="text" id="url" name="url" maxlength="100" placeholder="請輸入目標網址" style="width:100%;" />
            Content Type：
            <input type="radio" id="basic" name="way" value="basic" style="width:10%;height:20px" /> 一般 (Url Encoded Form)
            <input type="radio" id="json" name="way" value="json" style="width:10%;height:20px" /> JSON </br>
                                傳輸資料：
            <input type="text" id="name" name="name" maxlength="100" placeholder="姓名" style="width:100%" />
            <input type="text" id="birthday" name="birthday" maxlength="100" placeholder="生日" style="width:100%" />
            <input type="text" id="mobile" name="mobile" maxlength="100" placeholder="行動電話" style="width:100%" />
<!--             <input type="text" id="paramNames1" name="paramNames1" maxlength="100" placeholder="請輸入參數名稱列表(以逗號隔開)" style="width:100%;display:none" /> -->
<!--             <input type="text" id="paramValue1" name="paramValue1" maxlength="100" placeholder="請輸入參數值(以逗號隔開)" style="width:100%;display:none" /> -->
<!--             <input type="text" id="jsonString1" name="jsonString1" maxlength="100" placeholder="請輸入JSON格式" style="width:100%;display:none" /> -->
            <br/>
            <button id="sendBtn" class="sendbtn" type="button">測試傳送</button><br/>
            <button id="receiveBtn" class="sendbtn" type="button">測試接收</button><br/>
        </form>
        <fieldset style="font-size: 1.5em; margin-top: 20px;">
            <legend>結果</legend>
            <div id="resultBoard">
            </div>
        </fieldset>
	</div>
</body>
</html>