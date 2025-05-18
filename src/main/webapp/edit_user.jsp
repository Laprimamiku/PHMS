<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>编辑用户</title>
    <link rel="stylesheet" type="text/css" href="css/addedit.css">
</head>
<body>
    <div class="container">
        <h2>编辑用户信息</h2>

        <!-- 显示注册成功或登出成功的提示信息 -->
        <%
            String message = (String) request.getAttribute("message");
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (message != null) {
        %>
        <script>
            showMessage("<%= message %>", 'success'); 
        </script>
        <% } else if (errorMessage != null) { %>
        <script>
            alert("<%= errorMessage %>");
        </script>
        <% } %>

        <form action="UserServlet?action=update" method="POST">
            <input type="hidden" name="id" value="${user.id}" />
            <p><strong>用户名:</strong> <input type="text" name="username" value="${user.username}" required minlength="3" /></p>
            <p><strong>密码:</strong> <input type="password" name="password" value="${user.password}" required minlength="8" /></p>
            <p><strong>邮箱:</strong> <input type="email" name="email" value="${user.email}" required /></p>
            <p><strong>角色:</strong> 
                <select name="type">
                    <option value="user">用户</option>
                    <option value="admin">管理员</option>
                </select>
            </p>
            <p><button type="submit">更新</button></p>
            <p><a href="UserServlet?action=list">取消</a></p>
        </form>
    </div>
</body>
</html>
