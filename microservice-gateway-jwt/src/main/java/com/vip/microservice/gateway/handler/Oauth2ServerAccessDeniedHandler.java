package com.vip.microservice.gateway.handler;

import com.vip.microservice.commons.base.BaseCode;
import com.vip.microservice.commons.base.wrapper.WrapMapper;
import com.vip.microservice.commons.redis.utils.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * @author echo
 * @version 1.0
 * @date 2023/4/1 18:39
 */
@Slf4j
public class Oauth2ServerAccessDeniedHandler implements ServerAccessDeniedHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        return Mono.defer(() -> Mono.just(exchange.getResponse()))
                .flatMap(response -> {
                    response.setStatusCode(HttpStatus.OK);

                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                    DataBufferFactory dataBufferFactory = response.bufferFactory();

                    log.error("Oauth2ServerAccessDeniedHandler:{}", BaseCode.PERMISSION_DENIED);

                    String result = JacksonUtil.toJson(WrapMapper.fail(BaseCode.PERMISSION_DENIED));

                    DataBuffer buffer = dataBufferFactory.wrap(result.getBytes(Charset.defaultCharset()));

                    return response.writeWith(Mono.just(buffer));
                });
    }
}
