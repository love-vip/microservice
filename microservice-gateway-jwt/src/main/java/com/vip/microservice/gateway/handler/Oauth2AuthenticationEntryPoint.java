package com.vip.microservice.gateway.handler;

import com.vip.microservice.commons.base.BaseCode;
import com.vip.microservice.commons.base.wrapper.WrapMapper;
import com.vip.microservice.commons.core.utils.JacksonUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.Charset;
/**
 * @author echo
 * @version 1.0
 * @date 2023/4/1 18:39
 */
public class Oauth2AuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        return Mono.defer(() -> Mono.just(exchange.getResponse())).flatMap(response -> {

            response.setStatusCode(HttpStatus.UNAUTHORIZED);

            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            DataBufferFactory dataBufferFactory = response.bufferFactory();

            String result = JacksonUtil.toJson(WrapMapper.fail(BaseCode.USER_UNAUTHORIZED));

            DataBuffer buffer = dataBufferFactory.wrap(result.getBytes(Charset.defaultCharset()));

            return response.writeWith(Mono.just(buffer));
        });
    }
}
