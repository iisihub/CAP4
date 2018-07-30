<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  
  <style>
	@page {
	  size:210mm 297mm;
	  margin-top: 0;
	  margin-left:0.1cm;
	  margin-right:0.1cm;
	  margin-bottom:0;
	}
    * {
      font-family: 'Microsoft JhengHei';
    }
    pre {
      font-size:14px;
	  position:absolute;
	  left:580px;
    }
    
	<#--主要畫面CSS-->
    .pdfImg img{
      height:95%;
      width:94%;
    }
    <#---立約人簽名CSS--> 
    .custName2{
      top:410px;
    }
    <#---立約人姓名CSS--> 
    .custName1{
      top:325px;
    }
    <#---身分證字號CSS--> 
    .idNo{
      top:370px;
    }
    <#---電話CSS--> 
    .mPhone{
      top:460px;
    }
    <#---日期CSS--> 
    .applyDate{
      top:505px;
    }
  </style>


</head>

<body>
<#--主要畫面-->
  <div class="pdfImg"><img src="${imgPath!}/pdfImgSample.jpg"/></div>
<#--姓名--> 
  <pre class="custName1">${custName!}</pre>
  <pre class="custName2">${onlineSign!}</pre>
<#--身分證字號--> 
  <pre class="idNo">${idNo!}</pre>
<#--電話--> 
  <pre class="mPhone">${mPhone!}</pre>
<#--日期--> 
  <pre class="applyDate">${applyDate!}</pre>
</body>
</html>
