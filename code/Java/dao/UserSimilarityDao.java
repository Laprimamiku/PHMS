package dao;

import model.UserSimilarity;
import util.JdbcUtil;

import java.sql.*;
import java.util.*;

public class UserSimilarityDao {

    // 执行更新语句（新增/更新）
    private boolean executeUpdate(String sql, Object... params) {
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("数据库更新失败", e);
        }
    }

    // 执行查询语句
    private List<UserSimilarity> executeQuery(String sql, Object... params) {
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("数据库查询失败", e);
        }
    }

    // 设置参数到预编译语句中
    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    // 将结果集映射为 UserSimilarity 对象列表
    private List<UserSimilarity> mapResultSet(ResultSet rs) throws SQLException {
        List<UserSimilarity> list = new ArrayList<>();
        while (rs.next()) {
            UserSimilarity s = new UserSimilarity();
            s.setId(rs.getInt("id"));
            s.setUserId1(rs.getInt("user1_id"));
            s.setUserId2(rs.getInt("user2_id"));
            s.setSimilarity(rs.getDouble("similarity"));
            s.setCreateTime(rs.getString("created_at"));
            s.setUpdateTime(rs.getString("created_at")); // 若有 updated_at 字段可替换
            list.add(s);
        }
        return list;
    }

    // 保存用户相似度，若已存在则更新
    public void saveSimilarity(UserSimilarity similarity) {
        String sql = "INSERT INTO tb_user_similarity (user1_id, user2_id, similarity) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE similarity = ?";
        executeUpdate(sql,
            similarity.getUserId1(),
            similarity.getUserId2(),
            similarity.getSimilarity(),
            similarity.getSimilarity()
        );
    }

    // 获取两个用户之间的相似度
    public double getSimilarity(int userId1, int userId2) {
        String sql = "SELECT similarity FROM tb_user_similarity " +
                     "WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
        List<UserSimilarity> list = executeQuery(sql, userId1, userId2, userId2, userId1);
        return list.isEmpty() ? 0.0 : list.get(0).getSimilarity();
    }

    // 获取与指定用户最相似的其他用户（按相似度降序）
    public List<UserSimilarity> getTopSimilarUsers(int userId, int limit) {
        String sql = "SELECT * FROM tb_user_similarity " +
                     "WHERE user1_id = ? OR user2_id = ? " +
                     "ORDER BY similarity DESC LIMIT ?";
        return executeQuery(sql, userId, userId, limit);
    }
}
