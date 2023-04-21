package com.vip.microservice.commons.core.utils;

import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author echo
 * @title: SecurityUtils
 * @date 2023/3/15 14:45
 */
@UtilityClass
public class SecurityUtils {

    /**
     * 获取Authentication
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取用户
     */
    public AuthenticatedPrincipal getUser(Authentication authentication) {
        if (authentication instanceof AnonymousAuthenticationToken) {
            return new AdminPrincipal("admin");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedPrincipal) {
            return (AuthenticatedPrincipal) principal;
        }
        return null;
    }

    /**
     * 获取用户
     */
    public AuthenticatedPrincipal getUser() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        return getUser(authentication);
    }

    @RequiredArgsConstructor
    public static class AdminPrincipal implements AuthenticatedPrincipal {

        private final String name;

        @Override
        public String getName() {
            return name;
        }
    }

    /**
     * 获取用户角色信息
     * @return 角色集合
     */
/*    public List<Long> getRoles() {
        Authentication authentication = getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        List<Long> roleIds = new ArrayList<>();
        authorities.stream().filter(granted -> StrUtil.startWith(granted.getAuthority(), SecurityConstants.ROLE))
                .forEach(granted -> {
                    String id = StrUtil.removePrefix(granted.getAuthority(), SecurityConstants.ROLE);
                    roleIds.add(Long.parseLong(id));
                });
        return roleIds;
    }*/

}