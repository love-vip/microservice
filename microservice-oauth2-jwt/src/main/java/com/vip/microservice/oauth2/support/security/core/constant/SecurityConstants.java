package com.vip.microservice.oauth2.support.security.core.constant;

import org.springframework.security.oauth2.core.AuthorizationGrantType;

/**
 * @author echo
 * @title: SecurityConstants
 * @date 2023/3/15 13:12
 */
public interface SecurityConstants {

    /**
     * 角色前缀
     */
    String ROLE = "ROLE_";

    /**
     * 手机号登录
     */
    AuthorizationGrantType SMS = new AuthorizationGrantType("sms");

    /**
     * access_token+google_token
     */
    AuthorizationGrantType GOOGLE = new AuthorizationGrantType("google");

    /**
     * {bcrypt} 加密的特征码
     */
    String BCRYPT = "{bcrypt}";

    /**
     * {noop} 加密的特征码
     */
    String NOOP = "{noop}";

    /**
     * 用户信息
     */
    String DETAILS_USER = "user_info";

    /**
     * 验证码有效期,默认 60秒
     */
    long CODE_TIME = 60;

    /**
     * 验证码长度
     */
    String CODE_SIZE = "6";

    /**
     * 短信登录 参数名称
     */
    String SMS_PARAMETER_NAME = "mobile";

    /**
     * 授权码模式confirm
     */
    String CUSTOM_CONSENT_PAGE_URI = "/token/confirm/access";

    /**
     * 删除
     */
    String STATUS_DEL = "1";

    /**
     * 正常
     */
    String STATUS_NORMAL = "0";

    /**
     * 锁定
     */
    String STATUS_LOCK = "9";

    String DEFAULT_ID_SUFFIX = "}";

}
