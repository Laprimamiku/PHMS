package servlet;

import model.User;
import service.UserService;
import java.io.*;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserService userService = new UserService();

    // 创建一个通用的方法来设置 User 对象的属性
    private User createUserFromRequest(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String type = request.getParameter("type");    // 新增 type 字段（例如，"admin" 或 "user"）

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setType(type != null ? type : "user"); // 默认 type 为 "user"
        return user;//返回填充了相应属性的 User 对象
    }

    // 处理POST请求,检查请求中的action参数来确定执行哪种操作
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        // 注册用户
        if ("register".equals(action)) {
            User user = createUserFromRequest(request);

            // 检查用户名是否已存在
            List<User> existingUsers = userService.searchUsersByUsername(user.getUsername());
            if (!existingUsers.isEmpty()) {
                request.setAttribute("errorMessage", "用户名已存在，请选择其他用户名！");
                request.getRequestDispatcher("reg.jsp").forward(request, response);
            } else {
                if (userService.registerUser(user)) {
                    // 注册成功，传递成功信息
                    request.setAttribute("message", "注册成功，请登录");
                    request.getRequestDispatcher("log.jsp").forward(request, response); // 跳转到登录页面
                } else {
                    request.setAttribute("errorMessage", "注册失败，请稍后重试！");
                    request.getRequestDispatcher("reg.jsp").forward(request, response);
                }
            }
        }

        // 登录用户
        else if ("login".equals(action)) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            User user = userService.loginUser(username, password);
            if (user != null) {
                HttpSession session = request.getSession();
                
                session.setAttribute("userId", user.getid());  // 存储用户ID
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", user.getType());  // 将角色存入session
                session.setAttribute("email", user.getEmail());
                
                // 根据角色跳转到不同页面
                if ("admin".equals(user.getType())) {
                    response.sendRedirect("admin_home.jsp");
                } else {
                    response.sendRedirect("PetServlet?action=adopthome");
                }
            } else {
                request.setAttribute("errorMessage", "用户名或密码错误");
                request.getRequestDispatcher("log.jsp").forward(request, response);
            }
        }

        // 更新用户信息
        else if ("update".equals(action)) {
            int userId = Integer.parseInt(request.getParameter("id"));
            User user = createUserFromRequest(request);
            user.setid(userId);

            // 确保新用户名没有重复
            if (!user.getUsername().equals(userService.getUserById(userId).getUsername())) {
                List<User> existingUsers = userService.searchUsersByUsername(user.getUsername());
                if (!existingUsers.isEmpty()) {
                    request.setAttribute("errorMessage", "用户名已存在，请选择其他用户名！");
                    request.setAttribute("user", user);
                    request.getRequestDispatcher("edit_user.jsp").forward(request, response);
                    return;
                }
            }

            boolean success = userService.updateUser(user);
            if (success) {
                // 更新成功，跳转到用户列表页面并显示提示
                request.setAttribute("successMessage", "用户信息更新成功！");
                response.sendRedirect("UserServlet?action=search");
            } else {
                // 更新失败，跳转回编辑页面并显示错误信息
                request.setAttribute("errorMessage", "更新失败，请重试！");
                request.setAttribute("user", user);  // 将用户信息传递回页面
                request.getRequestDispatcher("edit_user.jsp").forward(request, response);
            }
        }

        // 添加用户
        else if ("add".equals(action)) {
            User user = createUserFromRequest(request);

            // 校验用户名是否已存在,            根据用户名搜索现有用户名
            List<User> existingUsers = userService.searchUsersByUsername(user.getUsername());
            if (!existingUsers.isEmpty()) {
                request.setAttribute("errorMessage", "用户名已存在，请选择其他用户名！");
                request.getRequestDispatcher("add_user.jsp").forward(request, response);
            } else {
                boolean success = userService.addUser(user);
                if (success) {
                    request.setAttribute("message", "添加成功！");
                    response.sendRedirect("UserServlet?action=search");  // 添加成功后跳转到用户列表
                } else {
                    request.setAttribute("errorMessage", "添加用户失败，请稍后再试！");
                    request.getRequestDispatcher("add_user.jsp").forward(request, response);
                }
            }
        }
        
        // 更新个人信息
        else if ("editProfile".equals(action)) {
            HttpSession session = request.getSession();
            
            // 确保 session 中有 "userId" 属性
            Integer currentUserId = (Integer) session.getAttribute("userId");
            if (currentUserId == null) {
                // 如果没有 "userId"，表示用户未登录，跳转到登录页面
                response.sendRedirect("log.jsp");
                return;
            }

            // 获取表单数据
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // 确保 "role" 字段有值，默认为 "user"
            String role = (String) session.getAttribute("role");  // 从 session 中获取当前用户的角色
            if (role == null) {
                role = "user";  // 默认值
            }

            // 检查用户名是否已存在
            if (!username.equals(session.getAttribute("username")) && userService.isUsernameExist(username, currentUserId)) {
                // 如果用户名已存在，返回错误信息
                request.setAttribute("errorMessage", "用户名已存在，请选择其他用户名！");
                request.getRequestDispatcher("edit_profile.jsp").forward(request, response);
                return;
            }

            // 创建用户对象并设置新信息
            User user = new User();
            user.setid(currentUserId);  // 使用当前用户ID
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password.isEmpty() ? (String) session.getAttribute("password") : password);  // 如果密码为空，保持原密码
            user.setType(role);  // 确保 "role" 字段有值

            // 更新用户信息
            boolean updated = userService.updateUser(user);
            if (updated) {
                // 更新成功，更新 session
                session.setAttribute("username", username);
                session.setAttribute("email", email);
                session.setAttribute("password", password); // 更新密码到 session

                // 设置成功消息
                request.setAttribute("successMessage", "个人信息更新成功！");
                request.getRequestDispatcher("edit_profile.jsp").forward(request, response);
            } else {
                // 更新失败，返回错误信息
                request.setAttribute("errorMessage", "更新失败，请稍后再试！");
                request.getRequestDispatcher("edit_profile.jsp").forward(request, response);
            }
        }
    }

    // 处理GET请求                     检查请求参数中的action来确定要执行的具体操作
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        // 登出
        if ("logout".equals(action)) {
            // 清除会话中的用户信息
            HttpSession session = request.getSession();
            session.invalidate(); // 使会话失效

            // 将登出成功的消息传递给前端
            request.setAttribute("message", "登出成功，请重新登录");

            // 重定向到登录页面，并带上提示信息
            request.getRequestDispatcher("log.jsp").forward(request, response);
        }

	   /* 
	    // 获取用户列表和总页数
	    List<User> users = userService.getUsersByPage(currentPage);
	    int totalPages = userService.getTotalPages();
	    // 将数据传递到 JSP 页面
	    request.setAttribute("users", users);
	    request.setAttribute("currentPage", currentPage);
	    request.setAttribute("totalPages", totalPages);
	    // 跳转到显示用户列表的 JSP 页面
	    request.getRequestDispatcher("manage_user.jsp").forward(request, response);
	    }
	    */

        // 删除用户
        else if ("delete".equals(action)) {
            int userId = Integer.parseInt(request.getParameter("userid"));
            userService.deleteUser(userId);
            response.sendRedirect("UserServlet?action=search");
        }
         // 搜索用户
            else if ("search".equals(action)) {
                String searchQuery = request.getParameter("searchQuery");//用户输入的用于搜索用户的关键词
             // 获取当前页码，默认值为 1
                int currentPage = 1;
                String pageStr = request.getParameter("page");
                if (pageStr != null) {
                    try {
                    	// 尝试将页面字符串转换为整数以确定当前页码
                        currentPage = Integer.parseInt(pageStr);
                    } catch (NumberFormatException e) {
                        // 错误处理
                    }
                }

                // 获取用户列表和总页数
                List<User> users = userService.getUsersByPage(currentPage,searchQuery);//获取与搜索条件匹配的用户列表
                int totalPages = userService.getTotalPages(searchQuery);//计算页数
     
                // 设置请求属性，以便在JSP页面中使用
                request.setAttribute("users", users);// 用户列表
                request.setAttribute("currentPage", currentPage);// 当前页码
                request.setAttribute("totalPages", totalPages);// 总页数
                
                // 如果搜索查询为空，则设置为空字符串，避免null值出现在JSP中
                request.setAttribute("searchQuery", searchQuery==null?"":searchQuery);
                
                RequestDispatcher dispatcher = request.getRequestDispatcher("manage_user.jsp");
                dispatcher.forward(request, response);
            }

            // 获取单个用户信息
            else if ("edit".equals(action)) {
                int userid = Integer.parseInt(request.getParameter("userid"));
                User user = userService.getUserById(userid);//传入用户ID
                if (user != null) {
                    request.setAttribute("user", user);
                    RequestDispatcher dispatcher = request.getRequestDispatcher("edit_user.jsp");
                    dispatcher.forward(request, response);
                } else {
                    response.sendRedirect("admin_home.jsp"); // 用户不存在，重定向到管理员主页
                }
            }
  }
}

