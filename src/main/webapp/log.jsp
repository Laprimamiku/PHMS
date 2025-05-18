<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="true" %> <!-- 开启 session -->
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>登录</title>
<link rel="stylesheet" type="text/css" href="css/reglog.css">
</head>
<body>
    <div class="form-container">
        <h2>登录页面</h2>

        <%-- 显示注册成功或登出成功的提示信息 --%>
        <%
            String message = (String) request.getAttribute("message");
            if (message != null) {
        %>
        <p class="success-message"><%= message %></p> 
        <% } %>

        <%-- 显示错误消息 --%>
        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null) {
        %>
        <p class="error-message"><%= errorMessage %></p>
        <% } %>

        <%-- 检查用户是否已经登录 --%>
        <%
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId != null) {
                // 如果用户已经登录，提示并重定向到用户主页
        %>
        <p class="info-message">您已经登录！点击 <a href="PetServlet?action=adopthome">这里</a> 返回主页。</p>
        <% } else { %>

        <form action="UserServlet" method="POST">
            <input type="hidden" name="action" value="login"> <!-- 确保 action 参数存在 -->
            
            <label for="username">用户名：</label>
            <input type="text" id="username" name="username" required><br><br>
            
            <label for="password">密码：</label>
            <input type="password" id="password" name="password" required><br><br>
            
            <button type="submit" class="submit-button">登录</button>
        </form>

        <p>没有账号？ <a href="reg.jsp">注册</a></p>
        <% } %>
    </div>
</body>
</html>
