package com.vip.microservice.oauth2;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * <p>授权服务中心启动类</p>
 * @author echo
 * @title: Oauth2ProviderApplication
 * @date 2023/3/18 13:43
 */
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan(basePackages = "com.vip.microservice.oauth2.mapper")
@EnableFeignClients(basePackages = "com.vip.microservice.oauth2.feign.api")
@OpenAPIDefinition(info = @Info(title = "统一认证授权服务 API", version = "2.0", description = "统一认证授权服务 Information"),
        servers = {@Server(url = "http://localhost:8000/oauth2")}, security = @SecurityRequirement(name = "Bearer access_token"))
@SecurityScheme(name = "Bearer access_token", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER,
        description = "直接将有效的access_token填入下方，后续该access_token将作为Bearer access_token")
public class Oauth2ProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2ProviderApplication.class, args);
    }

}
