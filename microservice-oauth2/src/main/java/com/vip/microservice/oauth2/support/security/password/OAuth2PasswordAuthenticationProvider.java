package com.vip.microservice.oauth2.support.security.password;

import com.vip.microservice.oauth2.support.security.base.OAuth2BaseAuthenticationProvider;
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
 * <p>处理用户名密码授权</p>
 * @author echo
 * @date 2023/3/14 14:52
 */
@Slf4j
public class OAuth2PasswordAuthenticationProvider extends OAuth2BaseAuthenticationProvider<OAuth2PasswordAuthenticationToken> {

    /**
     * Constructs an {@code OAuth2AuthorizationCodeAuthenticationProvider} using the
     * provided parameters.
     * @param authenticationManager  the authorization manager
     * @param authorizationService the authorization service
     * @param tokenGenerator the token generator
     * @since 0.2.3
     */
    public OAuth2PasswordAuthenticationProvider(AuthenticationManager authenticationManager,
                                                OAuth2AuthorizationService authorizationService,
                                                OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
        super(authenticationManager, authorizationService, tokenGenerator);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2PasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public void checkClient(@NonNull RegisteredClient registeredClient) {
        if (!registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.PASSWORD)) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(
                            OAuth2ErrorCodes.UNAUTHORIZED_CLIENT,
                            AuthorizationGrantType.PASSWORD.getValue(),
                            ERROR_URI
                    )
            );
        }
    }

    @Override
    public UsernamePasswordAuthenticationToken assemble(Map<String, Object> reqParameters) {
        String username = (String) reqParameters.get(OAuth2ParameterNames.USERNAME);
        String password = (String) reqParameters.get(OAuth2ParameterNames.PASSWORD);
        return new UsernamePasswordAuthenticationToken(username, password);
    }

}