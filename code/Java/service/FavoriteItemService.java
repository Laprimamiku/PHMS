package service;

import dao.FavoriteItemDao;
import dao.PetDao;
import model.FavoriteItem;
import model.Pet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FavoriteItemService {
    private final FavoriteItemDao itemDao = new FavoriteItemDao();
    private final PetDao petDao = new PetDao();

    // 核心收藏操作
    public boolean addFavorite(int folderId, int petId) {
        return !isFavorite(folderId, petId) && 
               getPet(petId)
                   .map(pet -> createFavoriteItem(folderId, pet))
                   .orElse(false);
    }

    public boolean removeFavorite(int folderId, int petId) {
        return itemDao.removeFavorite(folderId, petId);
    }

    // 数据查询
    public List<Pet> getFavoritesInFolder(int folderId) {
        return itemDao.getFavoritesByFolderId(folderId).stream()
                .map(item -> petDao.getPetById(item.getPetId()))
                .filter(pet -> pet != null)
                .collect(Collectors.toList());
    }

    public boolean isFavorite(int folderId, int petId) {
        return itemDao.isFavorite(folderId, petId);
    }

    // 统计分析方法
    public List<Map<String, Object>> getFavoriteTimeTrend(int userId) {
        return itemDao.queryTimeTrend(userId);
    }

    public List<Map<String, Object>> getPetTypeDistribution(int userId) {
        return itemDao.queryTypeDist(userId);
    }

    public List<Map<String, Object>> getFavoriteCountDistribution(int userId) {
        return itemDao.queryCountDist(userId);
    }

    // 私有工具方法
    private java.util.Optional<Pet> getPet(int petId) {
        return java.util.Optional.ofNullable(petDao.getPetById(petId));
    }

    private boolean createFavoriteItem(int folderId, Pet pet) {
        FavoriteItem item = new FavoriteItem();
        item.setFolderId(folderId);
        item.setPetId(pet.getId());
        item.setPetType(pet.getType());
        return itemDao.addFavorite(item);
    }
}