package com.vip.microservice.commons.core.config;

import com.vip.microservice.commons.core.generator.IdGenerator;
import com.vip.microservice.commons.core.generator.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author echo
 * @title: WtConfiguration
 * @date 2023/3/15 11:01
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({
})
public class CoreConfiguration {

    @Bean
    public IdGenerator idGenerator() {
        return new SnowflakeIdGenerator();
    }

}
