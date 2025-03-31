package edu.scut.tongzhou.service.impl;

import cn.hutool.core.lang.Assert;
import edu.scut.tongzhou.model.entity.User;
import edu.scut.tongzhou.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author DylanS
 * @version 1.0
 */
@SpringBootTest
@Slf4j
class UserServiceImplTest {
    @Resource
    private UserService userService;

    @Test
    void testSearchUserByTags() {
        List<String> tagList = List.of("Ja", "C++");
        List<User> users = userService.searchUserByTagsUsingSQL(tagList);
        if (!users.isEmpty()) {
            users.forEach(u -> log.info(u.toString()));
        } else {
            log.info("------------------no user found------------------");
        }
        Assertions.assertNotNull(users);
    }
}