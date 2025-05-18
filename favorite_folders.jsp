<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.FavoriteFolder" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>我的收藏</title>
    <link rel="stylesheet" type="text/css" href="css/user.css">
    <link rel="stylesheet" type="text/css" href="css/fav2.css">
    <link rel="stylesheet" type="text/css" href="css/fav3.css">
    
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
                <li><a href="FavoriteServlet?action=viewFolders">我的收藏</a></li>
                <li><a href="edit_profile.jsp">个人信息</a></li>
                <li><a href="UserServlet?action=logout">登出</a></li>
            </ul>
        </nav>
    </header>

    <%-- 显示消息 --%>
    <%
        String message = (String) request.getAttribute("message");
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (message != null) {
    %>
        <script>alert("<%= message %>");</script>
    <% } else if (errorMessage != null) { %>
        <script>alert("<%= errorMessage %>");</script>
    <% } %>

    <main>
    	<div class="favorite-header">
    		<h1>我的收藏夹</h1>
    			
    		<div class="action-buttons">
        		<button class="add-folder-btn" onclick="showAddFolderModal()">新建收藏夹</button>
        		<a href="FavoriteServlet?action=viewStats" class="stats-link">可视化分析</a>
    		</div>
		</div>

        <div class="folder-list">
            <%
                List<FavoriteFolder> folders = (List<FavoriteFolder>) request.getAttribute("folders");
                if (folders != null && !folders.isEmpty()) {
                    for (FavoriteFolder folder : folders) {
            %>
                <div class="folder-card">
                    <div class="folder-info">
                        <h3><%= folder.getFolderName() %></h3>
                        <p><%= folder.getDescription() != null ? folder.getDescription() : "暂无描述" %></p>
                    </div>
                    <div class="folder-actions">
                        <a href="FavoriteServlet?action=viewFolderContent&folderId=<%= folder.getId() %>" class="view-btn">查看详情</a>
                        <%-- Pass data using data attributes --%>
                        <button class="edit-btn" data-folder-id="<%= folder.getId() %>" data-folder-name="<%= folder.getFolderName() %>" data-folder-description="<%= folder.getDescription() != null ? folder.getDescription() : "" %>">编辑</button>
                        <%-- Use data attribute for delete button --%>
                        <button class="delete-btn" data-folder-id="<%= folder.getId() %>">删除</button>
                    </div>
                </div>
            <%
                    }
                } else {
            %>
                <p class="no-folders">暂无收藏夹，点击"新建收藏夹"创建</p>
            <%
                }
            %>
        </div>
    </main>

    <!-- 新建收藏夹模态框 -->
    <div id="addFolderModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeAddFolderModal()">&times;</span>
            <h2>新建收藏夹</h2>
            <form action="FavoriteServlet?action=addFolder" method="post">
                <div class="form-group">
                    <label for="addFolderName">收藏夹名称：</label>
                    <input type="text" id="addFolderName" name="folderName" required>
                </div>
                <div class="form-group">
                    <label for="addDescription">描述：</label>
                    <textarea id="addDescription" name="description"></textarea>
                </div>
                <div class="form-actions">
                    <button type="submit">创建</button>
                    <button type="button" onclick="closeAddFolderModal()">取消</button>
                </div>
            </form>
        </div>
    </div>

    <!-- 编辑收藏夹模态框 -->
    <div id="editFolderModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeEditFolderModal()">&times;</span>
            <h2>编辑收藏夹</h2>
            <form id="editFolderForm" action="FavoriteServlet?action=editFolder" method="post">
                 <input type="hidden" id="editFolderId" name="folderId">
                <div class="form-group">
                    <label for="editFolderName">收藏夹名称：</label>
                    <input type="text" id="editFolderName" name="folderName" required>
                </div>
                <div class="form-group">
                    <label for="editDescription">描述：</label>
                    <textarea id="editDescription" name="description"></textarea>
                </div>
                <div class="form-actions">
                    <button type="submit">保存</button>
                    <button type="button" onclick="closeEditFolderModal()">取消</button>
                </div>
            </form>
        </div>
    </div>


    <!-- 删除确认模态框 -->
    <div id="deleteModal" class="modal">
        <div class="modal-content">
            <h2>确认删除</h2>
            <p>确定要删除这个收藏夹吗？此操作不可恢复。</p>
            <div class="modal-actions">
                <button onclick="deleteFolder()" class="confirm-btn">确定</button>
                <button onclick="closeDeleteModal()" class="cancel-btn">取消</button>
            </div>
        </div>
    </div>

    <script>
        // 使用事件委托处理编辑和删除按钮的点击事件
        document.addEventListener('DOMContentLoaded', function() {
            document.querySelector('.folder-list').addEventListener('click', function(event) {
                const targetButton = event.target;

                if (targetButton.classList.contains('edit-btn')) {
                    const folderId = targetButton.getAttribute('data-folder-id');
                    const folderName = targetButton.getAttribute('data-folder-name');
                    const folderDescription = targetButton.getAttribute('data-folder-description');
                    showEditFolderModal(folderId, folderName, folderDescription);
                } else if (targetButton.classList.contains('delete-btn')) {
                     const folderId = targetButton.getAttribute('data-folder-id');
                     confirmDelete(folderId); // Pass folderId directly
                }
            });
        });

        
        
        
     // 搜索功能实现
        function searchFolders() {
            const searchText = document.getElementById('folderSearch').value.trim();
            
            // AJAX请求
            fetch('FavoriteServlet?action=getFolders&search=' + encodeURIComponent(searchText))
                .then(response => response.json())
                .then(data => {
                    updateFolderList(data);
                    highlightSearchResults(searchText);
                })
                .catch(error => console.error('Error:', error));
        }

        // 更新收藏夹列表
        function updateFolderList(folders) {
            const folderList = document.querySelector('.folder-list');
            folderList.innerHTML = ''; // 清空当前列表

            if (folders.length === 0) {
                folderList.innerHTML = '<p class="no-folders">未找到匹配的收藏夹</p>';
                return;
            }

            folders.forEach(folder => {
                const folderCard = `
                    <div class="folder-card">
                        <div class="folder-info">
                            <h3>${folder.folderName}</h3>
                            <p>${folder.description || '暂无描述'}</p>
                        </div>
                        <div class="folder-actions">
                            <a href="FavoriteServlet?action=viewFolderContent&folderId=${folder.id}" class="view-btn">查看详情</a>
                            <button class="edit-btn" data-folder-id="${folder.id}" 
                                    data-folder-name="${folder.folderName}" 
                                    data-folder-description="${folder.description || ''}">编辑</button>
                            <button class="delete-btn" data-folder-id="${folder.id}">删除</button>
                        </div>
                    </div>`;
                folderList.insertAdjacentHTML('beforeend', folderCard);
            });
        }

        // 高亮搜索结果
        function highlightSearchResults(searchText) {
            if (!searchText) return;

            const regex = new RegExp(searchText, 'gi');
            const folderNames = document.querySelectorAll('.folder-info h3');

            folderNames.forEach(element => {
                const originalText = element.textContent;
                const highlightedText = originalText.replace(regex, match => 
                    `<span class="highlight">${match}</span>`);
                element.innerHTML = highlightedText;
            });
        }
        
        
        
        
        
        
        
        
        
        

        // 新建收藏夹模态框
        function showAddFolderModal() {
            document.getElementById('addFolderModal').style.display = 'block';
        }

        function closeAddFolderModal() {
            document.getElementById('addFolderModal').style.display = 'none';
            // Optionally clear form fields
             document.getElementById('addFolderName').value = '';
             document.getElementById('addDescription').value = '';
        }

         // 编辑收藏夹模态框
         function showEditFolderModal(folderId, folderName, description) {
            document.getElementById('editFolderModal').style.display = 'block';
            document.getElementById('editFolderId').value = folderId;
            document.getElementById('editFolderName').value = folderName;
            document.getElementById('editDescription').value = description;
        }

        function closeEditFolderModal() {
            document.getElementById('editFolderModal').style.display = 'none';
             // Optionally clear form fields
             document.getElementById('editFolderId').value = '';
             document.getElementById('editFolderName').value = '';
             document.getElementById('editDescription').value = '';
        }


        // 删除确认模态框
        let folderIdToDelete = null; // Store folderId for deletion

        function confirmDelete(folderId) {
            folderIdToDelete = folderId; // Store the ID
            document.getElementById('deleteModal').style.display = 'block';
        }

        function closeDeleteModal() {
            document.getElementById('deleteModal').style.display = 'none';
            folderIdToDelete = null; // Clear the stored ID
        }

        // 执行删除操作
        function deleteFolder() {
            if (folderIdToDelete !== null) {
                window.location.href = 'FavoriteServlet?action=deleteFolder&folderId=' + folderIdToDelete;
            }
            closeDeleteModal(); // Close the modal
        }

        // 点击模态框外部关闭
        window.onclick = function(event) {
            if (event.target.classList.contains('modal')) { // Use classList.contains
                event.target.style.display = 'none';
            }
        }

         // Prevent modal closing when clicking inside
         document.getElementById('addFolderModal').querySelector('.modal-content').onclick = function(event){
            event.stopPropagation();
         }
          document.getElementById('editFolderModal').querySelector('.modal-content').onclick = function(event){
            event.stopPropagation();
         }
          document.getElementById('deleteModal').querySelector('.modal-content').onclick = function(event){
            event.stopPropagation();
         }


    </script>
</body>
</html>
