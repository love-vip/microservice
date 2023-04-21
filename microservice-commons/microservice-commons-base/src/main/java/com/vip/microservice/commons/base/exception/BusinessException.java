package com.vip.microservice.commons.base.exception;

import com.vip.microservice.commons.base.BaseCode;
import com.vip.microservice.commons.base.api.ApiCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.text.MessageFormat;
/**
 * <p>业务异常</p>
 * @author echo
 * @title: BusinessException
 * @date 2023/3/16 15:58
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BusinessException extends RuntimeException implements ApiCode {

    /** 异常码 */
    protected int code;

    private static final long serialVersionUID = 3160241586346324994L;

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(int code, String msgFormat, Object... args) {
        super(MessageFormat.format(msgFormat, args));
        this.code = code;
    }

    public BusinessException(BaseCode codeEnum, Object... args) {
        super(MessageFormat.format(codeEnum.getMessage(), args));
        this.code = codeEnum.getCode();
    }
}