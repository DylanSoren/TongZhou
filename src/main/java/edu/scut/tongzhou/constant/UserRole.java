package edu.scut.tongzhou.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author DylanS
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum UserRole {
    DEFAULT_ROLE(0, "普通用户"),
    ADMIN_ROLE(1, "管理员");

    /**
     * 权限值
     */
    @EnumValue
    private final int value;
    /**
     * 权限描述
     */
    @JsonValue
    private final String desc;
}
