<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="dao.PetDao" %>
<%@ page import="dao.UserDao" %>
<%@ page import="model.Pet" %>
<%@ page import="model.User" %>
<%
    // 获取宠物ID
    String petIdStr = request.getParameter("id");
    if (petIdStr == null || petIdStr.trim().isEmpty()) {
        response.sendRedirect("pet_manage.jsp"); // 如果没有ID，重定向到宠物管理页面
        return;
    }
    int petId = Integer.parseInt(petIdStr);
    
    // 获取宠物信息
    PetDao petDao = new PetDao();
    Pet pet = petDao.getPetById(petId);
    if (pet == null) {
        response.sendRedirect("pet_manage.jsp"); // 如果宠物不存在，重定向到宠物管理页面
        return;
    }
    
    // 获取上传的图片路径，如果有的话
    String[] imageUrls = (pet.getImage_url() != null && !pet.getImage_url().trim().isEmpty())
                            ? pet.getImage_url().split(",") : new String[0];

    // 获取领养人的信息
    UserDao userDao = new UserDao();
    User adopter = null;
    if (pet.getAdopt_id() != 0) {
        adopter = userDao.getUserById(pet.getAdopt_id());
    }
%>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>编辑宠物信息</title>
    <link rel="stylesheet" href="css/admin.css">
</head>
<body>
    <!-- Admin Navigation -->
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
    <div class="container">
        <h2>编辑宠物信息</h2>
        <form action="PetServlet?action=update" method="post" enctype="multipart/form-data">
            <input type="hidden" name="id" value="<%= pet.getId() %>">
            
            <div class="form-group">
                <label for="petName">宠物名称:</label>
                <input type="text" id="petName" name="name" value="<%= pet.getName() %>" required>
            </div>

            <div class="form-group">
                <label for="petType">宠物类型:</label>
                <input type="text" id="petType" name="type" value="<%= pet.getType() %>" required>
            </div>

            <div class="form-group">
                <label for="petBreed">宠物品种:</label>
                <input type="text" id="petBreed" name="breed" value="<%= pet.getBreed() %>">
            </div>

            <div class="form-group">
                <label for="petAge">宠物年龄:</label>
                <input type="number" id="petAge" name="age" value="<%= pet.getAge() %>" required>
            </div>

            <div class="form-group">
                <label for="petDescription">宠物描述:</label>
                <textarea id="petDescription" name="description" required><%= pet.getDescription() %></textarea>
            </div>

            <div class="form-group">
                <label for="petImage">宠物图片 (可上传多张):</label>
                <input type="file" id="petImage" name="images" multiple>
                <div class="image-preview">
                    <% if (imageUrls.length > 0) { %>
                        <p>当前图片:</p>
                        <% for (String imageUrl : imageUrls) { %>
                            <img src="<%= imageUrl %>" alt="Pet Image" style="width:100px; margin-right: 10px;">
                        <% } %>
                    <% } %>
                </div>
            </div>

            <div class="form-group">
                <label for="petStatus">状态:</label>
                <select id="petStatus" name="status">
                    <option value="approved" <%= "approved".equals(pet.getStatus()) ? "selected" : "" %>>通过审核</option>
                    <option value="pending" <%= "pending".equals(pet.getStatus()) ? "selected" : "" %>>待审核</option>
                    <option value="rejected" <%= "rejected".equals(pet.getStatus()) ? "selected" : "" %>>拒绝审核</option>
                </select>
            </div>

            <div class="form-group">
                <label for="petAdopted">领养状态:</label>
                <select id="petAdopted" name="adopted">
                    <option value="yes" <%= "yes".equals(pet.getAdopted()) ? "selected" : "" %>>已领养</option>
                    <option value="undetermined" <%= "undetermined".equals(pet.getAdopted()) ? "selected" : "" %>>未确认</option>
                    <option value="no" <%= "no".equals(pet.getAdopted()) ? "selected" : "" %>>未领养</option>
                </select>
            </div>

            <div class="form-group">
                <label for="adopter">领养人信息:</label>
                <% if (adopter != null) { %>
                    <p>领养人: <%= adopter.getUsername() %> | 邮箱: <%= adopter.getEmail() %></p>
                <% } else { %>
                    <p>当前没有领养人。</p>
                <% } %>
            </div>

            <button type="submit">更新宠物信息</button>
        </form>
    </div>
</body>
</html>
