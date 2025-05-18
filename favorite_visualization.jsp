<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="model.FavoriteItem" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>收藏统计</title>
    <link rel="stylesheet" type="text/css" href="css/user.css">
    <link rel="stylesheet" type="text/css" href="css/fav2.css">
    <link rel="stylesheet" type="text/css" href="css/fav4.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/3.9.1/chart.min.js"></script>
    
</head>
<body>
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
                <li><a href="FavoriteServlet?action=viewFolders">我的收藏</a></li>
                <li><a href="edit_profile.jsp">个人信息</a></li>
                <li><a href="UserServlet?action=logout">登出</a></li>
            </ul>
        </nav>
    </header>

    <main>
        <div class="stats-header">
            <h1>收藏统计</h1>
            <a href="FavoriteServlet?action=viewFolders" class="back-link">返回收藏夹列表</a>
        </div>

        <div class="chart-container">
            <div class="chart-wrapper">
                <h2 class="chart-title">收藏夹宠物类型分布</h2>
                <canvas id="typePieChart"></canvas>
            </div>

            <div class="chart-wrapper">
                <h2 class="chart-title">收藏夹宠物年龄分布</h2>
                <canvas id="ageBarChart"></canvas>
            </div>

            <div class="chart-wrapper">
                <h2 class="chart-title">收藏夹创建时间趋势</h2>
                <canvas id="timeLineChart"></canvas>
            </div>
        </div>
    </main>

    <script>
        // 从后端获取数据
        const stats = JSON.parse('<%= 
            List<FavoriteItem> stats = (List<FavoriteItem>) request.getAttribute("stats");
            StringBuilder json = new StringBuilder("[");
            if (stats != null) {
                for (int i = 0; i < stats.size(); i++) {
                    FavoriteItem item = stats.get(i);
                    if (i > 0) json.append(",");
                    json.append("{")
                        .append("\"type\":\"").append(item.getPetType()).append("\",")
                        .append("\"age\":").append(item.getPetAge()).append(",")
                        .append("\"count\":").append(item.getCount()).append(",")
                        .append("\"date\":\"").append(item.getCreateDate()).append("\"")
                        .append("}");
                }
            }
            json.append("]");
            out.print(json.toString().replace("'", "\\'").replace("\n", "\\n"));
        %>');

        // 处理数据
        const typeData = {};
        const ageData = {};
        const timeData = {};

        stats.forEach(item => {
            // 处理类型数据
            typeData[item.type] = (typeData[item.type] || 0) + item.count;
            
            // 处理年龄数据
            ageData[item.age] = (ageData[item.age] || 0) + item.count;
            
            // 处理时间数据
            const date = item.date.split(' ')[0]; // 只取日期部分
            timeData[date] = (timeData[date] || 0) + item.count;
        });

        // 绘制饼图
        new Chart(document.getElementById('typePieChart'), {
            type: 'pie',
            data: {
                labels: Object.keys(typeData),
                datasets: [{
                    data: Object.values(typeData),
                    backgroundColor: [
                        '#FF6384',
                        '#36A2EB',
                        '#FFCE56',
                        '#4BC0C0',
                        '#9966FF'
                    ]
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'right'
                    }
                }
            }
        });

        // 绘制柱状图
        new Chart(document.getElementById('ageBarChart'), {
            type: 'bar',
            data: {
                labels: Object.keys(ageData),
                datasets: [{
                    label: '宠物数量',
                    data: Object.values(ageData),
                    backgroundColor: '#36A2EB'
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1
                        }
                    }
                }
            }
        });

        // 绘制折线图
        const sortedDates = Object.keys(timeData).sort();
        new Chart(document.getElementById('timeLineChart'), {
            type: 'line',
            data: {
                labels: sortedDates,
                datasets: [{
                    label: '收藏数量',
                    data: sortedDates.map(date => timeData[date]),
                    borderColor: '#4CAF50',
                    tension: 0.1
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1
                        }
                    }
                }
            }
        });
    </script>
</body>
</html>