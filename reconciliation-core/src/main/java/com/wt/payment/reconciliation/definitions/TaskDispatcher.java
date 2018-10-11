package com.wt.payment.reconciliation.definitions;

/**
 * 任务分发器（对账任务启动入口）
 */
public interface TaskDispatcher {

    /**
     * 任务分发
     */
    void dispatch();

}
