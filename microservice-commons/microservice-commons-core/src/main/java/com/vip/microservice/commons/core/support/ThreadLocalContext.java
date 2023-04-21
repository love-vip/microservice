package com.vip.microservice.commons.core.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author echo
 * @title: ThreadLocalContext
 * @date 2023/3/15 15:15
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ThreadLocalContext {

    private static final ThreadLocal<Map<String, Object>> THREAD_CONTEXT = ThreadLocal.withInitial(ConcurrentHashMap::new);

    /**
     * 取得thread context Map的实例。
     *
     * @return thread context Map的实例
     */
    private static Map<String, Object> getContextMap() {
        return THREAD_CONTEXT.get();
    }

    /**
     * 清理线程所有被hold住的对象。以便重用！
     */
    public static void remove() {
        getContextMap().clear();
    }

    /**
     * Remove object.
     *
     * @param key the key
     *
     * @return the object
     */
    public static Object remove(String key) {
        return getContextMap().remove(key);
    }

    /**
     * Put.
     *
     * @param key   the key
     * @param value the value
     */
    public static void set(String key, Object value) {
        getContextMap().put(key, value);
    }

    /**
     * Get object.
     *
     * @param key the key
     *
     * @return the object
     */
    public static Object get(String key) {
        return getContextMap().get(key);
    }

}


