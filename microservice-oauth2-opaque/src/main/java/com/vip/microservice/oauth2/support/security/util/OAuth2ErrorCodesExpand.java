package com.vip.microservice.oauth2.support.security.util;

/**
 * @author echo
 * @title: OAuth2ErrorCodesExpand
 * @date 2023/3/15 14:44
 */
public interface OAuth2ErrorCodesExpand {

    /** 用户名未找到 */
    String USERNAME_NOT_FOUND = "username_not_found";

    /** 令牌错误*/
    String BAD_ACCESS_TOKEN= "bad_access_token";

    /** 验证码错误 */
    String BAD_CAPTCHA= "bad_captcha";

    /** 错误凭证 */
    String BAD_CREDENTIALS = "bad_credentials";

    /** 用户被锁 */
    String USER_LOCKED = "user_locked";

    /** 用户禁用 */
    String USER_DISABLE = "user_disable";

    /** 用户过期 */
    String USER_EXPIRED = "user_expired";

    /** 证书过期 */
    String CREDENTIALS_EXPIRED = "credentials_expired";

    /** scope 为空异常 */
    String SCOPE_IS_EMPTY = "scope_is_empty";

    /**
     * 令牌不存在
     */
    String TOKEN_MISSING = "token_missing";

    /** 未知的登录异常 */
    String UN_KNOW_LOGIN_ERROR = "un_know_login_error";

}