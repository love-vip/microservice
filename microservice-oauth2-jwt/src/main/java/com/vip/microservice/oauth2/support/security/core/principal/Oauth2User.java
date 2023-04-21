package com.vip.microservice.oauth2.support.security.core.principal;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author echo
 * @title: Oauth2User
 * @date 2023/3/15 15:15
 */
public class Oauth2User extends User implements OAuth2AuthenticatedPrincipal {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    /**
     * 用户ID
     */
    @Getter
    private final Long id;

    /**
     * 手机号
     */
    @Getter
    private final String mobile;

    /**
     * 邮箱
     */
    @Getter
    private final String email;

    /**
     * 真实姓名
     */
    @Getter
    private final String realName;

    /**
     * 是否初始用户
     */
    @Getter
    private final boolean initial;

    /**
     * 是否已绑定谷歌校验器
     */
    @Getter
    private final boolean bind;

    /**
     * 谷歌验证二维码地址
     */
    @Getter
    private final String qrCodeUrl;

    public Oauth2User(Long id, String username, String password, String mobile, String email, String realName, boolean initial,
            boolean enabled, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, true, true, accountNonLocked, authorities);
        this.id = id;
        this.mobile = mobile;
        this.email = email;
        this.realName = realName;
        this.initial = initial;
        this.bind = true;
        this.qrCodeUrl = "";
    }

    public Oauth2User(Long id, String username, String password, String mobile, String email, String realName, boolean initial, String qrCodeUrl,
                      boolean enabled, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, true, true, accountNonLocked, authorities);
        this.id = id;
        this.mobile = mobile;
        this.email = email;
        this.realName = realName;
        this.initial = initial;
        this.bind = false;
        this.qrCodeUrl = qrCodeUrl;
    }

    /**
     * Get the OAuth 2.0 token attributes
     * @return the OAuth 2.0 token attributes
     */
    @Override
    public Map<String, Object> getAttributes() {
        return new HashMap<>();
    }

    @Override
    public String getName() {
        return this.getUsername();
    }

}