package com.vip.microservice.commons.base.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vip.microservice.commons.base.BaseCode;
import com.vip.microservice.commons.base.api.ApiCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
/**
 * <p>The class Wrap.</p>
 * @author echo
 * @title: Wrapper
 * @date 2023/3/16 15:01
 */
@Data
@Builder
@JsonInclude(value= JsonInclude.Include.NON_NULL)
@Schema(title = "Wrapper", description = "通用响应数据")
public class Wrapper<T> implements Serializable {

    /**
     * 序列化标识
     */
    private static final long serialVersionUID = 4893280118017319089L;

    /**
     * 状态码.
     */
    @Builder.Default
    @Schema(name = "code", title = "状态码", example = "ok")
    private Integer code = BaseCode.SUCCESS.getCode();

    /**
     * 信息.
     */
    @Builder.Default
    @Schema(name = "message", title = "响应信息", example = "success")
    private String message = BaseCode.SUCCESS.getMessage();

    /**
     * 结果数据
     */
    @Schema(name = "data", title = "数据主体")
    private T data;

    /**
     * Instantiates a new wrapper. default status=ok
     */
    Wrapper() {
        this.code(BaseCode.SUCCESS.getCode()).message(BaseCode.SUCCESS.getMessage());
    }

    /**
     * Instantiates a new wrapper.
     *
     * @param apiCode  the code
     */
    Wrapper(ApiCode apiCode) {
        this.code(apiCode.getCode()).message(apiCode.getMessage());
    }

    /**
     * Instantiates a new wrapper.
     *
     * @param apiCode  the apiCode
     * @param data  the data
     */
    Wrapper(ApiCode apiCode, T data) {
        this.code(apiCode.getCode()).message(apiCode.getMessage()).data(data);
    }

    /**
     * Instantiates a new wrapper.
     *
     * @param code  the code
     * @param message the message
     */
    Wrapper(Integer code, String message) {
        this.code(code).message(message);
    }

    /**
     * Instantiates a new wrapper.
     *
     * @param code  the code
     * @param message the message
     * @param data  the data
     */
    Wrapper(Integer code, String message, T data) {
        this.code(code).message(message).data(data);
    }

    /**
     * Sets the 编号 , 返回自身的引用.
     *
     * @param code the new 编号
     *
     * @return the wrapper
     */
    private Wrapper<T> code(Integer code) {
        this.code = code;
        return this;
    }

    /**
     * Sets the 信息 , 返回自身的引用.
     *
     * @param message the new 信息
     *
     * @return the wrapper
     */
    private Wrapper<T> message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Sets the 结果数据 , 返回自身的引用.
     *
     * @param data the new 结果数据
     *
     * @return the wrapper
     */
    public Wrapper<T> data(T data) {
        this.data = data;
        return this;
    }

    /**
     * 判断是否成功： 依据 Wrapper.SUCCESS_STATUS == this.status
     *
     * @return status = ok,true;否则 false.
     */
    @JsonIgnore
    public boolean success() {
        return this.code != null && BaseCode.SUCCESS.getCode() == this.code;
    }

    /**
     * 判断是否成功： 依据 Wrapper.SUCCESS_STATUS != this.status
     *
     * @return status != ok,true;否则 false.
     */
    @JsonIgnore
    public boolean error() {
        return !success();
    }

}