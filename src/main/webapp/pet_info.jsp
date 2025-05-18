<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Pet"%>
<%@ page import="model.User"%>
<%@ page session="true"%>
<%@ page import="service.PetService"%>
<%@ page import="service.UserService"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="util.JdbcUtil"%>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>宠物详情</title>
    
    <!-- 引入统一的样式 -->
     <link rel="stylesheet" type="text/css" href="css/user.css">
    <link rel="stylesheet" type="text/css" href="css/petinfo.css">
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
                <li><a href="#">公告</a></li>
                <li><a href="edit_profile.jsp">个人信息</a></li>
                <li><a href="UserServlet?action=logout">登出</a></li>
            </ul>
        </nav>
    </header>

    <!-- 主体内容 -->
    <main>
        <section class="welcome-section">
            <h1>宠物详情</h1>
            <p>查看宠物的详细信息</p>
        </section>

        <!-- 宠物信息展示 -->
        <section class="pet-info">
            <div class="pet-infos">
                <% 
                // 获取宠物ID并验证
                String petId = request.getParameter("id");
                if (petId != null && petId.matches("\\d+")) {  
                    PetService petService = new PetService();
                    Pet pet = petService.getPetById(Integer.parseInt(petId));

                    if (pet != null) {
                        // 获取上传人的信息
                        UserService userService = new UserService();
                        User uploader = userService.getUserById(pet.getUser_id()); 
                %>
                    <!-- 图片容器 -->
                    <div class="pet-image">
                        <% 
                        // 获取图片 URL 字符串并提取所有图片路径
                        String imageUrls = pet.getImage_url();
                        if (imageUrls != null && !imageUrls.isEmpty()) {
                            String[] images = imageUrls.split(",");
                            for (String imageUrl : images) {
                        %>
                            <img src="<%= imageUrl.trim() %>" alt="<%= pet.getName() %>" class="pet-photo">
                        <% 
                            }
                        } else {
                        %>
                            <p>没有可用的图片</p>
                        <% 
                        }
                        %>
                    </div>

                    <!-- 宠物信息容器 -->
                    <div class="pet-infos-info">
                        <h2><%= pet.getName() %></h2>
                        <p><strong>类型:</strong> <%= pet.getType() %></p>
                        <p><strong>品种:</strong> <%= pet.getBreed() %></p>
                        <p><strong>年龄:</strong> <%= pet.getAge() %></p>
                        <p><strong>状态:</strong> <%= pet.getStatus() %></p>
                        <p><strong>描述:</strong> <%= pet.getDescription() %></p>
                        <% if (uploader != null) { %>
                            <p><strong>上传人:</strong> <%= uploader.getUsername() %></p>
                            <p><strong>上传人邮箱:</strong> <%= uploader.getEmail() %></p>
                        <% } else { %>
                            <p><strong>上传人信息:</strong> 未知</p>
                        <% } %>

                        <!-- 领养按钮 -->
                        <% if ("no".equals(pet.getAdopted())) { %>
                            <form action="PetServlet" method="post" onsubmit="return confirmAdopt()">
                                <input type="hidden" name="action" value="adopt">
                                <input type="hidden" name="petId" value="<%= pet.getId() %>">
                                <button type="submit" class="adopt-button">领养这只宠物</button>
                            </form>
                        <% } else if ("undetermined".equals(pet.getAdopted())) { %>
                            <p>该宠物正在等待审核领养。</p>
                        <% } else { %>
                            <p>该宠物已经被领养。</p>
                        <% } %>

                    </div>
                <% 
                    } else {
                %>
                    <p>未找到宠物信息。</p>
                <% 
                    }
                } else {
                %>
                    <p>无效的宠物ID。</p>
                <% 
                }
                %>
            </div>
        </section>
    </main>

    <!-- 页脚 -->
    <footer>
        <p>© 2024 宠物之家管理系统 | 所有权利保留</p>
    </footer>

    <!-- JavaScript -->
    <script>
        function confirmAdopt() {
            return confirm("您确定要领养这只宠物吗？");
        }
    </script>
</body>
</html>
