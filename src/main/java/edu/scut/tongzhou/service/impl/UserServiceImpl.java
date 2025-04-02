package edu.scut.tongzhou.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.scut.tongzhou.exception.ThrowUtils;
import edu.scut.tongzhou.model.entity.User;
import edu.scut.tongzhou.service.UserService;
import edu.scut.tongzhou.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static edu.scut.tongzhou.common.StatusCode.*;
import static edu.scut.tongzhou.constant.UserConstant.USER_LOGIN_STATE;
import static edu.scut.tongzhou.constant.UserConstant.USER_LOGOUT_SUCCESS;
import static edu.scut.tongzhou.constant.UserRole.ADMIN_ROLE;

/**
* @author DS
* &#064;description  针对表【user(用户)】的数据库操作Service实现
* &#064;createDate  2025-03-20 16:31:39
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    // 盐值，混淆密码
    private static final String SALT = "YSQ";

    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param confirmPassword 确认密码
     * @return 用户ID
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String confirmPassword) {
        // 非空校验
        ThrowUtils.throwIf(CharSequenceUtil.hasBlank(userAccount, userPassword, confirmPassword),
                PARAMS_ERROR, "账号为空");

        // 长度校验
        ThrowUtils.throwIf(userAccount.length() < 4, PARAMS_ERROR, "账号长度不能小于4");
        ThrowUtils.throwIf(userPassword.length() < 8, PARAMS_ERROR, "密码长度不能小于8");

        // 账号不能包含特殊字符
        ThrowUtils.throwIf(ReUtil.isMatch(".*(\\pP|\\pS|\\s+).*", userAccount),
                PARAMS_ERROR, "账号不能包含特殊字符");

        // 密码和确认密码要一致
        ThrowUtils.throwIf(!userPassword.equals(confirmPassword),
                PARAMS_ERROR, "两次输入的密码不一致");

        // 账号不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = this.count(queryWrapper);
        ThrowUtils.throwIf(count > 0, PARAMS_ERROR, "账号已存在");

        // 密码加密
        String securePassword = SecureUtil.md5(SALT + userPassword);

        // 将数据插入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(securePassword);
        boolean saved = this.save(user);
        ThrowUtils.throwIf(!saved, OPERATION_ERROR, "注册失败");

        return user.getId();
    }

    /**
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request HTTP请求
     * @return 脱敏后的用户信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 非空校验
        ThrowUtils.throwIf(CharSequenceUtil.hasBlank(userAccount, userPassword),
                PARAMS_ERROR, "账号为空");

        // 长度校验
        ThrowUtils.throwIf(userAccount.length() < 4, PARAMS_ERROR, "账号长度不能小于4");
        ThrowUtils.throwIf(userPassword.length() < 8, PARAMS_ERROR, "密码长度不能小于8");

        // 账号不能包含特殊字符
        ThrowUtils.throwIf(ReUtil.isMatch(".*(\\pP|\\pS|\\s+).*", userAccount),
                PARAMS_ERROR, "账号不能包含特殊字符");

        // 校验密码是否输入正确
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        // 密码加密
        String securePassword = SecureUtil.md5(SALT + userPassword);
        queryWrapper.eq("user_password", securePassword);
        User user = this.getOne(queryWrapper);
        // 用户不存在
        ThrowUtils.throwIf(user == null, NOT_FOUND_ERROR, "用户不存在");

        User safetyUser = getSafetyUser(user);

        // 记录用户状态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);

        return safetyUser;
    }

    /**
     * 用户注销
     * @param request HTTP请求
     * @return 注销返回信息
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return USER_LOGOUT_SUCCESS;
    }

    /**
     * 查询用户
     * @param username 用户名
     * @return 用户列表(脱敏后)
     */
    @Override
    public List<User> searchUsers(String username, HttpServletRequest request) {
        ThrowUtils.throwIf(isNotAdmin(request), NO_AUTH_ERROR);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (!CharSequenceUtil.hasBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = this.list(queryWrapper);
        return userList.stream().map(this::getSafetyUser).toList();
    }

    /**
     * 根据标签搜索用户(SQL方式)
     * @param tagList 标签列表
     * @return 用户列表(脱敏后)
     */
    @Override
    public List<User> searchUserByTagsUsingSQL(List<String> tagList) {
        ThrowUtils.throwIf(CollUtil.isEmpty(tagList), PARAMS_ERROR, "标签不能为空");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String tag : tagList) {
            queryWrapper.like("tags", tag);
        }
        List<User> userList = this.list(queryWrapper);
        return userList.stream().map(this::getSafetyUser).toList();
    }

    /**
     * 根据标签搜索用户(内存方式)
     * @param tagList 标签列表
     * @return 用户列表(脱敏后)
     */
    @Override
    public List<User> searchUserByTagsUsingMemory(List<String> tagList) {
        ThrowUtils.throwIf(CollUtil.isEmpty(tagList), PARAMS_ERROR, "标签不能为空");
        List<User> userList = this.list();
        Gson gson = new Gson();
        return userList.stream().filter(user -> {
                    String tagsStr = user.getTags();
                    Set<String> tagsOfUser = gson.fromJson(tagsStr, new TypeToken<Set<String>>(){}.getType());
                    if (CollUtil.isEmpty(tagsOfUser)) return false;
                    boolean flag = true;
                    for (String tag : tagList) {
                        flag = flag && tagsOfUser.stream().anyMatch(tU -> ReUtil.isMatch(".*" + tag + ".*", tU));
                    }
                    return flag;
                })
                .map(this::getSafetyUser).toList();
    }

    /**
     * 用户信息脱敏
     * @param originUser 原始用户信息
     * @return 脱敏后的用户信息
     */
    @Override
    public User getSafetyUser(User originUser) {
        User safetyUser = new User();
        safetyUser.setId(originUser.getId())
                .setUsername(originUser.getUsername())
                .setUserAccount(originUser.getUserAccount())
                .setTags(originUser.getTags())
                .setAvatarUrl(originUser.getAvatarUrl())
                .setGender(originUser.getGender())
                .setPhone(originUser.getPhone())
                .setEmail(originUser.getEmail())
                .setUserStatus(originUser.getUserStatus())
                .setUserRole(originUser.getUserRole())
                .setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

    /**
     * 获取当前用户
     * @param request HTTP请求
     * @return 脱敏后的用户信息
     */
    @Override
    public User getCurrentUser(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        ThrowUtils.throwIf(currentUser == null, NOT_FOUND_ERROR, "用户未登录");
        return currentUser;
    }

    @Override
    public boolean updateUser(User user, HttpServletRequest request) {
        Long id = user.getId();
        ThrowUtils.throwIf(id == null, PARAMS_ERROR, "用户ID不能为空");
        ThrowUtils.throwIf(id <= 0, PARAMS_ERROR, "用户ID应为正整数");
        // 不允许更新自己之外用户的信息
        ThrowUtils.throwIf(isNotAdmin(request)
                        && !Objects.equals(user.getId(), getCurrentUser(request).getId()),
                        NO_AUTH_ERROR, "用户无权限");
        // 判断要更新的用户是否存在
        User originUser = this.getById(id);
        ThrowUtils.throwIf(originUser == null, NOT_FOUND_ERROR, "用户不存在");
        // 更新用户信息
        return this.updateById(user);
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteUser(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(isNotAdmin(request), NO_AUTH_ERROR, "用户无权限");
        ThrowUtils.throwIf(id <= 0, PARAMS_ERROR, "用户ID应为正整数");
        return this.removeById(id);
    }

    /**
     * 是否为管理员
     * @param request HTTP请求
     * @return 判断结果
     */
    @Override
    public boolean isNotAdmin(HttpServletRequest request) {
        User user = getCurrentUser(request);
        return user == null || user.getUserRole() != ADMIN_ROLE;
    }

    /**
     * 返回主页信息（实际为部分用户的信息）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 用户列表
     */
    @Override
    public Page<User> homePageUsers(long pageNum, long pageSize) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Page<User> usersPage = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        ThrowUtils.throwIf(usersPage == null, NOT_FOUND_ERROR, "用户不存在");
        usersPage.setRecords(usersPage.getRecords().stream().map(this::getSafetyUser).toList());
        return usersPage;
    }
}



