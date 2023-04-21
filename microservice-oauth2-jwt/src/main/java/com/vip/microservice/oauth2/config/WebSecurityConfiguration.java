package com.vip.microservice.oauth2.config;

import com.vip.microservice.oauth2.support.security.core.FormIdentityLoginConfigurer;
import com.vip.microservice.oauth2.support.security.core.UserDetailsAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * <p>服务安全相关配置</p>
 * @author echo
 * @title: WebSecurityConfiguration
 * @date 22023/3/16 14:06
 */
public class WebSecurityConfiguration {

    /**
     * <p>为什么一个项目配置了两个甚至多个 SecurityFilterChain?</p>
     * 之所以有两个 SecurityFilterChain是因为程序设计要保证职责单一，无论是底层架构还是业务代码，
     * 为此 HttpSecurity被以基于原型（prototype）的Spring Bean注入Spring IoC。
     * 针对本应用中的两条过滤器链，分别是授权服务器的过滤器链和应用安全的过滤器链，它们之间其实互相没有太多联系
     * Spring Security 默认的安全策略
     * @param httpSecurity Security注入点
     * @return SecurityFilterChain
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //开放自定义的部分端点
        httpSecurity.authorizeHttpRequests().requestMatchers("/token/*").permitAll();
        //其它任意接口都需认证
        httpSecurity.authorizeHttpRequests().anyRequest().authenticated();
        //避免iframe同源无法登录 & 表单登录个性化
        httpSecurity.headers().frameOptions().sameOrigin().and().apply(new FormIdentityLoginConfigurer());
        // 处理 UsernamePasswordAuthenticationToken
        httpSecurity.authenticationProvider(new UserDetailsAuthenticationProvider());
        return httpSecurity.build();
    }

    /**
     * 暴露静态资源
     * <a href="https://github.com/spring-projects/spring-security/issues/10938">...</a>
     * @param httpSecurity Security注入点
     * @return SecurityFilterChain
     * @throws Exception 异常
     */
    @Bean
    @Order(0)
    public SecurityFilterChain resources(HttpSecurity httpSecurity) throws Exception {
        //开放自定义的部分端点
        httpSecurity.securityMatchers(configurer -> configurer.requestMatchers("/actuator/**", "/css/**", "/error"));
        //其它任意接口都需认证
        httpSecurity.authorizeHttpRequests().anyRequest().authenticated();
        //禁用部分不要的功能
        httpSecurity.requestCache().disable().securityContext().disable().sessionManagement().disable();
        return httpSecurity.build();
    }

}
