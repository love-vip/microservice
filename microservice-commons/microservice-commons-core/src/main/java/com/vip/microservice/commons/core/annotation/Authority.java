package com.vip.microservice.commons.core.annotation;

import java.lang.annotation.*;

/**
 * @author echo
 * @title: NotDisplaySql
 * @date 2023/3/15 16:24
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Authority {

}
