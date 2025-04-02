package edu.scut.tongzhou.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.scut.tongzhou.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author DS
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-03-20 16:31:39
*/
public interface UserService extends IService<User> {
    long userRegister(String userAccount, String userPassword, String confirmPassword);

    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    int userLogout(HttpServletRequest request);

    List<User> searchUsers(String username, HttpServletRequest request);

    List<User> searchUserByTagsUsingSQL(List<String> tagList);

    List<User> searchUserByTagsUsingMemory(List<String> tagList);

    User getSafetyUser(User originUser);

    User getCurrentUser(HttpServletRequest request);

    boolean updateUser(User user, HttpServletRequest request);

    boolean deleteUser(Long id, HttpServletRequest request);

    boolean isNotAdmin(HttpServletRequest request);

    Page<User> homePageUsers(long pageNum, long pageSize);
}
