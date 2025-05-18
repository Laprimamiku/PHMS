<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User"%>
<%@ page import="java.util.List"%>
<%@ page session="true"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>编辑个人信息</title>

<!-- 引入统一的样式 -->
<link rel="stylesheet" type="text/css" href="css/user.css">

<!-- 引入 Font Awesome 图标库 -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">

</head>
<body>
	<!-- 导航栏 -->
	<header>
		<nav>
			<div id="logo">
            	<a href="user_home.jsp">
               		<img src="image/logo1.png" alt="Logo" style="width: 350px; height: auto;">
            	</a>
        	</div>
			<ul id="nav">
				<li><a href="PetServlet?action=adopthome">首页</a></li>     	
				 <li><a href="PetServlet?action=viewMyPets">我的宠物</a></li>
				<li><a href="pet_add.jsp">上传宠物信息</a></li>
				<li><a href="#p">公告</a></li>		
				<li><a href="edit_profile.jsp">个人信息</a></li>
				<li><a href="UserServlet?action=logout">登出</a></li>
			</ul>
		</nav>
	</header>

	<!-- 主体内容 -->
	<main>
		<section class="welcome-section">
			<h1>编辑个人信息</h1>
			<p>修改您的个人信息</p>
		</section>

		<%-- 如果有错误信息，显示它 --%>
		<%
		if (request.getAttribute("errorMessage") != null) {
		%>
		<div class="error-message">
			<%=request.getAttribute("errorMessage")%>
		</div>
		<%
		}
		%>

		<%-- 如果有成功信息，显示它 --%>
		<%
		if (request.getAttribute("successMessage") != null) {
		%>
		<div class="success-message">
			<%=request.getAttribute("successMessage")%>
		</div>
		<%
		}
		%>

		<section class="user-info">
			<h2>个人信息</h2>
			<form action="UserServlet?action=editProfile" method="post">
				<div class="form-group">
					<label for="username">用户名:</label>
					<input type="text" id="username" name="username" value="${sessionScope.username}" required />
				</div>

				<div class="form-group">
					<label for="email">邮箱:</label>
					<input type="email" id="email" name="email" value="${sessionScope.email}" required />
				</div>

				<div class="form-group">
					<label for="password">密码:</label>
					<input type="text" id="password" name="password" value="${sessionScope.password}" required />
				</div>

				

				<div class="form-group">
					<button type="submit" class="submit-button">更新</button>
				</div>
			</form>
		</section>
	</main>

	<!-- 页脚 -->
	<footer>
		<p>© 2024 宠物之家管理系统 | 所有权利保留</p>
	</footer>
</body>
</html>
