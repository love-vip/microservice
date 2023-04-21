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
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author echo
 * @title: Oauth2SmsDetailsServiceImpl
 * @date 2023/3/16 19:14
 */
@Service
@RequiredArgsConstructor
public class Oauth2SmsDetailsServiceImpl implements Oauth2UserDetailsService {

    private final RbacUserService userService;

    @Override
    public UserDetails loadUserByUsername(String mobile) {

        RbacUser user = userService.getByMobile(mobile).orElseThrow(() -> new UsernameNotFoundException("mobile: [" + mobile + "] do not exist!"));

        // 获取用户可访问资源
        List<GrantedAuthority> authorities = Collections.singletonList(new UrlGrantedAuthority("/**"));

        /*Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(dbAuthsSet.toArray(new String[0]));*/

        // 构造security用户
        return new Oauth2User(user.getId(), user.getUsername(), SecurityConstants.BCRYPT + user.getPassword(),
                user.getMobile(), user.getEmail(), user.getRealName(), user.isInitial(),
                user.isEnabled(), user.isAccountNonLocked(), authorities);
    }

    @Override
    public UserDetails loadUserByUser(Oauth2User user) {
        return this.loadUserByUsername(user.getMobile());
    }

    /**
     * 是否支持此客户端校验
     * @param clientId 目标客户端
     * @return true/false
     */
    @Override
    public boolean support(String clientId, String grantType) {
        return SecurityConstants.SMS.getValue().equals(grantType);
    }

}
