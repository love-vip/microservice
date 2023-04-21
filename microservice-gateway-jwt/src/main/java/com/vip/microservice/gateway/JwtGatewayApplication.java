package com.vip.microservice.gateway;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
/**
 * @author echo
 * @version 1.0
 * @date 2023/4/1 20:17
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "网关聚合服务 API", version = "2.0", description = "网关聚合服务 Information"),
        servers = {@Server(url = "http://localhost:8000")}, security = @SecurityRequirement(name = "Bearer access_token"))
@SecurityScheme(name = "Bearer access_token", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER,
        description = "直接将有效的access_token填入下方，后续该access_token将作为Bearer access_token")
public class JwtGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtGatewayApplication.class, args);
    }

}
