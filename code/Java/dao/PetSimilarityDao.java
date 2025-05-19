package dao;

import model.PetSimilarity;
import util.JdbcUtil;

import java.sql.*;
import java.util.*;

public class PetSimilarityDao {

    // 通用更新执行
    private boolean executeUpdate(String sql, Object... params) {
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("数据库更新异常", e);
        }
    }

    // 通用查询执行
    private List<PetSimilarity> executeQuery(String sql, Object... params) {
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("数据库查询异常", e);
        }
    }

    // 设置预编译参数
    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    // 将结果集映射为实体列表
    private List<PetSimilarity> mapResultSet(ResultSet rs) throws SQLException {
        List<PetSimilarity> list = new ArrayList<>();
        while (rs.next()) {
            PetSimilarity s = new PetSimilarity();
            s.setId(rs.getInt("id"));
            s.setPetId1(rs.getInt("pet1_id"));
            s.setPetId2(rs.getInt("pet2_id"));
            s.setSimilarity(rs.getDouble("similarity"));
            s.setCreateTime(rs.getString("created_at"));
            s.setUpdateTime(rs.getString("created_at")); // 如果有 updated_at 字段建议改成它
            list.add(s);
        }
        return list;
    }

    // 保存宠物相似度（若已存在则更新）
    public void saveSimilarity(PetSimilarity similarity) {
        String sql = "INSERT INTO tb_pet_similarity (pet1_id, pet2_id, similarity) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE similarity = ?";
        executeUpdate(sql,
            similarity.getPetId1(),
            similarity.getPetId2(),
            similarity.getSimilarity(),
            similarity.getSimilarity()
        );
    }

    // 获取两个宠物之间的相似度
    public double getSimilarity(int petId1, int petId2) {
        String sql = "SELECT similarity FROM tb_pet_similarity " +
                     "WHERE (pet1_id = ? AND pet2_id = ?) OR (pet1_id = ? AND pet2_id = ?)";
        List<PetSimilarity> list = executeQuery(sql, petId1, petId2, petId2, petId1);
        return list.isEmpty() ? 0.0 : list.get(0).getSimilarity();
    }

    // 获取与指定宠物最相似的宠物列表（按相似度降序）
    public List<PetSimilarity> getTopSimilarPets(int petId, int limit) {
        String sql = "SELECT * FROM tb_pet_similarity " +
                     "WHERE pet1_id = ? OR pet2_id = ? " +
                     "ORDER BY similarity DESC LIMIT ?";
        return executeQuery(sql, petId, petId, limit);
    }
}
