package edu.scut.tongzhou.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.scut.tongzhou.exception.ThrowUtils;
import edu.scut.tongzhou.model.entity.User;
import edu.scut.tongzhou.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static edu.scut.tongzhou.common.StatusCode.NOT_FOUND_ERROR;

/**
 * @author DylanS
 * @version 1.0
 */
@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private final List<Long> vipIdList = List.of(1L);

    @Scheduled(cron = "0 0 0 * * *")
    public void doCacheHomePage() {
        for (Long userId : vipIdList) {
            String redisKey = "tongzhou:user:homepageuser:" + userId;
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            Page<User> usersPage = userService.page(new Page<>(1, 8), queryWrapper);
            ThrowUtils.throwIf(usersPage == null, NOT_FOUND_ERROR, "用户不存在");
            usersPage.setRecords(usersPage.getRecords().stream().map(userService::getSafetyUser).toList());
            // 将数据存入redis中
            try {
                valueOperations.set(redisKey, usersPage, 60, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.error("redis set error", e);
            }
        }
    }
}
