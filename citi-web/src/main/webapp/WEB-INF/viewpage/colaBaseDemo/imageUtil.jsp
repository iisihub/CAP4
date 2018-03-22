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

    </style>
</head>
<body>
<script>
    loadScript('js/colaBaseDemo/imageUtil');
</script>
<div style="height: 660px;">
    <form id="imageUtilForm" autocomplete="off" method="post">
        <h3>圖片檔案轉檔成TIFF</h3>
        輸出檔案資料夾
        <input class="longInput" type="text" id="outputFolderPath" name="" maxlength="200"
               placeholder="請輸入輸出檔案的Folder path"/><br/>
        <label for="inputFolder">
            <input class="radioInput" id="inputFolder" type="radio" name="inputType"
                   value="0"> 整個資料夾轉檔 <br/>
        </label>
        <input class="longInput" type="text" id="inputFolderPath" name="" maxlength="200"
               placeholder="請輸入要轉檔的Folder path"/><br/>
        <label for="inputFiles">
            <input class="radioInput" id="inputFiles" type="radio" name="inputType" value="1"> 多單檔轉檔 (最多5筆檔案) <br/>
        </label>
        <span id="singleFiles"></span>
        <%--<input class="longInput" type="text" name="inputFile" maxlength="200" placeholder="請輸入要轉檔的Folder path"/>--%>
        <br/>
        <button id="addFileBtn" class="sendbtn" type="button">＋新增檔案</button>

        <button id="sendTranBtn" class="sendbtn" type="button">轉檔</button>
        <br/><br/>
        <input class="longInput" type="text" id="transBase64InputFilePath" name="inputFile" maxlength="200"
               placeholder="請輸入要轉Base64的File path"/>
        <button id="sendBase64Btn" class="sendbtn" type="button">單檔轉成Base64字串</button>
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