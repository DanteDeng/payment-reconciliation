package com.wt.payment.reconciliation.definitions;

/**
 * 分布式任务执行器
 */
public interface DistributionExecutor {

    /**
     * 按照执行器执行
     * @param executorNo 执行器编号
     */
    void execute(String executorNo);

}
