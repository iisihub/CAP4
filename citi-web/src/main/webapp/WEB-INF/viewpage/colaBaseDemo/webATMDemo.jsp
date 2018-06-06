<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content=cola>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<script src="../../static/js/colaBaseDemo/CitiWebATMService.js"></script>
<script src="../../static/js/colaBaseDemo/CitiWebATMUtil.js"></script>
<title>WebATM元件測試</title>
</head>
<body>
    <script>
          loadScript('js/colaBaseDemo/webATMDemo');
        </script>
    <div>
        <form name="myform">
            version :<input type="text" name="Version" id="Version" value="" size=20><br>
            <div id="READER_NAMES">
                reader: <select name="READER_NAME" id="READER_NAME"><option selected></option></select>
            </div>
            <br> <input type="Button" name="btnConnectCard" id="btnConnectCard" value="ConnectCard"> <input type="Button" name="btnDisConnectCard" id="btnDisConnectCard"
                value="DisConnectCard"> <br> <br>
            <h3>登入及驗證密碼</h3>
            <hr>
             <input type="text" name="pw" id="pw" placeholder="請輸入密碼"><br>
             <input type="Button" name="login" id="login" value="登入">
             
            <h3>卡片資訊</h3><hr>
            <input type="Button" name="btnGetBankID" id="btnGetBankID" value="銀行代號"> <input type="text" name="bankid" id="bankid" value="" size=40><br> <input type="Button"
                name="btnGetRemark" id="btnGetRemark" value="備註欄"> <input type="text" name="remark" id="remark" value="" size=90><br> <input type="Button" name="btnGetAllAccount"
                id="btnGetAllAccount" value="帳號清單">
            <textarea name="Ef1001Account" id="Ef1001Account" rows="2" cols="110"></textarea>
            <br> <input type="Button" name="btnGetAllInAccount" id="btnGetAllInAccount" value="約定轉入帳號清單">
            <textarea name="Ef1002Account" id="Ef1002Account" rows="2" cols="107"></textarea>
            <br>
            <h3>更改密碼</h3>
            <hr>
             <input type="text" name="oldpw" id="oldpw" placeholder="請輸入原始密碼"><br>
             <input type="text" name="newpw1" id="newpw1" placeholder="請輸入新密碼"><br>
             <input type="text" name="newpw2" id="newpw2" placeholder="再次輸入新密碼"><br>
             <input type="Button" name="changePassword" id="changePassword" value="更改密碼">
            <hr>
            <a  href="balance" id="balance" onclick="javascript:event1(event);" title="餘額查詢">餘額查詢</a>
            <a  href="transfer" id="transfer" onclick="javascript:event1(event);" title="轉帳交易">轉帳交易</a>
            <H1>Status</H1>
            <textarea name="Status" id="Status" rows="5" cols="107"></textarea>
            <input type="hidden" name="iDisConnectType" id="iDisConnectType" size=2 value="2">
        </form>
    </div>
</body>
</html>