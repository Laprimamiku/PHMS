package dao;

import model.FavoriteFolder;
import util.JdbcUtil;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class FavoriteFolderDao {
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
    private List<FavoriteFolder> executeQuery(String sql, Object... params) {
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            return mapResultSet(ps.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    // 通用标量查询
    private <T> T executeScalar(String sql, Class<T> type, Object... params) {
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getObject(1, type) : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 结果集映射
    private List<FavoriteFolder> mapResultSet(ResultSet rs) throws SQLException {
        List<FavoriteFolder> folders = new ArrayList<>();
        while (rs.next()) {
            FavoriteFolder folder = new FavoriteFolder();
            folder.setId(rs.getInt("id"));
            folder.setUserId(rs.getInt("user_id"));
            folder.setFolderName(rs.getString("folder_name"));
            folder.setDescription(rs.getString("description"));
            folder.setCreatedAt(rs.getTimestamp("created_at"));
            folders.add(folder);
        }
        return folders;
    }

    // 参数设置
    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    // 创建收藏夹
    public boolean createFolder(FavoriteFolder folder) {
        return executeUpdate(
            "INSERT INTO tb_favorite_folder (user_id, folder_name, description) VALUES (?, ?, ?)",
            folder.getUserId(), folder.getFolderName(), folder.getDescription()
        );
    }

    // 删除收藏夹
    public boolean deleteFolder(int id) {
        return executeUpdate("DELETE FROM tb_favorite_folder WHERE id = ?", id);
    }

    // 更新收藏夹
    public boolean updateFolder(FavoriteFolder folder) {
        return executeUpdate(
            "UPDATE tb_favorite_folder SET folder_name = ?, description = ? WHERE id = ?",
            folder.getFolderName(), folder.getDescription(), folder.getId()
        );
    }

    // 分页查询
    public List<FavoriteFolder> getFoldersByPage(int userId, String keyword, int page, int pageSize, String sortField, String sortOrder) {
        String safeSort = validateSortField(sortField);
        String safeOrder = sortOrder.equalsIgnoreCase("DESC") ? "DESC" : "ASC";
        
        List<Object> params = new ArrayList<>(Arrays.asList(userId));
        String sql = buildSearchSql(keyword, safeSort, safeOrder, params);
        
        params.add(pageSize);
        params.add((page - 1) * pageSize);
        return executeQuery(sql, params.toArray());
    }

    // 获取总数
    public int getFolderCount(int userId, String keyword) {
        List<Object> params = new ArrayList<>(Arrays.asList(userId));
        String sql = buildCountSql(keyword, params);
        return Optional.ofNullable(executeScalar(sql, Integer.class, params.toArray()))
                      .orElse(0);
    }

    // 辅助方法：构建搜索SQL
    private String buildSearchSql(String keyword, String sortField, String sortOrder, List<Object> params) {
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM tb_favorite_folder WHERE user_id = ? ");
        appendSearchCondition(keyword, sql, params);
        sql.append("ORDER BY ").append(sortField).append(" ").append(sortOrder)
           .append(" LIMIT ? OFFSET ?");
        return sql.toString();
    }

    // 辅助方法：构建统计SQL
    private String buildCountSql(String keyword, List<Object> params) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM tb_favorite_folder WHERE user_id = ? ");
        appendSearchCondition(keyword, sql, params);
        return sql.toString();
    }

    // 辅助方法：添加搜索条件
    private void appendSearchCondition(String keyword, StringBuilder sql, List<Object> params) {
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND folder_name LIKE ? ");
            params.add("%" + keyword + "%");
        }
    }

    // 校验排序字段
    private String validateSortField(String sortField) {
        return Arrays.stream(new String[]{"folder_name", "created_at"})
                    .filter(f -> f.equalsIgnoreCase(sortField))
                    .findFirst()
                    .orElse("created_at");
    }

    // 其他方法保持不变...
    public List<FavoriteFolder> searchFolders(int userId, String keyword) {
        return executeQuery(
            "SELECT * FROM tb_favorite_folder WHERE user_id = ? AND folder_name LIKE ?",
            userId, "%" + keyword + "%"
        );
    }

    public List<FavoriteFolder> getFoldersByUserId(int userId) {
        return executeQuery("SELECT * FROM tb_favorite_folder WHERE user_id = ?", userId);
    }

    public FavoriteFolder getFolderById(int id) {
        List<FavoriteFolder> result = executeQuery("SELECT * FROM tb_favorite_folder WHERE id = ?", id);
        return result.isEmpty() ? null : result.get(0);
    }

    public boolean isFolderNameExist(String folderName, int userId) {
        return !executeQuery(
            "SELECT * FROM tb_favorite_folder WHERE folder_name = ? AND user_id = ?",
            folderName, userId
        ).isEmpty();
    }
}