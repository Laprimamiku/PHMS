package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.FavoriteFolder;
import model.FavoriteItem;
import model.Pet;
import service.FavoriteFolderService;
import service.FavoriteItemService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/FavoriteServlet")
public class FavoriteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private FavoriteFolderService folderService;
    private FavoriteItemService   itemService;

    public FavoriteServlet() {
        super();
        folderService = new FavoriteFolderService();
        itemService   = new FavoriteItemService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect("log.jsp");
            return;
        }
        try {
            switch (action) {
                case "getFolders":          getFolders(request, response, userId); break;
                case "viewFolders":         viewFolders(request, response, userId); break;
                case "viewFolderContent":   viewFolderContent(request, response); break;
                case "addFolder":           addFolder(request, response, userId); break;
                case "editFolder":          editFolder(request, response); break;
                case "deleteFolder":        deleteFolder(request, response); break;
                case "addToFavorite":       addToFavorite(request, response); break;
                case "removeFavorite":      removeFavorite(request, response); break;
                case "viewStats":           viewStats(request, response, userId); break;
                case "removeFromFavorite": removeFromFavorite(request, response); break;
                default: response.sendRedirect("adopt_home.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(response, false, "服务器内部错误：" + escapeJson(e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }

    /**
     * 获取收藏夹列表（JSON）
     */
    private void getFolders(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws IOException {
        System.out.println("开始获取用户ID " + userId + " 的收藏夹");
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        
        try {
            List<FavoriteFolder> folders = folderService.getUserFolders(userId);
            System.out.println("查询到收藏夹数量: " + (folders != null ? folders.size() : 0));
            
            JSONArray jsonArray = new JSONArray();
            if (folders != null) {
                for (FavoriteFolder folder : folders) {
                    JSONObject json = new JSONObject();
                    json.put("id", folder.getId());
                    json.put("folderName", folder.getFolderName());
                    jsonArray.put(json);
                }
            }
            
            out.print(jsonArray.toString());
            System.out.println("返回收藏夹数据: " + jsonArray.toString());
        } catch (Exception e) {
            System.err.println("获取收藏夹异常: " + e.getMessage());
            e.printStackTrace();
            JSONObject errorJson = new JSONObject();
            errorJson.put("error", "获取收藏夹失败: " + e.getMessage());
            out.print(errorJson.toString());
        }
    }

    /**
     * 添加收藏（Ajax）
     */
    private void addToFavorite(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject result = new JSONObject();
        
        try {
            // 获取参数
            String folderIdStr = request.getParameter("folderId");
            String petIdStr = request.getParameter("petId");
            
            // 记录传入的参数
            System.out.println("收藏请求参数 - folderId: " + folderIdStr + ", petId: " + petIdStr);
            
            // 参数校验
            if (folderIdStr == null || folderIdStr.trim().isEmpty() || 
                petIdStr == null || petIdStr.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "缺少必要参数");
                out.print(result.toString());
                return;
            }

            try {
                int folderId = Integer.parseInt(folderIdStr.trim());
                int petId = Integer.parseInt(petIdStr.trim());
                
                // 执行收藏操作
                boolean success = itemService.addFavorite(folderId, petId);
                
                if (success) {
                    result.put("success", true);
                    result.put("message", "收藏成功");
                } else {
                    result.put("success", false);
                    result.put("message", "该宠物已在此收藏夹中");
                }
            } catch (NumberFormatException e) {
                System.err.println("参数格式错误: " + e.getMessage());
                result.put("success", false);
                result.put("message", "参数格式错误");
            } catch (Exception e) {
                System.err.println("收藏操作异常: " + e.getMessage());
                e.printStackTrace();
                result.put("success", false);
                result.put("message", "收藏失败: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("处理请求异常: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "服务器内部错误");
        }
        
        out.print(result.toString());
        System.out.println("响应数据: " + result.toString());
    }


    // 其他方法保持不变，直接调用 Service 并转发或重定向
    private void viewFolders(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {
        List<FavoriteFolder> folders = folderService.getUserFolders(userId);
        req.setAttribute("folders", folders);
        req.getRequestDispatcher("favorite_folders.jsp").forward(req, resp);
    }

    private void viewFolderContent(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String fid = req.getParameter("folderId");
        if (fid == null || fid.trim().isEmpty()) {
            resp.sendRedirect("FavoriteServlet?action=viewFolders");
            return;
        }
        try {
            int folderId = Integer.parseInt(fid.trim());
            List<Pet> pets = itemService.getFavoritesInFolder(folderId);
            FavoriteFolder folder = folderService.getFolderById(folderId);
            req.setAttribute("pets", pets);
            req.setAttribute("folder", folder);
            req.getRequestDispatcher("favorite_content.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect("FavoriteServlet?action=viewFolders");
        }
    }

    private void addFolder(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {
        String name = req.getParameter("folderName");
        if (name == null || name.trim().isEmpty()) {
            req.setAttribute("errorMessage", "收藏夹名称不能为空。");
            viewFolders(req, resp, userId);
            return;
        }
        FavoriteFolder f = new FavoriteFolder();
        f.setUserId(userId);
        f.setFolderName(name.trim());
        folderService.createFolder(f);
        resp.sendRedirect("FavoriteServlet?action=viewFolders");
    }

    private void editFolder(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String fid = req.getParameter("folderId");
        String name = req.getParameter("folderName");
        if (fid != null && name != null && !fid.trim().isEmpty() && !name.trim().isEmpty()) {
            try {
                FavoriteFolder f = new FavoriteFolder();
                f.setId(Integer.parseInt(fid.trim()));
                f.setFolderName(name.trim());
                folderService.updateFolder(f);
            } catch (NumberFormatException ignored) {}
        }
        resp.sendRedirect("FavoriteServlet?action=viewFolders");
    }

    private void deleteFolder(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String fid = req.getParameter("folderId");
        if (fid != null && !fid.trim().isEmpty()) {
            try {
                folderService.deleteFolder(Integer.parseInt(fid.trim()));
            } catch (NumberFormatException ignored) {}
        }
        resp.sendRedirect("FavoriteServlet?action=viewFolders");
    }

    private void removeFavorite(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String fid = req.getParameter("folderId");
        String pid = req.getParameter("petId");
        if (fid != null && pid != null) {
            try {
                itemService.removeFavorite(
                    Integer.parseInt(fid.trim()),
                    Integer.parseInt(pid.trim())
                );
            } catch (NumberFormatException ignored) {}
        }
        resp.sendRedirect("FavoriteServlet?action=viewFolderContent&folderId=" + fid);
    }

    private void viewStats(HttpServletRequest req, HttpServletResponse resp, int userId)
            throws ServletException, IOException {
        List<FavoriteItem> stats = itemService.getFavoriteStats(userId);
        req.setAttribute("stats", stats);
        req.getRequestDispatcher("favorite_stats.jsp").forward(req, resp);
    }

    private void removeFromFavorite(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = response.getWriter();
    JSONObject result = new JSONObject();
    
    try {
        String folderIdStr = request.getParameter("folderId");
        String petIdStr = request.getParameter("petId");
        
        if (folderIdStr == null || folderIdStr.trim().isEmpty() || 
            petIdStr == null || petIdStr.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "缺少必要参数");
            out.print(result.toString());
            return;
        }

        int folderId = Integer.parseInt(folderIdStr.trim());
        int petId = Integer.parseInt(petIdStr.trim());
        
        boolean success = itemService.removeFavorite(folderId, petId);
        
        if (success) {
            result.put("success", true);
            result.put("message", "移除成功");
        } else {
            result.put("success", false);
            result.put("message", "移除失败");
        }
    } catch (Exception e) {
        result.put("success", false);
        result.put("message", "操作失败：" + e.getMessage());
    }
    
    out.print(result.toString());
}

    /**
     * 统一返回 JSON 响应
     */
    private void sendJson(HttpServletResponse resp, boolean success, String msg)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            JSONObject json = new JSONObject();
            json.put("success", success);
            json.put("message", msg);
            out.print(json.toString());
        }
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
