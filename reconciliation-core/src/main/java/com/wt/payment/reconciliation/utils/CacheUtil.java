package com.wt.payment.reconciliation.utils;

import com.wt.payment.reconciliation.definitions.CacheOperator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 缓存操作工具
 */
public class CacheUtil {

    /**
     * 缓存操作实例
     */
    private static CacheOperator cacheOperator;

    /**
     * 设置缓存操作实例
     * @param cacheOperator 缓存操作实例
     */
    private static void setCacheOperator(CacheOperator cacheOperator) {
        CacheUtil.cacheOperator = cacheOperator;
    }

    /**
     * 获取缓存操作实例
     * @return 缓存操作实例
     */
    private static CacheOperator getCacheOperator() {
        CacheOperator cacheOperator = CacheUtil.cacheOperator;
        if (cacheOperator == null) {
            throw new NullPointerException("please invoke CacheUtil.setCacheOperator(CacheOperator cacheOperator) first to instance a cache operator");
        }
        return cacheOperator;
    }

    /**
     * key value 设置
     * @param key    键
     * @param value  值
     * @param expire 过期时间
     */
    public static void set(String key, Object value, Long expire) {
        cacheOperator.set(key, value, expire);
    }

    /**
     * key value 设置
     * @param key   键
     * @param value 值
     */
    public static void set(String key, Object value) {
        cacheOperator.set(key, value);
    }

    /**
     * 获取value
     * @param key    键
     * @param clazz  类
     * @param expire 过期时间
     * @param <T>    类型
     * @return value
     */
    public static <T> T get(String key, Class<T> clazz, Long expire) {
        return cacheOperator.get(key, clazz, expire);
    }

    /**
     * 获取value
     * @param key   键
     * @param clazz 类
     * @param <T>   类型
     * @return value
     */
    public static <T> T get(String key, Class<T> clazz) {
        return cacheOperator.get(key, clazz);
    }

    /**
     * 获取value
     * @param key    键
     * @param expire 过期时间
     * @return value
     */
    public static String get(String key, Long expire) {
        return cacheOperator.get(key, expire);
    }

    /**
     * 获取value
     * @param key 键
     * @return value
     */
    public static String get(String key) {
        return cacheOperator.get(key);
    }

    /**
     * 删除key
     * @param key 键
     */
    public static boolean delete(String key) {
        return cacheOperator.delete(key);
    }

    /**
     * 按key锁定
     * @param key 锁定的key
     * @return 锁定结果
     */
    public static boolean lock(String key, Long expire) {
        return cacheOperator.lock(key, expire);
    }

    /**
     * 解锁key
     * @param key 锁定的key
     * @return 结果结果
     */
    public static boolean unlock(String key) {
        return cacheOperator.unlock(key);
    }

    /**
     * 设置int值
     * @param key   键
     * @param value 值
     */
    public static void setInt(String key, int value) {
        cacheOperator.setInt(key, value);
    }

    /**
     * 设置int值
     * @param key    键
     * @param value  值
     * @param expire 过期时间
     */
    public static void setInt(String key, int value, Long expire) {
        cacheOperator.setInt(key, value, expire);
    }

    /**
     * 获取int值
     * @param key 键
     * @return 值
     */
    public static Integer getInt(String key) {
        return cacheOperator.getInt(key);
    }

    /**
     * 获取int值
     * @param key    键
     * @param expire 过期时间
     * @return 值
     */
    public static Integer getInt(String key, Long expire) {
        return cacheOperator.getInt(key, expire);
    }

    /**
     * 设置long值
     * @param key   键
     * @param value 值
     */
    public static void setLong(String key, long value) {
        cacheOperator.setLong(key, value);
    }

    /**
     * 设置long值
     * @param key    键
     * @param value  值
     * @param expire 过期时间
     */
    public static void setLong(String key, long value, Long expire) {
        cacheOperator.setLong(key, value, expire);
    }

    /**
     * 获取long值
     * @param key 键
     * @return 值
     */
    public static Long getLong(String key) {
        return cacheOperator.getLong(key);
    }

    /**
     * 获取long值
     * @param key    键
     * @param expire 过期时间
     * @return 值
     */
    public static Long getLong(String key, Long expire) {
        return cacheOperator.getLong(key, expire);
    }

    /**
     * 递增并获取int
     * @param key  键
     * @param size 增长值
     * @return 值
     */
    public static Integer incrementAndGet(String key, int size) {
        return cacheOperator.incrementAndGet(key, size);
    }

    /**
     * 递增并获取int
     * @param key    键
     * @param size   增长值
     * @param expire 过期时间
     * @return 值
     */
    public static Integer incrementAndGet(String key, int size, Long expire) {
        return cacheOperator.incrementAndGet(key, size, expire);
    }

    /**
     * 递增并获取int
     * @param key  键
     * @param size 增长值
     * @return 值
     */
    public static Integer decrementAndGet(String key, int size) {
        return cacheOperator.decrementAndGet(key, size);
    }

    /**
     * 递增并获取int
     * @param key    键
     * @param size   增长值
     * @param expire 过期时间
     * @return 值
     */
    public static Integer decrementAndGet(String key, int size, Long expire) {
        return cacheOperator.decrementAndGet(key, size, expire);
    }

    /**
     * 设置hash值
     * @param key     键
     * @param hashKey map中的键
     * @param value   map中的值
     */
    public static <V> void setHash(String key, String hashKey, V value) {
        cacheOperator.setHash(key, hashKey, value);
    }

    /**
     * map中全部的k-v存入缓存
     * @param key 键
     * @param map map
     */
    public static <T> void setHashAll(String key, Map<String, T> map) {
        cacheOperator.setHashAll(key, map);
    }

    /**
     * 设置hash值
     * @param key     键
     * @param hashKey map中的键
     * @return 结果
     */
    public static boolean removeHash(String key, String hashKey) {
        return cacheOperator.removeHash(key, hashKey);
    }

    /**
     * 获取hash值
     * @param key     键
     * @param hashKey map中的键
     * @return map中的值
     */
    public static Object getHash(String key, String hashKey) {
        return cacheOperator.getHash(key, hashKey);
    }

    /**
     * 获取map
     * @param key 键
     * @return map
     */
    public static Map<String, Object> getMap(String key) {
        return cacheOperator.getMap(key);
    }

    /**
     * 获取map全部的key
     * @param key 键
     * @return set
     */
    public static Set<String> getMapKeys(String key) {
        return cacheOperator.getMapKeys(key);
    }

    /**
     * 获取map全部的value
     * @param key 键
     * @return list
     */
    public static List<Object> getMapValues(String key) {
        return cacheOperator.getMapValues(key);
    }

    /**
     * 获取map size
     * @param key 键
     * @return map size
     */
    public static Long getMapSize(String key) {
        return cacheOperator.getMapSize(key);
    }

    /**
     * 往set中添加元素
     * @param key   键
     * @param value 值
     * @return 添加是否成功
     */
    public static <V> boolean addToSet(String key, V value) {
        return cacheOperator.addToSet(key, value);
    }

    /**
     * 获取set
     * @param key 键
     * @return set
     */
    public static Set<Object> getSet(String key) {
        return cacheOperator.getSet(key);
    }

    /**
     * 获取set的size
     * @param key 键
     * @return size
     */
    public static Long getSetSize(String key) {
        return cacheOperator.getSetSize(key);
    }

    /**
     * 往list中添加元素
     * @param key   键
     * @param value 值
     * @return 添加是否成功
     */
    public static <V> boolean addToList(String key, V value) {
        return cacheOperator.addToList(key, value);
    }

    /**
     * 往list中添加元素
     * @param key    键
     * @param values 值集合
     * @return 添加是否成功
     */
    public static boolean addAllToList(String key, Collection<Object> values) {
        return cacheOperator.addAllToList(key, values);
    }

    /**
     * 从list中取出子集
     * @param key   键
     * @param start 起始位置
     * @return 子集
     */
    public static List<Object> subList(String key, int start, int end) {
        return cacheOperator.subList(key, start, end);
    }

    /**
     * 取出整个list全部元素
     * @param key 键
     * @return 子集
     */
    public static List<Object> getList(String key) {
        return cacheOperator.getList(key);
    }

    /**
     * 获取list大小
     * @param key 键
     * @return list大小
     */
    public static Long getListSize(String key) {
        return cacheOperator.getListSize(key);
    }

}
