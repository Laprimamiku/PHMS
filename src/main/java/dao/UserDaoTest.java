package dao;

import model.User;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class UserDaoTest {

    private UserDao userDao;

    @Before
    public void setUp() {
        userDao = new UserDao();
    }

    @Test  // 表示该方法是一个测试用例
    public void testAddUser_NormalCase() {
        // 创建一个用户对象并设置属性
        User user = new User();
        user.setUsername("addTest");                // 设置用户名
        user.setPassword("123456");                 // 设置密码
        user.setType("user");                       // 设置用户类型
        user.setEmail("add@example.com");           // 设置邮箱

        // 调用 addUser 方法添加用户到数据库
        boolean result = userDao.addUser(user);

        // 断言添加结果为 true，表示添加成功
        assertTrue(result);

        // 根据用户名搜索用户，验证用户是否被正确添加
        List<User> users = userDao.searchUsersByUsername("addTest");

        // 断言返回结果不为空，表示添加成功
        assertFalse(users.isEmpty());

        // 删除测试数据，防止对其他测试产生影响
        userDao.deleteUser(users.get(0).getid());
    }


    @Test
    public void testDeleteUser_ValidId() {
        // 添加一个待删除的测试用户
        User user = new User();
        user.setUsername("deleteTest");           // 设置用户名
        user.setPassword("pass");                 // 设置密码
        user.setType("user");                     // 设置类型
        user.setEmail("del@test.com");            // 设置邮箱
        userDao.addUser(user);                    // 执行添加操作

        // 查询该用户，获取其 id
        List<User> list = userDao.searchUsersByUsername("deleteTest");
        assertFalse(list.isEmpty());              // 确保用户确实存在

        int id = list.get(0).getid();             // 获取该用户的 id

        // 执行删除操作
        boolean deleted = userDao.deleteUser(id);
        assertTrue(deleted);                      // 验证删除是否成功

        // 再次查询该用户，确保已被删除
        assertNull(userDao.getUserById(id));      // 查询结果应为 null
    }


    @Test
    public void testUpdateUser_ValidUser() {
        // 添加一个待更新的测试用户
        User user = new User();
        user.setUsername("updateTest");               // 设置用户名
        user.setPassword("000");                      // 设置初始密码
        user.setType("user");                         // 设置类型
        user.setEmail("before@update.com");           // 设置初始邮箱
        userDao.addUser(user);                        // 添加用户到数据库

        // 查询该用户以获取完整对象
        List<User> list = userDao.searchUsersByUsername("updateTest");
        assertFalse(list.isEmpty());                  // 确保用户存在

        // 修改该用户的属性
        User u = list.get(0);                         // 获取用户对象
        u.setEmail("after@update.com");               // 设置新的邮箱
        u.setPassword("999");                         // 设置新的密码

        // 执行更新操作
        boolean updated = userDao.updateUser(u);
        assertTrue(updated);                          // 验证更新成功

        // 再次查询该用户，确认更新结果
        User updatedUser = userDao.getUserById(u.getid());
        assertEquals("after@update.com", updatedUser.getEmail());  // 验证邮箱更新是否成功

        // 删除测试数据，保持测试环境干净
        userDao.deleteUser(u.getid());
    }
}

