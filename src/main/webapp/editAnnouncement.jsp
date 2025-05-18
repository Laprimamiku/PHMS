<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    model.Announcement announcement = (model.Announcement) request.getAttribute("announcement");
    if (announcement == null) {
%>
<h2 style="color: red; text-align: center;">公告不存在或未找到！</h2>
<%
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>修改公告</title>
    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f7f7f7;
            margin: 0;
            padding: 0;
            color: #333;
        }
        h1 {
            text-align: center;
            color: #4CAF50;
            margin-top: 50px;
        }
        .container {
            width: 60%;
            margin: 30px auto;
            padding: 20px;
            background-color: white;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            border-radius: 8px;
        }
        form {
            display: flex;
            flex-direction: column;
        }
        label {
            font-weight: bold;
            margin-bottom: 8px;
        }
        input[type="text"], textarea {
            width: 100%;
            padding: 12px;
            margin-bottom: 15px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 14px;
        }
        input[type="text"]:focus, textarea:focus {
            border-color: #4CAF50;
            outline: none;
        }
        textarea {
            resize: vertical;
        }
        button {
            background-color: #4CAF50;
            color: white;
            padding: 12px 20px;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }
        button:hover {
            background-color: #45a049;
        }
        .cancel-link {
            display: block;
            text-align: center;
            margin-top: 20px;
            color: #4CAF50;
            font-weight: bold;
            text-decoration: none;
        }
        .cancel-link:hover {
            text-decoration: underline;
        }
        .footer {
            text-align: center;
            position: fixed;
            bottom: 0;
            width: 100%;
            padding: 10px;
            background-color: #333;
            color: white;
        }
    </style>
</head>
<body>

<h1>修改公告</h1>

<div class="container">
    <form action="announcement?action=update" method="post">
        <input type="hidden" name="id" value="<%= announcement.getId() %>">

        <label for="title">标题：</label>
        <input type="text" id="title" name="title" value="<%= announcement.getTitle() %>" required>

        <label for="content">内容：</label>
        <textarea id="content" name="content" rows="6" required><%= announcement.getContent() %></textarea>

        <button type="submit">提交</button>
    </form>

    <a href="announcement?action=list" class="cancel-link">返回公告列表</a>
</div>

<div class="footer">
    <p>&copy; 2025 公告管理系统</p>
</div>

</body>
</html>
