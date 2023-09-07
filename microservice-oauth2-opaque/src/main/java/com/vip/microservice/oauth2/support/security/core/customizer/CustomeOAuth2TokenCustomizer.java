package com.vip.microservice.oauth2.support.security.core.customizer;

import com.vip.microservice.oauth2.support.security.core.constant.SecurityConstants;
import com.vip.microservice.oauth2.support.security.core.principal.Oauth2User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsSet;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.stream.Collectors;

/**
 * <p>token 输出增强</p>
 * @author echo
 * @title: CustomeOAuth2TokenCustomizer
 * @date 2023/3/15 15:44
 */
public class CustomeOAuth2TokenCustomizer implements OAuth2TokenCustomizer<OAuth2TokenClaimsContext> {

    /**
     * Customize the OAuth 2.0 Token attributes.
     * @param context the context containing the OAuth 2.0 Token attributes
     */
    @Override
    public void customize(OAuth2TokenClaimsContext context) {
        OAuth2TokenClaimsSet.Builder claims = context.getClaims();

        claims.claim(OAuth2ParameterNames.GRANT_TYPE, context.getAuthorizationGrantType());
        claims.claim(OAuth2ParameterNames.CLIENT_ID, context.getAuthorizationGrant().getName());

        // 客户端模式不返回具体用户信息
        if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(context.getAuthorizationGrantType().getValue())) {
            return;
        }

        Oauth2User oauth2User = (Oauth2User) context.getPrincipal().getPrincipal();
        claims.claim(SecurityConstants.DETAILS_USER, oauth2User);

        /*
         * <p>authorities的数据来源于claims里的scope</p>
         * @see org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector#convertClaimsSet
         */
        String scope = oauth2User.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));
        claims.claim(OAuth2ParameterNames.SCOPE, scope);
    }

}