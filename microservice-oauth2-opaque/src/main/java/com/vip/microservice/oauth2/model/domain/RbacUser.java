package com.vip.microservice.oauth2.model.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.vip.microservice.commons.core.mybatis.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import javax.persistence.Table;

/**
 * @author echo
 * @title: RbacUser
 * @date 2023/3/16 15:44
 */
@Data
@Table(name = "rbac_user")
@Alias(value = "rbacUser")
@EqualsAndHashCode(callSuper = true)
public class RbacUser extends BaseEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码，加密存储
     */
    private String password;

    /**
     * 真实姓名
     */
    @TableField(value = "realname")
    private String realName;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态（0:禁用 1:启用）
     */
    private boolean enabled;

    /**
     * 是否还未锁定(0:否 1是)
     */
    @TableField(value = "account_non_locked")
    private boolean accountNonLocked;

    /**
     * 谷歌校验器密钥
     */
    private String secret;

    /**
     * 是否初始用户
     */
    private boolean initial;

    /**
     * 是否已绑定谷歌校验器
     */
    private boolean bind;

    /**
     * 密码输入错误次数
     */
    @TableField(value = "login_error_times")
    private Integer loginErrorTimes;

    /**
     * 验证码输入错误次数
     */
    @TableField(value = "verify_error_times")
    private Integer verifyErrorTimes;

}