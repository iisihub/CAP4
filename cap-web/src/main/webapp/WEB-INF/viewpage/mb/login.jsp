<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE>
<html>
<head>
<meta name="decorator" content="mb">
<title>Hello Mobile Bank ::: Login</title>
<style>
</style>
</head>
<body>
    <script>
    	loadScript('js/mb/login');
    </script>
    <h1>Hello Mobile Bank ::: Login</h1>
    <form method="post" autocomplete="off" onsubmit="return false;">
        <div>Login</div>
        <label for="user">User name:</label>
        <br/>
        <input id="user" name="user"/>
        <br/>
        <label for="password">Password:</label>
        <br/>
        <input type="password" id="password" name="password"/>
        <br/>
        <button id="submit">Submit</button>
    </form>
</body>
</body>
</html>