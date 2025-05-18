package servlet;

import model.Announcement;
import service.AnnouncementService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet("/announcement")
public class AnnouncementController extends HttpServlet {
    private AnnouncementService announcementService = new AnnouncementService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            // 获取表单数据
            String title = request.getParameter("title");
            String content = request.getParameter("content");

            // 创建公告对象并设置数据
            Announcement announcement = new Announcement();
            announcement.setTitle(title);
            announcement.setContent(content);
            announcement.setCreatedAt(new Date());
            announcement.setCreatedBy((Integer) request.getSession().getAttribute("userId")); // 假设 session 中存储了 userId

            // 保存公告
            announcementService.addAnnouncement(announcement);

            // 重定向到公告列表
            response.sendRedirect("announcement?action=list");
        }
        else if ("update".equals(action)) {
            // 获取表单数据
            int id = Integer.parseInt(request.getParameter("id"));
            String title = request.getParameter("title");
            String content = request.getParameter("content");

            // 创建公告对象并更新数据
            Announcement announcement = new Announcement();
            announcement.setId(id);
            announcement.setTitle(title);
            announcement.setContent(content);

            // 更新公告
            announcementService.updateAnnouncement(announcement);

            // 重定向到公告列表
            response.sendRedirect("announcement?action=list");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("list".equals(action)) {
            // 获取所有公告
            List<Announcement> announcements = announcementService.getAllAnnouncements();

            // 设置请求属性并转发到 JSP
            request.setAttribute("announcements", announcements);
            request.getRequestDispatcher("announcementList.jsp").forward(request, response);
        } else if ("view".equals(action)) {
            // 根据 ID 获取公告
            int id = Integer.parseInt(request.getParameter("id"));
            Announcement announcement = announcementService.getAnnouncementById(id);
            // 设置请求属性并转发到 JSP
            request.setAttribute("announcement", announcement);
            request.getRequestDispatcher("viewAnnouncement.jsp").forward(request, response);
        }
        else if ("viewAll".equals(action)) { // 新增查看所有公告的逻辑
            // 获取所有公告
            List<Announcement> announcements = announcementService.getAllAnnouncements();

            // 设置请求属性并转发到只读页面
            request.setAttribute("announcements", announcements);
            request.getRequestDispatcher("viewAllAnnouncements.jsp").forward(request, response);
        } else if ("edit".equals(action)) { // 处理编辑公告的请求
            // 根据 ID 获取公告信息
            int id = Integer.parseInt(request.getParameter("id"));
            Announcement announcement = announcementService.getAnnouncementById(id);
            if (announcement != null) {
                // 将公告信息设置到请求中
                request.setAttribute("announcement", announcement);

                // 转发到 editAnnouncement.jsp
                request.getRequestDispatcher("editAnnouncement.jsp").forward(request, response);
            } else {
                // 如果公告不存在，重定向到公告列表页面
                response.sendRedirect("announcement?action=list");
            }
        } else if ("delete".equals(action)) {
            // 删除公告
            int id = Integer.parseInt(request.getParameter("id"));
            announcementService.deleteAnnouncement(id);

            // 重定向到公告列表
            response.sendRedirect("announcement?action=list");
        } else {
            // 默认跳转到公告列表
            response.sendRedirect("announcement?action=list");
        }
    }
}
