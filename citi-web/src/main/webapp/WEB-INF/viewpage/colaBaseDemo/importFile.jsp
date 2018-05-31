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
        loadScript('js/colaBaseDemo/importFile');
    </script>
    <div style="height: 700px;">
        <form id="importForm" autocomplete="off" method="post" onsubmit="return false;">
            <h3>現有匯檔流程</h3>
            <input type="text" id="localFilePath1" name="localFilePath1" maxlength="100" placeholder="請輸入本地檔案路徑" style="width: 40%;" />
            <input type="text" id="localFileName1" name="localFileName1" maxlength="100" placeholder="請輸入本地檔案名稱" style="width: 40%;" />
            <input type="text" id="days1" name="days1" maxlength="100" placeholder="請輸入指定天數" style="width: 40%;" />
            <input type="text" id="date1" name="date1" maxlength="100" placeholder="請輸入指定天(yyyyMMdd)" style="width: 40%;" />
            <input type="text" id="remoteFilePathName1" name="remoteFilePathName1" maxlength="100" placeholder="請輸入遠端檔案路徑與名稱(完整路徑)" style="width: 40%;" />
            <input type="text" id="storedProcedureName1" name="storedProcedureName1" maxlength="100" placeholder="請輸入Stored Procedure名稱" style="width: 40%;" /><br/>
            <button id="sendBtn" class="sendbtn" type="button">測試匯檔</button><br/>
            <span>-----------------------------------------------------------------------------------------------------</span><br/>
            <h3>匯檔步驟</h3>
            <button id="checkTimeBtn" class="sendbtn" type="button">1-1.檢查本地檔案是否在指定天數之內</button><br/>
            <button id="checkDateBtn" class="sendbtn" type="button">1-2.檢查本地檔案是否在指定天(yyyyMMdd)</button><br/>
            <button id="countRowsBtn" class="sendbtn" type="button">2.算本地檔案的資料數</button><br/>
            <button id="runSPBtn" class="sendbtn" type="button">3.執行Stored Procedure進行匯檔</button><br/>
        </form>
        <fieldset style="font-size: 1.5em; margin-top: 20px;">
            <legend>結果</legend>
            <div id="resultBoard">
            </div>
        </fieldset>
	</div>
</body>
</html>