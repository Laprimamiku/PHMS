<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="true" %>
<%
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
        response.sendRedirect("log.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>上传宠物信息</title>
    <link rel="stylesheet" type="text/css" href="css/user.css">
    <link rel="stylesheet" type="text/css" href="css/petinfo.css">
    <link rel="stylesheet" type="text/css" href="css/petadd.css">
</head>
<body>
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
                <li><a href="#">公告</a></li>
                <li><a href="edit_profile.jsp">个人信息</a></li>
                <li><a href="UserServlet?action=logout">登出</a></li>
            </ul>
        </nav>
    </header>

    <main>
        <section class="welcome-section">
            <h1>上传宠物信息</h1>
            <p>请输入宠物的相关信息，并上传宠物图片。</p>

            <form action="PetServlet?action=add" method="post" enctype="multipart/form-data">
                <input type="hidden" name="userId" value="<%= userId %>">
                
                <div class="form-group">
                    <label for="petName">宠物名称:</label>
                    <input type="text" id="petName" name="name" required>
                </div>

                <div class="form-group">
                    <label for="petType">宠物类型:</label>
                    <input type="text" id="petType" name="type" required>
                </div>

                <div class="form-group">
                    <label for="petBreed">宠物品种:</label>
                    <input type="text" id="petBreed" name="breed" required>
                </div>

                <div class="form-group">
                    <label for="petAge">宠物年龄:</label>
                    <input type="number" id="petAge" name="age" required>
                </div>

                <div class="form-group">
                    <label for="petDescription">宠物描述:</label>
                    <textarea id="petDescription" name="description" required></textarea>
                </div>

                <div class="form-group">
                    <label for="petImages">宠物图片:</label>
                    <input type="file" id="petImages" name="images" multiple required> 
                </div>

                <button type="submit">上传宠物</button>
            </form>
        </section>
    </main>

   <!-- 页脚 -->
	<footer>
		<p>© 2024 宠物之家管理系统 | 所有权利保留</p>
	</footer>
</body>
</html>
