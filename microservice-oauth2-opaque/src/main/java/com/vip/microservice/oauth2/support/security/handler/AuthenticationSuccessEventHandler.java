package com.vip.microservice.oauth2.support.security.handler;

import com.vip.microservice.oauth2.support.security.util.OAuth2EndpointUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * @author echo
 * @title: AuthenticationSuccessEventHandler
 * @date 2023/3/15 11:57
 */
@Slf4j
public class AuthenticationSuccessEventHandler implements AuthenticationSuccessHandler {

    private final HttpMessageConverter<OAuth2AccessTokenResponse> accessTokenHttpResponseConverter = new OAuth2AccessTokenResponseHttpMessageConverter();

    /**
     * Called when a user has been successfully authenticated.
     * @param request the request which caused the successful authentication
     * @param response the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     * the authentication process.
     */
    @SneakyThrows
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        log.info("用户：{} 登录成功", authentication.getPrincipal());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 输出token
        sendAccessTokenResponse(response, authentication);
    }

    private void sendAccessTokenResponse(HttpServletResponse response, Authentication authentication) throws IOException {

        OAuth2AccessTokenResponse accessTokenResponse = OAuth2EndpointUtils.sendAccessTokenResponse(authentication);

        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);

        // 无状态 注意删除 context 上下文的信息
        SecurityContextHolder.clearContext();

        this.accessTokenHttpResponseConverter.write(accessTokenResponse, MediaType.APPLICATION_JSON, httpResponse);
    }

}