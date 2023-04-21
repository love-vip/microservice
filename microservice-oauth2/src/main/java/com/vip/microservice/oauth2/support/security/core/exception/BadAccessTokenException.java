package com.vip.microservice.oauth2.support.security.core.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

/**
 * @author echo
 * @title: BadAccessTokenException
 * @date 2023/3/15 16:44
 */
public class BadAccessTokenException extends OAuth2AuthenticationException {

    /**
     * Constructs a <code>BadCaptchaException</code> with the specified message.
     * @param msg the detail message
     */
    public BadAccessTokenException(String msg) {
        super(new OAuth2Error(msg), msg);
    }

    /**
     * Constructs a <code>BadCaptchaException</code> with the specified message and
     * root cause.
     * @param msg the detail message
     * @param cause root cause
     */
    public BadAccessTokenException(String msg, Throwable cause) {
        super(new OAuth2Error(msg), cause);
    }

}