package com.vip.microservice.oauth2.web;

import com.vip.microservice.commons.base.exception.BusinessException;
import com.vip.microservice.commons.base.wrapper.WrapMapper;
import com.vip.microservice.commons.base.wrapper.Wrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * @author echo
 * @date 2019/3/18 16:19
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 参数非法异常.
     *
     * @param e the e
     *
     * @return the wrapper
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Wrapper<?> illegalArgumentException(BindException e) {
        log.error("参数非法异常={}", e.getMessage(), e);
        StringBuilder result = new StringBuilder();
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> errors = bindingResult.getFieldErrors();
        for (FieldError error: errors) {
            String field = error.getField();
            String msg = error.getDefaultMessage();
            result.append(field).append(":").append(msg).append(";");
        }
        return WrapMapper.illegalArgument(result.toString());
    }

    /**
     * 业务异常.
     *
     * @param e the e
     *
     * @return the wrapper
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Wrapper<?> businessException(BusinessException e) {
        log.error("业务异常={}", e.getMessage(), e);
        return WrapMapper.fail(e.getMessage());
    }

    /**
     * 全局异常.
     *
     * @param e the e
     *
     * @return the wrapper
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Wrapper<?> exception(Exception e) {
        log.error("保存全局异常信息 ex={}", e.getMessage(), e);
        return WrapMapper.error();
    }
}