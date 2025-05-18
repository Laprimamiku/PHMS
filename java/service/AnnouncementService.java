package service;

import dao.AnnouncementDao;
import model.Announcement;

import java.util.List;

public class AnnouncementService {
    private AnnouncementDao announcementDao = new AnnouncementDao();

    // 添加公告
    public boolean addAnnouncement(Announcement announcement) {
        return announcementDao.addAnnouncement(announcement);
    }

    // 删除公告
    public boolean deleteAnnouncement(int id) {
        return announcementDao.deleteAnnouncement(id);
    }

    // 更新公告
    public boolean updateAnnouncement(Announcement announcement) {
        return announcementDao.updateAnnouncement(announcement);
    }

    // 获取所有公告
    public List<Announcement> getAllAnnouncements() {
        return announcementDao.getAllAnnouncements();
    }

    // 根据ID获取公告
    public Announcement getAnnouncementById(int id) {
        return announcementDao.getAnnouncementById(id);
    }
}
