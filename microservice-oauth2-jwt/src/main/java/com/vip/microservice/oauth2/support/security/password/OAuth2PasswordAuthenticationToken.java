package com.vip.microservice.oauth2.support.security.password;

import com.vip.microservice.oauth2.support.security.base.OAuth2BaseAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Map;
import java.util.Set;

/**
 * <p>密码授权token信息</p>
 * @author echo
 * @title: OAuth2PasswordAuthenticationToken
 * @date 2023/3/15 14:53
 */
public class OAuth2PasswordAuthenticationToken extends OAuth2BaseAuthenticationToken {

    public OAuth2PasswordAuthenticationToken(AuthorizationGrantType authorizationGrantType,
                                             Authentication clientPrincipal,
                                             Set<String> scopes,
                                             Map<String, Object> additionalParameters) {
        super(authorizationGrantType, clientPrincipal, scopes, additionalParameters);
    }

}