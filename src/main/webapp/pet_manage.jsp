<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, java.sql.*" %>
<%@ page import="dao.PetDao" %>
<%@ page import="model.Pet" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>宠物管理</title>
    <link rel="stylesheet" href="css/admin.css">
     <link rel="stylesheet" href="css/petmanage.css">
    <script>
        // JavaScript function for delete confirmation
        function confirmDelete(petId) {
            if (confirm('确定要删除该宠物信息吗？')) {
                window.location.href = 'PetServlet?action=delete&id=' + petId;
            }
        }

        // 显示删除操作的成功或失败消息
        <%
            String message = (String) request.getAttribute("message");
            String errorMessage = (String) request.getAttribute("errorMessage");

            if (message != null) {
        %>
        window.onload = function() {
            alert('<%= message %>');
        };
        <%
            }
            if (errorMessage != null) {
        %>
        window.onload = function() {
            alert('<%= errorMessage %>');
        };
        <%
            }
        %>
    </script>
</head>
<body>
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
        <h2>宠物管理</h2>         
        <!-- 查询表单 -->     
        <form action="PetServlet" method="get" style="margin-bottom: 20px;">
            <input type="hidden" name="action" value="search">
            <label for="searchQuery">搜索宠物名称：</label>
            <input type="text" id="searchQuery" name="searchQuery" placeholder="请输入宠物名称" 
                   value="<%= request.getAttribute("searchQuery") != null ? request.getAttribute("searchQuery") : "" %>" />
            <button type="submit">搜索</button>
        </form>

        <table border="1" style="width: 100%; text-align: left; margin-top: 20px;">
            <thead>
                <tr>
                    <th>宠物编号</th>
                    <th>宠物姓名</th>
                    <th>宠物种类</th>
                    <th>宠物年龄</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
               <%
                List<Pet> pets = (List<Pet>) request.getAttribute("pets");
                if (pets != null && !pets.isEmpty()) {
                    for (Pet pet : pets) {
                %>
                <tr>
                    <td><%= pet.getId() %></td>
                    <td><%= pet.getName() %></td>
                    <td><%= pet.getType() %></td>
                    <td><%= pet.getAge() %></td>
                    <td>
                        <a href="pet_edit.jsp?id=<%= pet.getId() %>" class="btn btn-secondary">编辑</a>
                        <button onclick="confirmDelete(<%= pet.getId() %>)">删除</button>
                    </td>
                </tr>
                <%
                    }
                } else {
                %>
                <tr>
                    <td colspan="5" style="text-align: center;">暂无宠物信息</td>
                </tr>
                <%
                }
                %>
            </tbody>
        </table>
    </div>
</body>
</html>
