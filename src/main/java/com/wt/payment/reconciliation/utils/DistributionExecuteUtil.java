package com.wt.payment.reconciliation.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 分布式执行器
 */
public class DistributionExecuteUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DistributionExecuteUtil.class);

    /**
     * 分布式同步执行
     * @param lockKey  锁定key
     * @param lockTime 锁定时间（秒）
     * @param retry    执行逻辑
     * @param task     重试次数
     * @param <R>      结果类型
     * @return 执行返回结果
     */
    public static <R> R synchronizeExecute(String lockKey, Long lockTime, int retry, Callable<R> task) {
        LOG.debug(String.format("distribution execute start lock key %s time %s retry %s", lockKey, lockTime, retry));
        if (lockKey == null || lockKey.isEmpty() || (lockTime != null && lockTime <= 0) || retry < 0) {
            throw new RuntimeException(String.format("distribution execute param illegal lock key is %s time is %s", lockKey, lockTime));
        }
        boolean lock;
        int tryCount = 0;
        do {
            lock = RedisUtil.lock(lockKey, lockTime);
            tryCount++;
            SleepUtil.sleepMilliSeconds(300L);
        } while (!lock && tryCount <= retry);

        if (!lock) {
            return null;
        }
        try {
            R r = task.call();
            LOG.debug(String.format("distribution execute end lock key %s time %s retry %s result %s", lockKey, lockTime, retry, r));
            return r;
        } catch (Exception e) {
            LOG.error(String.format("distribution execute error lock key is %s time %s retry %s", lockKey, lockTime, retry), e);
            throw new RuntimeException(e);
        } finally {
            RedisUtil.unlock(lockKey);
        }
    }

    /**
     * 分布式同步执行直到获取需要的记过
     * @param lockKey       锁定key
     * @param periodSeconds 间隔秒数
     * @param query         查询逻辑
     * @param execute       执行修改的逻辑
     * @param <R>           返回类型
     * @return 结果
     */
    public static <R> R synchronizeExecute(String lockKey, int periodSeconds, Callable<R> query, Callable<R> execute) {
        // 1.执行查询结果查到结果则返回
        R result = runCallSilently(query);
        if (result != null) {
            return result;
        }
        // 2.定时执行同步操作以设置结果
        ScheduledExecutorService schedule = new ScheduledThreadPoolExecutor(1);
        AtomicInteger counter = new AtomicInteger(0);
        schedule.scheduleAtFixedRate(() -> {
            counter.incrementAndGet();
            try {
                R r = runCallSilently(query);   // 查到结果则直接返回
                if (r == null) {
                    synchronizeExecute(lockKey, null, 0, execute);  // 无过期锁，当且仅当处理成功才释放
                }
            } finally {
                counter.decrementAndGet();
            }
        }, periodSeconds, periodSeconds, TimeUnit.SECONDS);
        // 3.阻塞线程直到获取到结果
        while (true) {
            result = runCallSilently(query);
            if (result != null) {
                if (!schedule.isShutdown()) {
                    schedule.shutdown();
                } else {
                    if (counter.get() == 0) { // 所有操作处理完停止线程阻塞
                        break;
                    }
                }
            } else {    // 没有处理完成时候进行休眠
                SleepUtil.sleepMilliSeconds(periodSeconds * 1000L);
            }

        }
        return result;
    }

    /**
     * 处理call逻辑异常以运行
     * @param call 执行逻辑
     * @param <R>  返回类型
     * @return 结果
     */
    private static <R> R runCallSilently(Callable<R> call) {
        R result;
        try {
            result = call.call();
        } catch (Exception e) {
            throw new RuntimeException(e);  // 检测异常转化为运行异常
        }
        return result;
    }
}
