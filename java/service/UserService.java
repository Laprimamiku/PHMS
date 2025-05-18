package service;

import model.User;
import java.util.List;
import dao.UserDao;

public class UserService {

    private UserDao userDao = new UserDao();

    // 注册用户
    public boolean registerUser(User user) {
        // 确保传递的 User 对象已经包含了 type 和 email
        // 注册时，我们假设默认用户类型为 "user"，如果需要其他类型的默认值可以修改此处
        user.setType("user");  // 设置默认用户类型为 "user"
        return userDao.regUser(user);
    }

    // 用户登录
    public User loginUser(String username, String password) {
        return userDao.logUser(username, password);
    }

    // 添加用户
    public boolean addUser(User user) {
        // 确保传递的 User 对象已经包含了 type 和 email
        // 新增用户时，我们假设默认用户类型为 "user"
        user.setType("user");  // 设置默认用户类型为 "user"
        return userDao.addUser(user);
    }

    // 删除用户
    public boolean deleteUser(int userid) {
        return userDao.deleteUser(userid);
    }

    // 更新用户信息
    public boolean updateUser(User user) {
        // 确保传递的 User 对象已经包含了 type 和 email
        return userDao.updateUser(user);
    }

    // 获取所有用户
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    // 根据用户名模糊查询用户
    public List<User> searchUsersByUsername(String searchQuery) {
        return userDao.searchUsersByUsername(searchQuery);
    }

    // 根据用户ID获取用户
    public User getUserById(int userid) {
        return userDao.getUserById(userid);
    }

    // 检查用户名是否已存在
    public boolean isUsernameExist(String username, int excludeUserId) {
        return userDao.isUsernameExist(username, excludeUserId);
    }
    
    /*// 获取分页用户，排除管理员
    public List<User> getUsersExcludingAdminsByPage(int page, int pageSize) {
        return userDao.getUsersExcludingAdminsByPage(page, pageSize);
    }
    
    // 获取分页用户
    public List<User> getUsersByPage(int page, int pageSize) {
        return userDao.getUsersByPage(page, pageSize);
    }
	
    // 获取总用户数，排除管理员
    public int getUserCount() {
        return userDao.getUserCount();
    }
    */
    
    // 获取分页用户
    public List<User> getUsersByPage(int currentPage,String searchQuery) {
        int pageSize = 10;  // 每页显示10个用户
        int offset = (currentPage - 1) * pageSize;    // 计算数据库查询的偏移量，即从哪条记录开始取数据
        return userDao.getUsersByPage(offset, pageSize,searchQuery);    // 调用UserDao中的方法来获取分页用户列表
    }

    // 获取用户总条数
    public int getTotalPages(String searchQuery) {
        int totalUsers = userDao.getCount(searchQuery);
        return totalUsers;
    }
 }
