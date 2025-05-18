package dao;

import model.FavoriteItem;
import util.JdbcUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavoriteItemDao {
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
    private List<FavoriteItem> executeQuery(String sql, Object... params) {
        List<FavoriteItem> items = new ArrayList<>();
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FavoriteItem item = new FavoriteItem();
                    item.setId(rs.getInt("id"));
                    item.setFolderId(rs.getInt("folder_id"));
                    item.setPetId(rs.getInt("pet_id"));
                    item.setCreatedAt(rs.getTimestamp("created_at"));
                    item.setPetType(rs.getString("pet_type"));
                    item.setCount(rs.getInt("count"));
                    item.setLastUpdated(rs.getTimestamp("last_updated"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    // 添加收藏
    public boolean addFavorite(FavoriteItem item) {
        String sql = "INSERT INTO tb_favorite_item (folder_id, pet_id, pet_type) VALUES (?, ?, ?)";
        return executeUpdate(sql, item.getFolderId(), item.getPetId(), item.getPetType());
    }

    // 取消收藏
    public boolean removeFavorite(int folderId, int petId) {
        String sql = "DELETE FROM tb_favorite_item WHERE folder_id = ? AND pet_id = ?";
        return executeUpdate(sql, folderId, petId);
    }

    // 获取收藏夹中的所有宠物
    public List<FavoriteItem> getFavoritesByFolderId(int folderId) {
        String sql = "SELECT * FROM tb_favorite_item WHERE folder_id = ?";
        return executeQuery(sql, folderId);
    }

    // 检查是否已收藏
    public boolean isFavorite(int folderId, int petId) {
        String sql = "SELECT * FROM tb_favorite_item WHERE folder_id = ? AND pet_id = ?";
        List<FavoriteItem> items = executeQuery(sql, folderId, petId);
        return !items.isEmpty();
    }

    // 获取收藏统计信息（用于图表展示）
    public List<FavoriteItem> getFavoriteStats(int userId) {
        String sql = "SELECT i.* FROM tb_favorite_item i " +
                    "JOIN tb_favorite_folder f ON i.folder_id = f.id " +
                    "WHERE f.user_id = ? " +
                    "GROUP BY i.pet_type";
        return executeQuery(sql, userId);
    }
}