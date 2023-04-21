package com.vip.microservice.commons.base.enums;

import java.lang.annotation.*;

/**
 * <p>处理器.</p>
 * @author echo
 * @title: HandlerType
 * @date 2023/3/16 16:23
 */
@Inherited
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HandlerType {

    String[] values();
}