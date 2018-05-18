<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content=cola>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>PDF</title>
</head>
<body>
    <script>
          loadScript('js/colaBaseDemo/pdf');
        </script>
    <div style="height: auto;">
        <form id="pdfForm" autocomplete="off" method="post">
            <h3>Input Demo PDF Content</h3>
            <input id="custName" type="text" maxlength="20" placeholder="請輸入姓名" value=""/>
            <br />
            <input id="idNo" type="text" maxlength="10" placeholder="請輸入身分證字號" value=""/>
            <br />
            <input id="mPhone" type="text" maxlength="10" placeholder="請輸入手機號碼" value=""/>
            <br />
            <hr/>
            <h3>Generate PDF</h3>
	        <input id="pdfPath" type="text" placeholder="請輸入PDF產生路徑" value=""/>
            <br />
	        <input id="pdfName" type="text" placeholder="請輸入PDF檔案名稱" value=""/>
            <br />
	        <input id="pdfPwd" type="password" maxlength="20" placeholder="請輸入PDF加密密碼" value=""/>
            <br />
            <button id="genPDF" class="sendbtn" type="button" style="">產生PDF</button>
            <button id="dwnPDF" class="sendbtn" type="button" style="background: red;">下載PDF</button>
            <br />
            <hr/>
            <h3>Merge PDF</h3>
	        <input id="mgPDFPath1" type="text" placeholder="請輸入要合併的PDF1路徑" value=""/>
            <br />
	        <input id="mgPDFPath2" type="text" placeholder="請輸入要合併的PDF2路徑" value=""/>
            <br />    
	        <input id="genMgPDFPath" type="text" placeholder="請輸入要合併後PDF產生路徑" value=""/>
            <br />    
            <input id="genMgPDFName" type="text" placeholder="請輸入要合併後PDF檔案名稱" value=""/>
            <br />
            <button id="mergePDF" class="sendbtn" type="button" style="">合併PDF</button>
            <br />
            <hr/>
            <h3>Partition PDF</h3>
	        <input id="partPDFPath" type="text" placeholder="請輸入欲分割之PDF路徑" value=""/>
            <br />
	        <input id="partPDFOutputPath" type="text" placeholder="請輸入分割後PDF路徑" value=""/>
            <br />
	        <input id="partPDFStartPage" type="text" placeholder="請輸入PDF欲分割之頁數" value=""/>
            <br />
            <button id="partitionPDF" class="sendbtn" type="button" style="">分割PDF</button>
            <h3>PDF Add WaterMark</h3>
	        <input id="wmPDFInputPath" type="text" placeholder="請輸入欲加入浮水印之PDF路徑" value=""/>
            <br />
	        <input id="wmPDFOutputPath" type="text" placeholder="請輸入加入浮水印後之PDF路徑" value=""/>
            <br />
	        <input id="wmNamePDF" type="text" placeholder="請輸入PDF欲加入浮水印文字" value=""/>
            <br />
            <button id="waterMarkPDF" class="sendbtn" type="button" style="">PDF加入浮水印</button>
            <fieldset>
                <legend>
                    <h3>Message</h3>
                </legend>
                 <span id="pdfResultMsg"></span><br />
            </fieldset>
        </form>
    </div>
</body>
</html>