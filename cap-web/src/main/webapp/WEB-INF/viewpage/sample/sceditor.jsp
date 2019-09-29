<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<meta name="decorator" content="none">
<link rel="stylesheet" href="../static/lib/js/jquery/plugin/sceditor/themes/modern.min.css" type="text/css" media="all" />
<title>ProtoTyping Editor</title>
</head>
<body>
    <script>
          loadScript('js/sample/sceditor');
        </script>
    <button id="ok" type="button" class="btn1">ok</button>
    <button id="reset" type="button" class="btn1">reset</button>
    <div class="preview">
        <div class="script"></div>
    </div>
    <hr>
    <div>
        <textarea id="tt" name="tt" style="height: 280px; width: 730px;"></textarea>
        <p>If you are using IE9+ or any other browser then it should automatically replace :) and other emoticon codes with theit emoticon images.</p>
    </div>
    <p>
        SCEditor is licensed under the <a href="http://www.opensource.org/licenses/mit-license.php">MIT</a>
    </p>
</body>
</html>
