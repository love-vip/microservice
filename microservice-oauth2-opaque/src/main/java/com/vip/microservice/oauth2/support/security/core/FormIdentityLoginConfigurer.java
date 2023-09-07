package com.vip.microservice.oauth2.support.security.core;

import com.vip.microservice.oauth2.support.security.handler.FormAuthenticationFailureHandler;
import com.vip.microservice.oauth2.support.security.handler.SsoLogoutSuccessHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * <p>基于授权码模式 统一认证登录 spring security & sas 都可以使用 所以抽取成 HttpConfigurer</p>
 * @author echo
 * @title: FormIdentityLoginConfigurer
 * @date 2023/3/15 13:15
 */
public final class FormIdentityLoginConfigurer extends AbstractHttpConfigurer<FormIdentityLoginConfigurer, HttpSecurity> {

    @Override
    public void init(HttpSecurity http) throws Exception {
        http.formLogin(formLogin -> {
            formLogin.loginPage("/token/login");
            formLogin.loginProcessingUrl("/token/form");
            formLogin.failureHandler(new FormAuthenticationFailureHandler());

        }).logout() // SSO登出成功处理
                .logoutSuccessHandler(new SsoLogoutSuccessHandler()).deleteCookies("JSESSIONID")
                .invalidateHttpSession(true).and().csrf().disable();
    }

}
