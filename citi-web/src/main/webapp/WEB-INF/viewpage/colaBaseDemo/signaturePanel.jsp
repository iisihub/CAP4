<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content=cola>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, user-scalable=0">
<title>Signature Panel</title>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/fancybox/2.1.5/jquery.fancybox.css" />
<link rel="stylesheet" href="../../static/css/colaBaseDemo/signature_panel.css" />
</head>
<body>
    <div id="msg">請橫放手機</div>
    <section id="description" class="horizontalCenter">
    <div>
        <span>Description</span>
        <p>Please sign here...</p>
    </div>
    </section>
    <section id="canvasArea" class="horizontalCenter"> <canvas id="mainCanvas" width="550" height="250"></canvas> </section>
    <section id="btnArea" class="horizontalCenter">
    <button id="cleanCanvasBtn">清除</button>
    <button id="getCanvasImgBtn" download="signature.png">確認</button>
    </section>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/fancybox/2.1.5/jquery.fancybox.min.js"></script>
    <script src="../../static/js/colaBaseDemo/signature_panel.js"></script>
</body>
</html>