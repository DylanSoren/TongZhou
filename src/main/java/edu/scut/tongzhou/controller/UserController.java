package edu.scut.tongzhou.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.scut.tongzhou.common.BaseResponse;
import edu.scut.tongzhou.common.ResultUtils;
import edu.scut.tongzhou.exception.ThrowUtils;
import edu.scut.tongzhou.model.entity.User;
import edu.scut.tongzhou.model.request.UserLoginRequest;
import edu.scut.tongzhou.model.request.UserRegisterRequest;
import edu.scut.tongzhou.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static edu.scut.tongzhou.common.StatusCode.*;

/**
 * @author DylanS
 * @version 1.0
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, PARAMS_ERROR);

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String confirmPassword = userRegisterRequest.getConfirmPassword();
        ThrowUtils.throwIf(CharSequenceUtil.hasBlank(userAccount, userPassword, confirmPassword),
                PARAMS_ERROR);

        long id = userService.userRegister(userAccount, userPassword, confirmPassword);
        return ResultUtils.success(id);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, PARAMS_ERROR);
        ThrowUtils.throwIf(request == null, PARAMS_ERROR);

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        ThrowUtils.throwIf(CharSequenceUtil.hasBlank(userAccount, userPassword), PARAMS_ERROR);

        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, PARAMS_ERROR);
        Integer result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, PARAMS_ERROR);
        User currentUser = userService.getCurrentUser(request);
        return ResultUtils.success(currentUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, PARAMS_ERROR);
        List<User> users = userService.searchUsers(username, request);
        return ResultUtils.success(users);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagList) {
        ThrowUtils.throwIf(CollUtil.isEmpty(tagList), PARAMS_ERROR);
        List<User> users = userService.searchUserByTagsUsingSQL(tagList);
        return ResultUtils.success(users);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody User user, HttpServletRequest request) {
        ThrowUtils.throwIf(user == null, PARAMS_ERROR);
        ThrowUtils.throwIf(request == null, PARAMS_ERROR);
        Boolean result = userService.updateUser(user, request);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id == null, PARAMS_ERROR);
        Boolean result = userService.deleteUser(id, request);
        return ResultUtils.success(result);
    }

    @GetMapping("/homePage")
    public BaseResponse<Page<User>> homePageUsers(Long pageNum, Long pageSize) {
        ThrowUtils.throwIf(pageNum == null, PARAMS_ERROR, "pageNum不能为空");
        ThrowUtils.throwIf(pageSize == null, PARAMS_ERROR, "pageSize不能为空");
        ThrowUtils.throwIf(pageNum < 0, PARAMS_ERROR, "pageNum不能小于等于0");
        ThrowUtils.throwIf(pageSize < 0, PARAMS_ERROR, "pageSize不能小于等于0");
        Page<User> usersPage = userService.homePageUsers(pageNum, pageSize);
        return ResultUtils.success(usersPage);
    }
}