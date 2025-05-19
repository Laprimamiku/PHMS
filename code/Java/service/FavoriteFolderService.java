package service;

import dao.FavoriteFolderDao;
import model.FavoriteFolder;
import java.util.List;

public class FavoriteFolderService {
    private final FavoriteFolderDao folderDao = new FavoriteFolderDao();

    // 核心业务方法
    public boolean createFolder(FavoriteFolder folder) {
        return !isFolderNameExists(folder) && folderDao.createFolder(folder);
    }

    public boolean updateFolder(FavoriteFolder folder) {
        return isValidUpdate(folder) && folderDao.updateFolder(folder);
    }

    public boolean deleteFolder(int id) {
        return folderDao.deleteFolder(id);
    }

    // 查询方法
    public List<FavoriteFolder> searchFolders(int userId, String keyword) {
        return folderDao.searchFolders(userId, keyword);
    }

    public List<FavoriteFolder> getFoldersByPage(int userId, String keyword, 
                                               int page, int pageSize, 
                                               String sortField, String sortOrder) {
        return folderDao.getFoldersByPage(userId, keyword, page, pageSize, sortField, sortOrder);
    }

    public FavoriteFolder getFolderById(int id) {
        return folderDao.getFolderById(id);
    }

    public List<FavoriteFolder> getUserFolders(int userId) {
        return folderDao.getFoldersByUserId(userId);
    }

    // 验证方法
    public boolean isFolderNameAvailable(String folderName, int userId) {
        return !folderDao.isFolderNameExist(folderName, userId);
    }

    public int getFolderCount(int userId, String keyword) {
        return folderDao.getFolderCount(userId, keyword);
    }

    // 私有验证逻辑
    private boolean isFolderNameExists(FavoriteFolder folder) {
        return folderDao.isFolderNameExist(folder.getFolderName(), folder.getUserId());
    }

    private boolean isValidUpdate(FavoriteFolder folder) {
        FavoriteFolder existing = folderDao.getFolderById(folder.getId());
        return existing != null && 
              (isSameFolderName(existing, folder) || isNewNameAvailable(folder));
    }

    private boolean isSameFolderName(FavoriteFolder existing, FavoriteFolder updated) {
        return existing.getFolderName().equals(updated.getFolderName());
    }

    private boolean isNewNameAvailable(FavoriteFolder folder) {
        return !folderDao.isFolderNameExist(folder.getFolderName(), folder.getUserId());
    }
}