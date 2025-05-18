<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="model.Pet" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>宠物审核</title>
    <link rel="stylesheet" href="css/admin.css">
     <link rel="stylesheet" href="css/petapprove.css">
     
      <script>
        // 弹出确认框函数
        function confirmAction(action, petId) {
            var message = "";
            if (action == "approve") {
                message = "您确定通过该宠物的信息吗？";
            } else if (action == "reject") {
                message = "您确定拒绝该宠物的信息吗？";
            } 
            // 显示确认框
            var isConfirmed = confirm(message);
            if (isConfirmed) {
                // 如果确认，提交表单
                document.getElementById("form-" + action + "-" + petId).submit();
            }
        }
    </script>
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

    <main>
            <!-- 待审核宠物列表 -->
            <div class="left" style="flex: 1;">
                <h1>待审核宠物列表</h1>
                <table border="1" cellpadding="10" cellspacing="0">
                    <thead>
                        <tr>
                            <th>宠物编号</th>
                            <th>名称</th>
                            <th>种类</th>
                            <th>品种</th>
                            <th>年龄</th>
                            <th>描述</th>
                            <th>图片</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                            List<Pet> pendingPets = (List<Pet>) request.getAttribute("pendingPets");
                            if (pendingPets != null && !pendingPets.isEmpty()) {
                                for (Pet pet : pendingPets) {
                        %>
                        <tr>
                            <td><%= pet.getId() %></td>
                            <td><%= pet.getName() %></td>
                            <td><%= pet.getType() %></td>
                            <td><%= pet.getBreed() %></td>
                            <td><%= pet.getAge() %></td>
                            <td><%= pet.getDescription() %></td>
                            <td><img src="<%= pet.getImage_url().split(",")[0] %>" alt="宠物图片" width="100"></td>

                            <td>
                                <!-- 审核通过表单 -->
                                <form id="form-approve-<%= pet.getId() %>" action="PetServlet" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="approve">
                                    <input type="hidden" name="id" value="<%= pet.getId() %>">
                                    <button type="button" class="approve-btn" <% if ("approved".equalsIgnoreCase(pet.getStatus())) { %>disabled<% } %> onclick="confirmAction('approve', <%= pet.getId() %>)">通过</button>
                                </form>

                                <!-- 审核拒绝表单 -->
                                <form id="form-reject-<%= pet.getId() %>" action="PetServlet" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="reject">
                                    <input type="hidden" name="id" value="<%= pet.getId() %>">
                                    <button type="button" class="reject-btn" <% if ("rejected".equalsIgnoreCase(pet.getStatus())) { %>disabled<% } %> onclick="confirmAction('reject', <%= pet.getId() %>)">拒绝</button>
                                </form>
                            </td>
                        </tr>
                        <% 
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="8" style="text-align:center;">暂无待审核的宠物信息</td>
                        </tr>
                        <% 
                            }
                        %>
                    </tbody>
                </table>
            </div>

    </main>

    <!-- 页脚 -->
    <footer>
        <p>© 2024 宠物之家管理系统 | 所有权利保留</p>
    </footer>
</body>
</html>
