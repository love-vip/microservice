package com.vip.microservice.oauth2.support.security.handler;

import com.vip.microservice.commons.base.wrapper.WrapMapper;
import com.vip.microservice.oauth2.support.security.core.constant.SecurityConstants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author echo
 * @title: AuthenticationFailureEventHandler
 * @date 2023/3/15 12:00
 */
@Slf4j
public class AuthenticationFailureEventHandler implements AuthenticationFailureHandler {

    private final MappingJackson2HttpMessageConverter errorHttpResponseConverter = new MappingJackson2HttpMessageConverter();

    /**
     * Called when an authentication attempt fails.
     * @param request the request during which the authentication attempt occurred.
     * @param response the response.
     * @param exception the exception which was thrown to reject the authentication
     * request.
     */
    @Override
    @SneakyThrows
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {

        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);

        String user = request.getParameter(SecurityConstants.SMS_PARAMETER_NAME);

        if(AuthorizationGrantType.PASSWORD.getValue().equals(grantType)){
            user = request.getParameter(OAuth2ParameterNames.USERNAME);
        }

        if(AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(grantType)){
            user = request.getParameter(OAuth2ParameterNames.CODE);
        }

        if(SecurityConstants.GOOGLE.getValue().equals(grantType)){
            user = request.getParameter(OAuth2ParameterNames.ACCESS_TOKEN);
        }

        log.info("用户：{} 登录失败，异常：{}", user, exception.getLocalizedMessage());

        // 写出错误信息
        sendErrorResponse(request, response, exception);
    }

    private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        OAuth2Error error = ((OAuth2AuthenticationException) exception).getError();

        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);

        httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);

        this.errorHttpResponseConverter.write(WrapMapper.fail(error.getDescription()), MediaType.APPLICATION_JSON, httpResponse);
    }

}
