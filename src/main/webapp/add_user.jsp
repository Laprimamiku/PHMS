<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>添加新用户</title>
    <link rel="stylesheet" type="text/css" href="css/addedit.css">
    <script>
        // 用于弹出提示框的函数
        function showMessage(message, type) {
            alert(message);  // 使用浏览器的 alert 弹窗
            if (type === 'success') {
                window.location.href = "UserServlet?action=list"; // 添加成功后跳转到用户列表
            }
        }
    </script>
</head> 
<body>
    <div class="container">
        <h2>添加新用户</h2>

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

        <form action="UserServlet?action=add" method="POST">
            <p><strong>用户名:</strong> <input type="text" name="username" required /></p>
            <p><strong>密码:</strong> <input type="password" name="password" required minlength="8" /></p>
            <p><strong>邮箱:</strong> <input type="email" name="email" required /></p>
            
            <p><strong>角色:</strong> 
                <select name="type">
                    <option value="user">用户</option>
                    <option value="admin">管理员</option>
                </select>
            </p>
            <p><button type="submit">添加</button></p>
        	<p><a href="UserServlet?action=list">取消</a></p>
        </form>
    </div>
</body>
</html>
