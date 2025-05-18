<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Announcement" %>

<%
    // 获取公告列表
    List<model.Announcement> announcements = (List<model.Announcement>) request.getAttribute("announcements");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>公告列表</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
        }
        header {
            background-color: #333;
            color: white;
            padding: 10px 0;
            text-align: center;
        }
        h1 {
            margin: 0;
        }
        .container {
            width: 80%;
            margin: 20px auto;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            padding: 12px;
            text-align: center;
            border: 1px solid #ddd;
        }
        th {
            background-color: #4CAF50;
            color: white;
        }
        tr:nth-child(even) {
            background-color: #f9f9f9;
        }
        a {
            text-decoration: none;
            color: #4CAF50;
            margin: 0 10px;
        }
        a:hover {
            color: #45a049;
        }
        .no-announcements {
            text-align: center;
            font-size: 1.2em;
            color: #888;
        }
        .add-announcement {
            display: inline-block;
            margin-bottom: 20px;
            padding: 10px 20px;
            background-color: #4CAF50;
            color: white;
            border-radius: 5px;
            text-decoration: none;
        }
        .add-announcement:hover {
            background-color: #45a049;
        }
        .footer {
            text-align: center;
            margin-top: 40px;
            padding: 10px;
            background-color: #333;
            color: white;
        }
    </style>
</head>
<body>
<header>
    <h1>公告列表</h1>
</header>

<div class="container">
    <a href="addAnnouncement.jsp" class="add-announcement">新增公告</a>
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>标题</th>
            <th>创建时间</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <% if (announcements != null && !announcements.isEmpty()) { %>
        <% for (model.Announcement announcement : announcements) { %>
        <tr>
            <td><%= announcement.getId() %></td>
            <td><%= announcement.getTitle() %></td>
            <td><%= announcement.getCreatedAt() %></td>
            <td>
                <a href="announcement?action=view&id=<%= announcement.getId() %>">查看</a>
                <a href="announcement?action=edit&id=<%= announcement.getId() %>">修改</a>
                <a href="announcement?action=delete&id=<%= announcement.getId() %>" onclick="return confirm('确认删除该公告？')">删除</a>
            </td>
        </tr>
        <% } %>
        <% } else { %>
        <tr>
            <td colspan="4" class="no-announcements">暂无公告</td>
        </tr>
        <% } %>
        </tbody>
    </table>
</div>

<div class="footer">
    <p>&copy; 2025 公告管理系统</p>
</div>
</body>
</html>
