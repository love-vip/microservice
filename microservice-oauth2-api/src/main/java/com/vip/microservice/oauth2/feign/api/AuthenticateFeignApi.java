package com.vip.microservice.oauth2.feign.api;

import com.vip.microservice.commons.base.wrapper.Wrapper;
import com.vip.microservice.oauth2.feign.api.hystrix.AuthenticateFeignHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p></p>
 * @author echo
 * @version 1.0
 * @date 2023/3/30 15:21
 */
@FeignClient(value = "oauth2", fallback = AuthenticateFeignHystrix.class)
public interface AuthenticateFeignApi {

    @RequestMapping(value ="/api/oauth2/notify")
    Wrapper<?> notifyUpdateResources();
}
