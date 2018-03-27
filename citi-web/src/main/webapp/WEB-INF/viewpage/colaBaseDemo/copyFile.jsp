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
        loadScript('js/colaBaseDemo/copyFile');
    </script>
    <div style="height: 700px;">
        <form id="copyForm" autocomplete="off" method="post" onsubmit="return false;">
            <h3>現有搬檔流程</h3>
            <input type="text" id="domain1" name="domain1" maxlength="100" placeholder="請輸入網域" />
            <input type="text" id="path1" name="path1" maxlength="100" placeholder="請輸入網路路徑" />
            <input type="text" id="userName1" name="userName1" maxlength="100" placeholder="請輸入使用者名稱" />
            <input type="password" id="userXwd1" name="userXwd1" maxlength="100" placeholder="請輸入使用者密碼" />
            <input type="text" id="exportFilePath1" name="exportFilePath1" maxlength="100" placeholder="請輸入搬出資料夾路徑" />
            <input type="text" id="importFilePath1" name="importFilePath1" maxlength="100" placeholder="請輸入搬入資料夾路徑(網路路徑底下)" />
            <input type="text" id="fileName1" name="fileName1" maxlength="100" placeholder="請輸入檔案名稱" />
            <button id="sendBtn" class="sendbtn" type="button">將檔案搬移至指定資料夾</button><br/>
            <span>-----------------------------------------------------------------------------------------------------</span><br/>
            <h3>搬檔步驟</h3>
            <button id="mappingBtn" class="sendbtn" type="button">1.檢查本地是否已掛載目標網路磁碟機</button><br/><br/>
            <input type="text" id="drive1" name="drive1" maxlength="100" placeholder="請輸入指定的磁碟機代號" style="width: 40%;" />
            <button id="connectBtn1" class="sendbtn" type="button">2.連接網路磁碟機，指定本地磁碟機代號</button><br/>
            <input type="text" id="driveLetters1" name="driveLetters1" maxlength="100" placeholder="請輸入可使用的磁碟機代號列表" style="width: 40%;" />
            <button id="connectBtn2" class="sendbtn" type="button">2.連接網路磁碟機，不指定本地磁碟機代號</button><br/>
            <button id="copyBtn" class="sendbtn" type="button">3.搬檔</button><br/>
            <button id="disconnectBtn" class="sendbtn" type="button">4.卸載特定網路磁碟機</button><br/>
            <button id="disconnectAllBtn" class="sendbtn" type="button">4.卸載所有網路磁碟機</button><br/>
        </form>
        <fieldset style="font-size: 1.5em; margin-top: 20px;">
            <legend>結果</legend>
            <div id="resultBoard">
            </div>
        </fieldset>
	</div>
</body>
</html>