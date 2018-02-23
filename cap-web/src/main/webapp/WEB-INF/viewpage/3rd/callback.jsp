<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE>
<html>
<head>
<meta name="decorator" content="form">
<title>Hello ::: callback</title>
<style>
</style>
</head>
<body>
    <script>
          loadScript('js/3rd/callback');
        </script>
    <h1>Hello ::: callback</h1>
    <!-- <a href="/cap-web/page/index">回首頁</a> -->
    <form method="post" autocomplete="off" onsubmit="return false;">
        <div>
            <strong>Username:</strong> <span id="username" class="field"></span>
        </div>
        <div>
            <strong>AppEndUser:</strong> <span id="app_enduser" class="field"></span>
        </div>
        <div>
            <strong>Scope:</strong> <span id="scope" class="field"></span>
        </div>
        <div>
            <strong>Token:</strong> <span id="access_token" class="field"></span>
        </div>
        <div>
            <button type="button" id="redo">查詢</button>
        </div>
    </form>
    <strong>Token:</strong>
    <div id="token"></div>
    <strong>Inquiry:</strong>
    <div id="result"></div>
</body>
</body>
</html>