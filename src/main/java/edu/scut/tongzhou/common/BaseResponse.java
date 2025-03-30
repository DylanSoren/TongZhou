package edu.scut.tongzhou.common;

import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

/**
 * 返回结果包装类
 * @author DylanS
 * @version 1.0
 */
@Data
public class BaseResponse<T> implements Serializable {
    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(StatusCode statusCode) {
        this(statusCode.getCode(), null, statusCode.getMessage());
    }
}
