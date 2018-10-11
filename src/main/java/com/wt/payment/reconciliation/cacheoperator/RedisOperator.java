package com.wt.payment.reconciliation.cacheoperator;

import com.google.gson.Gson;
import com.wt.payment.reconciliation.definitions.CacheOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisOperator implements CacheOperator {
    /**
     * redis操作模板
     */
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * redis value操作模板实例
     */
    private ValueOperations<String, String> valueOperations;
    /**
     * redis hash操作模板实例
     */
    private HashOperations<String, String, Object> hashOperations;
    /**
     * redis list操作模板实例
     */
    private ListOperations<String, Object> listOperations;
    /**
     * redis set操作模板实例
     */
    private SetOperations<String, Object> setOperations;
    /**
     * redis ZSet操作模板实例
     */
    private ZSetOperations<String, Object> zSetOperations;
    /**
     * 默认过期时长，单位：秒
     */
    private final static long DEFAULT_EXPIRE = 60 * 60 * 24;
    /**
     * 不设置过期时长
     */
    private final static long NOT_EXPIRE = -1;
    /**
     * 锁定值
     */
    private final static String LOCK_VALUE = "1";
    /**
     * json转换操作实例
     */
    private final static Gson gson = new Gson();

    /**
     * 初始化工具类
     * @param redisTemplate   redis操作模板
     * @param valueOperations value操作模板实例
     * @param hashOperations  hash操作模板实例
     * @param listOperations  list操作模板实例
     * @param setOperations   set操作模板实例
     * @param zSetOperations  ZSet操作模板实例
     */
    @Autowired
    private RedisOperator(RedisTemplate<String, Object> redisTemplate, ValueOperations<String, String> valueOperations,
                          HashOperations<String, String, Object> hashOperations, ListOperations<String, Object> listOperations,
                          SetOperations<String, Object> setOperations, ZSetOperations<String, Object> zSetOperations) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = valueOperations;
        this.hashOperations = hashOperations;
        this.listOperations = listOperations;
        this.setOperations = setOperations;
        this.zSetOperations = zSetOperations;
    }

    /**
     * key value 设置
     * @param key    键
     * @param value  值
     * @param expire 过期时间
     */
    public void set(String key, Object value, Long expire) {
        valueOperations.set(key, toJson(value));
        if (expire != null) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    /**
     * key value 设置
     * @param key   键
     * @param value 值
     */
    public void set(String key, Object value) {
        set(key, value, DEFAULT_EXPIRE);
    }

    /**
     * 获取value
     * @param key    键
     * @param clazz  类
     * @param expire 过期时间
     * @param <T>    类型
     * @return value
     */
    public <T> T get(String key, Class<T> clazz, Long expire) {
        String value = valueOperations.get(key);
        if (expire != null) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : fromJson(value, clazz);
    }

    /**
     * 获取value
     * @param key   键
     * @param clazz 类
     * @param <T>   类型
     * @return value
     */
    public <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, NOT_EXPIRE);
    }

    /**
     * 获取value
     * @param key    键
     * @param expire 过期时间
     * @return value
     */
    public String get(String key, Long expire) {
        String value = valueOperations.get(key);
        if (expire != null) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value;
    }

    /**
     * 获取value
     * @param key 键
     * @return value
     */
    public String get(String key) {
        return get(key, (Long) null);
    }

    /**
     * 删除key
     * @param key 键
     */
    public boolean delete(String key) {
        boolean deleted = false;
        Boolean delete = redisTemplate.delete(key);
        if (delete != null && delete) {
            deleted = true;
        }
        return deleted;
    }

    /**
     * 按key锁定
     * @param key 锁定的key
     * @return 锁定结果
     */
    public boolean lock(String key, Long expire) {
        boolean locked = false;
        BoundValueOperations<String, Object> boundValueOps = redisTemplate.boundValueOps(key);
        Boolean ifAbsent = boundValueOps.setIfAbsent(LOCK_VALUE);
        if (ifAbsent != null && ifAbsent) {
            locked = true;
            if (expire != null) {
                redisTemplate.expire(key, expire, TimeUnit.SECONDS);
            }
        }

        return locked;
    }

    /**
     * 解锁key
     * @param key 锁定的key
     * @return 结果结果
     */
    public boolean unlock(String key) {
        return delete(key);
    }

    /**
     * 设置int值
     * @param key   键
     * @param value 值
     */
    public void setInt(String key, int value) {
        setInt(key, value, null);
    }

    /**
     * 设置int值
     * @param key    键
     * @param value  值
     * @param expire 过期时间
     */
    public void setInt(String key, int value, Long expire) {
        set(key, value, expire);
    }

    /**
     * 获取int值
     * @param key 键
     * @return 值
     */
    public Integer getInt(String key) {
        return getInt(key, null);
    }

    /**
     * 获取int值
     * @param key    键
     * @param expire 过期时间
     * @return 值
     */
    public Integer getInt(String key, Long expire) {
        return get(key, Integer.class, expire);
    }

    /**
     * 设置long值
     * @param key   键
     * @param value 值
     */
    public void setLong(String key, long value) {
        setLong(key, value, null);
    }

    /**
     * 设置long值
     * @param key    键
     * @param value  值
     * @param expire 过期时间
     */
    public void setLong(String key, long value, Long expire) {
        set(key, value, expire);
    }

    /**
     * 获取long值
     * @param key 键
     * @return 值
     */
    public Long getLong(String key) {
        return getLong(key, null);
    }

    /**
     * 获取long值
     * @param key    键
     * @param expire 过期时间
     * @return 值
     */
    public Long getLong(String key, Long expire) {
        return get(key, Long.class, expire);
    }

    /**
     * 递增并获取int
     * @param key  键
     * @param size 增长值
     * @return 值
     */
    public Integer incrementAndGet(String key, int size) {

        return incrementAndGet(key, size, null);
    }

    /**
     * 递增并获取int
     * @param key    键
     * @param size   增长值
     * @param expire 过期时间
     * @return 值
     */
    public Integer incrementAndGet(String key, int size, Long expire) {
        Long result = valueOperations.increment(key, size);
        if (result != null && expire != null) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return result == null ? null : Math.toIntExact(result);
    }

    /**
     * 递增并获取int
     * @param key  键
     * @param size 增长值
     * @return 值
     */
    public Integer decrementAndGet(String key, int size) {
        return decrementAndGet(key, size, null);
    }

    /**
     * 递增并获取int
     * @param key    键
     * @param size   增长值
     * @param expire 过期时间
     * @return 值
     */
    public Integer decrementAndGet(String key, int size, Long expire) {
        return incrementAndGet(key, -size, expire);
    }

    /**
     * 设置hash值
     * @param key     键
     * @param hashKey map中的键
     * @param value   map中的值
     */
    public <V> void setHash(String key, String hashKey, V value) {
        hashOperations.put(key, hashKey, value);
    }

    /**
     * map中全部的k-v存入缓存
     * @param key 键
     * @param map map
     */
    public <T> void setHashAll(String key, Map<String, T> map) {
        hashOperations.putAll(key, map);
    }

    /**
     * 设置hash值
     * @param key     键
     * @param hashKey map中的键
     * @return 结果
     */
    public boolean removeHash(String key, String hashKey) {
        boolean deleted = false;
        Long delete = hashOperations.delete(key, hashKey);
        if (delete == 1) {
            deleted = true;
        }
        return deleted;
    }

    /**
     * 获取hash值
     * @param key     键
     * @param hashKey map中的键
     * @return map中的值
     */
    public Object getHash(String key, String hashKey) {
        return hashOperations.get(key, hashKey);
    }

    /**
     * 获取map
     * @param key 键
     * @return map
     */
    public Map<String, Object> getMap(String key) {
        return hashOperations.entries(key);
    }

    /**
     * 获取map全部的key
     * @param key 键
     * @return set
     */
    public Set<String> getMapKeys(String key) {
        return hashOperations.keys(key);
    }

    /**
     * 获取map全部的value
     * @param key 键
     * @return list
     */
    public List<Object> getMapValues(String key) {
        return hashOperations.values(key);
    }

    /**
     * 获取map size
     * @param key 键
     * @return map size
     */
    public Long getMapSize(String key) {
        return hashOperations.size(key);
    }

    /**
     * 往set中添加元素
     * @param key   键
     * @param value 值
     * @return 添加是否成功
     */
    public <V> boolean addToSet(String key, V value) {
        boolean added = false;
        Long add = setOperations.add(key, value);
        if (add != null && add == 1) {
            added = true;
        }
        return added;
    }

    /**
     * 获取set
     * @param key 键
     * @return set
     */
    public Set<Object> getSet(String key) {
        return setOperations.members(key);
    }

    /**
     * 获取set的size
     * @param key 键
     * @return size
     */
    public Long getSetSize(String key) {
        return setOperations.size(key);
    }

    /**
     * 往list中添加元素
     * @param key   键
     * @param value 值
     * @return 添加是否成功
     */
    public <V> boolean addToList(String key, V value) {
        boolean added = false;
        Long add = listOperations.leftPush(key, value);
        if (add != null && add == 1) {
            added = true;
        }
        return added;
    }

    /**
     * 往list中添加元素
     * @param key    键
     * @param values 值集合
     * @return 添加是否成功
     */
    public boolean addAllToList(String key, Collection<Object> values) {
        boolean added = false;
        Long add = listOperations.leftPushAll(key, values);
        if (add != null && add > 0) {
            added = true;
        }
        return added;
    }

    /**
     * 从list中取出子集
     * @param key   键
     * @param start 起始位置
     * @return 子集
     */
    public List<Object> subList(String key, int start, int end) {
        return listOperations.range(key, start, end - 1);
    }

    /**
     * 取出整个list全部元素
     * @param key 键
     * @return 子集
     */
    public List<Object> getList(String key) {
        return listOperations.range(key, 0, -1);
    }

    /**
     * 获取list大小
     * @param key 键
     * @return list大小
     */
    public Long getListSize(String key) {
        return listOperations.size(key);
    }

    /**
     * 执行lua脚本
     * @param scriptName 脚本名称
     * @param tClass     返回结果类型
     * @param keys       传入参数集合
     * @param args       补充传入的参数
     * @param <T>        返回类型
     * @return 结果
     */
    public <T> T executeLuaScript(String scriptName, Class<T> tClass, List<String> keys, Object... args) {
        DefaultRedisScript<T> script = new DefaultRedisScript<>();
        script.setResultType(tClass);
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/luascript/" + scriptName + ".lua")));
        return redisTemplate.execute(script, keys, args);
    }

    /**
     * Object转成JSON数据
     */
    private String toJson(Object object) {
        if (object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String) {
            return String.valueOf(object);
        }
        return gson.toJson(object);
    }

    /**
     * JSON数据，转成Object
     */
    private <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

}