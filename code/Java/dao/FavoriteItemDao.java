package dao;

import model.FavoriteItem;
import util.JdbcUtil;
import java.sql.*;
import java.util.*;

public class FavoriteItemDao {
    // 通用更新方法
    private boolean executeUpdate(String sql, Object... params) {
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 通用查询方法（返回实体列表）
    private List<FavoriteItem> executeQuery(String sql, Object... params) {
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            return mapResultSet(ps.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    // 通用存在性检查
    private boolean executeExists(String sql, Object... params) {
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 通用Map结果查询
    private List<Map<String, Object>> executeQueryForMap(String sql, Object... params) {
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            return mapToDictionary(ps.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    // 结果集映射到实体
    private List<FavoriteItem> mapResultSet(ResultSet rs) throws SQLException {
        List<FavoriteItem> items = new ArrayList<>();
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
        return items;
    }

    // 结果集映射到字典
    private List<Map<String, Object>> mapToDictionary(ResultSet rs) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(metaData.getColumnLabel(i), rs.getObject(i));
            }
            result.add(row);
        }
        return result;
    }

    // 参数设置
    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    // 核心业务方法
    public boolean addFavorite(FavoriteItem item) {
        return executeUpdate(
            "INSERT INTO tb_favorite_item (folder_id, pet_id, pet_type) VALUES (?, ?, ?)",
            item.getFolderId(), item.getPetId(), item.getPetType()
        );
    }

    public boolean removeFavorite(int folderId, int petId) {
        return executeUpdate(
            "DELETE FROM tb_favorite_item WHERE folder_id = ? AND pet_id = ?",
            folderId, petId
        );
    }

    public List<FavoriteItem> getFavoritesByFolderId(int folderId) {
        return executeQuery("SELECT * FROM tb_favorite_item WHERE folder_id = ?", folderId);
    }

    public boolean isFavorite(int folderId, int petId) {
        return executeExists(
            "SELECT 1 FROM tb_favorite_item WHERE folder_id = ? AND pet_id = ?",
            folderId, petId
        );
    }

    // 统计分析方法
    /*
    public List<Map<String, Object>> getFavoriteStats(int userId) {
        return queryTypeDist(userId);
    }
*/






public List<Map<String, Object>> getFavoriteStats(int userId) {
    String sql;
    if (userId == 0) {
        // 获取所有用户的收藏数据
        sql = "SELECT fi.pet_id, fi.pet_type, ff.user_id, COUNT(*) as count " +
              "FROM tb_favorite_item fi " +
              "JOIN tb_favorite_folder ff ON fi.folder_id = ff.id " +
              "GROUP BY fi.pet_id, fi.pet_type, ff.user_id";
    } else {
        // 获取特定用户的收藏数据
        sql = "SELECT fi.pet_id, fi.pet_type, COUNT(*) as count " +
              "FROM tb_favorite_item fi " +
              "JOIN tb_favorite_folder ff ON fi.folder_id = ff.id " +
              "WHERE ff.user_id = ? " +
              "GROUP BY fi.pet_id, fi.pet_type";
    }
    
    List<Map<String, Object>> stats = new ArrayList<>();
    try (Connection conn = JdbcUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        if (userId != 0) {
            stmt.setInt(1, userId);
        }
        
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("pet_id", rs.getInt("pet_id"));
            stat.put("pet_type", rs.getString("pet_type"));
            stat.put("count", rs.getInt("count"));
            if (userId == 0) {
                stat.put("user_id", rs.getInt("user_id"));
            }
            stats.add(stat);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return stats;
}



    public List<Map<String, Object>> queryTimeTrend(int userId) {
        return executeQueryForMap(
            "SELECT DATE(created_at) AS date, COUNT(*) AS count " +
            "FROM tb_favorite_item WHERE folder_id IN " +
            "(SELECT id FROM tb_favorite_folder WHERE user_id = ?) " +
            "GROUP BY date ORDER BY date",
            userId
        );
    }

    public List<Map<String, Object>> queryTypeDist(int userId) {
        return executeQueryForMap(
            "SELECT pet_type AS type, COUNT(*) AS cnt " +
            "FROM tb_favorite_item WHERE folder_id IN " +
            "(SELECT id FROM tb_favorite_folder WHERE user_id = ?) " +
            "GROUP BY pet_type",
            userId
        );
    }

    public List<Map<String, Object>> queryCountDist(int userId) {
        return executeQueryForMap(
            "SELECT count AS times, COUNT(*) AS freq " +
            "FROM tb_favorite_item WHERE folder_id IN " +
            "(SELECT id FROM tb_favorite_folder WHERE user_id = ?) " +
            "GROUP BY count",
            userId
        );
    }
}