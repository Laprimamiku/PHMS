package service;

import dao.FavoriteItemDao;
import dao.PetDao;
import model.FavoriteItem;
import model.Pet;
import java.util.List;
import java.util.ArrayList;

public class FavoriteItemService {
    private FavoriteItemDao itemDao;
    private PetDao petDao;
    
    public FavoriteItemService() {
        this.itemDao = new FavoriteItemDao();
        this.petDao = new PetDao();
    }
    
    // 添加收藏
    public boolean addFavorite(int folderId, int petId) {
        // 检查是否已收藏
        if (itemDao.isFavorite(folderId, petId)) {
            return false;
        }
        
        // 获取宠物信息
        Pet pet = petDao.getPetById(petId);
        if (pet == null) {
            return false;
        }
        
        // 创建收藏记录
        FavoriteItem item = new FavoriteItem();
        item.setFolderId(folderId);
        item.setPetId(petId);
        item.setPetType(pet.getType());
        
        return itemDao.addFavorite(item);
    }
    
    // 取消收藏
    public boolean removeFavorite(int folderId, int petId) {
        return itemDao.removeFavorite(folderId, petId);
    }
    
    // 获取收藏夹中的所有宠物
    public List<Pet> getFavoritesInFolder(int folderId) {
        List<FavoriteItem> items = itemDao.getFavoritesByFolderId(folderId);
        List<Pet> pets = new ArrayList<>();
        
        for (FavoriteItem item : items) {
            Pet pet = petDao.getPetById(item.getPetId());
            if (pet != null) {
                pets.add(pet);
            }
        }
        
        return pets;
    }
    
    // 检查是否已收藏
    public boolean isFavorite(int folderId, int petId) {
        return itemDao.isFavorite(folderId, petId);
    }
    
    // 获取收藏统计信息
    public List<FavoriteItem> getFavoriteStats(int userId) {
        return itemDao.getFavoriteStats(userId);
    }
    
    // 获取宠物类型收藏分布
    public List<FavoriteItem> getPetTypeDistribution(int userId) {
        return itemDao.getFavoriteStats(userId);
    }
    
    // 获取收藏时间趋势
    public List<FavoriteItem> getFavoriteTimeTrend(int userId) {
        // 这里可以添加时间趋势的统计逻辑
        return itemDao.getFavoriteStats(userId);
    }
}