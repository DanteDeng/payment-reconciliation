package com.wt.payment.reconciliation;

import com.alibaba.fastjson.parser.ParserConfig;
import com.wt.payment.reconciliation.definitions.BeanManager;
import com.wt.payment.reconciliation.definitions.TaskDispatcher;
import com.wt.payment.reconciliation.model.ProcessInfo;
import com.wt.payment.reconciliation.utils.CacheKeyUtil;
import com.wt.payment.reconciliation.utils.CacheUtil;
import com.wt.payment.reconciliation.utils.ReconciliationAssembler;
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

@RunWith(SpringRunner.class)
@SpringBootTest
public class PaymentReconciliationApplicationTests {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentReconciliationApplicationTests.class);

    @Autowired
    private BeanManager beanManager;

    @Test
    public void executeTaskTest() {
        // 分发任务并启动对账
        TaskDispatcher dispatcher = beanManager.getBean(TaskDispatcher.class);
        dispatcher.dispatch();
    }

    @Test
    public void redisTest() {
        CacheUtil.setHash("dengyu", "age", 28);
        Object hash = CacheUtil.getHash("dengyu", "age");

        System.out.println("hash = " + hash);
    }

    @Test
    public void redisIntTest() {
        //Integer test = RedisUtil.decrementAndGet("test", 1);// 比对方数据加入缓存
        Integer integer = CacheUtil.getInt(CacheKeyUtil.getReconciliationTaskTotal("0"));
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
        boolean b = CacheUtil.addAllToList("123", list);
        System.out.println(b);
//        listOperations.leftPushAll("123", list);
//        template.opsForList().leftPushAll("123", list);
    }

    @Test
    public void testGetMap() {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        LOG.info("test get map start");
        Map<String, Object> dataMap = CacheUtil.getMap(CacheKeyUtil.getImportedDataMap("001"));
        LOG.info("test get map end");
        LOG.info("test get map size start");
        Long mapSize = CacheUtil.getMapSize(CacheKeyUtil.getImportedDataMap("001"));
        LOG.info("test get map size end " + mapSize);
    }

    @Test
    public void testGetProcessInfo() {
        ProcessInfo processInfo = ReconciliationAssembler.getProcessInfoByProcessNo(null);
        LOG.info("processInfo --------------------------------------------------------- " + processInfo);
    }

    @Test
    public void testAddAllToSet() {
        Set<Object> set = new HashSet<>();
        set.add("111");
        set.add("121");
        set.add("211");
        CacheUtil.addAllToList("test2", set);
        LOG.info(String.format("set %s", CacheUtil.getList("test2")));
    }
}
