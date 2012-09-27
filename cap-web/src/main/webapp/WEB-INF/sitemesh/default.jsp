<%@ taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
 <!DOCTYPE HTML>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <title><decorator:title default="CapWebMVC" /></title><decorator:head/><!--
        <link rel="stylesheet" href="webroot/static/lib/css/yui_reset.css" />--><!--<link rel="stylesheet" href="webroot/static/lib/js/jquery/ui/css/custom-theme/jquery-ui-1.8.16.custom.css" />--><!--<link rel="stylesheet" href="webroot/static/lib/js/jquery/plugin/jquery.jqGrid-4.2.0/css/ui.jqgrid.css" />--><!--<link rel="stylesheet" href="webroot/static/lib/js/jquery/plugin/formValidator/css/validationEngine.jquery.css" />-->
        <link rel="stylesheet" href="webroot/static/css/main.css" />
        <!--[if lt IE 9]>jquery-ui-1.8.23.custom.min.js
            <script src="webroot/js/html5.js"></script>
        <![endif]--><!--<script data-main="webroot/static/js/main" src="webroot/static/requirejs/2.0.6/require.js"></script>
        -->
		<script src="webroot/static/requirejs/2.0.6/require.min.js"></script>
		<script src="webroot/static/js/main.js"></script>
    </head>
    <body>
        <div class="mainBody">
            <header>
                <div>
                    <a><img src="webroot/static/images/logo.png"></a>
                </div>
            </header><!-- MENU栏 -->
            <nav class="top">
                <ul class="block">
                </ul>
                <ul class="navmenu">
                    <!-- top menu space -->
                </ul>
            </nav>
            <div class="clear">
            </div>
            <div class="main">
                <nav class="sub">
                </nav>
                <article id="article">
                    <decorator:body/>
                </article>
            </div>
            <div class="clear">
            </div>
            <footer>
                <div class="copyright">
                    資拓宏宇國際股份有限公司 © 2011 版權所有
                </div>
            </footer>
            <div class="bg-around right">
                &nbsp;
            </div>
            <div class="bg-around left">
                &nbsp;
            </div>
        </div>
    </body>
</html>
