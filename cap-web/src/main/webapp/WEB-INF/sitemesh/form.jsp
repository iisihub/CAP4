<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
        <!DOCTYPE html>
        <html class="no-js" lang="zh-Hant-TW">
        <!--[if lt IE 7 ]><html class="ie7"><![endif]-->
        <!--[if IE 7 ]><html class="ie7"><![endif]-->
        <!--[if IE 8 ]><html class="ie8"><![endif]-->
        <!--[if IE 9 ]><html class="ie9"><![endif]-->
        <!--[if (gt IE 9)|!(IE) ]><html class=""><![endif]-->

        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
            <meta http-equiv="X-UA-Compatible" content="IE=edge" />
            <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1, user-scalable=0" />
            <title>
                <decorator:title default="CapWebMVC" />
            </title>
            <link rel="stylesheet" href="../../static/css/main.css" />
            <link rel="stylesheet" href="../../static/css/form.css" />
            <link rel="stylesheet" href="../../static/lib/css/common.message.css" />
            <!-- common.message (iLog) -->
            <link rel="shortcut icon" href="../static/images/favicon.ico" />
            <link rel="bookmark" href="../static/images/favicon.ico" />
            <link rel="apple-touch-icon" href="../static/images/apple-touch-icon.png" />
            <!--[if lt IE 9]>
    <script src="../static/lib/js/html5.js"></script>
    <script src="../static/lib/js/respond/respond.min.js"></script>
    <link rel="stylesheet" href="../static/css/ie.css">
<![endif]-->
            <style>
                html {
                    display: none;
                }
            </style>
            <script>
                <!--
                if (self == top) {
                    document.documentElement.style.display = 'block';
                } else {
                    top.location = self.location;
                }
                var baseUrl = "../static";
                //-->
            </script>
            <script src="../../static/requirejs/2.3.2/require.min.js"></script>
            <script src="../../static/main-built.js"></script>
            <decorator:getProperty property="reqJSON" default="" />
            <decorator:getProperty property="i18n" default="" />
            <decorator:head />
        </head>

        <body>
            <div class="mainBody container">
                <header>
                    <div class="logo">
                        <a><img src="../../static/images/logo.png"></a>
                    </div>
                    <ol style="height: 18px; width: 35px; right: 140px;">
                        <li class="lang"><a href="../../j_spring_security_logout">登出</a></li>
                    </ol>
                    <ol style="height: 18px;">
                        <li class="lang"><a href="#language">&nbsp;LANGUAGE&nbsp;</a></li>
                        <li>&nbsp;-<a href="?lang=zh_TW">&nbsp;正體&nbsp;</a></li>
                        <li>&nbsp;-<a href="?lang=zh_CN">&nbsp;简体&nbsp;</a></li>
                        <li>&nbsp;-<a href="?lang=en">&nbsp;ENGLISH&nbsp;</a></li>
                    </ol>
                </header>
                <div class="main">
                    <decorator:body />
                </div>
                <div class="clear"></div>
                <footer>
                    <div class="copyright">資拓宏宇國際股份有限公司 © 2012 版權所有</div>
                </footer>
                <div class="bg-around right">&nbsp;</div>
                <div class="bg-around left">&nbsp;</div>
            </div>
        </body>
        <!--[if lt IE 8]>
    <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
<![endif]-->

        </html>