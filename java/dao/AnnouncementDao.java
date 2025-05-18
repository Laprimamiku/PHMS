package dao;

import model.Announcement;
import util.JdbcUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDao {
    // 添加公告
    public boolean addAnnouncement(Announcement announcement) {
        String sql = "INSERT INTO tb_announcement (title, content, created_at, created_by) VALUES (?, ?, ?, ?)";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, announcement.getTitle());
            ps.setString(2, announcement.getContent());
            ps.setTimestamp(3, new Timestamp(announcement.getCreatedAt().getTime()));
            ps.setInt(4, announcement.getCreatedBy());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 删除公告
    public boolean deleteAnnouncement(int id) {
        String sql = "DELETE FROM tb_announcement WHERE id = ?";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 更新公告
    public boolean updateAnnouncement(Announcement announcement) {
        String sql = "UPDATE tb_announcement SET title = ?, content = ? WHERE id = ?";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, announcement.getTitle());
            ps.setString(2, announcement.getContent());
            ps.setInt(3, announcement.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 获取所有公告
    public List<Announcement> getAllAnnouncements() {
        String sql = "SELECT * FROM tb_announcement ORDER BY created_at DESC";
        List<Announcement> announcements = new ArrayList<>();
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Announcement announcement = new Announcement();
                announcement.setId(rs.getInt("id"));
                announcement.setTitle(rs.getString("title"));
                announcement.setContent(rs.getString("content"));
                announcement.setCreatedAt(rs.getTimestamp("created_at"));
                announcement.setCreatedBy(rs.getInt("created_by"));
                announcements.add(announcement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return announcements;
    }

    // 根据ID获取公告
    public Announcement getAnnouncementById(int id) {
        String sql = "SELECT * FROM tb_announcement WHERE id = ?";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Announcement announcement = new Announcement();
                    announcement.setId(rs.getInt("id"));
                    announcement.setTitle(rs.getString("title"));
                    announcement.setContent(rs.getString("content"));
                    announcement.setCreatedAt(rs.getTimestamp("created_at"));
                    announcement.setCreatedBy(rs.getInt("created_by"));
                    return announcement;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
