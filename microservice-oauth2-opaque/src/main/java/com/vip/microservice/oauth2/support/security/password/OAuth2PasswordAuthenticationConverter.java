package com.vip.microservice.oauth2.support.security.password;

import com.vip.microservice.oauth2.support.security.base.OAuth2BaseAuthenticationConverter;
import com.vip.microservice.oauth2.support.security.util.OAuth2EndpointUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

/**
 * <p>密码认证转换器</p>
 * @author echo
 * @title: OAuth2PasswordAuthenticationConverter
 * @date 2023/3/15 13:48
 */
public class OAuth2PasswordAuthenticationConverter extends OAuth2BaseAuthenticationConverter<OAuth2PasswordAuthenticationToken> {

    /**
     * 支持密码模式
     * @param grantType 授权类型
     */
    @Override
    public boolean support(String grantType) {
        return AuthorizationGrantType.PASSWORD.getValue().equals(grantType);
    }

    @Override
    public OAuth2PasswordAuthenticationToken buildToken(Authentication clientPrincipal, Set<String> requestedScopes, Map<String, Object> additionalParameters) {
        return new OAuth2PasswordAuthenticationToken(AuthorizationGrantType.PASSWORD, clientPrincipal, requestedScopes, additionalParameters);
    }

    /**
     * 校验扩展参数 密码模式密码必须不为空
     * @param request 参数列表
     */
    @Override
    public void checkParams(HttpServletRequest request) {
        MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);
        // username (REQUIRED)
        String username = parameters.getFirst(OAuth2ParameterNames.USERNAME);
        if (!StringUtils.hasText(username) || parameters.get(OAuth2ParameterNames.USERNAME).size() != 1) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.USERNAME, ACCESS_TOKEN_REQUEST_ERROR_URI));
        }

        // password (REQUIRED)
        String password = parameters.getFirst(OAuth2ParameterNames.PASSWORD);
        if (!StringUtils.hasText(password) || parameters.get(OAuth2ParameterNames.PASSWORD).size() != 1) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.PASSWORD, ACCESS_TOKEN_REQUEST_ERROR_URI));
        }
    }

}
