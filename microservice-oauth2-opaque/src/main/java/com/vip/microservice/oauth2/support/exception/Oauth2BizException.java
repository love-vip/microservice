package com.vip.microservice.oauth2.support.exception;

import com.vip.microservice.commons.base.exception.BusinessException;
import com.vip.microservice.oauth2.support.enums.Oauth2ApiCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author echo
 * @date 2023/3/28 15:54
 */
@Slf4j
public class Oauth2BizException extends BusinessException {

    private static final long serialVersionUID = 5926842866052951590L;

    /**
     * Instantiates a new Oauth2 exception.
     *
     * @param code      the code
     * @param msgFormat the msg format
     * @param args      the args
     */
    public Oauth2BizException(int code, String msgFormat, Object... args) {
        super(code, msgFormat, args);
        log.info("<== Oauth2BizException, code:" + this.code + ", message:" + super.getMessage());

    }

    /**
     * Instantiates a new Oauth2 exception.
     *
     * @param code the code
     * @param msg  the msg
     */
    public Oauth2BizException(int code, String msg) {
        super(code, msg);
        log.info("<== Oauth2BizException, code:" + this.code + ", message:" + super.getMessage());
    }

    /**
     * Instantiates a new Oauth2 exception.
     *
     * @param code the code enum
     */
    public Oauth2BizException(Oauth2ApiCode code, Object... args) {
        super(code.getCode(), code.getMessage());
        log.info("<== Oauth2BizException, code:" + this.code + ", message:" + super.getMessage());
    }

}