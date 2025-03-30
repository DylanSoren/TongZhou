package edu.scut.tongzhou.exception;

import edu.scut.tongzhou.common.StatusCode;

public class ThrowUtils {
    private ThrowUtils() {}

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param runtimeException
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param code
     * @param message
     */
    public static void throwIf(boolean condition, int code, String message) {
        throwIf(condition, new BusinessException(code, message));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param statusCode
     */
    public static void throwIf(boolean condition, StatusCode statusCode) {
        throwIf(condition, new BusinessException(statusCode));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition
     * @param statusCode
     * @param message
     */
    public static void throwIf(boolean condition, StatusCode statusCode, String message) {
        throwIf(condition, new BusinessException(statusCode, message));
    }
}