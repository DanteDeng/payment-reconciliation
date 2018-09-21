package com.wt.payment.reconciliation.luatest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class LuaTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    public Object test(String ip, int limit, int timeout) {
        List<String> keys = Arrays.asList(ip, String.valueOf(limit), String.valueOf(timeout));
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("/luascript/test.lua")));
        return redisTemplate.execute(script, keys);
    }

    private DefaultRedisScript<List> getRedisScript;

    @PostConstruct
    public void init() {
        getRedisScript = new DefaultRedisScript<>();
        getRedisScript.setResultType(List.class);
        getRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("luascript/json.lua")));
    }

    public void redisAddScriptExec() {
        /**
         * List设置lua的KEYS
         */
        List<String> keyList = new ArrayList<>();
        keyList.add("count");
        keyList.add("rate.limiting:127.0.0.1");

        /**
         * 用Mpa设置Lua的ARGV[1]
         */
        Map<String, Object> argvMap = new HashMap<>();
        argvMap.put("expire", 10000);
        argvMap.put("times", 10);

        /**
         * 调用脚本并执行
         */
        List result = redisTemplate.execute(getRedisScript, keyList, argvMap);
        System.out.println(result);

    }


}
