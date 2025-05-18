<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>注册</title>
<link rel="stylesheet" type="text/css" href="css/reglog.css">
</head>
<body>
    <div class="form-container"> 
        <h2>注册页面</h2>
        
        <%-- 显示错误信息 --%>
        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null) {
        %>
        <p class="error-message"><%= errorMessage %></p>
        <% } %>

        <%-- 显示注册成功信息 --%>
        <%
            String successMessage = (String) request.getAttribute("successMessage");
            if (successMessage != null) {
        %>
        <p class="success-message"><%= successMessage %></p>
        <% } %>

        <form action="UserServlet" method="POST">
            <input type="hidden" name="action" value="register">
            <input type="hidden" name="type" value="user"> <!-- 默认用户类型为 user -->
            
            <label for="username">用户名：</label>
            <input type="text" id="username" name="username" required><br><br>
            
            <label for="password">密码：</label>
            <input type="password" id="password" name="password" required><br><br>
            
            <label for="email">邮箱：</label>
            <input type="email" id="email" name="email" required><br><br>
             
            <button type="submit" class="submit-button">注册</button>
        </form>

        <p>已有账号？ <a href="log.jsp">登录</a></p>
    </div>
</body>
</html>
