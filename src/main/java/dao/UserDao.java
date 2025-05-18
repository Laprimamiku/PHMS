package dao;

import model.User;
import util.JdbcUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.util.StringUtils;

public class UserDao {
    // 执行数据库更新的通用方法executeUpdate私有，更新、插入或删除,进入预编译的SQL语句中
    private boolean executeUpdate(String sql, Object... params) {
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {   //使用完毕后自动关闭
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);        //传入参数设为预编译占位符
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 执行数据库查询 的通用方法,私有,同上
    private List<User> executeQuery(String sql, Object... params) {
        List<User> users = new ArrayList<>();             //创空列表，存数据库信息
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {         //使用完毕后自动关闭
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);           //传入参数设为预编译占位符
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setid(rs.getInt("id"));  // 修正字段名
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setType(rs.getString("type"));
                    user.setEmail(rs.getString("email"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // 注册
    public boolean regUser(User user) {
        String sql = "INSERT INTO tb_user (username, password, type, email) VALUES (?, ?, ?, ?)";      //预编译占位符
        return executeUpdate(sql, user.getUsername(), user.getPassword(), "user", user.getEmail());
    }

    // 登录
    public User logUser(String username, String password) {
        String sql = "SELECT * FROM tb_user WHERE username = ? AND password = ?";
        List<User> users = executeQuery(sql, username, password);
        return users.isEmpty() ? null : users.get(0);
    }

    // 增加用户
    public boolean addUser(User user) {
        String sql = "INSERT INTO tb_user (username, password, type, email) VALUES (?, ?, ?, ?)";  // 
        return executeUpdate(sql, user.getUsername(), user.getPassword(), user.getType(), user.getEmail());
    }

    // 删除用户
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM tb_user WHERE id = ?";
		return executeUpdate(sql, id);
    }

    // 更新用户
    public boolean updateUser(User user) {
        String sql = "UPDATE tb_user SET username = ?, password = ?, type = ?, email = ? WHERE id = ?";  // 
        return executeUpdate(sql, user.getUsername(), user.getPassword(), user.getType(), user.getEmail(), user.getid());
    }

    // 获取所有用户
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM tb_user";
        return executeQuery(sql);
    }

    // 根据用户名模糊查询用户
    public List<User> searchUsersByUsername(String searchQuery) {
        String sql = "SELECT * FROM tb_user WHERE username LIKE ?";
        return executeQuery(sql, "%" + searchQuery + "%");
    }

    // 根据用户ID获取用户
    public User getUserById(int id) {
        String sql = "SELECT * FROM tb_user WHERE id = ?";
        List<User> users = executeQuery(sql, id);
        return users.isEmpty() ? null : users.get(0);
    }
    
    // 检查用户名是否已存在（除了当前用户）
    public boolean isUsernameExist(String username, int excludeUserId) {
        String sql = "SELECT * FROM tb_user WHERE username = ? AND id != ?";
        List<User> users = executeQuery(sql, username, excludeUserId);
        return !users.isEmpty();
    }

   /* // 获取排除管理员的分页数据
    public List<User> getUsersExcludingAdminsByPage(int page, int pageSize) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM tb_user WHERE type != 'admin' LIMIT ? OFFSET ?";

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pageSize);  // 每页显示的记录数
            ps.setInt(2, (page - 1) * pageSize);  // 计算偏移量
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setid(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setType(rs.getString("type"));
                    user.setEmail(rs.getString("email"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // 获取总用户数，排除管理员
    public int getUserCount() {
        String sql = "SELECT COUNT(*) FROM tb_user WHERE type != 'admin'";  // 确保与数据库一致
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
    }*/
    
    
    
    //根据搜索查询获取用户数量
    public int getCount(String searchQuery) {
        String sql = "SELECT COUNT(*) FROM tb_user ";  // 选择所有记录的数量
        if (!StringUtils.isNullOrEmpty(searchQuery)) {         //检查是否为空或null
        	sql+="where username LIKE '%"+searchQuery+"%'";    //只计算那些用户名包含搜索查询的用户
		}
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
    //分页获取用户列表，并根据搜索查询进行筛选
	public List<User> getUsersByPage(int offset, int pageSize,String searchQuery) {//offset（分页起始位置），pageSize（每页大小）
		List<User> users = new ArrayList<>();                    //以及 searchQuery（用来在用户名字段中进行模糊匹配的搜索关键字）
        String sql = "SELECT * FROM tb_user ";
        if (!StringUtils.isNullOrEmpty(searchQuery)) {         //检查是否为空或null
        	sql+="where username LIKE '%"+searchQuery+"%'";    //where过滤,只计算那些用户名包含搜索查询的用户
		}
        sql+=" LIMIT ?,?";//添加分页限制
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset); // 设置分页起始位置
            ps.setInt(2, pageSize); // 设置每页大小
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setid(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setType(rs.getString("type"));
                    user.setEmail(rs.getString("email"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
	}
}
