package dao;

import model.Pet;
import util.JdbcUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PetDao {
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
    private List<Pet> executeQuery(String sql, Object... params) {
        List<Pet> pets = new ArrayList<>();
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pet pet = new Pet();
                    pet.setId(rs.getInt("id"));
                    pet.setName(rs.getString("name"));
                    pet.setType(rs.getString("type"));
                    pet.setBreed(rs.getString("breed"));
                    pet.setAge(rs.getInt("age"));
                    pet.setDescription(rs.getString("description"));
                    pet.setImage_url(rs.getString("image_url"));
                    pet.setStatus(rs.getString("status"));
                    pet.setUser_id(rs.getInt("user_id"));  // 设置用户ID
                    pet.setAdopted(rs.getString("adopted"));  // 获取 adopted 状态
                    pet.setAdopt_id(rs.getInt("adopt_id"));  // 获取 adopt_id
                    pets.add(pet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pets;
    }

    // 根据宠物类型获取宠物信息
    public List<Pet> getPetsByType(String type) {
        String sql = "SELECT * FROM tb_pet WHERE type = ?";
        return executeQuery(sql, type);
    }

    // 根据宠物名称模糊查询宠物信息
    public List<Pet> searchPetsByName(String searchQuery) {
        String sql = "SELECT * FROM tb_pet WHERE name LIKE ?";
        return executeQuery(sql, "%" + searchQuery + "%");
    }

    // 上传宠物信息
    public boolean addPet(Pet pet) {
        String sql = "INSERT INTO tb_pet (name, type, breed, age, description, image_url, status, adopted, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        // 默认将宠物的 adopt 状态设置为 'no'
        return executeUpdate(sql, pet.getName(), pet.getType(), pet.getBreed(), pet.getAge(), pet.getDescription(), pet.getImage_url(), "pending", "no", pet.getUser_id());
    }

    // 删除宠物信息
    public boolean deletePet(int id) {
        String sql = "DELETE FROM tb_pet WHERE id = ?";
        return executeUpdate(sql, id);
    }

    // 更新宠物信息
    public boolean updatePet(Pet pet) {
        String sql = "UPDATE tb_pet SET name = ?, type = ?, breed = ?, age = ?, description = ?, image_url = ?, status = ?, adopted = ?, user_id = ? WHERE id = ?";
        return executeUpdate(sql, pet.getName(), pet.getType(), pet.getBreed(), pet.getAge(), pet.getDescription(), pet.getImage_url(), pet.getStatus(), pet.getAdopted(), pet.getUser_id(), pet.getId());
    }

    // 获取所有宠物信息
    public List<Pet> getAllPets() {
        String sql = "SELECT * FROM tb_pet";
        return executeQuery(sql);
    }

    // 根据宠物ID获取宠物信息
    public Pet getPetById(int id) {
        String sql = "SELECT * FROM tb_pet WHERE id = ?";
        List<Pet> pets = executeQuery(sql, id);
        return pets.isEmpty() ? null : pets.get(0);
    }

    // 获取adopted='no' AND status='approved'的所有宠物，并进行分页
    public List<Pet> getApprovedPetsForAdoption(int offset, int pageSize) {
        String sql = "SELECT * FROM tb_pet WHERE adopted = 'no' AND status = 'approved' LIMIT ? OFFSET ?";
        return executeQuery(sql, pageSize, offset);
    }

    // 获取符合条件的宠物总数，用于分页计算
    public int getTotalApprovedPetsCount() {
        String sql = "SELECT COUNT(*) FROM tb_pet WHERE adopted = 'no' AND status = 'approved'";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 获取指定用户上传的宠物信息
    public List<Pet> getPetsByUserId(int userId) {
        String sql = "SELECT * FROM tb_pet WHERE user_id = ?";  // 根据 user_id 查询宠物信息
        return executeQuery(sql, userId);
    }
    
    //获取领养人的宠物信息
    public List<Pet> getPetsByAdoptId(int adoptId) {
        String sql = "SELECT * FROM tb_pet WHERE adopt_id = ?";
        return executeQuery(sql, adoptId);
    }

    // 获取待审核的宠物列表
    public List<Pet> getPendingPets() {
        String sql = "SELECT * FROM tb_pet WHERE status = 'pending'";
        return executeQuery(sql);
    }

    // 更新宠物的审批状态
    public void updatePetStatus(int petId, String status) {
        String sql = "UPDATE tb_pet SET status = ? WHERE id = ?";
        executeUpdate(sql, status, petId);
    }

    //获取待确认领养宠物
    public List<Pet> getPetsByAdoptedStatus(String adoptedStatus) {
        String sql = "SELECT * FROM tb_pet WHERE adopted = ? AND status = 'approved'";
        return executeQuery(sql, adoptedStatus);
    }
    
    // 更新宠物的领养状态 (adopted)
    public void updateAdoptedStatus(int petId, String adoptedStatus) {
        String sql = "UPDATE tb_pet SET adopted = ? WHERE id = ?";
        executeUpdate(sql, adoptedStatus, petId);
    }

    // 更新宠物的领养状态，并设置 adopt_id（与领养者的用户ID相关联）
    public void updateAdoptedStatusWithAdoptId(int petId, String adoptedStatus, int adoptId) {
        String sql = "UPDATE tb_pet SET adopted = ?, adopt_id = ? WHERE id = ?";
        executeUpdate(sql, adoptedStatus, adoptId, petId);
    }
}
