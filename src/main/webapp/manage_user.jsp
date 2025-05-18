<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List, model.User"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>管理用户</title>
    <link rel="stylesheet" type="text/css" href="css/admin.css">
    <script type="text/javascript">
        function confirmDelete(userid) {
            // 使用 JavaScript 的 confirm() 方法显示确认框
            var result = confirm("您确定要删除该用户吗？"); 

            if (result) {
                // 如果用户点击"确定"，则跳转到删除请求的 URL
                window.location.href = "UserServlet?action=delete&userid=" + userid;
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
                <li><a href="announcement?action=list">发布/维护公告信息</a></li>
                <li><a href="UserServlet?action=search">维护用户信息</a></li>
               <li><a href="UserServlet?action=logout" class="logout">登出</a></li>
            </ul>
        </nav>
    </header>

    <!-- 主体内容 -->
    <div class="container">
        <!-- 第二部分：显示用户列表并支持查询 -->
        <div class="section">
            <h3>所有用户列表</h3>

            <!-- 查询框 -->
            <form action="UserServlet" method="get">
                <input type="text" name="searchQuery" placeholder="输入用户名查询"
                    value="${param.searchQuery}" /> <input type="hidden" name="action"
                    value="search" />
                <button type="submit">查询</button>
            </form>

            <!-- 如果查询没有结果，显示提示 -->
            <%
            List<User> users = (List<User>) request.getAttribute("users");
            if (users == null || users.isEmpty()) {
            %>
            <p>
                没有找到用户。 <a href="UserServlet?action=search">单击此处显示完整的用户列表</a>
            </p>
            <%
            } else {
            %>
            <!-- 用户列表表格 -->
            <h4>用户列表</h4>
            <table>
                <tr>
                    <th>用户名</th>
                    <th>角色</th>
                    <th>邮箱</th>      
                    <th>操作</th>
                </tr>
                <%
                // 渲染用户列表
                for (User user : users) {
                %>
                <tr>
                    <td><%=user.getUsername()%></td>
                    <td><%=user.getType()%></td>
                    <td><%=user.getEmail()%></td>
                    <td>
                        <a href="javascript:void(0);" onclick="confirmDelete(<%=user.getid()%>)">删除</a> |
                        <a href="UserServlet?action=edit&userid=<%=user.getid()%>">编辑</a>
                    </td>
                </tr>
                <%
                }
                %>
            </table>
            <%
            }
            %>
            
           
        </div>
<!-- 分页 -->
         <div class="pagination-container">
             <% 
             // 获取总数
             int totalPets = (int) request.getAttribute("totalPages");
             int currentPage = (int) request.getAttribute("currentPage");
             String searchQuery=(String) request.getAttribute("searchQuery");
          
             int totalPages = (totalPets + 10 - 1) / 10;  // 计算总页数

             // 显示上一页按钮
             if (currentPage > 1) {  
             %>
                 <a href="UserServlet?action=search&page=<%= currentPage - 1 %>&searchQuery=<%=searchQuery %>" class="pagination-btn">上一页</a>
             <% 
             } else {
             %>
                 <span class="pagination-btn disabled">上一页</span>
             <% 
             }
             %>
             
             <span>当前页 <%= currentPage %> / <%= totalPages %></span>

             <% 
             // 显示下一页按钮
             if (currentPage < totalPages) {  
             %>
                 <a href="UserServlet?action=search&page=<%= currentPage + 1 %>&searchQuery=<%=searchQuery %>" class="pagination-btn">下一页</a>
             <% 
             } else {
             %>
                 <span class="pagination-btn disabled">下一页</span>
             <% 
             }
             %>
         </div>
        <!-- 第三部分：跳转到添加用户页面 -->
        <div class="section">
            <h3>管理操作</h3>
            <p>
                <a href="add_user.jsp">添加用户</a>
            </p>
        </div>
    </div>

    <!-- 页脚 -->
    <footer>
        <p>© 2024 宠物之家管理系统 | 所有权利保留</p>
    </footer>
</body>
</html>
