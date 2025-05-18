<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
  model.Announcement announcement = (model.Announcement) request.getAttribute("announcement");
%>
<!DOCTYPE html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>查看公告</title>
  <style>
    body {
      font-family: 'Arial', sans-serif;
      background-color: #f9f9f9;
      color: #333;
      margin: 0;
      padding: 0;
    }
    h1 {
      color: #4CAF50;
      text-align: center;
      margin-top: 40px;
    }
    .container {
      width: 70%;
      margin: 40px auto;
      padding: 20px;
      background-color: #fff;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
      border-radius: 8px;
    }
    p {
      font-size: 16px;
      line-height: 1.6;
      margin: 10px 0;
    }
    b {
      color: #4CAF50;
    }
    .back-link {
      display: inline-block;
      margin-top: 30px;
      background-color: #4CAF50;
      color: white;
      padding: 10px 20px;
      text-decoration: none;
      border-radius: 4px;
      font-weight: bold;
      text-align: center;
    }
    .back-link:hover {
      background-color: #45a049;
    }
    footer {
      text-align: center;
      margin-top: 50px;
      padding: 10px;
      background-color: #333;
      color: white;
    }
  </style>
</head>
<body>

<div class="container">
  <h1>${announcement.title}</h1>
  <p><b>发布时间：</b>${announcement.createdAt}</p>
  <p><b>发布人：</b>${announcement.createdBy}</p>
  <p>${announcement.content}</p>

  <a href="announcement?action=list" class="back-link">返回公告列表</a>
</div>

<footer>
  <p>&copy; 2025 公告管理系统</p>
</footer>

</body>
</html>
