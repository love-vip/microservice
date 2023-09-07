package com.vip.microservice.oauth2.support.security.base;

import cn.hutool.extra.spring.SpringUtil;
import com.vip.microservice.oauth2.support.security.core.exception.BadAccessTokenException;
import com.vip.microservice.oauth2.support.security.core.exception.BadCaptchaException;
import com.vip.microservice.oauth2.support.security.core.principal.Oauth2User;
import com.vip.microservice.oauth2.support.security.util.OAuth2ErrorCodesExpand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

import static com.vip.microservice.oauth2.support.security.util.OAuth2AuthenticationProviderUtils.getAuthenticatedClientElseThrowInvalidClient;

/**
 * <p>处理自定义授权</p>
 * @author echo
 * @date 2023/3/15 14:43
 */
@Slf4j
public abstract class OAuth2BaseAuthenticationProvider<T extends OAuth2BaseAuthenticationToken> implements AuthenticationProvider {

    public static final String ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1";

    private Supplier<String> refreshTokenGenerator;
    private final MessageSourceAccessor messages;

    private final AuthenticationManager authenticationManager;

    private final OAuth2AuthorizationService authorizationService;

    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    /**
     * Constructs an {@code OAuth2AuthorizationCodeAuthenticationProvider} using the
     * provided parameters.
     * @param authorizationService the authorization service
     * @param tokenGenerator the token generator
     * @since 0.2.3
     */
    public OAuth2BaseAuthenticationProvider(AuthenticationManager authenticationManager,
                                            OAuth2AuthorizationService authorizationService,
                                            OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
        Assert.notNull(authorizationService, "authorizationService cannot be null");
        Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
        this.authenticationManager = authenticationManager;
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;

        // 国际化配置
        this.messages = new MessageSourceAccessor(SpringUtil.getBean("securityMessageSource"), Locale.CHINA);
    }

    @Deprecated
    public void setRefreshTokenGenerator(Supplier<String> refreshTokenGenerator) {
        Assert.notNull(refreshTokenGenerator, "refreshTokenGenerator cannot be null");
        this.refreshTokenGenerator = refreshTokenGenerator;
    }

    /**
     * 封装简易principal
     * @param reqParameters 请求参数
     * @return Principal
     */
    public abstract UsernamePasswordAuthenticationToken assemble(Map<String, Object> reqParameters);

    /**
     * 当前provider是否支持此令牌类型
     * @param authentication 认证
     * @return boolean
     */
    @Override
    public abstract boolean supports(Class<?> authentication);

    /**
     * 当前的请求客户端是否支持此模式
     * @param registeredClient 客户端
     */
    public abstract void checkClient(RegisteredClient registeredClient);

    /**
     * Performs authentication with the same contract as
     * @see org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeAuthenticationProvider#authenticate(Authentication)
     * @see org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientCredentialsAuthenticationProvider#authenticate(Authentication)
     * {@link AuthenticationManager#authenticate(Authentication)} .
     * @param authentication the authentication request object.
     * @throws AuthenticationException if authentication fails.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        OAuth2BaseAuthenticationToken baseAuthentication = (OAuth2BaseAuthenticationToken) authentication;

        /*
         * @see com.vip.microservice.commons.security.converter.OAuth2BaseAuthenticationConverter#convert(HttpServletRequest)
         */
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(baseAuthentication);

        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        checkClient(registeredClient);

        // Default to configured scopes
        Set<String> authorizedScopes = Objects.requireNonNull(registeredClient).getScopes();
        if (!CollectionUtils.isEmpty(baseAuthentication.getScopes())) {
            if(baseAuthentication.getScopes().stream().noneMatch(scope -> registeredClient.getScopes().contains(scope))){
                throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_SCOPE);
            }
            authorizedScopes = new LinkedHashSet<>(baseAuthentication.getScopes());
        }

        Map<String, Object> reqParameters = baseAuthentication.getAdditionalParameters();
        try {
            /* 自行构造UsernamePasswordAuthenticationToken，用来去数据库进行校验 */
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = assemble(reqParameters);

            /*
             * @see org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider#authenticate(Authentication)
             * @return UsernamePasswordAuthenticationToken 更加丰富信息的【principal】
             */
            Authentication principal = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

            // @formatter:off
            DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                    .principal(principal)
                    .registeredClient(registeredClient)
                    .authorizedScopes(authorizedScopes)
                    .authorizationServerContext(AuthorizationServerContextHolder.getContext())
                    .authorizationGrantType(baseAuthentication.getAuthorizationGrantType())
                    .authorizationGrant(baseAuthentication);
            // @formatter:on

            /* ACCESS TOKEN CONTEXT */
            OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();

            // -------------------- Access token start --------------------
            OAuth2Token generatedAccessToken = Optional.ofNullable(this.tokenGenerator.generate(tokenContext)).orElseThrow(() ->
                    new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR, "The token generator failed to generate the access token.", ERROR_URI)));

            OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                    generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
                    generatedAccessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());
            // -------------------- Access token end --------------------

            /*
             * 封装OAuth2Authorization，给OAuth2AuthorizationService保存信息
             * @see org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationProvider#authenticate(Authentication)
             * 两个attribute属性放在这是因为OAuth2RefreshTokenAuthenticationProvider调用authorizationService.findByToken读取用
             */
            OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization
                    .withRegisteredClient(registeredClient).principalName(principal.getName())
                    .id(((Oauth2User)principal.getPrincipal()).getUsername())
                    .authorizationGrantType(baseAuthentication.getAuthorizationGrantType())
                    .authorizedScopes(authorizedScopes)// 0.4.0 新增的方法
                    ;

            /*
             * 如果Generator有扩展信息输出增强
             * @see com.vip.microservice.oauth2.support.core.customizer.CustomeOAuth2JwtTokenCustomizer#customize(JwtEncodingContext)
             * @see org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Builder#token(OAuth2Token)
             */
            if (generatedAccessToken instanceof ClaimAccessor) {
                authorizationBuilder.token(accessToken, (metadata) -> {
                    metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, ((ClaimAccessor) generatedAccessToken).getClaims());
                    //这个其实可要可不要，底层默认会添加，并就是false
                    metadata.put(OAuth2Authorization.Token.INVALIDATED_METADATA_NAME, false);
                })
                .authorizedScopes(authorizedScopes)// 0.4.0 新增的方法
                ;
            }
            else {
                authorizationBuilder.accessToken(accessToken);
            }

            // ----- Refresh token -----
            OAuth2RefreshToken refreshToken = null;
            if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN) && !clientPrincipal.getClientAuthenticationMethod().equals(ClientAuthenticationMethod.NONE)) {
                if (this.refreshTokenGenerator != null) {
                    Instant issuedAt = Instant.now();
                    Instant expiresAt = issuedAt.plus(registeredClient.getTokenSettings().getRefreshTokenTimeToLive());
                    refreshToken = new OAuth2RefreshToken(this.refreshTokenGenerator.get(), issuedAt, expiresAt);
                }
                else {
                    /* REFRESH TOKEN CONTEXT */
                    tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
                    // -------------------- Refresh token start --------------------
                    OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
                    if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
                        throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR, "The token generator failed to generate the refresh token.", ERROR_URI));
                    }
                    refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
                    // -------------------- Refresh token end --------------------
                }
                authorizationBuilder.refreshToken(refreshToken);
            }

            OAuth2Authorization authorization = authorizationBuilder.build();

            this.authorizationService.save(authorization);

            log.debug("returning OAuth2AccessTokenAuthenticationToken");

            return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken,
                    refreshToken, Objects.requireNonNull(authorization.getAccessToken().getClaims()));

        }
        catch (Exception ex) {
            log.error("problem in authenticate", ex);
            throw oAuth2AuthenticationException(authentication, (AuthenticationException) ex);
        }

    }

    /**
     * 登录异常转换为OAuth2AuthenticationException异常，才能被AuthenticationFailureEventHandler处理
     * @param authentication 身份验证
     * @param authenticationException 身份验证异常
     * @return {@link OAuth2AuthenticationException}
     */
    private OAuth2AuthenticationException oAuth2AuthenticationException(Authentication authentication,
                                                                        AuthenticationException authenticationException) {
        if (authenticationException instanceof UsernameNotFoundException) {
            return new OAuth2AuthenticationException(
                    new OAuth2Error(
                            OAuth2ErrorCodesExpand.USERNAME_NOT_FOUND,
                            this.messages.getMessage("JdbcDaoImpl.notFound", new Object[] { authentication.getName() }, "Username {0} not found"),
                            ERROR_URI
                    )

            );
        }
        if (authenticationException instanceof BadAccessTokenException) {
            return new OAuth2AuthenticationException(
                    new OAuth2Error(
                            OAuth2ErrorCodesExpand.BAD_ACCESS_TOKEN, this.messages.getMessage(
                            "AbstractUserDetailsAuthenticationProvider.badAccessToken", "Bad access token"),
                            ERROR_URI
                    )
            );
        }
        if (authenticationException instanceof BadCaptchaException) {
            return new OAuth2AuthenticationException(
                    new OAuth2Error(
                            OAuth2ErrorCodesExpand.BAD_CAPTCHA, this.messages.getMessage(
                            "AbstractUserDetailsAuthenticationProvider.badCaptcha", "Bad captcha"),
                            ERROR_URI
                    )
            );
        }
        if (authenticationException instanceof BadCredentialsException) {
            return new OAuth2AuthenticationException(
                    new OAuth2Error(
                            OAuth2ErrorCodesExpand.BAD_CREDENTIALS,
                            this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"),
                            ERROR_URI
                    )
            );
        }
        if (authenticationException instanceof LockedException) {
            return new OAuth2AuthenticationException(
                    new OAuth2Error(
                            OAuth2ErrorCodesExpand.USER_LOCKED,
                            this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"),
                            ERROR_URI
                    )
            );
        }
        if (authenticationException instanceof DisabledException) {
            return new OAuth2AuthenticationException(
                    new OAuth2Error(
                            OAuth2ErrorCodesExpand.USER_DISABLE,
                            this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"),
                            ERROR_URI
                    )
            );
        }
        if (authenticationException instanceof AccountExpiredException) {
            return new OAuth2AuthenticationException(
                    new OAuth2Error(
                            OAuth2ErrorCodesExpand.USER_EXPIRED,
                            this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"),
                            ERROR_URI
                    )
            );
        }
        if (authenticationException instanceof CredentialsExpiredException) {
            return new OAuth2AuthenticationException(
                    new OAuth2Error(
                            OAuth2ErrorCodesExpand.CREDENTIALS_EXPIRED,
                            this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.credentialsExpired", "User credentials have expired"),
                            ERROR_URI
                    )
            );
        }
        return new OAuth2AuthenticationException(
                new OAuth2Error(OAuth2ErrorCodesExpand.UN_KNOW_LOGIN_ERROR, authenticationException.getMessage(), ERROR_URI)
        );
    }

}