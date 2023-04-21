package com.vip.microservice.oauth2.support.security.google;

import com.vip.microservice.oauth2.support.security.base.OAuth2BaseAuthenticationProvider;
import com.vip.microservice.oauth2.support.security.core.constant.SecurityConstants;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import java.util.Map;

/**
 * <p>处理access_token、google_code授权</p>
 * @author echo
 * @date 2023/3/14 14:52
 */
@Slf4j
public class OAuth2GoogleAuthenticationProvider extends OAuth2BaseAuthenticationProvider<OAuth2GoogleAuthenticationToken> {

    /**
     * Constructs an {@code OAuth2AuthorizationCodeAuthenticationProvider} using the
     * provided parameters.
     * @param authenticationManager  the authorization manager
     * @param authorizationService the authorization service
     * @param tokenGenerator the token generator
     * @since 0.2.3
     */
    public OAuth2GoogleAuthenticationProvider(AuthenticationManager authenticationManager,
                                              OAuth2AuthorizationService authorizationService,
                                              OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
        super(authenticationManager, authorizationService, tokenGenerator);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2GoogleAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public void checkClient(@NonNull RegisteredClient registeredClient) {
        if (!registeredClient.getAuthorizationGrantTypes().contains(SecurityConstants.GOOGLE)) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(
                            OAuth2ErrorCodes.UNAUTHORIZED_CLIENT, SecurityConstants.GOOGLE.getValue(), ERROR_URI
                    )
            );
        }
    }

    @Override
    public UsernamePasswordAuthenticationToken assemble(Map<String, Object> reqParameters) {
        String accessToken = (String) reqParameters.get(OAuth2ParameterNames.ACCESS_TOKEN);
        String code = (String) reqParameters.get(OAuth2ParameterNames.CODE);
        return new UsernamePasswordAuthenticationToken(accessToken, code);
    }

}