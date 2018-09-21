package com.wt.payment.reconciliation.constant;

/**
 * 分布式任务key
 */
public interface DistributionTaskKey {

    /**
     * 对账导入数据任务总数锁
     */
    String IMPORT_TASK_TOTAL_LOCK = "reconciliation:import:task:total:lock";
    /**
     * 对账导入数据任务编号锁
     */
    String IMPORT_TASK_NO_LOCK = "reconciliation:import:task:no:lock";
    /**
     * 对账导入数据合并keys锁
     */
    String IMPORT_MERGE_KEYS_LOCK = "reconciliation:import:merge:keys:lock";
    /**
     * 对账任务总数锁
     */
    String CHECK_TASK_TOTAL_LOCK = "reconciliation:check:task:no:lock";
    /**
     * 对账任务编号锁
     */
    String CHECK_TASK_NO_LOCK = "reconciliation:check:task:no:lock";

    /**
     * 导入数据临时list锁
     */
    String IMPORT_TEMP_LIST_LOCK = "reconciliation:import:temp:list:lock";

}
