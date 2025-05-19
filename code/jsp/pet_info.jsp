<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Pet"%>
<%@ page import="model.User"%>
<%@ page session="true"%>
<%@ page import="service.PetService"%>
<%@ page import="service.UserService"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="util.JdbcUtil"%>

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>宠物详情</title>
    
    <!-- 引入统一的样式 -->
     <link rel="stylesheet" type="text/css" href="css/user.css">
    <link rel="stylesheet" type="text/css" href="css/petinfo.css">
    <link rel="stylesheet" type="text/css" href="css/fav1.css">
</head>
<body>
    <!-- 导航栏 -->
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
                <li><a href="edit_profile.jsp">个人信息</a></li>
                <li><a href="UserServlet?action=logout">登出</a></li>
            </ul>
        </nav>
    </header>

    <!-- 主体内容 -->
    <main>
        <section class="welcome-section">
            <h1>宠物详情</h1>
            <p>查看宠物的详细信息</p>
        </section>

        <!-- 宠物信息展示 -->
        <section class="pet-info">
            <div class="pet-infos">
                <% 
                // 获取宠物ID并验证
                String petId = request.getParameter("id");
                if (petId != null && petId.matches("\\d+")) {  
                    PetService petService = new PetService();
                    Pet pet = petService.getPetById(Integer.parseInt(petId));

                    if (pet != null) {
                        // 获取上传人的信息
                        UserService userService = new UserService();
                        User uploader = userService.getUserById(pet.getUser_id()); 
                %>
                    <!-- 图片容器 -->
                    <div class="pet-image">
                        <% 
                        // 获取图片 URL 字符串并提取所有图片路径
                        String imageUrls = pet.getImage_url();
                        if (imageUrls != null && !imageUrls.isEmpty()) {
                            String[] images = imageUrls.split(",");
                            for (String imageUrl : images) {
                        %>
                            <img src="<%= imageUrl.trim() %>" alt="<%= pet.getName() %>" class="pet-photo">
                        <% 
                            }
                        } else {
                        %>
                            <p>没有可用的图片</p>
                        <% 
                        }
                        %>
                    </div>

                    <!-- 宠物信息容器 -->
                    <div class="pet-infos-info">
                        <h2><%= pet.getName() %></h2>
                        <p><strong>类型:</strong> <%= pet.getType() %></p>
                        <p><strong>品种:</strong> <%= pet.getBreed() %></p>
                        <p><strong>年龄:</strong> <%= pet.getAge() %></p>
                        <p><strong>状态:</strong> <%= pet.getStatus() %></p>
                        <p><strong>描述:</strong> <%= pet.getDescription() %></p>
                        <% if (uploader != null) { %>
                            <p><strong>上传人:</strong> <%= uploader.getUsername() %></p>
                            <p><strong>上传人邮箱:</strong> <%= uploader.getEmail() %></p>
                        <% } else { %>
                            <p><strong>上传人信息:</strong> 未知</p>
                        <% } %>

                        <!-- 领养按钮 -->
                        <% if ("no".equals(pet.getAdopted())) { %>
                            <form action="PetServlet" method="post" onsubmit="return confirmAdopt()">
                                <input type="hidden" name="action" value="adopt">
                                <input type="hidden" name="petId" value="<%= pet.getId() %>">
                                <button type="submit" class="adopt-button">领养这只宠物</button>
                            </form>
                        <% } else if ("undetermined".equals(pet.getAdopted())) { %>
                            <p>该宠物正在等待审核领养。</p>
                        <% } else { %>
                            <p>该宠物已经被领养。</p>
                        <% } %>

                        <!-- 收藏按钮 -->
                        <button class="favorite-btn" 
                                data-pet-id="<%= pet.getId() %>"
                                onclick="openFavoriteModal(<%= pet.getId() %>)">
                            收藏
                        </button>

                    </div>
                <% 
                    } else {
                %>
                    <p>未找到宠物信息。</p>
                <% 
                    }
                } else {
                %>
                    <p>无效的宠物ID。</p>
                <% 
                }
                %>
            </div>
        </section>
    </main>

    <!-- 收藏模态框 -->
    <div id="favoriteModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3 id="modalTitle">选择收藏夹</h3>
                <span class="close">&times;</span>
            </div>
            
            <!-- 收藏夹列表视图 -->
            <div id="folderListView">
                <div id="foldersList">
                    <div class="loading">加载收藏夹中...</div>
                </div>
                <div class="folder-actions">
                    <button id="createFolderBtn" class="action-btn">+ 创建新收藏夹</button>
                </div>
            </div>
            
            <!-- 创建收藏夹视图 -->
            <div id="createFolderView" style="display:none;">
                <form id="createFolderForm">
                    <div class="form-group">
                        <label for="newFolderName">收藏夹名称</label>
                        <input type="text" id="newFolderName" required placeholder="请输入收藏夹名称">
                    </div>
                    <div class="form-group">
                        <label for="newFolderDesc">描述 (可选)</label>
                        <textarea id="newFolderDesc" placeholder="请输入收藏夹描述"></textarea>
                    </div>
                    <div class="form-actions">
                        <button type="button" id="backToListBtn" class="secondary-btn">返回</button>
                        <button type="submit" id="submitFolderBtn" class="primary-btn">创建</button>
                    </div>
                </form>
            </div>
            
            <div class="modal-footer">
                <button id="cancelBtn">取消</button>
            </div>
        </div>
    </div>

    <!-- 页脚 -->
    <footer>
        <p>© 2024 宠物之家管理系统 | 所有权利保留</p>
    </footer>

    <!-- JavaScript -->
    <script>
        function confirmAdopt() {
            return confirm("您确定要领养这只宠物吗？");
        }

        // 全局变量用于存储当前操作的宠物ID
        let currentPetId = null;

        // 获取DOM元素
        const modal = document.getElementById('favoriteModal');
        const modalTitle = document.getElementById('modalTitle');
        const closeBtn = modal.querySelector('.close');
        const cancelBtn = document.getElementById('cancelBtn');
        const foldersList = document.getElementById('foldersList');

        // 视图元素
        const folderListView = document.getElementById('folderListView');
        const createFolderView = document.getElementById('createFolderView');

        // 按钮
        const createFolderBtn = document.getElementById('createFolderBtn');
        const backToListBtn = document.getElementById('backToListBtn');

        // 表单元素
        const createFolderForm = document.getElementById('createFolderForm');
        const newFolderName = document.getElementById('newFolderName');
        const newFolderDesc = document.getElementById('newFolderDesc');

        // 打开收藏模态框
        function openFavoriteModal(petId) {
            // 保存宠物ID
            currentPetId = petId;
            console.log('打开收藏模态框，宠物ID:', petId);
            
            // 显示加载状态
            foldersList.innerHTML = '<div class="loading">加载收藏夹中...</div>';
            
            // 确保显示正确的视图
            switchToFolderListView();
            
            // 显示模态框
            modal.style.display = 'block';
            
            // 加载收藏夹数据
            loadFolders();
        }

        // 加载收藏夹
        function loadFolders() {
            // 请求收藏夹数据
            fetch('FavoriteServlet?action=getFolders')
                .then(response => {
                    console.log('收藏夹请求状态:', response.status);
                    if (!response.ok) {
                        throw new Error('网络请求失败: ' + response.status);
                    }
                    return response.text();
                })
                .then(text => {
                    console.log('收藏夹原始响应:', text);
                    if (!text || text.trim() === '') {
                        foldersList.innerHTML = '<div class="error-message">服务器返回空数据</div>';
                        return;
                    }
                    
                    // 尝试解析JSON
                    try {
                        const folders = JSON.parse(text);
                        console.log('解析后的收藏夹数据:', folders);
                        
                        // 检查数据格式
                        if (!Array.isArray(folders)) {
                            foldersList.innerHTML = '<div class="error-message">数据格式错误</div>';
                            return;
                        }
                        
                        // 渲染收藏夹列表
                        renderFoldersListSimple(folders);
                    } catch (error) {
                        console.error('解析收藏夹数据失败:', error, '原始数据:', text);
                        foldersList.innerHTML = `<div class="error-message">解析数据失败: ${error.message}</div>`;
                    }
                })
                .catch(error => {
                    console.error('获取收藏夹失败:', error);
                    foldersList.innerHTML = `<div class="error-message">获取收藏夹失败: ${error.message}</div>`;
                });
        }

        // 简化版渲染收藏夹列表 - 直接使用最基础的DOM操作
        function renderFoldersListSimple(folders) {
            // 清空当前内容
            foldersList.innerHTML = '';
            
            // 检查是否有收藏夹
            if (!folders || folders.length === 0) {
                const emptyMsg = document.createElement('div');
                emptyMsg.className = 'empty-message';
                emptyMsg.textContent = '您还没有创建收藏夹，请点击下方按钮创建';
                foldersList.appendChild(emptyMsg);
                return;
            }
            
            // 逐个创建收藏夹项
            folders.forEach(folder => {
                if (!folder || !folder.id) {
                    console.warn('无效的收藏夹数据:', folder);
                    return;
                }
                
                // 创建文件夹项元素
                const folderItem = document.createElement('div');
                folderItem.className = 'folder-item';
                
                // 使用textContent确保安全
                folderItem.textContent = folder.folderName || '未命名收藏夹';
                
                // 添加ID为调试目的
                folderItem.setAttribute('data-folder-id', folder.id);
                
                // 绑定点击事件
                folderItem.addEventListener('click', function() {
                    addToFavorite(folder.id, currentPetId);
                });
                
                // 添加到列表
                foldersList.appendChild(folderItem);
            });
            
            // 输出调试信息
            console.log('渲染了', folders.length, '个收藏夹项');
        }

        // 添加到收藏夹
        function addToFavorite(folderId, petId) {
            console.log('添加收藏 - 收藏夹ID:', folderId, '宠物ID:', petId);
            
            // 参数校验
            if (!folderId || !petId) {
                alert('参数无效，无法完成收藏');
                return;
            }
            
            // 构建请求数据
            const formData = new URLSearchParams();
            formData.append('action', 'addToFavorite');
            formData.append('folderId', folderId);
            formData.append('petId', petId);
            
            // 发送请求
            fetch('FavoriteServlet', {
                method: 'POST',
                body: formData,
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            })
            .then(response => {
                console.log('收藏请求状态:', response.status);
                if (!response.ok) {
                    throw new Error('服务器响应错误: ' + response.status);
                }
                return response.text();
            })
            .then(text => {
                console.log('收藏响应原始数据:', text);
                try {
                    const result = JSON.parse(text);
                    console.log('解析后的响应:', result);
                    
                    // 显示结果
                    if (result.success) {
                        alert(result.message || '收藏成功');
                    } else {
                        alert(result.message || '收藏失败');
                    }
                    
                    // 关闭模态框
                    closeModal();
                } catch (error) {
                    console.error('解析响应失败:', error);
                    alert('操作可能已完成，但无法解析响应');
                    closeModal();
                }
            })
            .catch(error => {
                console.error('收藏请求失败:', error);
                alert('收藏失败: ' + error.message);
            });
        }

        // 创建新收藏夹
        function createNewFolder() {
            const folderName = newFolderName.value.trim();
            const description = newFolderDesc.value.trim();
            
            if (!folderName) {
                alert('请输入收藏夹名称');
                return;
            }
            
            console.log('创建新收藏夹:', folderName, description);
            
            // 构建请求数据
            const formData = new URLSearchParams();
            formData.append('action', 'addFolder');
            formData.append('folderName', folderName);
            formData.append('description', description);
            
            // 发送请求
            fetch('FavoriteServlet', {
                method: 'POST',
                body: formData,
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            })
            .then(response => {
                console.log('创建收藏夹请求状态:', response.status);
                if (!response.ok) {
                    throw new Error('服务器响应错误: ' + response.status);
                }
                return response.text();
            })
            .then(text => {
                console.log('创建收藏夹响应:', text);
                
                // 清空输入框
                newFolderName.value = '';
                newFolderDesc.value = '';
                
                // 显示成功消息
                const successMsg = document.createElement('div');
                successMsg.className = 'success-message';
                successMsg.textContent = '收藏夹创建成功！';
                
                // 插入到列表视图的顶部
                folderListView.insertBefore(successMsg, folderListView.firstChild);
                
                // 3秒后移除成功消息
                setTimeout(() => {
                    if (successMsg.parentNode) {
                        successMsg.parentNode.removeChild(successMsg);
                    }
                }, 3000);
                
                // 切换回列表视图并重新加载收藏夹
                switchToFolderListView();
                loadFolders();
            })
            .catch(error => {
                console.error('创建收藏夹失败:', error);
                alert('创建收藏夹失败: ' + error.message);
            });
        }

        // 切换到收藏夹列表视图
        function switchToFolderListView() {
            modalTitle.textContent = '选择收藏夹';
            folderListView.style.display = 'block';
            createFolderView.style.display = 'none';
        }

        // 切换到创建收藏夹视图
        function switchToCreateFolderView() {
            modalTitle.textContent = '创建新收藏夹';
            folderListView.style.display = 'none';
            createFolderView.style.display = 'block';
        }

        // 关闭模态框
        function closeModal() {
            modal.style.display = 'none';
            // 清空收藏夹列表，避免缓存问题
            foldersList.innerHTML = '';
            // 重置表单
            newFolderName.value = '';
            newFolderDesc.value = '';
            // 重置当前宠物ID
            currentPetId = null;
        }

        // 绑定创建收藏夹按钮事件
        createFolderBtn.addEventListener('click', switchToCreateFolderView);

        // 绑定返回列表按钮事件
        backToListBtn.addEventListener('click', switchToFolderListView);

        // 绑定创建收藏夹表单提交事件
        createFolderForm.addEventListener('submit', function(event) {
            event.preventDefault();
            createNewFolder();
        });

        // 绑定关闭按钮事件
        closeBtn.addEventListener('click', closeModal);
        cancelBtn.addEventListener('click', closeModal);

        // 点击模态框外部关闭
        window.addEventListener('click', function(event) {
            if (event.target === modal) {
                closeModal();
            }
        });
    </script>
</body>
</html>
