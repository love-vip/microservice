package com.vip.microservice.oauth2.config;

import com.vip.microservice.oauth2.support.security.core.constant.SecurityConstants;
import com.vip.microservice.oauth2.support.security.core.service.RedisOAuth2AuthorizationConsentService;
import com.vip.microservice.oauth2.support.security.core.service.RedisOAuth2AuthorizationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.util.Arrays;

public class OAuth2AuthorizationConfiguration {

    /**
     * BCrypt密码编码
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        RegisteredClient registeredClient = RegisteredClient.withId("client")
                /* 客户端ID和密码 */
                .clientId("client")
                .clientSecret(passwordEncoder.encode("secret"))
                /* 授权方法 */
                .clientAuthenticationMethods(AuthenticationMethods ->
                        AuthenticationMethods.addAll(Arrays.asList(
                                        ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
                                        ClientAuthenticationMethod.CLIENT_SECRET_POST
                                )
                        )
                )
                /* 授权类型 */
                .authorizationGrantTypes(authorizationGrantTypes ->
                        authorizationGrantTypes.addAll(Arrays.asList(
                                        // 授权码
                                        AuthorizationGrantType.AUTHORIZATION_CODE,
                                        // 刷新token
                                        AuthorizationGrantType.REFRESH_TOKEN,
                                        // 客户端模式
                                        AuthorizationGrantType.CLIENT_CREDENTIALS,
                                        // 密码模式
                                        AuthorizationGrantType.PASSWORD,
                                        //谷歌验证模式
                                        SecurityConstants.GOOGLE,
                                        //短信模式
                                        SecurityConstants.SMS
                                )
                        )
                )
                // 重定向url
                .redirectUris(redirectUris ->
                        redirectUris.add("https://www.baidu.com")
                )
                // 客户端申请的作用域，也可以理解这个客户端申请访问用户的哪些信息
                .scope(OidcScopes.PROFILE)
                /*.scopes(scopes -> scopes.addAll(Arrays.asList(OidcScopes.OPENID, OidcScopes.PROFILE)))*/
                .tokenSettings(TokenSettings.builder()
                        .accessTokenFormat(OAuth2TokenFormat.REFERENCE)//REFERENCE
                        // accessToken 的有效期
                        .accessTokenTimeToLive(Duration.ofHours(12L))
                        // refreshToken 的有效期
                        .refreshTokenTimeToLive(Duration.ofHours(12L))
                        // 是否可重用刷新令牌
                        .reuseRefreshTokens(true)
                        .build()
                )
                // 是否需要用户确认一下客户端需要获取用户的哪些权限
                // 比如：客户端需要获取用户的 用户信息、用户照片
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .build()
                )
                .build();

        /* 每次都会初始化 生产的话 只初始化 JdbcRegisteredClientRepository */
        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);

        registeredClientRepository.save(registeredClient);

        return registeredClientRepository;
    }

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    public OAuth2AuthorizationService redisOAuth2AuthorizationService(RedisTemplate<String, Object> redisTemplate){
        return new RedisOAuth2AuthorizationService(redisTemplate);
    }

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    public OAuth2AuthorizationConsentService redisOAuth2AuthorizationConsentService(RedisTemplate<String, Object> redisTemplate){
        return new RedisOAuth2AuthorizationConsentService(redisTemplate);
    }

//    @Bean
//    public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
//        JdbcOAuth2AuthorizationService service = new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
//        JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper rowMapper =
//                new JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper(registeredClientRepository);
//        JdbcOAuth2AuthorizationService.OAuth2AuthorizationParametersMapper oAuth2AuthorizationParametersMapper =
//                new JdbcOAuth2AuthorizationService.OAuth2AuthorizationParametersMapper();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        ClassLoader classLoader = JdbcOAuth2AuthorizationService.class.getClassLoader();
//        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
//        objectMapper.registerModules(securityModules);
//        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
//        objectMapper.addMixIn(Oauth2User.class, Oauth2UserMixin.class);
//
//        rowMapper.setObjectMapper(objectMapper);
//        oAuth2AuthorizationParametersMapper.setObjectMapper(objectMapper);
//
//        service.setAuthorizationRowMapper(rowMapper);
//        service.setAuthorizationParametersMapper(oAuth2AuthorizationParametersMapper);
//
//        return service;
//    }
//    @Bean
//    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
//        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
//    }

}
