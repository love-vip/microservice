package com.vip.microservice.oauth2.support.enums;

import com.vip.microservice.commons.base.api.ApiCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * @author echo
 * @version 1.0
 * @date 2023/4/18 20:11
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Oauth2ApiCode implements ApiCode {

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

    CLIENT_NON_NULL(2001, "请求头中client信息为空"),
    FAILED_DECODE_AUTH(2002, "Failed to decode basic authentication token"),
    INVALID_BASIC_AUTH_TOKEN(2003, "Invalid basic authentication token"),

    CN10001(10001, "记录不存在"),

    CN10002(10002, "新输入密码不能和旧密码一致"),

    CN10003(10003, "用户名已存在"),

    CN10004(10004, "手机号已存在"),

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
