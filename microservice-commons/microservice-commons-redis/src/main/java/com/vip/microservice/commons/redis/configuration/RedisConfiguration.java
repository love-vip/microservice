package com.vip.microservice.commons.redis.configuration;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vip.microservice.commons.redis.handler.RedisHandler;
import com.vip.microservice.commons.redis.handler.RedisHandlerImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization.Token;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * @author echo
 * @title: RedisConfiguration
 * @date 2023/3/15 16:42
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnClass(RedisProperties.class)
public class RedisConfiguration {

    private final Jackson2ObjectMapperBuilder builder;

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 创建ObjectMapper并添加默认配置
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        //------------------序列化，带上类信息------------------
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        //------------------end------------------
        objectMapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        objectMapper.enable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);

        // java8日期日期处理 Jackson Mixin
        objectMapper.registerModule(new JavaTimeModule());
        // 添加Security提供的Jackson Mixin
        objectMapper.registerModule(new CoreJackson2Module());
        // 添加OAuth2提供的Jackson Mixin
        objectMapper.registerModule(new OAuth2Jackson2Module());
        /*objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());*/


        Jackson2JsonRedisSerializer<Object> valueSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        // 字符串序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // RedisTemplate 模板
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 设置值序列化
        redisTemplate.setValueSerializer(valueSerializer);
        // 设置hash格式数据值的序列化器
        redisTemplate.setHashValueSerializer(valueSerializer);
        // 默认的Key序列化器为：JdkSerializationRedisSerializer
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // 设置字符串序列化器
        redisTemplate.setStringSerializer(stringRedisSerializer);
        // 设置hash结构的key的序列化器
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // 设置连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        return redisTemplate;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {

        StringRedisTemplate redisTemplate = new StringRedisTemplate();

        redisTemplate.setConnectionFactory(redisConnectionFactory);

        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(RedisHandler.class)
    public RedisHandler redisHandler(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
        return new RedisHandlerImpl(redisTemplate, stringRedisTemplate);
    }
    public static class OAuth2Jackson2Module extends SimpleModule {
        public OAuth2Jackson2Module() {
            super(OAuth2Jackson2Module.class.getName(), new Version(1, 0, 0, null, null, null));
        }

        @Override
        public void setupModule(SetupContext context) {
            SecurityJackson2Modules.enableDefaultTyping(context.getOwner());
            context.setMixInAnnotations(AuthorizationGrantType.class, AuthorizationGrantTypeMixin.class);
            context.setMixInAnnotations(OAuth2AccessToken.class, OAuth2AccessTokenMixin.class);
            context.setMixInAnnotations(OAuth2RefreshToken.class, OAuth2RefreshTokenMixin.class);
            context.setMixInAnnotations(TokenType.class, TokenTypeMixin.class);
            context.setMixInAnnotations(Token.class, TokenMixin.class);
        }
    }
    public abstract static class AuthorizationGrantTypeMixin {
        @JsonCreator
        public AuthorizationGrantTypeMixin(@JsonProperty("value") String value) { }
    }

    public abstract static class OAuth2AccessTokenMixin {
        @JsonCreator
        public OAuth2AccessTokenMixin(@JsonProperty("tokenType") TokenType tokenType, @JsonProperty("tokenValue") String tokenValue,
                                      @JsonProperty("issuedAt") Instant issuedAt, @JsonProperty("expiresAt") Instant expiresAt, @JsonProperty("scopes")Set<String> scopes) { }
    }

    public abstract static class OAuth2RefreshTokenMixin {
        @JsonCreator
        public OAuth2RefreshTokenMixin(@JsonProperty("tokenValue") String tokenValue, @JsonProperty("issuedAt") Instant issuedAt, @JsonProperty("expiresAt") Instant expiresAt) { }
    }

    public abstract static class TokenTypeMixin {
        @JsonCreator
        public TokenTypeMixin(@JsonProperty("value") String value) { }
    }

    @JsonAutoDetect(fieldVisibility = Visibility.ANY,
            creatorVisibility = Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
    public abstract static class TokenMixin {
        @JsonCreator
        public TokenMixin(@JsonProperty("token") String token, @JsonProperty("metadata") Map<String, Object> metadata) { }
    }

}