package com.wt.payment.reconciliation;

import com.alibaba.fastjson.parser.ParserConfig;
import com.wt.payment.reconciliation.definitions.TaskDispatcher;
import com.wt.payment.reconciliation.luatest.LuaTest;
import com.wt.payment.reconciliation.model.ProcessInfo;
import com.wt.payment.reconciliation.utils.ReconciliationAssembler;
import com.wt.payment.reconciliation.utils.RedisKeyUtil;
import com.wt.payment.reconciliation.utils.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static com.wt.payment.reconciliation.utils.SpringContextUtil.getBean;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PaymentReconciliationApplicationTests {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentReconciliationApplicationTests.class);

    @Test
    public void executeTaskTest() {
        // 分发任务并启动对账
        TaskDispatcher dispatcher = getBean(TaskDispatcher.class);
        dispatcher.dispatch();
    }

    @Test
    public void redisTest() {
        RedisUtil.setHash("dengyu", "age", 28);
        Object hash = RedisUtil.getHash("dengyu", "age");

        System.out.println("hash = " + hash);
    }

    @Test
    public void redisIntTest() {
        //Integer test = RedisUtil.decrementAndGet("test", 1);// 比对方数据加入缓存
        Integer integer = RedisUtil.getInt(RedisKeyUtil.getReconciliationTaskTotal("0"));
        System.out.println("test          =             " + integer);
    }

    @Autowired
    private RedisTemplate<String, Object> template;

    @Autowired
    private ListOperations<String, Object> listOperations;

    @Test
    public void redisListTest() {
        Collection<Object> list = new ArrayList<>();
        list.add("123");
        list.add("1234");
        list.add("12345");
        boolean b = RedisUtil.addAllToList("123", list);
        System.out.println(b);
//        listOperations.leftPushAll("123", list);
//        template.opsForList().leftPushAll("123", list);
    }

    @Autowired
    private LuaTest luaTest;

    @Test
    public void testLua() {

        Object limit = luaTest.test("192.168.10.11", 1, 300);
        LOG.info("limit --------------------------------------------------------- " + limit);
    }

    @Test
    public void testLua1() {
        RedisUtil.executeLuaScript("gen_map_values_list", Void.class, Arrays.asList(RedisKeyUtil.getImportedDataMap("001")), "aTempList");
        //luaTest.redisAddScriptExec();
    }

    @Test
    public void testLua2() {
        List<String> keys = new ArrayList<>();
        keys.add("reconciliation:data:map:dataType001:20180913");
        keys.add("reconciliation:data:map:dataType002:20180913");
        keys.add("reconciliation:data:map:dataType003:20180913");
        Void v = RedisUtil.executeLuaScript("merge_different_type_keys", Void.class, keys, "reconciliation:data:list:process001:20180913");
        LOG.info("test --------------------------------------------------------- " + v);
    }

    @Test
    public void testGetMap() {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        LOG.info("test get map start");
        Map<String, Object> dataMap = RedisUtil.getMap(RedisKeyUtil.getImportedDataMap("001"));
        LOG.info("test get map end");
        LOG.info("test get map size start");
        Long mapSize = RedisUtil.getMapSize(RedisKeyUtil.getImportedDataMap("001"));
        LOG.info("test get map size end " + mapSize);
    }

    @Test
    public void testGetProcessInfo() {
        ProcessInfo processInfo = ReconciliationAssembler.assembleProcessInfoByProcessNo(null);
        LOG.info("processInfo --------------------------------------------------------- " + processInfo);
    }

    @Test
    public void testAddAllToSet() {
        Set<Object> set = new HashSet<>();
        set.add("111");
        set.add("121");
        set.add("211");
        RedisUtil.addAllToList("test2", set);
        LOG.info(String.format("set %s", RedisUtil.getList("test2")));
    }
}
