<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<!DOCTYPE html>
<html>
<head>
<title><decorator:title /></title>
<decorator:getProperty property="i18n" default="" />
<decorator:head />
</head>
<body>
    <decorator:body />
</body>
</html>
