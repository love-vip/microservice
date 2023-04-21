package com.vip.microservice.oauth2.support.security.core.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
public class OAuth2ClientException extends OAuth2AuthenticationException {

    /**
     * Constructs a <code>ScopeException</code> with the specified message.
     * @param msg the detail message.
     */
    public OAuth2ClientException(String msg) {
        super(new OAuth2Error(msg), msg);
    }

    /**
     * Constructs a {@code ScopeException} with the specified message and root cause.
     * @param msg the detail message.
     * @param cause root cause
     */
    public OAuth2ClientException(String msg, Throwable cause) {
        super(new OAuth2Error(msg), cause);
    }

}
