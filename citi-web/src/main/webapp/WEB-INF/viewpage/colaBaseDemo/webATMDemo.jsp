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
<link type="text/css" rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css"/>
<title>WebATM元件測試</title>
</head>
<style>
    .dlg-no-close .ui-dialog-titlebar-close {display: none;}
</style>
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
            <h3>卡片資訊</h3><hr>
            <input type="Button"  name="btnGetBankID" id="btnGetBankID" value="銀行代號"> <input type="text" name="bankid" id="bankid" value="" size=40><br> 
            <input type="Button" name="btnGetAllAccount" id="btnGetAllAccount" value="帳號清單">
            <textarea name="Ef1001Account" id="Ef1001Account" rows="5" cols="110"></textarea>
            <br> <input type="Button" name="btnGetAllInAccount" id="btnGetAllInAccount" value="約定轉入帳號清單">
            <textarea name="Ef1002Account" id="Ef1002Account" rows="5" cols="107"></textarea>
             <input type="Button" name="btnGetRemark" id="btnGetRemark" value="備註欄"> 
            <input type="text" name="remark" id="remark" value="" size=90><br> 
            <br>    
            <h3>登入及驗證密碼</h3>
            <hr>
             <input type="password" name="pw" id="pw" placeholder="請輸入密碼"><br>
             <input type="Button" name="login" id="login" value="登入">
             <input type="Button" name="logout" id="logout" value="登出">
             <div class="deal">
             <br><br>
             <h3>餘額查詢</h3>
             <hr>
              	查詢帳號:
              <select name="balanceAcc" id="balanceAcc" style="width: 300px"></select><br><br>
              <input type="Button"  name="goBalance" id="goBalance" value="餘額查詢"> 
              <div id="showBalance" style="display:none">
    			<h4 >您的帳戶明細</h4>
					<table border=1 summary="餘額查詢結果表" id="balanceDetail">
						<tr>
							<th class="w-30">交易日期&時間</th>
							<td class="w-70"></td>
						</tr>
						<tr>
							<th>交易機號&序號</th>
							<td></td>
						</tr>
						<tr>
							<th>交易結果</th>
							<td class="red"></td>
						</tr>
						<tr>
							<th>查詢帳號</th>
							<td></td>
						</tr>
						<tr>
							<th>帳戶餘額</th>
							<td></td>
						</tr>
						<tr>
							<th>可用餘額</th>
							<td></td>
						</tr>
						<tr>
							<th>中心交易序號</th>
							<td></td>
						</tr>
					</table>
    		  </div>
             <br><br>
             <h3>轉帳交易</h3>
             <hr>   
            	轉出帳號:
              <select name="transferOutAcc" id="transferOutAcc" style="width: 300px"></select><br><br>
              	轉入帳號:
              <select name="transferInAcc" id="transferInAcc" style="width: 300px"></select><br><br>
              	轉出金額:
              <input name="money" id="money" maxlength="7"  type="text"   style="width: 300px" title="轉出金額"><br>
              <input type="Button" name="goTransfer" id="goTransfer" value="轉帳交易"> <br>
              <div id="showTransfer" style="display:none">
    			<h4 >您的帳戶明細</h4>
					<table border=1 summary="顯示轉帳交易結果畫面" id="transferDetail">
						<tr>
							<th class="w-30">交易結果</th>
							<td class="w-70 red"></td>
						</tr>
						<tr>
							<th>交易日期&時間</th>
							<td></td>
						</tr>
						<tr>
							<th>交易機號&序號</th>
							<td></td>
						</tr>
						<tr>
							<th>轉出銀行</th>
							<td></td>
						</tr>
						<tr>
							<th>轉出帳號</th>
							<td></td>
						</tr>
						<tr>
							<th>轉入銀行</th>
							<td></td>
						</tr>
						<tr>
							<th>轉入帳號</th>
							<td></td>
						</tr>
						<tr>
							<th>交易金額</th>
							<td></td>
						</tr>
						<tr>
							<th>手續費</th>
							<td></td>
						</tr>
						<tr>
							<th>帳戶餘額</th>
							<td></td>
						</tr>
						<tr>
							<th>可用餘額</th>
							<td></td>
						</tr>
						<tr>
							<th>中心交易序號</th>
							<td></td>
						</tr>
					</table>
    		  </div>
             <br>
			</div>
            <br>    
            <hr>
            
            <H1>Status</H1>
            <textarea name="Status" id="Status" rows="5" cols="107"></textarea>
            <input type="hidden" name="iDisConnectType" id="iDisConnectType" size=2 value="2">
            <input type="hidden" name="login_sessionid" id="login_sessionid">
            <input type="hidden" name="TRNS_OUT_ACCOUNT_INDEX" id="TRNS_OUT_ACCOUNT_INDEX">
            <input type="hidden" name="randKeypadList" id="randKeypadList">
            <input type="hidden" name="TmlID" id="TmlID">
            <input type="hidden" name="tNow" id="tNow">
            
            
            
            <div id="checkOutInCard" title="請抽插拔卡片" style="display:none">
        	<strong>為確保您的交易安全，請於<nobr >30</nobr>秒內，將晶片卡「<nobr >抽出後再重新插入</nobr>」，才能繼續進行交易。</strong>
    		</div>
    		
    		<div id="checkPassword" title="請輸入密碼" style="display:none">
    		<label for="pw">請輸入晶片金融卡密碼</label>
        	<input type="password" name="checkpw" id="checkpw" title="請輸入晶片金融卡密碼">
    		</div>
            
            
        </form>
    </div>
</body>
</html>