package dao;

import model.FavoriteFolder;
import util.JdbcUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavoriteFolderDao {
    // 执行数据库更新的通用方法
    private boolean executeUpdate(String sql, Object... params) {
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 执行数据库查询的通用方法
    private List<FavoriteFolder> executeQuery(String sql, Object... params) {
        List<FavoriteFolder> folders = new ArrayList<>();
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FavoriteFolder folder = new FavoriteFolder();
                    folder.setId(rs.getInt("id"));
                    folder.setUserId(rs.getInt("user_id"));
                    folder.setFolderName(rs.getString("folder_name"));
                    folder.setDescription(rs.getString("description"));
                    folder.setCreatedAt(rs.getTimestamp("created_at"));
                    folders.add(folder);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return folders;
    }

    // 创建收藏夹
    public boolean createFolder(FavoriteFolder folder) {
        String sql = "INSERT INTO tb_favorite_folder (user_id, folder_name, description) VALUES (?, ?, ?)";
        return executeUpdate(sql, folder.getUserId(), folder.getFolderName(), folder.getDescription());
    }

    // 删除收藏夹
    public boolean deleteFolder(int id) {
        String sql = "DELETE FROM tb_favorite_folder WHERE id = ?";
        return executeUpdate(sql, id);
    }

    // 更新收藏夹
    public boolean updateFolder(FavoriteFolder folder) {
        String sql = "UPDATE tb_favorite_folder SET folder_name = ?, description = ? WHERE id = ?";
        return executeUpdate(sql, folder.getFolderName(), folder.getDescription(), folder.getId());
    }

    // 获取用户的所有收藏夹
    public List<FavoriteFolder> getFoldersByUserId(int userId) {
        String sql = "SELECT * FROM tb_favorite_folder WHERE user_id = ?";
        return executeQuery(sql, userId);
    }

    // 根据ID获取收藏夹
    public FavoriteFolder getFolderById(int id) {
        String sql = "SELECT * FROM tb_favorite_folder WHERE id = ?";
        List<FavoriteFolder> folders = executeQuery(sql, id);
        return folders.isEmpty() ? null : folders.get(0);
    }

    // 检查收藏夹名称是否已存在（同一用户下）
    public boolean isFolderNameExist(String folderName, int userId) {
        String sql = "SELECT * FROM tb_favorite_folder WHERE folder_name = ? AND user_id = ?";
        List<FavoriteFolder> folders = executeQuery(sql, folderName, userId);
        return !folders.isEmpty();
    }
}