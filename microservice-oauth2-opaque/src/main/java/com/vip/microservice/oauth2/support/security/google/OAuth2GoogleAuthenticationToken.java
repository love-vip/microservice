package com.vip.microservice.oauth2.support.security.google;

import com.vip.microservice.oauth2.support.security.base.OAuth2BaseAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Map;
import java.util.Set;

/**
 * <p>谷歌验证token信息</p>
 * @author echo
 * @title: OAuth2GoogleAuthenticationToken
 * @date 2023/3/15 14:53
 */
public class OAuth2GoogleAuthenticationToken extends OAuth2BaseAuthenticationToken {

    public OAuth2GoogleAuthenticationToken(AuthorizationGrantType authorizationGrantType,
                                           Authentication clientPrincipal,
                                           Set<String> scopes,
                                           Map<String, Object> additionalParameters) {
        super(authorizationGrantType, clientPrincipal, scopes, additionalParameters);
    }

}