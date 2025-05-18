<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="model.Pet" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>我的宠物</title>
    <link rel="stylesheet" type="text/css" href="css/user.css">
    <link rel="stylesheet" type="text/css" href="css/userpets.css">
</head>
<body>

  <header>
    <!-- 导航栏 -->
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
    <h1>我的宠物</h1>

    <!-- 用户上传的宠物列表 -->
    <div class="pet-table">
        <h2 style="text-align: center;">我上传的宠物</h2>
        <table>
            <thead>
                <tr>
                    <th>宠物名称</th>
                    <th>宠物类型</th>
                    <th>宠物品种</th>
                    <th>年龄</th>
                    <th>描述</th>
                    <th>状态</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <%-- 显示已上传的宠物 --%>
                <%
                    List<Pet> uploadedPets = (List<Pet>) request.getAttribute("uploadedPets");
                    if (uploadedPets != null && !uploadedPets.isEmpty()) {
                        for (Pet pet : uploadedPets) {
                %>
                <tr>
                    <td><%= pet.getName() %></td>
                    <td><%= pet.getType() %></td>
                    <td><%= pet.getBreed() %></td>
                    <td><%= pet.getAge() %></td>
                    <td><%= pet.getDescription() %></td>
                    <td><%= pet.getStatus() %></td>
                    <td>
                        <!-- 查看详情按钮 -->
                        <a href="PetServlet?action=view&id=<%= pet.getId() %>" class="view-btn">查看详情</a>
                    </td>
                </tr>
                <%  
                        }
                    } else {
                %>
                <tr>
                    <td colspan="7" class="empty-message">没有上传任何宠物。</td>
                </tr>
                <%  
                    }
                %>
            </tbody>
        </table>
    </div>

    <!-- 用户领养的宠物列表 -->
    <div class="pet-table">
        <h2 style="text-align: center;">我领养的宠物</h2>
        <table>
            <thead>
                <tr>
                    <th>宠物名称</th>
                    <th>宠物类型</th>
                    <th>宠物品种</th>
                    <th>年龄</th>
                    <th>描述</th>
                    <th>领养状态</th>
                    <th>操作</th> 
                </tr>
            </thead>
            <tbody>
                <%-- Displaying adopted pets --%>
                <%
                    List<Pet> adoptedPets = (List<Pet>) request.getAttribute("adoptedPets");
                    if (adoptedPets != null && !adoptedPets.isEmpty()) {
                        for (Pet pet : adoptedPets) {
                %>
                <tr>
                    <td><%= pet.getName() %></td>
                    <td><%= pet.getType() %></td>
                    <td><%= pet.getBreed() %></td>
                    <td><%= pet.getAge() %></td>
                    <td><%= pet.getDescription() %></td>
                    <td><%= pet.getAdopted() %></td>
                    <td>
                        <!-- 查看详情按钮 -->
                        <a href="PetServlet?action=view&id=<%= pet.getId() %>" class="view-btn">查看详情</a>
                    </td>
                </tr>
                <%  
                        }
                    } else {
                %>
                <tr>
                    <td colspan="7" class="empty-message">没有领养任何宠物。</td>
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
