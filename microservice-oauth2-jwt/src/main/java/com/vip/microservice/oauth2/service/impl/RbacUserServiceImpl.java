package com.vip.microservice.oauth2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vip.microservice.commons.base.enums.BooleanEnum;
import com.vip.microservice.commons.core.support.BaseService;
import com.vip.microservice.commons.core.support.Objects;
import com.vip.microservice.oauth2.mapper.RbacUserMapper;
import com.vip.microservice.oauth2.model.domain.RbacUser;
import com.vip.microservice.oauth2.model.query.UserPageQuery;
import com.vip.microservice.oauth2.service.RbacUserService;
import com.vip.microservice.oauth2.support.enums.Oauth2ApiCode;
import com.vip.microservice.oauth2.support.exception.Oauth2BizException;
import com.vip.microservice.oauth2.support.security.core.principal.UserInfo;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author echo
 * @title: RbacUserServiceImpl
 * @date 2021/11/22 15:49
 */
@Service
@RequiredArgsConstructor
public class RbacUserServiceImpl extends BaseService<RbacUser> implements RbacUserService {

    private final RbacUserMapper rbacUserMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<RbacUser> getByUsername(String username) {

        return Optional.ofNullable(rbacUserMapper.selectOne(new QueryWrapper<RbacUser>().eq("username", username)));
    }

    @Override
    public Optional<RbacUser> getByMobile(String mobile) {
        return Optional.ofNullable(rbacUserMapper.selectOne(new QueryWrapper<RbacUser>().eq("mobile", mobile)));
    }

    @Override
    public void locked(String username) {
        RbacUser user = getByUsername(username).orElseThrow(() -> new Oauth2BizException(Oauth2ApiCode.CN10001));
        user.setAccountNonLocked(BooleanEnum.NEGATIVE.isBool());
        rbacUserMapper.updateById(user);
    }

    @Override
    public void unlock(String username) {
        RbacUser user = getByUsername(username).orElseThrow(() -> new Oauth2BizException(Oauth2ApiCode.CN10001));
        user.setAccountNonLocked(BooleanEnum.POSITIVE.isBool());
        rbacUserMapper.updateById(user);
    }

    @Override
    public String fillSecret(String username) {
        RbacUser user = getByUsername(username).orElseThrow(() -> new Oauth2BizException(Oauth2ApiCode.CN10001));
        if(Objects.isNotEmpty(user.getSecret())){
            return user.getSecret();
        }
        //从未绑定过谷歌校验器
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        GoogleAuthenticatorKey googleAuthenticatorKey = googleAuthenticator.createCredentials();
        String secret = googleAuthenticatorKey.getKey();
        user.setSecret(secret);
//        user.setBind(BooleanEnum.POSITIVE.isBool());
        rbacUserMapper.updateById(user);
        return secret;

    }

    @Override
    public IPage<UserInfo> selectByPage(UserPageQuery query) {

        Page<RbacUser> page = new Page<>(query.getPageNum(), query.getPageSize());

        page = rbacUserMapper.selectPage(page, new QueryWrapper<RbacUser>().eq("username", query.getUsername()));

        return Objects.convert(page, UserInfo.class);
    }

}
