package service;

import dao.FavoriteFolderDao;
import model.FavoriteFolder;
import java.util.List;

public class FavoriteFolderService {
    private FavoriteFolderDao folderDao;
    
    public FavoriteFolderService() {
        this.folderDao = new FavoriteFolderDao();
    }
    
    // 创建收藏夹
    public boolean createFolder(FavoriteFolder folder) {
        // 检查收藏夹名称是否已存在
        if (folderDao.isFolderNameExist(folder.getFolderName(), folder.getUserId())) {
            return false;
        }
        return folderDao.createFolder(folder);
    }
    
    // 删除收藏夹
    public boolean deleteFolder(int id) {
        return folderDao.deleteFolder(id);
    }
    
    // 更新收藏夹
    public boolean updateFolder(FavoriteFolder folder) {
        // 检查收藏夹是否存在
        FavoriteFolder existingFolder = folderDao.getFolderById(folder.getId());
        if (existingFolder == null) {
            return false;
        }
        
        // 如果名称改变，检查新名称是否已存在
        if (!existingFolder.getFolderName().equals(folder.getFolderName()) &&
            folderDao.isFolderNameExist(folder.getFolderName(), folder.getUserId())) {
            return false;
        }
        
        return folderDao.updateFolder(folder);
    }
    
    // 获取用户的所有收藏夹
    public List<FavoriteFolder> getUserFolders(int userId) {
        return folderDao.getFoldersByUserId(userId);
    }
    
    // 获取收藏夹详情
    public FavoriteFolder getFolderById(int id) {
        return folderDao.getFolderById(id);
    }
    
    // 检查收藏夹名称是否可用
    public boolean isFolderNameAvailable(String folderName, int userId) {
        return !folderDao.isFolderNameExist(folderName, userId);
    }
}