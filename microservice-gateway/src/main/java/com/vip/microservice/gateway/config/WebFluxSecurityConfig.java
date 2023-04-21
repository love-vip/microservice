package com.vip.microservice.gateway.config;

import com.vip.microservice.gateway.handler.Oauth2AuthenticationEntryPoint;
import com.vip.microservice.gateway.handler.Oauth2ServerAccessDeniedHandler;
import com.vip.microservice.gateway.manager.Oauth2AuthorizationManager;
import com.vip.microservice.gateway.config.IgnoreUrlsConfiguration.IgnoreUrlsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * @author echo
 * @version 1.0
 * @date 2023/4/1 18:28
 */
@RefreshScope
@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebFluxSecurityConfig {

    private final IgnoreUrlsConfig ignoreUrlsConfig;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        // opaque处理
        httpSecurity.oauth2ResourceServer().opaqueToken();
        // 自定义处理token请求头过期或签名错误的结果
        httpSecurity.oauth2ResourceServer().authenticationEntryPoint(new Oauth2AuthenticationEntryPoint());
        // 自定义处理token请求头鉴权失败的结果
        httpSecurity.oauth2ResourceServer().accessDeniedHandler(new Oauth2ServerAccessDeniedHandler());
        //AJAX进行跨域请求时的预检,需要向另外一个域名的资源发送一个HTTP OPTIONS请求头,用以判断实际发送的请求是否安全
        httpSecurity.authorizeExchange().pathMatchers(HttpMethod.OPTIONS).permitAll();
        //白名单不拦截
        httpSecurity.authorizeExchange().pathMatchers(ignoreUrlsConfig.getUrls()).permitAll();
        /* 请求拦截处理 */
        httpSecurity.authorizeExchange().anyExchange().access(new Oauth2AuthorizationManager());
        //跨域保护禁用
        httpSecurity.csrf().disable();
        return httpSecurity.build();
    }

}