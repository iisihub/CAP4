<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
<head>
<link rel="stylesheet" href="../../static/css/colaBaseDemo/colabase.css" />
<link rel="stylesheet" href="../../static/lib/css/common.message.css" />
<script type="text/javascript">
  var baseUrl = "../../static";
</script>
<title>Cola Base</title>
<!-- jQuery -->
<script type="text/javascript" src="https://www.citibank.com.tw/sim/zh-tw/js/jquery-1.8.3.min.js"></script>
<script src="../../static/requirejs/2.3.2/require.min.js"></script>
<script src="../../static/main.js"></script>
<decorator:getProperty property="reqJSON" default="" />
<decorator:getProperty property="i18n" default="" />
<decorator:getProperty property="prop" default="" />
<decorator:head />
</head>
<body>
    <div class="mainBody">
        <header>
            <div class="header">
                <img src="../../static/images/colaBaseDemo/colaBaseBanner.jpg">
            </div>
        </header>
        <div class="main">
            <decorator:body />
        </div>
        <footer class="footer">
            <p class="copyright">資拓宏宇國際股份有限公司 Copyright ® 2018 IISIGroup</p>
        </footer>
    </div>
</body>
</html>
