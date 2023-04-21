package com.vip.microservice.commons.core.support;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author echo
 * @title: Objects
 * @date 2023/3/15 15:46
 */
@UtilityClass
public class Objects extends org.springframework.util.ObjectUtils {

    /**
     * 数组不为空
     * @param array 数组
     * @return boolean
     */
    public static boolean isNotEmpty(@Nullable Object[] array){
        return !isEmpty(array);
    }

    /**
     * 对象不为空
     * @param obj 对象
     * @return boolean
     */
    public static boolean isNotEmpty(@Nullable Object obj){
        return !isEmpty(obj);
    }

    /**
     * 对象转换
     * @param s 需转换对象
     * @param clazz class类型
     * @param <S> 输入领域对象
     * @param <D> 输出对象
     * @return 输出转换对象
     */
    public static <S, D> D convert(S s, Class<D> clazz){

        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return mapper.map(s, clazz);
    }

    /**
     * 对象转换
     * @param s 输入对象
     * @param clazz class类型
     * @param <S> 输入领域对象
     * @param <D> 输出对象
     * @return 输出转换对象
     */
    public static <S, D> D convert(S s, Class<D> clazz, BiConsumer<S, D> consumer){

        D d = convert(s, clazz);

        consumer.accept(s, d);

        return d;
    }

    /**
     * 对象转换DOMAIN -> VO
     * @param original 输入对象
     * @param <DOMAIN> 输入领域对象
     * @param <VO> 输出对象
     * @return 输出转换对象
     */
    public static <DOMAIN, VO> List<VO> convert(List<DOMAIN> original, Class<VO> clazz){

        return original.parallelStream().map(entity -> convert(entity, clazz)).collect(Collectors.toList());
    }

    /**
     * 分页对象转换DOMAIN -> VO
     * @param page 分页数据
     * @param <DOMAIN> 输入领域对象
     * @param <VO> 输出对象
     * @return 输出转换对象
     */
    public static <DOMAIN, VO> IPage<VO> convert(IPage<DOMAIN> page, Class<VO> clazz){

        return page.convert(v -> convert(v, clazz));
    }

    /**
     * 对象转换DOMAIN -> VO
     * @param original 输入对象
     * @param <DOMAIN> 输入领域对象
     * @param <VO> 输出对象
     * @return 输出转换对象
     */
    public static <DOMAIN, VO> List<VO> convertList(List<DOMAIN> original, Class<VO> clazz, BiConsumer<DOMAIN, VO> consumer){

        return original.parallelStream().map(entity -> convert(entity, clazz, consumer)).collect(Collectors.toList());
    }

    /**
     * 分页对象转换DOMAIN -> VO
     * @param page 分页原数据
     * @param <DOMAIN> 输入领域对象
     * @param <VO> 输出对象
     * @return 输出转换对象
     */
    public static <DOMAIN, VO> IPage<VO> convertPage(IPage<DOMAIN> page, Class<VO> clazz, BiConsumer<DOMAIN, VO> consumer){
        return page.convert(domain -> {
            VO vo = convert(domain, clazz);
            consumer.accept(domain, vo);
            return vo;
        });
    }

}
