package com.vip.microservice.oauth2.config;

import com.vip.microservice.oauth2.support.security.core.customizer.CustomeOAuth2TokenCustomizer;
import com.vip.microservice.oauth2.support.security.google.OAuth2GoogleAuthenticationConverter;
import com.vip.microservice.oauth2.support.security.google.OAuth2GoogleAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import com.vip.microservice.oauth2.support.security.password.OAuth2PasswordAuthenticationConverter;
import com.vip.microservice.oauth2.support.security.sms.OAuth2SmsAuthenticationConverter;
import com.vip.microservice.oauth2.support.security.core.FormIdentityLoginConfigurer;
import com.vip.microservice.oauth2.support.security.core.UserDetailsAuthenticationProvider;
import com.vip.microservice.oauth2.support.security.core.constant.SecurityConstants;
import com.vip.microservice.oauth2.support.security.handler.AuthenticationFailureEventHandler;
import com.vip.microservice.oauth2.support.security.handler.AuthenticationSuccessEventHandler;
import com.vip.microservice.oauth2.support.security.password.OAuth2PasswordAuthenticationProvider;
import com.vip.microservice.oauth2.support.security.sms.OAuth2SmsAuthenticationProvider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.oauth2.server.authorization.web.authentication.*;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;

/**
 * <p>认证服务器配置 </p>
 * @author echo
 * @title: AuthorizationServerConfiguration
 * @date 2023/3/16 13:55
 */
@EnableWebSecurity
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@Import({OAuth2AuthorizationConfiguration.class, WebSecurityConfiguration.class, SecurityMessageSourceConfiguration.class})
public class AuthorizationServerConfiguration {

    private final OAuth2AuthorizationService authorizationService;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain serverSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(httpSecurity);
        OAuth2AuthorizationServerConfigurer configurer = httpSecurity.getConfigurer(OAuth2AuthorizationServerConfigurer.class);
        // @formatter:off 拦截 授权服务器相关的请求端点
        configurer.tokenEndpoint(
                // 个性化认证授权端点
                (tokenEndpoint) -> {
                    // 注入自定义的授权认证Converter
                    tokenEndpoint.accessTokenRequestConverter(accessTokenRequestConverter());
                    // 登录成功处理器
                    tokenEndpoint.accessTokenResponseHandler(new AuthenticationSuccessEventHandler());
                    // 登录失败处理器
                    tokenEndpoint.errorResponseHandler(new AuthenticationFailureEventHandler());
                }
         );
        // 个性化客户端认证-处理客户端认证异常
        configurer.clientAuthentication(oAuth2ClientAuthenticationConfigurer -> oAuth2ClientAuthenticationConfigurer.errorResponseHandler(new AuthenticationFailureEventHandler()));
        // 授权码端点个性化confirm页面
        configurer.authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint.consentPage(SecurityConstants.CUSTOM_CONSENT_PAGE_URI));
        // redis存储token的实现
        configurer.authorizationService(authorizationService);
        // 服务端点
        configurer.authorizationServerSettings(AuthorizationServerSettings.builder().build());
        httpSecurity.apply(configurer);

        // TODO 你可以根据需求对authorizationServerConfigurer进行一些个性化配置
        RequestMatcher endpointsMatcher = configurer.getEndpointsMatcher();

        // 授权码登录的登录页个性化
        DefaultSecurityFilterChain securityFilterChain = httpSecurity.securityMatcher(endpointsMatcher).apply(new FormIdentityLoginConfigurer()).and().build();
        // @formatter:on

        /* 注入自定义授权模式实现  */
        addCustomOAuth2GrantAuthenticationProvider(httpSecurity);

        return securityFilterChain;
    }

    /**
     * request -> xToken 注入请求转换器
     * webflux
     * @see org.springframework.security.web.server.authentication.ServerAuthenticationConverter
     * @return DelegatingAuthenticationConverter
     */
    private AuthenticationConverter accessTokenRequestConverter() {
        return new DelegatingAuthenticationConverter(Arrays.asList(
                new OAuth2ClientCredentialsAuthenticationConverter(),//client_credentials
                new OAuth2AuthorizationCodeAuthenticationConverter(),//authorization_code
                new OAuth2AuthorizationCodeRequestAuthenticationConverter(),
                new OAuth2RefreshTokenAuthenticationConverter(),//refresh_token
                new OAuth2PasswordAuthenticationConverter(),//password
                new OAuth2GoogleAuthenticationConverter(),//google
                new OAuth2SmsAuthenticationConverter()//sms
                ));
    }

    /**
     * 注入授权模式实现提供方
     * 1. 密码模式 </br>
     * 2. 短信登录 </br>
     */
    @SuppressWarnings("unchecked")
    private void addCustomOAuth2GrantAuthenticationProvider(HttpSecurity httpSecurity) {

        OAuth2TokenGenerator<Jwt> tokenGenerator = httpSecurity.getSharedObject(OAuth2TokenGenerator.class);

        AuthenticationManager authenticationManager = httpSecurity.getSharedObject(AuthenticationManager.class);

        OAuth2AuthorizationService authorizationService = httpSecurity.getSharedObject(OAuth2AuthorizationService.class);

        OAuth2PasswordAuthenticationProvider oauth2PasswordAuthenticationProvider = new OAuth2PasswordAuthenticationProvider(
                authenticationManager, authorizationService, tokenGenerator);

        OAuth2SmsAuthenticationProvider oauth2SmsAuthenticationProvider = new OAuth2SmsAuthenticationProvider(
                authenticationManager, authorizationService, tokenGenerator);

        OAuth2GoogleAuthenticationProvider oauth2GoogleAuthenticationProvider = new OAuth2GoogleAuthenticationProvider(
                authenticationManager, authorizationService, tokenGenerator);

        // 处理 UsernamePasswordAuthenticationToken
        httpSecurity.authenticationProvider(new UserDetailsAuthenticationProvider());

        // 处理 OAuth2PasswordAuthenticationToken
        httpSecurity.authenticationProvider(oauth2PasswordAuthenticationProvider);

        // 处理 OAuth2SmsAuthenticationToken
        httpSecurity.authenticationProvider(oauth2SmsAuthenticationProvider);

        // 处理 OAuth2GoogleAuthenticationToken
        httpSecurity.authenticationProvider(oauth2GoogleAuthenticationProvider);
    }

    /**
     * 令牌生成规则实现 </br>
     * @return OAuth2TokenGenerator
     */
    @Bean
    public OAuth2TokenGenerator<OAuth2Token> oAuth2TokenGenerator() {

        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();

        accessTokenGenerator.setAccessTokenCustomizer(new CustomeOAuth2TokenCustomizer());


        return new DelegatingOAuth2TokenGenerator(accessTokenGenerator, new OAuth2RefreshTokenGenerator());
    }

}