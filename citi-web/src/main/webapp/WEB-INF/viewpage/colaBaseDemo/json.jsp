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

        .mainBody input {
             margin: 0 auto 10px;
             border: 1px solid #d7d7d7;
             font-size: 16px;
         }
        .mainBody input[type='checkbox']{
            width: 20px;
            height: 20px;
        }

    </style>
</head>
<body>
<script>
    loadScript('js/colaBaseDemo/json');
</script>
<div style="height: 1060px;">
    <form id="jsonForm" autocomplete="off" method="post">
        <h3>JSON測試頁</h3>
        父親姓名 <input type="text" name="parentName"> <input type="checkbox" name="nVnS_parentName" value="1">空值不送
        <input type="checkbox" name="pri_parentName" value="1">同類必要 <br/>

        姓名 <input type="text" name="name"> <input type="checkbox" name="nVnS_name" value="1">空值不送
        <input type="checkbox" name="pri_name" value="1">同類必要 <br/>

        性別 <input type="text" name="sex"> <input type="checkbox" name="nVnS_sex" value="1">空值不送
        <input type="checkbox" name="pri_sex" value="1">同類必要 <br/>

        電話1 <input type="text" name="phoneNumber1"> <input type="checkbox" class="nVnS_phoneNumber" name="nVnS_phoneNumber" value="1">空值不送
        <input type="checkbox" class="pri_phoneNumber" name="pri_phoneNumber" value="1">同類必要 <br/>

        區碼1 <input type="text" name="phoneArea1"> <input type="checkbox" class="nVnS_phoneArea" name="nVnS_phoneArea" value="1">空值不送
        <input type="checkbox" class="pri_phoneArea" name="pri_phoneArea" value="1">同類必要 <br/>

        電話2 <input type="text" name="phoneNumber2"> <input type="checkbox" class="nVnS_phoneNumber" name="nouse" value="1">空值不送
        <input type="checkbox" class="pri_phoneNumber" name="nouse" value="1">同類必要 <br/>

        區碼2 <input type="text" name="phoneArea2"> <input type="checkbox" class="nVnS_phoneArea" name="nouse" value="1">空值不送
        <input type="checkbox" class="pri_phoneArea" name="nouse" value="1">同類必要 <br/>




        <%--自定義Header JSON字串 ex: {"accept": ["application/json", "otherType"]}--%>
        <%--<textarea rows="20" cols="100" id="ownHeader" name="ownHeader"></textarea>--%>
        <%--傳送JSON字串--%>
        <%--<textarea rows="20" cols="100" id="ownData" name="ownData"></textarea>--%>
        <button id="sendData" class="sendbtn" type="button">傳送</button>
        <br/>
    </form>
    <fieldset style="font-size: 1.5em; margin-top: 20px;">
        <legend>回應結果</legend>
        <div id="resultBoard" style="color: red; width: 650px; display: block; word-wrap: break-word;">
            <%--success !--%>
        </div>
    </fieldset>
</div>
</body>
</html>