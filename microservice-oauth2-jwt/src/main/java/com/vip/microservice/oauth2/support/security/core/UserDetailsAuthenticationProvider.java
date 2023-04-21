package com.vip.microservice.oauth2.support.security.core;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.vip.microservice.commons.core.utils.RedisKeyUtil;
import com.vip.microservice.commons.core.utils.WebUtils;
import com.vip.microservice.commons.redis.handler.RedisHandler;
import com.vip.microservice.oauth2.model.domain.RbacUser;
import com.vip.microservice.oauth2.service.RbacUserService;
import com.vip.microservice.oauth2.support.enums.Oauth2ApiCode;
import com.vip.microservice.oauth2.support.exception.Oauth2BizException;
import com.vip.microservice.oauth2.support.security.core.constant.SecurityConstants;
import com.vip.microservice.oauth2.support.security.core.exception.BadAccessTokenException;
import com.vip.microservice.oauth2.support.security.core.exception.BadCaptchaException;
import com.vip.microservice.oauth2.support.security.core.service.Oauth2UserDetailsService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter;
import org.springframework.util.Assert;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author echo
 * @title: UserDetailsAuthenticationProvider
 * @date 2023/3/15 15:32
 */
public class UserDetailsAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    /**
     * The plaintext password used to perform PasswordEncoder#matches(CharSequence, String)
     * on when the user is not found to avoid SEC-2056.
     */
    private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";

    private static final Integer ALLOW_LOGIN_ERROR_TIMES = 5;

    private static final Integer ALLOW_VERIFY_ERROR_TIMES = 3;

    private final static BasicAuthenticationConverter basicConvert = new BasicAuthenticationConverter();

    @Getter
    private PasswordEncoder passwordEncoder;

    /**
     * The password used to perform {@link PasswordEncoder#matches(CharSequence, String)}
     * on when the user is not found to avoid SEC-2056. This is necessary, because some
     * {@link PasswordEncoder} implementations will short circuit if the password is not
     * in a valid format.
     */
    private volatile String userNotFoundEncodedPassword;

    @Getter
    @Setter
    private UserDetailsService userDetailsService;

    @Setter
    private UserDetailsPasswordService userDetailsPasswordService;

    public UserDetailsAuthenticationProvider() {
        setMessageSource(SpringUtil.getBean("securityMessageSource"));
        setPasswordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
    }
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        String grantType = WebUtils.getRequest().get().getParameter(OAuth2ParameterNames.GRANT_TYPE);

        if (authentication.getCredentials() == null) {
            this.logger.debug("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException(
                    this.messages.getMessage(
                            "AbstractUserDetailsAuthenticationProvider.badCredentials",
                            "Bad credentials"
                    )
            );
        }

        if (StrUtil.equals(SecurityConstants.SMS.getValue(), grantType)) {
            String code = authentication.getCredentials().toString();
            if(!StrUtil.equals(code, "123456")){
                this.logger.debug("Failed to authenticate since code does not match stored value");
                throw new BadCaptchaException(
                        this.messages.getMessage(
                                "AbstractUserDetailsAuthenticationProvider.badCaptcha",
                                "Bad captcha"
                        )
                );
            }
        }

        if (StrUtil.equals(SecurityConstants.GOOGLE.getValue(), grantType)) {
            String username = userDetails.getUsername();
            String code = authentication.getCredentials().toString();
            GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
            RbacUserService userService = SpringUtil.getBean(RbacUserService.class);
            RbacUser user = userService.getByUsername(username).orElseThrow(() -> new Oauth2BizException(Oauth2ApiCode.CN10001));
            boolean isCodeValid = googleAuthenticator.authorize(user.getSecret(), Integer.parseInt(code));
            RedisHandler redisHandler = SpringUtil.getBean(RedisHandler.class);
            String compositeKey = RedisKeyUtil.getOauth2VerifyErrorTimes(authentication.getName(), authentication.getPrincipal().toString());
            //谷歌验证码校验失败
            if(!isCodeValid){
                Long errorTimes = redisHandler.getStringRedisTemplate().opsForValue().increment(compositeKey);
                //如果连续第4次输入错误验证码则锁定账户
                if(Objects.requireNonNull(errorTimes).intValue() > ALLOW_VERIFY_ERROR_TIMES){
                    userService.locked(authentication.getPrincipal().toString());
                }
                this.logger.debug("Failed to authenticate since code does not match stored value");
                throw new BadCaptchaException(
                        this.messages.getMessage(
                                "AbstractUserDetailsAuthenticationProvider.badCaptcha",
                                "Bad captcha"
                        )
                );
            }
            //正确验证，清除验证错误次数
            redisHandler.getStringRedisTemplate().delete(compositeKey);
        }

        if (StrUtil.equals(AuthorizationGrantType.PASSWORD.getValue(), grantType)) {
            String presentedPassword = authentication.getCredentials().toString();
            String encodedPassword = extractEncodedPassword(userDetails.getPassword());
            RedisHandler redisHandler = SpringUtil.getBean(RedisHandler.class);
            String compositeKey = RedisKeyUtil.getOauth2LoginErrorTimes(authentication.getName(), authentication.getPrincipal().toString());
            if (!this.passwordEncoder.matches(presentedPassword, encodedPassword)) {
                Long errorTimes = redisHandler.getStringRedisTemplate().opsForValue().increment(compositeKey);
                //如果连续第6次输入错误密码则锁定账户
                if(Objects.requireNonNull(errorTimes).intValue() > ALLOW_LOGIN_ERROR_TIMES){
                    RbacUserService rbacUserService = SpringUtil.getBean(RbacUserService.class);
                    rbacUserService.locked(authentication.getPrincipal().toString());
                }
                this.logger.debug("Failed to authenticate since password does not match stored value");
                throw new BadCredentialsException(
                        this.messages.getMessage(
                                "AbstractUserDetailsAuthenticationProvider.badCredentials",
                                "Bad credentials"
                        )
                );
            }
            //正确登录，清除登录错误次数
            redisHandler.getStringRedisTemplate().delete(compositeKey);
        }
    }

    @SneakyThrows
    @Override
    protected final UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) {
        prepareTimingAttackProtection();
        HttpServletRequest request = WebUtils.getRequest().orElseThrow(
                (Supplier<Throwable>) () -> new InternalAuthenticationServiceException("web request is empty"));

        Map<String, String> paramMap = JakartaServletUtil.getParamMap(request);
        String grantType = paramMap.get(OAuth2ParameterNames.GRANT_TYPE);
        String clientId = paramMap.get(OAuth2ParameterNames.CLIENT_ID);

        if (StrUtil.isBlank(clientId)) {
            clientId = basicConvert.convert(request).getName();
        }

        Map<String, Oauth2UserDetailsService> userDetailsServiceMap = SpringUtil.getBeansOfType(Oauth2UserDetailsService.class);

        String finalClientId = clientId;
        Optional<Oauth2UserDetailsService> optional = userDetailsServiceMap.values().stream()
                .filter(service -> service.support(finalClientId, grantType))
                .max(Comparator.comparingInt(Ordered::getOrder));

        optional.orElseThrow((() -> new InternalAuthenticationServiceException("UserDetailsService error , not register")));

        try {
            UserDetails loadedUser = optional.get().loadUserByUsername(username);
            Optional.ofNullable(loadedUser).orElseThrow(() ->
                    new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation"));
            return loadedUser;
        }
        catch (UsernameNotFoundException | BadAccessTokenException ex) {
            mitigateAgainstTimingAttack(authentication);
            throw ex;
        }
        catch (InternalAuthenticationServiceException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        boolean upgradeEncoding = this.userDetailsPasswordService != null
                && this.passwordEncoder.upgradeEncoding(user.getPassword());
        if (upgradeEncoding) {
            String presentedPassword = authentication.getCredentials().toString();
            String newPassword = this.passwordEncoder.encode(presentedPassword);
            user = this.userDetailsPasswordService.updatePassword(user, newPassword);
        }
        return super.createSuccessAuthentication(principal, authentication, user);
    }

    private void prepareTimingAttackProtection() {
        if (this.userNotFoundEncodedPassword == null) {
            this.userNotFoundEncodedPassword = this.passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
        }
    }

    private void mitigateAgainstTimingAttack(UsernamePasswordAuthenticationToken authentication) {
        if (authentication.getCredentials() != null) {
            String presentedPassword = authentication.getCredentials().toString();
            this.passwordEncoder.matches(presentedPassword, this.userNotFoundEncodedPassword);
        }
    }

    /**
     * Sets the PasswordEncoder instance to be used to encode and validate passwords. If
     * not set, the password will be compared using
     * {@link PasswordEncoderFactories#createDelegatingPasswordEncoder()}
     * @param passwordEncoder must be an instance of one of the {@code PasswordEncoder}
     * types.
     */
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
        this.passwordEncoder = passwordEncoder;
        this.userNotFoundEncodedPassword = null;
    }

    private String extractEncodedPassword(String prefixEncodedPassword) {
        int start = prefixEncodedPassword.indexOf(SecurityConstants.DEFAULT_ID_SUFFIX);
        return prefixEncodedPassword.substring(start + SecurityConstants.DEFAULT_ID_SUFFIX.length());
    }
}