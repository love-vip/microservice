package com.vip.microservice.commons.core.support;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.*;

/**
 * @author echo
 * @title: BaseService
 * @date 2023/3/15 17:33
 * @param <T> the type parameter
 */
@Slf4j
public abstract class BaseService<T> implements IService<T> {

    /**
     * The Mapper.
     */
    @Autowired
    protected BaseMapper<T> mapper;

    /**
     * Gets mapper.
     *
     * @return the mapper
     */
    public Mapper<T> getMapper() {
        return mapper;
    }

    /**
     * Select list.
     *
     * @param wrapper the wrapper
     *
     * @return the list
     */
    @Override
    public List<T> select(Wrapper<T> wrapper) {
        return mapper.selectList(wrapper);
    }

    /**
     * Select by key t.
     *
     * @param key the key
     *
     * @return the t
     */
    @Override
    public T selectByKey(Serializable key) {
        return mapper.selectById(key);
    }

    /**
     * Select one t.
     *
     * @param wrapper the wrapper
     *
     * @return the t
     */
    @Override
    public T selectOne(Wrapper<T> wrapper) {
        return mapper.selectOne(wrapper);
    }

    /**
     * Select count int.
     *
     * @param wrapper the wrapper
     *
     * @return the int
     */
    @Override
    public long selectCount(Wrapper<T> wrapper) {
        return mapper.selectCount(wrapper);
    }

    /**
     * Save int.
     *
     * @param record the record
     *
     * @return the int
     */
    @Override
    public int save(T record) {
        return mapper.insert(record);
    }

    /**
     * Update int.
     *
     * @param entity the entity
     *
     * @return the int
     */
    @Override
    public int update(T entity) {
        return mapper.updateById(entity);
    }

    /**
     * Delete int.
     *
     * @param wrapper the wrapper
     *
     * @return the int
     */
    @Override
    public int delete(Wrapper<T> wrapper) {
        return mapper.delete(wrapper);
    }

    /**
     * Delete by key int.
     *
     * @param key the key
     *
     * @return the int
     */
    @Override
    public int deleteByKey(Serializable key) {
        return mapper.deleteById(key);
    }

    /**
     * Update by example int.
     *
     * @param record  the record
     * @param wrapper the wrapper
     *
     * @return the int
     */
    @Override
    public int updateByWrapper(T record, Wrapper<T> wrapper) {
        return mapper.update(record, wrapper);
    }


}