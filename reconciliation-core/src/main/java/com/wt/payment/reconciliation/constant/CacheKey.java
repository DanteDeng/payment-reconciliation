package com.wt.payment.reconciliation.constant;

/**
 * 缓存key
 */
public interface CacheKey {

    /**
     * 对账处理机器编号
     */
    String MACHINE_NO = "reconciliation:machine:no:";
    /**
     * 对账处理机器map集合
     */
    String MACHINE_MAP = "reconciliation:machine:map:";
    /**
     * 对账处理中任务map
     */
    String RECONCILIATION_HANDLING_TASK_MAP = "reconciliation:handling:task:map:";
    /**
     * 数据导入处理中任务map
     */
    String IMPORT_DATA_HANDLING_TASK_MAP = "reconciliation:import:data:handling:task:map:";
    /**
     * 任务处理到的下标
     */
    String TASK_INDEX = "reconciliation:task:index:";
    /**
     * 任务总数
     */
    String TASK_TOTAL = "reconciliation:task:total:";
    /**
     * 数据总数
     */
    String DATA_TOTAL = "reconciliation:data:total:";
    /**
     * 数据已处理
     */
    String DATA_HANDLED = "reconciliation:data:handled:";

    /**
     * 对账唯一标识集合
     */
    String KEY_LIST = "reconciliation:key:list:";
    /**
     * 对账缓存数据map
     */
    String DATA_MAP = "reconciliation:data:map:";
    /**
     * 对账任务最大执行时间
     */
    String RECONCILIATION_TASK_MAX_EXECUTE = "reconciliation:task:max:execute:";
    /**
     * 数据导入任务最大执行时间
     */
    String IMPORT_DATA_TASK_MAX_EXECUTE = "reconciliation:import:data:task:max:execute:";
    /**
     * 导入数据任务处理到的下标
     */
    String IMPORT_DATA_INDEX = "reconciliation:import:data:index:";
}
