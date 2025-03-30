package edu.scut.tongzhou.common;

import static edu.scut.tongzhou.common.StatusCode.SUCCESS;

/**
 * 返回结果工具类
 * @author DylanS
 * @version 1.0
 */
public class ResultUtils {
    private ResultUtils() {}

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(SUCCESS.getCode(), data, SUCCESS.getMessage());
    }

    public static <T> BaseResponse<T> error(StatusCode statusCode) {
        return new BaseResponse<>(statusCode);
    }

    public static <T> BaseResponse<T> error(StatusCode statusCode, String message) {
        return new BaseResponse<>(statusCode.getCode(), null, message);
    }

    public static <T> BaseResponse<T> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }
}
