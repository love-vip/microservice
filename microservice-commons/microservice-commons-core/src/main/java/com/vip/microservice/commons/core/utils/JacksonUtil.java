package com.vip.microservice.commons.core.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Preconditions;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * <p>Jackson Json 工具类</p>
 * @author echo
 * @title: JacksonUtil
 * @date 2023/3/16 14:48
 */
@UtilityClass
public class JacksonUtil {

    private static final ObjectMapper defaultMapper;

    private static final ObjectMapper formatMapper;

    static {
        // 默认的ObjectMapper
        defaultMapper = new ObjectMapper();
        defaultMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        defaultMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        defaultMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        defaultMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        defaultMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        defaultMapper.enable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);

        formatMapper = new ObjectMapper();
        formatMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        formatMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        formatMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        formatMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        formatMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        formatMapper.enable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        // java8日期日期处理
        formatMapper.registerModule(new JavaTimeModule());

    }

    /**
     * 将对象转化为json数据
     *
     * @param obj the obj
     *
     * @return string string
     *
     * @throws IOException the io exception
     */
    @SneakyThrows(IOException.class)
    public static String toJson(Object obj) {
        Preconditions.checkArgument(obj != null, "this argument is required; it must not be null");
        return defaultMapper.writeValueAsString(obj);
    }

    /**
     * json数据转化为对象(Class)
     * User u = JacksonUtil.parseJson(jsonValue, User.class);
     * User[] arr = JacksonUtil.parseJson(jsonValue, User[].class);
     *
     * @param <T>       the type parameter
     * @param jsonValue the json value
     * @param valueType the value type
     *
     * @return t t
     *
     * @throws IOException the io exception
     */
    @SneakyThrows(IOException.class)
    public static <T> T parseJson(String jsonValue, Class<T> valueType) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(jsonValue), "this argument is required; it must not be null");
        return defaultMapper.readValue(jsonValue, valueType);
    }

    /**
     * json数据转化为对象(JavaType)
     *
     * @param <T>       the type parameter
     * @param jsonValue the json value
     * @param valueType the value type
     *
     * @return t t
     *
     * @throws IOException the io exception
     */
    @SneakyThrows(IOException.class)
    public static <T> T parseJson(String jsonValue, JavaType valueType){
        Preconditions.checkArgument(StringUtils.isNotEmpty(jsonValue), "this argument is required; it must not be null");
        return defaultMapper.readValue(jsonValue, valueType);
    }

    /**
     * json数据转化为对象(TypeReference)
     *
     * @param <T>          the type parameter
     * @param jsonValue    the json value
     * @param valueTypeRef the value type ref
     *
     * @return t t
     *
     * @throws IOException the io exception
     */
    @SneakyThrows(IOException.class)
    public static <T> T parseJson(String jsonValue, TypeReference<T> valueTypeRef){
        Preconditions.checkArgument(StringUtils.isNotEmpty(jsonValue), "this argument is required; it must not be null");
        return defaultMapper.readValue(jsonValue, valueTypeRef);
    }

    /**
     * 将对象转化为json数据(时间转换格式： "yyyy-MM-dd HH:mm:ss")
     *
     * @param obj the obj
     *
     * @return string string
     *
     * @throws IOException the io exception
     */
    @SneakyThrows(IOException.class)
    public static String toJsonWithFormat(Object obj){
        Preconditions.checkArgument(obj != null, "this argument is required; it must not be null");
        return formatMapper.writeValueAsString(obj);
    }

    /**
     * json数据转化为对象(时间转换格式： "yyyy-MM-dd HH:mm:ss")
     * User u = JacksonUtil.parseJsonWithFormat(jsonValue, User.class);
     * User[] arr = JacksonUtil.parseJsonWithFormat(jsonValue, User[].class);
     *
     * @param <T>       the type parameter
     * @param jsonValue the json value
     * @param valueType the value type
     *
     * @return t t
     *
     * @throws IOException the io exception
     */
    @SneakyThrows(IOException.class)
    public static <T> T parseJsonWithFormat(String jsonValue, Class<T> valueType) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(jsonValue), "this argument is required; it must not be null");
        return formatMapper.readValue(jsonValue, valueType);
    }

    /**
     * json数据转化为对象(JavaType)
     *
     * @param <T>       the type parameter
     * @param jsonValue the json value
     * @param valueType the value type
     *
     * @return t t
     *
     * @throws IOException the io exception
     */
    @SneakyThrows(IOException.class)
    public static <T> T parseJsonWithFormat(String jsonValue, JavaType valueType) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(jsonValue), "this argument is required; it must not be null");
        return formatMapper.readValue(jsonValue, valueType);
    }

    /**
     * json数据转化为对象(TypeReference)
     *
     * @param <T>          the type parameter
     * @param jsonValue    the json value
     * @param valueTypeRef the value type ref
     *
     * @return t t
     *
     * @throws IOException the io exception
     */
    @SneakyThrows(IOException.class)
    public static <T> T parseJsonWithFormat(String jsonValue, TypeReference<T> valueTypeRef){
        Preconditions.checkArgument(StringUtils.isNotEmpty(jsonValue), "jsonValue is not null");
        return formatMapper.readValue(jsonValue, valueTypeRef);
    }

}