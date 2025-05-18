package service;

import model.Pet;
import dao.PetDao;
import java.util.List;

public class PetService {

    private PetDao petDao = new PetDao();
    // 查询宠物信息
    public List<Pet> searchPetsByName(String searchQuery) {
        return petDao.searchPetsByName(searchQuery);
    }

    
    // 上传宠物信息，并关联到指定的用户
    public boolean addPet(int userId, Pet pet) {
        pet.setUser_id(userId);  // 设置外键关联到用户
        pet.setStatus("pending");  // 默认状态为待审核
        return petDao.addPet(pet);
    }

    // 删除宠物信息
    public boolean deletePet(int petId) {
        return petDao.deletePet(petId);
    }

    // 更新宠物信息
    public boolean updatePet(Pet pet) {
        return petDao.updatePet(pet);
    }

    // 获取所有宠物信息
    public List<Pet> getAllPets() {
        return petDao.getAllPets();
    }

    // 根据宠物ID获取宠物信息
    public Pet getPetById(int petId) {
        return petDao.getPetById(petId);
    }

    // 根据宠物类型获取宠物信息
    public List<Pet> getPetsByType(String type) {
        return petDao.getPetsByType(type);  // 调用 PetDao 中的查询方法
    }

    // 获取adopted='no' AND status='approved'的所有宠物，并进行分页
    public List<Pet> getApprovedPetsForAdoption(int offset, int pageSize) {
        return petDao.getApprovedPetsForAdoption(offset, pageSize);
    }

    // 获取符合条件的宠物总数，用于分页计算
    public int getTotalApprovedPetsCount() {
        return petDao.getTotalApprovedPetsCount();
    }
    
    // 获取指定用户上传的宠物信息
    public List<Pet> getPetsByUserId(int userId) {
        return petDao.getPetsByUserId(userId);  // 获取某个用户上传的宠物
    }
    
    //获取领养人的宠物信息
    public List<Pet> getPetsByAdoptId(int adoptId) {
        return petDao.getPetsByAdoptId(adoptId);
    }

    // 获取待审核的宠物列表
    public List<Pet> getPendingPets() {
        return petDao.getPendingPets();
    }

    // 更新宠物的审批状态（管理员审批）
 // PetService.java
    public void approvePet(int petId) {
        petDao.updatePetStatus(petId, "approved");
    }

    public void rejectPet(int petId) {
        petDao.updatePetStatus(petId, "rejected");
    }

    // 获取待领养的宠物列表
    public List<Pet> getAdoptPendingPets() {
        return petDao.getPetsByAdoptedStatus("undetermined");
    }
    
    
    
    
    // 更新宠物的领养状态 (adopted)
    public void updateAdoptedStatus(int petId, String adoptedStatus) {
        // adoptedStatus 应该是 'yes', 'undetermined', 或 'no'
        if ("yes".equals(adoptedStatus) || "undetermined".equals(adoptedStatus) || "no".equals(adoptedStatus)) {
            petDao.updateAdoptedStatus(petId, adoptedStatus);
        } else {
            throw new IllegalArgumentException("Invalid adopted status: " + adoptedStatus);
        }
    }

    // 更新宠物的领养状态 (adopted)，并且设置 adopt_id（领养者的 ID）
    public void updateAdoptedStatusWithAdoptId(int petId, String adoptedStatus, int adoptId) {
        // adoptedStatus 应该是 'yes', 'undetermined', 或 'no'
        if ("yes".equals(adoptedStatus) || "undetermined".equals(adoptedStatus) || "no".equals(adoptedStatus)) {
            petDao.updateAdoptedStatusWithAdoptId(petId, adoptedStatus, adoptId);
        } else {
            throw new IllegalArgumentException("Invalid adopted status: " + adoptedStatus);
        }
    }
}
