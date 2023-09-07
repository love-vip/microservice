package com.vip.microservice.oauth2.support.security.core.principal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author echo
 * @title: UserInfo
 * @date 2023/3/15 13:55
 */
@Data
public class UserInfo implements Serializable {

    /**
     * 用户ID
     */
    @Schema(name = "id", description = "用户ID", example = "1")
    private Long id;

    /**
     * 手机号
     */
    @Schema(name = "mobile", description = "手机号", example = "13812348888")
    private String mobile;

    /**
     * 邮箱
     */
    @Schema(name = "email", description = "邮箱", example = "10@gmail.com")
    private String email;

    /**
     * 真实姓名
     */
    @Schema(name = "realName", description = "真实姓名", example = "七七")
    private String realName;

    /**
     * 是否初始用户
     */
    @Schema(name = "initial", description = "是否初始用户", example = "true")
    private boolean initial;

    /**
     * 是否已绑定谷歌校验器
     */
    @Schema(name = "bind", description = "是否已绑定谷歌校验器", example = "true")
    private boolean bind;

    /**
     * 谷歌验证二维码地址
     */
    @Schema(name = "qrCodeUrl", description = "谷歌验证二维码地址", example = "https://www.google.com/chart?chs=200x200&cht=qr&chl=otpauth://totp/odic:echo?secret=S77BJIKKI3N33P6FFQGJNIQIEZA4LZ3Q&issuer=odic")
    private String qrCodeUrl;
}
