package com.wt.payment.reconciliation.definitions;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 缓存操作
 */
public interface CacheOperator {

    /**
     * key value 设置
     * @param key    键
     * @param value  值
     * @param expire 过期时间
     */
    void set(String key, Object value, Long expire);

    /**
     * key value 设置
     * @param key   键
     * @param value 值
     */
    void set(String key, Object value);

    /**
     * 获取value
     * @param key    键
     * @param clazz  类
     * @param expire 过期时间
     * @param <T>    类型
     * @return value
     */
    <T> T get(String key, Class<T> clazz, Long expire);

    /**
     * 获取value
     * @param key   键
     * @param clazz 类
     * @param <T>   类型
     * @return value
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 获取value
     * @param key    键
     * @param expire 过期时间
     * @return value
     */
    String get(String key, Long expire);

    /**
     * 获取value
     * @param key 键
     * @return value
     */
    String get(String key);

    /**
     * 删除key
     * @param key 键
     */
    boolean delete(String key);

    /**
     * 按key锁定
     * @param key 锁定的key
     * @return 锁定结果
     */
    boolean lock(String key, Long expire);

    /**
     * 解锁key
     * @param key 锁定的key
     * @return 结果结果
     */
    boolean unlock(String key);

    /**
     * 设置int值
     * @param key   键
     * @param value 值
     */
    void setInt(String key, int value);

    /**
     * 设置int值
     * @param key    键
     * @param value  值
     * @param expire 过期时间
     */
    void setInt(String key, int value, Long expire);

    /**
     * 获取int值
     * @param key 键
     * @return 值
     */
    Integer getInt(String key);

    /**
     * 获取int值
     * @param key    键
     * @param expire 过期时间
     * @return 值
     */
    Integer getInt(String key, Long expire);

    /**
     * 设置long值
     * @param key   键
     * @param value 值
     */
    void setLong(String key, long value);

    /**
     * 设置long值
     * @param key    键
     * @param value  值
     * @param expire 过期时间
     */
    void setLong(String key, long value, Long expire);

    /**
     * 获取long值
     * @param key 键
     * @return 值
     */
    Long getLong(String key);

    /**
     * 获取long值
     * @param key    键
     * @param expire 过期时间
     * @return 值
     */
    Long getLong(String key, Long expire);

    /**
     * 递增并获取int
     * @param key  键
     * @param size 增长值
     * @return 值
     */
    Integer incrementAndGet(String key, int size);

    /**
     * 递增并获取int
     * @param key    键
     * @param size   增长值
     * @param expire 过期时间
     * @return 值
     */
    Integer incrementAndGet(String key, int size, Long expire);

    /**
     * 递增并获取int
     * @param key  键
     * @param size 增长值
     * @return 值
     */
    Integer decrementAndGet(String key, int size);

    /**
     * 递增并获取int
     * @param key    键
     * @param size   增长值
     * @param expire 过期时间
     * @return 值
     */
    Integer decrementAndGet(String key, int size, Long expire);

    /**
     * 设置hash值
     * @param key     键
     * @param hashKey map中的键
     * @param value   map中的值
     */
    <V> void setHash(String key, String hashKey, V value);

    /**
     * map中全部的k-v存入缓存
     * @param key 键
     * @param map map
     */
    <T> void setHashAll(String key, Map<String, T> map);

    /**
     * 设置hash值
     * @param key     键
     * @param hashKey map中的键
     * @return 结果
     */
    boolean removeHash(String key, String hashKey);

    /**
     * 获取hash值
     * @param key     键
     * @param hashKey map中的键
     * @return map中的值
     */
    Object getHash(String key, String hashKey);

    /**
     * 获取map
     * @param key 键
     * @return map
     */
    Map<String, Object> getMap(String key);

    /**
     * 获取map全部的key
     * @param key 键
     * @return set
     */
    Set<String> getMapKeys(String key);

    /**
     * 获取map全部的value
     * @param key 键
     * @return list
     */
    List<Object> getMapValues(String key);

    /**
     * 获取map size
     * @param key 键
     * @return map size
     */
    Long getMapSize(String key);

    /**
     * 往set中添加元素
     * @param key   键
     * @param value 值
     * @return 添加是否成功
     */
    <V> boolean addToSet(String key, V value);

    /**
     * 获取set
     * @param key 键
     * @return set
     */
    Set<Object> getSet(String key);

    /**
     * 获取set的size
     * @param key 键
     * @return size
     */
    Long getSetSize(String key);

    /**
     * 往list中添加元素
     * @param key   键
     * @param value 值
     * @return 添加是否成功
     */
    <V> boolean addToList(String key, V value);

    /**
     * 往list中添加元素
     * @param key    键
     * @param values 值集合
     * @return 添加是否成功
     */
    boolean addAllToList(String key, Collection<Object> values);

    /**
     * 从list中取出子集
     * @param key   键
     * @param start 起始位置
     * @return 子集
     */
    List<Object> subList(String key, int start, int end);

    /**
     * 取出整个list全部元素
     * @param key 键
     * @return 子集
     */
    List<Object> getList(String key);

    /**
     * 获取list大小
     * @param key 键
     * @return list大小
     */
    Long getListSize(String key);

}
