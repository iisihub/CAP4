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
    </style>
</head>
<body>
<script>
    loadScript('js/colaBaseDemo/address');
</script>
<div style="height: 1060px;">
    <form id="sslClientForm" autocomplete="off" method="post">
        <h3>地址正規化測試頁</h3>
        輸入地址: <br/> *格式：3 or 5 碼 zip code + 縣市 + 鄉鎮市區 + (村里) + (鄰) + 路段 + 巷 + 弄 + 號 + 樓 + 室
        <input type="text" id="address" name="address" class="longInput"
               value="100臺北市中正區仁愛路１段149-2號9樓">
        <br/>
        <%--正規化結果：--%>
        <%--<textarea rows="20" cols="100" id="result" name="result"></textarea>--%>
        <%--傳送JSON字串--%>
        <%--<textarea rows="20" cols="100" id="ownData" name="ownData"></textarea>--%>
        <button id="sendData" class="sendbtn" type="button">傳送</button>
        <%--<br/>--%>
    </form>
    <fieldset style="font-size: 1.5em; margin-top: 20px;">
        <legend>正規化結果</legend>
        <div id="resultBoard" style="color: red; width: 650px; display: block; word-wrap: break-word;">
            <%--success !--%>
        </div>
    </fieldset>
</div>
</body>
</html>