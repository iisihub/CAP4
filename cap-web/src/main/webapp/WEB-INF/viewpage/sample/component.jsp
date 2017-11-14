<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE>
<html>
<head>
<meta name="decorator" content="none">
<title><spring:message code="component.title"></spring:message></title>
<style>
</style>
</head>
<body>
    <script>
          loadScript('js/common/CitiWebATMLib', 'js/common/component');
          // loadScript('js/sample/custComponent');
        </script>
    <h1>
        <spring:message code="component.title"></spring:message>
    </h1>
    <br />
    <form method="post" autocomplete="off" onsubmit="return false;"></form>
</body>
</body>
</html>
