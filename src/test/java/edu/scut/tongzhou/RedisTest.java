package edu.scut.tongzhou;

import edu.scut.tongzhou.model.entity.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

/**
 * @author DylanS
 * @version 1.0
 */
@SpringBootTest
class RedisTest {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void opsTest() {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("ds1", 111, 60, TimeUnit.SECONDS);
        valueOperations.set("ds2", "ererer", 60, TimeUnit.SECONDS);
        valueOperations.set("ds3", 3.333, 60, TimeUnit.SECONDS);
        valueOperations.set("ds4", new User(), 60, TimeUnit.SECONDS);

        Object o;
        o = valueOperations.get("ds1");
        Assertions.assertEquals(111, o);
        o = valueOperations.get("ds2");
        Assertions.assertEquals("ererer", o);
        o = valueOperations.get("ds3");
        Assertions.assertEquals(3.333, o);
        o = valueOperations.get("ds4");
        System.out.println(o);
    }
}
