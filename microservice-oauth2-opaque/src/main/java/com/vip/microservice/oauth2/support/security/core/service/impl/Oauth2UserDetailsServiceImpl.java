package com.vip.microservice.oauth2.support.security.core.service.impl;

import com.vip.microservice.oauth2.model.domain.RbacUser;
import com.vip.microservice.oauth2.service.RbacUserService;
import com.vip.microservice.oauth2.support.security.core.constant.SecurityConstants;
import com.vip.microservice.oauth2.support.security.core.principal.Oauth2User;
import com.vip.microservice.oauth2.support.security.core.service.Oauth2UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author echo
 * @title: Oauth2UserDetailsServiceImpl
 * @date 2023/3/16 15:36
 */
@Service
@RequiredArgsConstructor
public class Oauth2UserDetailsServiceImpl implements Oauth2UserDetailsService {
    private static final String ISSUER = "VIP";
    private final RbacUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        RbacUser user = userService.getByUsername(username).orElseThrow(() -> new UsernameNotFoundException("mobile: [" + username + "] do not exist!"));

        // 获取用户仅可访问资源(初始用户修改密码)
        /*List<GrantedAuthority> authorities = Collections.singletonList(new UrlGrantedAuthority("/oauth2/**"));*/

        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("/oauth2/vip/**", "/cms/vip/ok");

        if(!user.isBind()){
            //未绑定谷歌验证器
            String secret = userService.fillSecret(username);
            String account = ISSUER + ":" + username;
            String googleAuthenticatorKeyUriFormat = "otpauth://totp/%s?secret=%s&issuer=%s";
            String qrCodeData = String.format(googleAuthenticatorKeyUriFormat, account, secret, ISSUER);
            String googleChartsQrCodeFormat = "https://www.google.com/chart?chs=200x200&cht=qr&chl=%s";
            String qrCodeUrl = String.format(googleChartsQrCodeFormat, qrCodeData);
            // 构造security用户
            return new Oauth2User(user.getId(), user.getUsername(), SecurityConstants.BCRYPT + user.getPassword(),
                    user.getMobile(), user.getEmail(), user.getRealName(), user.isInitial(),  qrCodeUrl,
                    user.isEnabled(), user.isAccountNonLocked(), authorities);
        }

        // 构造security用户
        return new Oauth2User(user.getId(), user.getUsername(), SecurityConstants.BCRYPT + user.getPassword(),
                user.getMobile(), user.getEmail(), user.getRealName(), user.isInitial(),
                user.isEnabled(), user.isAccountNonLocked(), authorities);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}

