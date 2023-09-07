package com.vip.microservice.oauth2.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vip.microservice.commons.core.support.IService;
import com.vip.microservice.oauth2.model.domain.RbacUser;
import com.vip.microservice.oauth2.model.query.UserPageQuery;
import com.vip.microservice.oauth2.support.security.core.principal.UserInfo;

import java.util.Optional;

/**
 * @author echo
 * @title: RbacUserService
 * @date 2023/3/16 15:47
 */
public interface RbacUserService extends IService<RbacUser> {

    String DEFAULT_PASSWORD = "123456";

    /**
     * <p>根据唯一用户名获取用户信息</p>
     * @param username 用户名
     * @return 用户
     */
    Optional<RbacUser> getByUsername(String username);

    /**
     * <p>根据唯一手机号获取用户信息</p>
     * @param mobile 手机号
     * @return 用户
     */
    Optional<RbacUser> getByMobile(String mobile);

    /**
     * <p>锁定账户</p>
     * @param username 用户名
     */
    void locked(String username);

    /**
     * <p>解锁账户</p>
     * @param username 用户名
     */
    void unlock(String username);

    /**
     * <p>保存密钥</p>
     * @param username 用户名
     * @return 密钥
     */
    String fillSecret(String username);

    /**
     * <p></p>
     * @param query 查询条件
     * @return 分页数据
     */
    IPage<UserInfo> selectByPage(UserPageQuery query);

}
