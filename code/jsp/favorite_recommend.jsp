<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>宠物推荐</title>
    <link rel="stylesheet" href="css/fav5.css">
</head>
<body>
    <div class="container">
        <!-- 你可能喜欢的宠物 -->
        <div class="recommendation-section">
            <h2 class="recommendation-title">你可能喜欢的宠物</h2>
            <div class="pet-grid">
                <%
                List<model.Pet> itemBased = (List<model.Pet>) request.getAttribute("itemBasedRecommendations");
                if (itemBased == null || itemBased.isEmpty()) {
                %>
                    <div class="no-recommendations">
                        暂无推荐内容，快去收藏几只心仪的宠物吧！
                    </div>
                <%
                } else {
                    for (model.Pet pet : itemBased) {
                %>
                    <div class="pet-card">
                        <!-- 宠物基本信息 -->
                        <div class="pet-info">
                            <h3 class="pet-name"><%= pet.getName() %></h3>
                            <p class="pet-details">
                                品种：<%= pet.getBreed() %><br>
                                年龄：<%= pet.getAge() %> 岁<br>
                                类型：<%= pet.getType() %>
                            </p>
                        </div>
                        <!-- 悬停时右下角出现 “详情” -->
                        <div class="pet-actions">
                            <a href="PetServlet?action=view&id=<%= pet.getId() %>" 
                               class="action-btn view-btn">
                                详情
                            </a>
                        </div>
                    </div>
                <%
                    }
                }
                %>
            </div>
        </div>

        <!-- 其他用户也喜欢 -->
        <div class="recommendation-section">
            <h2 class="recommendation-title">其他用户也喜欢</h2>
            <div class="pet-grid">
                <%
                List<model.Pet> userBased = (List<model.Pet>) request.getAttribute("userBasedRecommendations");
                if (userBased == null || userBased.isEmpty()) {
                %>
                    <div class="no-recommendations">
                        暂无推荐内容，收藏更多宠物可获得更精准推荐哦！
                    </div>
                <%
                } else {
                    for (model.Pet pet : userBased) {
                %>
                    <div class="pet-card">
                        <div class="pet-info">
                            <h3 class="pet-name"><%= pet.getName() %></h3>
                            <p class="pet-details">
                                品种：<%= pet.getBreed() %><br>
                                年龄：<%= pet.getAge() %> 岁<br>
                                类型：<%= pet.getType() %>
                            </p>
                        </div>
                        <div class="pet-actions">
                            <a href="PetServlet?action=view&id=<%= pet.getId() %>" 
                               class="action-btn view-btn">
                                详情
                            </a>
                        </div>
                    </div>
                <%
                    }
                }
                %>
            </div>
        </div>
    </div>
</body>
</html>
