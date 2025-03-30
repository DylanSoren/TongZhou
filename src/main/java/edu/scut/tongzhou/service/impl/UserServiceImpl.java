package edu.scut.tongzhou.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.scut.tongzhou.exception.ThrowUtils;
import edu.scut.tongzhou.model.entity.User;
import edu.scut.tongzhou.service.UserService;
import edu.scut.tongzhou.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static edu.scut.tongzhou.common.StatusCode.*;
import static edu.scut.tongzhou.constant.UserConstant.USER_LOGIN_STATE;
import static edu.scut.tongzhou.constant.UserConstant.USER_LOGOUT_SUCCESS;

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
    public Integer userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return USER_LOGOUT_SUCCESS;
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
    public User getCurrectUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute(USER_LOGIN_STATE);
    }

    /**
     * 查询用户
     * @param username 用户名
     * @return 用户列表
     */
    @Override
    public List<User> searchUsers(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (!CharSequenceUtil.hasBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = this.list(queryWrapper);
        return userList.stream().map(this::getSafetyUser).toList();
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteUser(Long id) {
        ThrowUtils.throwIf(id <= 0, PARAMS_ERROR, "用户ID应为正整数");
        return this.removeById(id);
    }
}



