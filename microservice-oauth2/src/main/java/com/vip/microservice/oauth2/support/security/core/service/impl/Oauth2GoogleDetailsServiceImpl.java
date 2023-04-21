package com.vip.microservice.oauth2.support.security.core.service.impl;

import com.vip.microservice.oauth2.model.domain.RbacUser;
import com.vip.microservice.oauth2.service.RbacUserService;
import com.vip.microservice.oauth2.support.security.core.authority.UrlGrantedAuthority;
import com.vip.microservice.oauth2.support.security.core.constant.SecurityConstants;
import com.vip.microservice.oauth2.support.security.core.principal.Oauth2User;
import com.vip.microservice.oauth2.support.security.core.service.Oauth2UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author echo
 * @title: Oauth2GoogleDetailsServiceImpl
 * @date 2023/3/16 19:14
 */
@Service
@RequiredArgsConstructor
public class Oauth2GoogleDetailsServiceImpl implements Oauth2UserDetailsService {

    private final RbacUserService userService;

    private final OAuth2AuthorizationService authorizationService;

    @Override
    public UserDetails loadUserByUsername(String accessToken) {

        OAuth2Authorization authorization = authorizationService.findByToken(accessToken, OAuth2TokenType.ACCESS_TOKEN);

        String principalName = Optional.ofNullable(authorization).map(OAuth2Authorization::getPrincipalName).orElseThrow(() -> new UsernameNotFoundException("access_token: [" + accessToken + "] do not exist!"));

        RbacUser user = userService.getByUsername(principalName).orElseThrow(() -> new UsernameNotFoundException("username: [" + principalName + "] do not exist!"));

        // 获取用户可访问资源
        List<GrantedAuthority> authorities = Collections.singletonList(new UrlGrantedAuthority("/vip/ok"));

        /*Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(dbAuthsSet.toArray(new String[0]));*/

        // 构造security用户
        return new Oauth2User(user.getId(), user.getUsername(), SecurityConstants.BCRYPT + user.getPassword(),
                user.getMobile(), user.getEmail(), user.getRealName(), user.isInitial(),
                user.isEnabled(), user.isAccountNonLocked(), authorities);
    }

    /**
     * 是否支持此客户端校验
     * @param clientId 目标客户端
     * @return true/false
     */
    @Override
    public boolean support(String clientId, String grantType) {
        return SecurityConstants.GOOGLE.getValue().equals(grantType);
    }

}
