package com.vip.microservice.oauth2.support.security.google;

import com.vip.microservice.oauth2.support.security.base.OAuth2BaseAuthenticationConverter;
import com.vip.microservice.oauth2.support.security.core.constant.SecurityConstants;
import com.vip.microservice.oauth2.support.security.util.OAuth2EndpointUtils;
import org.springframework.security.core.Authentication;
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
 * <p>谷歌认证转换器</p>
 * @author echo
 * @title: OAuth2GoogleAuthenticationConverter
 * @date 2023/3/15 13:48
 */
public class OAuth2GoogleAuthenticationConverter extends OAuth2BaseAuthenticationConverter<OAuth2GoogleAuthenticationToken> {

    /**
     * 支持谷歌验证码模式
     * @param grantType 授权类型
     */
    @Override
    public boolean support(String grantType) {
        return SecurityConstants.GOOGLE.getValue().equals(grantType);
    }

    @Override
    public OAuth2GoogleAuthenticationToken buildToken(Authentication clientPrincipal, Set<String> requestedScopes, Map<String, Object> additionalParameters) {
        return new OAuth2GoogleAuthenticationToken(SecurityConstants.GOOGLE, clientPrincipal, requestedScopes, additionalParameters);
    }

    /**
     * 校验扩展参数 谷歌模式code必须不为空
     * @param request 参数列表
     */
    @Override
    public void checkParams(HttpServletRequest request) {
        MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);
        // access_token (REQUIRED)
        String accessToken = parameters.getFirst(OAuth2ParameterNames.ACCESS_TOKEN);
        if (!StringUtils.hasText(accessToken) || parameters.get(OAuth2ParameterNames.ACCESS_TOKEN).size() != 1) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.ACCESS_TOKEN, ACCESS_TOKEN_REQUEST_ERROR_URI));
        }

        // code (REQUIRED)
        String code = parameters.getFirst(OAuth2ParameterNames.CODE);
        if (!StringUtils.hasText(code) || parameters.get(OAuth2ParameterNames.CODE).size() != 1) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST, OAuth2ParameterNames.CODE, ACCESS_TOKEN_REQUEST_ERROR_URI));
        }
    }

}
