package com.vip.microservice.oauth2.support.security.core.customizer;

import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OidcConfigurer;

import java.util.HashMap;
import java.util.Map;

public class OidcCustomizer implements Customizer<OidcConfigurer> {

    @Override
    public void customize(OidcConfigurer oidcConfigurer) {
        oidcConfigurer.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userInfoMapper(oidcUserInfoAuthenticationContext -> {
            OAuth2AccessToken accessToken = oidcUserInfoAuthenticationContext.getAccessToken();
            Map<String, Object> claims  = new HashMap<>();
            claims.put("accessToken", accessToken);
            claims.put("sub", oidcUserInfoAuthenticationContext.getAuthorization().getPrincipalName());
            return new OidcUserInfo(claims);
        }));
    }
}
