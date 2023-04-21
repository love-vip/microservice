package com.vip.microservice.commons.core.annotation;

import java.lang.annotation.*;

/**
 * <p>配合 SqlLogInterceptor 对指定方法 禁止打印SQL到控制台</p>
 * @author echo
 * @title: NotDisplaySql
 * @date 2023/3/15 16:24
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface NotDisplaySql {
}
