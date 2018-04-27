<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta name="decorator" content=cola>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>ImageUtil</title>
    <style type="text/css">
        .mainBody .longInput {
            width: 100%;
        }

        .mainBody .radioInput {
            width: 5%;
        }
        #targetUrl {
            width: 100%;
        }

    </style>
</head>
<body>
<script>
    loadScript('js/colaBaseDemo/sslClient');
</script>
<div style="height: 1060px;">
    <form id="sslClientForm" autocomplete="off" method="post">
        <h3>SSL雙向驗證測試頁</h3>
        targetUrl: <br/>
        <input type="text" id="targetUrl" name="targetUrl"
               value="https://127.0.0.1:8443/mutual-authentication-server/v1/tw/onboarding/customers/deduplicationFlag">
        <br/>
        自定義Header JSON字串
        <textarea rows="20" cols="100" id="ownHeader" name="ownHeader"></textarea>
        傳送JSON字串
        <textarea rows="20" cols="100" id="ownData" name="ownData"></textarea>
        <button id="sendData" class="sendbtn" type="button">傳送</button>
        <br/>
    </form>
    <fieldset style="font-size: 1.5em; margin-top: 20px;">
        <legend>轉檔結果</legend>
        <div id="resultBoard" style="color: red; width: 650px; display: block; word-wrap: break-word;">
            <%--success !--%>
        </div>
    </fieldset>


    <div id="inputTemplate" style="display: none">
        <input class="longInput" type="text" name="inputFile" maxlength="200"
               placeholder="請輸入要轉檔的Folder path"/>
    </div>
</div>
</body>
</html>