<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content=cola>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>利率計算</title>
</head>
<body>
    <script>
           loadScript('js/colaBaseDemo/loanPage');
    </script>
    <div>
        <legend>
            <h1>Caculate Loan Test Page</h1>
        </legend>
        <form id="oneTier" autocomplete="off" method="post" onsubmit="return false;">
            <h3>一階段利率測試</h3>
            <input type="hidden" id="tierChoose" name="tierChoose" value="oneTier" maxlength="100" readonly/>
            <input type="text" id="amount" name="amount" maxlength="100" placeholder="請輸入貸款金額" required/>
            <input type="text" id="tenor" name="tenor" maxlength="100" placeholder="請輸入總貸款期數" required/>
            <input type="text" id="eppRate" name="eppRate" maxlength="100" placeholder="請輸入利率" required/>
            <input type="text" id="amountupfrontFee" name="amountupfrontFee" maxlength="100" placeholder="請輸入手續費" required/>
            <br />
            <button id="oneTierSendBtn" class="sendbtn" type="button">開始計算結果</button>
            <br />
        </form>
        <p>-------------------------------------------------------------------------------------------</p>
        <form id="twoTier" autocomplete="off" method="post" onsubmit="return false;">
            <h3>二階段利率測試</h3>
            <input type="hidden" id="tierChoose" name="tierChoose" value="twoTier" maxlength="100" readonly/>
            <input type="text" id="amount" name="amount" maxlength="100" placeholder="請輸入貸款金額" />
            <input type="text" id="tenor" name="tenor" maxlength="100" placeholder="請輸入總貸款期數" />
            <input type="text" id="tenorTier1" name="tenorTier1" maxlength="100" placeholder="請輸入第一期期數" />
            <input type="text" id="eppRateTier1" name="eppRateTier1" maxlength="100" placeholder="請輸入第一期利率" />
            <input type="text" id="eppRateTier2" name="eppRateTier2" maxlength="100" placeholder="請輸入第二期利率" />
            <input type="text" id="amountupfrontFee" name="amountupfrontFee" maxlength="100" placeholder="請輸入手續費" />
            <button id="twoTierSendBtn" class="sendbtn" type="button">開始計算結果</button>
            <br />
        </form>
            <textarea name="cauculateResult" rows="10" cols="116"></textarea>
    </div>
</body>
</html>