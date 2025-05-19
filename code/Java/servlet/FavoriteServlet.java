package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.FavoriteFolder;
import model.Pet;
import org.json.JSONArray;
import org.json.JSONObject;
import service.FavoriteFolderService;
import service.FavoriteItemService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/FavoriteServlet")
public class FavoriteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private FavoriteFolderService folderService = new FavoriteFolderService();
    private FavoriteItemService  itemService   = new FavoriteItemService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 登录校验
        HttpSession session = req.getSession(false);
        Integer userId = (session == null) ? null : (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect("log.jsp");
            return;
        }

        String action = req.getParameter("action");
        try {
            switch (action) {
                case "getFolders":        getFolders(req, resp, userId);      break;
                case "viewFolders":       viewFolders(req, resp, userId);     break;
                case "viewFolderContent": viewFolderContent(req, resp);       break;
                case "addFolder":         addFolder(req, resp, userId);       break;
                case "editFolder":        editFolder(req, resp);              break;
                case "deleteFolder":      deleteFolder(req, resp);            break;
                case "addToFavorite":     favoriteOp(req, resp, true);        break;
                case "removeFromFavorite":favoriteOp(req, resp, false);       break;
                case "viewStats":         viewStats(req, resp, userId);       break;
                default:
                    resp.sendRedirect("adopt_home.jsp");
            }
        } catch (Exception e) {
            log("处理失败", e);
            sendJson(resp, false, "服务器内部错误：" + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }

    // ------ 公共工具 ------

    /** 发送简单 JSON 响应 */
    private void sendJson(HttpServletResponse resp, boolean success, String message) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        JSONObject json = new JSONObject()
            .put("success", success)
            .put("message", message);
        try (PrintWriter out = resp.getWriter()) {
            out.print(json);
        }
    }

    /** 获取整数参数，出错时自动返回 JSON 并退出 */
    private Integer getIntParam(HttpServletRequest req, HttpServletResponse resp,
                                String name) throws IOException {
        String s = req.getParameter(name);
        if (s == null || s.isEmpty()) {
            sendJson(resp, false, "缺少参数：" + name);
            return null;
        }
        try {
            return Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            sendJson(resp, false, "参数格式错误：" + name);
            return null;
        }
    }

    /** 输出 JSON 数组 */
    private void writeJsonArray(HttpServletResponse resp, JSONArray arr) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.print(arr);
        }
    }

    /** 安全解析整数，失败返回默认 */
    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s); }
        catch (Exception e) { return def; }
    }

    // ------ 各业务处理 ------

    /** Ajax：获取收藏夹列表 */
    private void getFolders(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws IOException {
        String kw = req.getParameter("search");
        List<FavoriteFolder> list = (kw == null || kw.isEmpty())
            ? folderService.getUserFolders(userId)
            : folderService.searchFolders(userId, kw.trim());
        JSONArray arr = new JSONArray();
        for (FavoriteFolder f : list) {
            arr.put(new JSONObject()
                .put("id", f.getId())
                .put("folderName", f.getFolderName())
                .put("description", f.getDescription() == null ? "" : f.getDescription())
            );
        }
        writeJsonArray(resp, arr);
    }

    /** JSP：分页查看收藏夹 */
    private void viewFolders(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {
        int page     = parseInt(req.getParameter("page"), 1);
        int pageSize = 6;
        String sortField = defaultStr(req.getParameter("sortField"), "created_at");
        String sortOrder = defaultStr(req.getParameter("sortOrder"), "DESC");
        String keyword   = defaultStr(req.getParameter("keyword"), "");

        List<FavoriteFolder> folders = folderService.getFoldersByPage(
            userId, keyword, page, pageSize, sortField, sortOrder);
        int total = folderService.getFolderCount(userId, keyword);
        int totalPages = (total + pageSize - 1) / pageSize;

        req.setAttribute("folders", folders);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("sortField", sortField);
        req.setAttribute("sortOrder", sortOrder);
        req.setAttribute("keyword", keyword);
        req.getRequestDispatcher("favorite_folders.jsp").forward(req, resp);
    }

    /** JSP：查看单个收藏夹内容 */
    private void viewFolderContent(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String fid = req.getParameter("folderId");
        int id = parseInt(fid, -1);
        if (id < 0) {
            resp.sendRedirect("FavoriteServlet?action=viewFolders");
            return;
        }
        req.setAttribute("pets", folderService.getFolderById(id) != null
            ? itemService.getFavoritesInFolder(id)
            : List.of());
        req.setAttribute("folder", folderService.getFolderById(id));
        req.getRequestDispatcher("favorite_content.jsp").forward(req, resp);
    }

    /** 新增收藏夹 */
    private void addFolder(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws IOException, ServletException {
        String name = defaultStr(req.getParameter("folderName"), "").trim();
        if (name.isEmpty()) {
            req.setAttribute("errorMessage", "收藏夹名称不能为空");
            viewFolders(req, resp, userId);
            return;
        }
        FavoriteFolder f = new FavoriteFolder();
        f.setUserId(userId);
        f.setFolderName(name);
        folderService.createFolder(f);
        resp.sendRedirect("FavoriteServlet?action=viewFolders");
    }

    /** 编辑收藏夹 */
    private void editFolder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = getIntParam(req, resp, "folderId");
        String name = req.getParameter("folderName");
        String desc = req.getParameter("description");
        if (id != null && name != null && !name.trim().isEmpty()) {
            FavoriteFolder orig = folderService.getFolderById(id);
            if (orig != null) {
                orig.setFolderName(name.trim());
                orig.setDescription(desc);
                folderService.updateFolder(orig);
            }
        }
        resp.sendRedirect("FavoriteServlet?action=viewFolders");
    }

    /** 删除收藏夹 */
    private void deleteFolder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = getIntParam(req, resp, "folderId");
        if (id != null) folderService.deleteFolder(id);
        resp.sendRedirect("FavoriteServlet?action=viewFolders");
    }

    /** Ajax：添加/移除收藏项 */
    private void favoriteOp(HttpServletRequest req, HttpServletResponse resp, boolean isAdd)
            throws IOException {
        Integer folderId = getIntParam(req, resp, "folderId");
        Integer petId    = getIntParam(req, resp, "petId");
        if (folderId == null || petId == null) return;
        boolean ok = isAdd
            ? itemService.addFavorite(folderId, petId)
            : itemService.removeFavorite(folderId, petId);
        sendJson(resp, ok, ok
            ? (isAdd ? "收藏成功" : "移除成功")
            : (isAdd ? "该宠物已在此收藏夹中" : "操作失败"));
    }

    /** JSP：查看收藏统计 */
    private void viewStats(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {
        req.setAttribute("timeTrend", itemService.getFavoriteTimeTrend(userId));
        req.setAttribute("typeDist", itemService.getPetTypeDistribution(userId));
        req.getRequestDispatcher("favorite_visualization.jsp").forward(req, resp);
    }

    /** 默认字符串 */
    private String defaultStr(String s, String def) {
        return (s == null) ? def : s;
    }
}
