package com.vip.microservice.gateway.config;

import com.vip.microservice.gateway.handler.Oauth2AuthenticationEntryPoint;
import com.vip.microservice.gateway.handler.Oauth2ServerAccessDeniedHandler;
import com.vip.microservice.gateway.manager.Oauth2AuthorizationManager;
import com.vip.microservice.gateway.config.IgnoreUrlsConfiguration.IgnoreUrlsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

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
        // jwt处理
        httpSecurity.oauth2ResourceServer(customizer -> customizer.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));;
        // 自定义处理token请求头过期或签名错误的结果
        httpSecurity.oauth2ResourceServer(customizer -> customizer.authenticationEntryPoint(new Oauth2AuthenticationEntryPoint()));
        // 自定义处理token请求头鉴权失败的结果
        httpSecurity.oauth2ResourceServer(customizer -> customizer.accessDeniedHandler(new Oauth2ServerAccessDeniedHandler()));
        //AJAX进行跨域请求时的预检,需要向另外一个域名的资源发送一个HTTP OPTIONS请求头,用以判断实际发送的请求是否安全
        httpSecurity.authorizeExchange(customizer -> customizer.pathMatchers(HttpMethod.OPTIONS).permitAll());
        //白名单不拦截
        httpSecurity.authorizeExchange(customizer -> customizer.pathMatchers(ignoreUrlsConfig.getUrls()).permitAll());
        /* 请求拦截处理 */
        httpSecurity.authorizeExchange(customizer -> customizer.anyExchange().access(new Oauth2AuthorizationManager()));
        //跨域保护禁用
        httpSecurity.csrf(ServerHttpSecurity.CsrfSpec::disable);
        return httpSecurity.build();
    }

    /**
     * 从 JWT 的 scope 中获取的权限 取消 SCOPE_ 的前缀
     * 设置从 jwt claim 中那个字段获取权限
     * 如果需要同多个字段中获取权限或者是通过url请求获取的权限，则需要自己提供jwtAuthenticationConverter()这个方法的实现
     */
    private Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        // 去掉 SCOPE_ 的前缀
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        // 从jwt claim 中那个字段获取权限，模式是从 scope 或 scp 字段中获取
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("scope");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}
