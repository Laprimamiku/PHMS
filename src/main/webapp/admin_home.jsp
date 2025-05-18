<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List, model.User"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>管理员界面</title>

    <!-- 引入新的样式 -->
    <link rel="stylesheet" type="text/css" href="css/admin.css">
</head>
<body>
    <!-- 导航栏 -->
    <header>
        <nav> 
            <div id="logo">
            	<a href="admin_home.jsp">
               		<img src="image/logo1.png" alt="Logo" style="width: 350px; height: auto;">
            	</a>
        	</div>
            <ul id="nav">
                <li><a href="admin_home.jsp">首页</a></li>
                <li><a href="pet_manage.jsp">维护宠物信息</a></li>
                <li><a href="PetServlet?action=approveList">审核宠物信息</a></li>
                <li><a href="PetServlet?action=adoptList">审核领养信息</a></li>
                <li><a href="#">发布/维护公告信息</a></li>
                <li><a href="UserServlet?action=search">维护用户信息</a></li>
               <li><a href="UserServlet?action=logout" class="logout">登出</a></li>
            </ul>
        </nav>
    </header>

    <!-- 主体内容 -->
    <div class="container">
        <!-- 第一部分：显示登录用户信息 -->
        <div class="section">
            <h2>欢迎管理员</h2>
            <p><strong>用户名：</strong>${sessionScope.username}</p>
            <p><strong>角色：</strong>${sessionScope.role}</p>
            <p><strong>邮箱：</strong>${sessionScope.email}</p>

        </div>
    </div>

    <!-- 页脚 -->
    <footer>
        <p>© 2024 宠物之家管理系统 | 所有权利保留</p>
    </footer>
</body>
</html>
