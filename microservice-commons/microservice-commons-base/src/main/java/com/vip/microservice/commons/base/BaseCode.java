package com.vip.microservice.commons.base;

import com.vip.microservice.commons.base.api.ApiCode;
import lombok.RequiredArgsConstructor;

/**
 * @author echo
 * @version 1.0
 * @date 2023/4/19 00:23
 */
@RequiredArgsConstructor
public enum BaseCode implements ApiCode {

    // 统一成功
    SUCCESS(200, "成功!"),

    // 请求错误
    ERROR_PARAMETER(400, "参数有错误!"),
    ERROR_AUTHORIZE(401, "请重新登录!"),
    ERROR_FORBIDDEN(403, "无操作权限!"),
    ERROR_NOTFOUND(404, "资源不存在!"),

    // 系统错误
    SERVER_ERROR(500, "系统异常，请稍后重试!"),
    SERVER_LIMIT(503, "请求频繁，请休息片刻!"),
    SERVER_TIMEOUT(504, "网关超时!"),

    /**
     * 用户账号操作异常
     */
    LOGIN_PASSWORD_ERROR(5000,"账号或密码错误"),

    ACCOUNT_EXPIRED(5001,"账号已过期"),

    ACCOUNT_LOCKED(5002,"账号已被锁定"),

    ACCOUNT_CREDENTIAL_EXPIRED(5003,"账号凭证已过期"),

    ACCOUNT_DISABLE(5004,"账号已被禁用"),

    PERMISSION_DENIED(5005,"账号没有权限"),

    USER_UNAUTHORIZED(5006,"账号未认证"),

    ACCOUNT_NOT_EXIST(5007,"账号不存在"),

    // token 异常
    TOKEN_ERROR(5008,"非法token异常"),

    TOKEN_EXPIRED(5009,"token 过期"),

    ;

    private final int code;
    private final String message;

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}