<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Announcement" %>

<%
    // 获取公告列表
    List<model.Announcement> announcements = (List<model.Announcement>) request.getAttribute("announcements");
%>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>查看所有公告</title>
    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f7f7f7;
            color: #333;
            margin: 0;
            padding: 0;
        }
        h1 {
            text-align: center;
            color: #4CAF50;
            margin-top: 40px;
        }
        .container {
            width: 80%;
            margin: 20px auto;
            background-color: #fff;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            border-radius: 8px;
            padding: 20px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            padding: 12px;
            border: 1px solid #ddd;
            text-align: left;
        }
        th {
            background-color: #4CAF50;
            color: white;
        }
        tr:nth-child(even) {
            background-color: #f9f9f9;
        }
        td a {
            color: #4CAF50;
            text-decoration: none;
            padding: 6px 10px;
            border: 1px solid #4CAF50;
            border-radius: 4px;
        }
        td a:hover {
            background-color: #4CAF50;
            color: white;
            border-color: #4CAF50;
        }
        .no-announcements {
            text-align: center;
            font-size: 1.2em;
            color: #888;
        }
        .back-link {
            display: inline-block;
            margin-top: 20px;
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 4px;
            font-weight: bold;
        }
        .back-link:hover {
            background-color: #45a049;
        }
        footer {
            text-align: center;
            margin-top: 40px;
            padding: 10px;
            background-color: #333;
            color: white;
        }
    </style>
</head>
<body>

<h1>所有公告</h1>

<div class="container">
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>标题</th>
            <th>内容</th>
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
            <td><%= announcement.getContent() %></td>
            <td><%= announcement.getCreatedAt() %></td>
            <td>
                <a href="announcement?action=view&id=<%= announcement.getId() %>">查看</a>
            </td>
        </tr>
        <% } %>
        <% } else { %>
        <tr>
            <td colspan="5" class="no-announcements">暂无公告</td>
        </tr>
        <% } %>
        </tbody>
    </table>

    <a href="announcement?action=list" class="back-link">返回公告列表</a>
</div>

<footer>
    <p>&copy; 2025 公告管理系统</p>
</footer>

</body>
</html>
