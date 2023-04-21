package com.vip.microservice.commons.base.wrapper;

import com.vip.microservice.commons.base.BaseCode;
import com.vip.microservice.commons.base.api.ApiCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <p>The class Wrap mapper.</p>
 * @author echo
 * @title: WrapMapper
 * @date 2023/3/16 15:06
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WrapMapper {

    /**
     * Wrap SUCCESS. code=200
     *
     * @param <T> the element type
     *
     * @return the wrapper
     */
    public static <T> Wrapper<T> ok() {
        return new Wrapper<>();
    }
    /**
     * Wrap SUCCESS. code=200
     *
     * @param <T> the type parameter
     *
     * @return the wrapper
     */
    public static <T> Wrapper<T> success(T data) {
        return new Wrapper<>(BaseCode.SUCCESS, data);
    }

    /**
     * Wrap. code=500
     *
     * @return the wrapper
     */
    public static <T> Wrapper<T> error() {
        return new Wrapper<>(BaseCode.SERVER_ERROR);
    }

    /**
     * Wrap.
     *
     * @param apiCode the apiCode
     *
     * @return the wrapper
     */
    public static <T> Wrapper<T> fail(ApiCode apiCode) {
        return new Wrapper<>(apiCode);
    }

    /**
     * Wrap.
     *
     * @param message the message
     *
     * @return the wrapper
     */
    public static <T> Wrapper<T> fail(String message) {
        return new Wrapper<>(BaseCode.SERVER_ERROR.getCode(), message);
    }

    /**
     * Wrap ERROR. code=400
     *
     * @param <T> the element type
     *
     * @return the wrapper
     */
    public static <T> Wrapper<T> illegalArgument(T data) {
        return new Wrapper<>(BaseCode.ERROR_PARAMETER, data);
    }

    /**
     * Wrap WAIT. code=503
     *
     * @param <T> the element type
     *
     * @return the wrapper
     */
    public static <T> Wrapper<T> waiting() {
        return new Wrapper<>(BaseCode.SERVER_TIMEOUT);
    }

    /**
     * Un wrapper.
     *
     * @param <T>     the element type
     * @param wrapper the wrapper
     *
     * @return the e
     */
    public static <T> T unWrap(Wrapper<T> wrapper) {
        return wrapper.getData();
    }

}