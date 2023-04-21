package com.vip.microservice.oauth2.feign.api.hystrix;

import com.vip.microservice.commons.base.wrapper.WrapMapper;
import com.vip.microservice.commons.base.wrapper.Wrapper;
import com.vip.microservice.oauth2.feign.api.AuthenticateFeignApi;
import org.springframework.stereotype.Component;

/**
 * <p></p>
 * @author echo
 * @version 1.0
 * @date 2023/3/30 15:21
 */
@Component
public class AuthenticateFeignHystrix implements AuthenticateFeignApi {

    @Override
    public Wrapper<?> notifyUpdateResources() {
        return WrapMapper.waiting();
    }
}