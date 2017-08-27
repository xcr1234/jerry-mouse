<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <#if message??><script>
        alert("${message}");
    </script></#if>
</head>
<body>
<form action="${contextPath}/login.do" method="post">
    <p>请登录:</p>
    <p>用户名：<input type="text" name="user"></p>
    <p>密码：<input type="password" name="pass"></p>
    <p><input type="submit" value="登录"></p>
</form>
</body>
</html>