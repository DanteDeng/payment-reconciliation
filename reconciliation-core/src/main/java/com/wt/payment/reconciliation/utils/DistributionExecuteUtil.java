package com.wt.payment.reconciliation.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static <R> R synchronizeExecute(String lockKey, Long lockTime, int retry, Callback<R> task) {
        LOG.debug(String.format("distribution execute start lock key %s time %s retry %s", lockKey, lockTime, retry));
        if (lockKey == null || lockKey.isEmpty() || (lockTime != null && lockTime <= 0) || retry < 0) {
            throw new RuntimeException(String.format("distribution execute param illegal lock key is %s time is %s", lockKey, lockTime));
        }
        boolean lock;
        int tryCount = 0;
        do {
            lock = CacheUtil.lock(lockKey, lockTime);
            tryCount++;
            SleepUtil.sleepMillis(300L);
        } while (!lock && tryCount <= retry);

        if (!lock) {
            return null;
        }
        try {
            R r = task.call();
            LOG.debug(String.format("distribution execute end lock key %s time %s retry %s result %s", lockKey, lockTime, retry, r));
            return r;
        } finally {
            CacheUtil.unlock(lockKey);
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
    public static <R> R synchronizeExecute(String lockKey, int periodSeconds, Callback<R> query, Callback<R> execute) {
        LOG.debug(String.format("synchronize execute start lock key %s period %s seconds", lockKey, periodSeconds));
        R result;
        while (true) {
            result = query.call(); // 执行查询结果查到结果则返回
            if (result != null) {
                break;
            } else {
                boolean lock = CacheUtil.lock(lockKey, null);
                if (lock) {
                    try {
                        result = query.call();  // 执行查询结果查到结果则返回
                        if (result != null) {
                            break;
                        } else {
                            execute.call();  // 查询无结果执行处理逻辑以设置结果
                        }
                    } finally {
                        CacheUtil.unlock(lockKey);
                    }
                } else {    // 竞争锁失败则休眠指定时间后再行获取
                    SleepUtil.sleepSeconds(periodSeconds * 1L);
                }
            }
        }
        LOG.debug(String.format("synchronize execute end lock key %s period %s seconds result is %s", lockKey, periodSeconds, result));
        return result;
    }

    /**
     * 回调类
     * @param <R> 执行结果
     */
    public interface Callback<R> {
        R call();
    }
}
