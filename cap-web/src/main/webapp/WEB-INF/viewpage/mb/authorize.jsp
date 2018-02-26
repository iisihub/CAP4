<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:useBean id="sysProp" class="com.iisigroup.cap.base.CapSystemProperties" scope="application" />
<!DOCTYPE>
<html>
<head>
<meta name="decorator" content="mb">
<title>Hello Mobile Bank ::: Authorize</title>
<style>
</style>
</head>
<body>
    <script>
  	loadScript('js/mb/authorize');
    </script>
    <h1>Hello Mobile Bank ::: Authorize</h1>
    <form method="get" autocomplete="off" onsubmit="return false;">
        <div>Scopes</div>
        Inquiry:
        <label>
            <input type="radio" id="inquiry" name="inquiry" class="validate[required]" value="inquiry" checked="checked"/>Approve
        </label>
        <label>
            <input type="radio" id="inquiry" name="inquiry" class="validate[required]" value="X"/>Deny
        </label>
        <br/>
        Pay:
        <label>
            <input type="radio" id="pay" name="pay" class="validate[required]" value="pay" checked="checked"/>Approve
        </label>
        <label>
            <input type="radio" id="pay" name="pay" class="validate[required]" value="X"/>Deny
        </label>
        <br/>
        Other:
        <label>
            <input type="radio" id="other" name="other" value="other"/>Approve
        </label>
        <label>
            <input type="radio" id="other" name="other" value=""/>Deny
        </label>
        <br/>
        <input type="hidden" id="client_id" name="client_id" value="${sysProp['client_id']}"/>
        <!-- <input type="hidden" id="username" name="username" value=""/> -->
        <!-- <input type="hidden" id="scope" name="scope" value="inquiry%20pay"/> -->
        <!-- <input type="hidden" id="scope" name="scope" value="NO_SCOPE"/> -->
        <button id="submit">Submit</button>
    </form>
</body>
</body>
</html>