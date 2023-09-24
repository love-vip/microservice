CREATE TABLE oauth2_authorization_consent
(
    registered_client_id varchar(100)  NOT NULL,
    principal_name       varchar(200)  NOT NULL,
    authorities          varchar(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
);

CREATE TABLE oauth2_registered_client
(
    id                            varchar(100)                            NOT NULL,
    client_id                     varchar(100)                            NOT NULL,
    client_id_issued_at           timestamp     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret                 varchar(200)  DEFAULT NULL,
    client_secret_expires_at      timestamp     DEFAULT NULL,
    client_name                   varchar(200)                            NOT NULL,
    client_authentication_methods varchar(1000)                           NOT NULL,
    authorization_grant_types     varchar(1000)                           NOT NULL,
    redirect_uris                 varchar(1000) DEFAULT NULL,
    post_logout_redirect_uris     varchar(1000) DEFAULT NULL,
    scopes                        varchar(1000)                           NOT NULL,
    client_settings               varchar(2000)                           NOT NULL,
    token_settings                varchar(2000)                           NOT NULL,
    PRIMARY KEY (id)
);

/*
IMPORTANT:
    If using PostgreSQL, update ALL columns defined with 'blob' to 'text',
    as PostgreSQL does not support the 'blob' data type.
*/
CREATE TABLE oauth2_authorization
(
    id                            varchar(100) NOT NULL,
    registered_client_id          varchar(100) NOT NULL,
    principal_name                varchar(200) NOT NULL,
    authorization_grant_type      varchar(100) NOT NULL,
    authorized_scopes             varchar(1000) DEFAULT NULL,
    attributes                    blob          DEFAULT NULL,
    state                         varchar(500)  DEFAULT NULL,
    authorization_code_value      blob          DEFAULT NULL,
    authorization_code_issued_at  timestamp     DEFAULT NULL,
    authorization_code_expires_at timestamp     DEFAULT NULL,
    authorization_code_metadata   blob          DEFAULT NULL,
    access_token_value            blob          DEFAULT NULL,
    access_token_issued_at        timestamp     DEFAULT NULL,
    access_token_expires_at       timestamp     DEFAULT NULL,
    access_token_metadata         blob          DEFAULT NULL,
    access_token_type             varchar(100)  DEFAULT NULL,
    access_token_scopes           varchar(1000) DEFAULT NULL,
    oidc_id_token_value           blob          DEFAULT NULL,
    oidc_id_token_issued_at       timestamp     DEFAULT NULL,
    oidc_id_token_expires_at      timestamp     DEFAULT NULL,
    oidc_id_token_metadata        blob          DEFAULT NULL,
    refresh_token_value           blob          DEFAULT NULL,
    refresh_token_issued_at       timestamp     DEFAULT NULL,
    refresh_token_expires_at      timestamp     DEFAULT NULL,
    refresh_token_metadata        blob          DEFAULT NULL,
    user_code_value               blob          DEFAULT NULL,
    user_code_issued_at           timestamp     DEFAULT NULL,
    user_code_expires_at          timestamp     DEFAULT NULL,
    user_code_metadata            blob          DEFAULT NULL,
    device_code_value             blob          DEFAULT NULL,
    device_code_issued_at         timestamp     DEFAULT NULL,
    device_code_expires_at        timestamp     DEFAULT NULL,
    device_code_metadata          blob          DEFAULT NULL,
    PRIMARY KEY (id)
);


CREATE TABLE `rbac_user`
(
    `id`                 bigint(20)  NOT NULL AUTO_INCREMENT,
    `username`           varchar(32) NOT NULL COMMENT '用户名',
    `password`           longtext    NOT NULL COMMENT '密码，加密存储',
    `realname`           varchar(32) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '真实姓名',
    `mobile`             varchar(16)                       DEFAULT NULL COMMENT '手机号',
    `email`              varchar(32)                       DEFAULT NULL COMMENT '邮箱',
    `enabled`            tinyint(1)  NOT NULL              DEFAULT '1' COMMENT '在职状态（0:离职 1:在职）',
    `account_non_locked` tinyint(1)                        DEFAULT '1' COMMENT '是否还未锁定(0:否 1是)',
    `login_error_times`  tinyint(2)  NOT NULL              DEFAULT '0' COMMENT '密码输入错误次数',
    `verify_error_times` tinyint(2)  NOT NULL              DEFAULT '0' COMMENT '验证码输入错误次数',
    `secret`             varchar(255)                      DEFAULT NULL COMMENT '谷歌校验器密钥',
    `initial`            tinyint(1)  NOT NULL              DEFAULT '1' COMMENT '是否初始用户',
    `bind`               tinyint(1)  NOT NULL              DEFAULT '0' COMMENT '是否已绑定谷歌校验器',
    `access_token`       varchar(255)                      DEFAULT NULL COMMENT '访问令牌',
    `expire_time`        datetime                          DEFAULT NULL COMMENT '访问令牌过期时间',
    `creator`            varchar(255)                      DEFAULT NULL COMMENT '创建人',
    `creator_id`         bigint(20)                        DEFAULT NULL COMMENT '创建人id',
    `create_time`        datetime    NOT NULL COMMENT '创建时间',
    `last_operator`      varchar(255)                      DEFAULT NULL COMMENT '最近操作人',
    `last_operator_id`   bigint(20)                        DEFAULT NULL COMMENT '最近操作人id',
    `update_time`        datetime                          DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `idx-username` (`username`) USING BTREE,
    UNIQUE KEY `idx-email` (`email`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8 COMMENT ='用户表';

INSERT INTO `rbac_user` (`username`, `password`, `realname`, `mobile`, `email`, `enabled`, `account_non_locked`,
                         `login_error_times`, `verify_error_times`, `secret`, `initial`, `bind`, `access_token`,
                         `expire_time`, `creator`, `creator_id`, `create_time`, `last_operator`, `last_operator_id`,
                         `update_time`)
VALUES ('echo@gmail.com', '{bcrypt}$2a$10$wCwES4jMZQYpYhIyAedd9.t16i7mZ1w1kw/FQK00bjjDsbWiJfCUC', '森林迷了🦌', NULL,
        'echo@gmail.com', 1, 1, 0, 0, 'S77BJIKKI3N33P6FFQGJNIQIEZA4LZ3Q', 0, 1,
        'o37jZVMEe4b0gCOvRVhBLmT3oIpVJz7fXVWpfl09vgdDQWx36Ludfaf_Z42hfcvm0RwNRFT8eJ9ufT5uKlEAhQi7XdnN-ajNxj42JgYCCaPhN-dRe6Xa11zBqkBbDXcW',
        '2023-09-23 23:42:56', 'admin', 0, '2023-03-21 14:08:26', 'echo@gmail.com', 2, '2023-08-14 14:26:23');