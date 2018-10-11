package com.wt.payment.reconciliation.utils;

import com.wt.payment.reconciliation.constant.CacheKey;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * redis key 处理工具
 */
public class CacheKeyUtil {

    private static final String KEY_SEPARATOR = ":";

    private static final String DATE_FORMAT_PATTERN = "yyyyMMdd";

    /**
     * 获取对账处理机器编号
     * @return 对账处理机器编号
     */
    public static String getMachineNo() {
        return CacheKey.MACHINE_NO + getToday() + KEY_SEPARATOR;
    }

    /**
     * 获取对账机器的map集合
     * @return 对账机器的map集合
     */
    public static String getMachineMap() {
        return CacheKey.MACHINE_MAP + getToday();
    }

    /**
     * 获取处理中对账任务的map集合
     * @return 处理中对账任务的map集合
     */
    public static String getReconciliationHandlingTaskMap() {
        return CacheKey.RECONCILIATION_HANDLING_TASK_MAP + getToday();
    }

    public static String getImportDataHandlingTaskMap() {
        return CacheKey.IMPORT_DATA_HANDLING_TASK_MAP + getToday();
    }

    /**
     * 获取任务下标
     * @param processNo 过程编号
     * @return 任务下标
     */
    public static String getReconciliationTaskIndex(String processNo) {
        return CacheKey.TASK_INDEX + processNo + KEY_SEPARATOR + getToday();
    }

    /**
     * 获取任务总数
     * @param processNo 过程编号
     * @return 任务总数
     */
    public static String getReconciliationTaskTotal(String processNo) {
        return CacheKey.TASK_TOTAL + processNo + KEY_SEPARATOR + getToday();
    }

    /**
     * 获取数据总数
     * @param dataType 任务类型
     * @return 数据总数
     */
    public static String getDataImportTotal(String dataType) {
        return CacheKey.DATA_TOTAL + dataType + KEY_SEPARATOR + getToday();
    }

    /**
     * 获取数据已处理数量
     * @param processNo 过程编号
     * @return 数据已处理数量
     */
    public static String getReconciliationDataHandled(String processNo) {
        return CacheKey.DATA_HANDLED + processNo + KEY_SEPARATOR + getToday();
    }

    /**
     * 获取对账唯一标识集合
     * @param processNo 过程编号
     * @return 对账唯一标识集合
     */
    public static String getReconciliationKeyList(String processNo) {
        return CacheKey.KEY_LIST + processNo + KEY_SEPARATOR + getToday();
    }

    /**
     * 获取任务最大执行时间
     * @param processNo 任务类型
     * @return 任务最大执行时间
     */
    public static String getReconciliationTaskMaxExecute(String processNo) {
        return CacheKey.RECONCILIATION_TASK_MAX_EXECUTE + processNo + KEY_SEPARATOR + getToday();
    }


    public static String getImportDataTaskMaxExecute(String dataTypeNo) {
        return CacheKey.IMPORT_DATA_TASK_MAX_EXECUTE + dataTypeNo + KEY_SEPARATOR + getToday();
    }

    /**
     * 获取导入数据任务下标
     * @param dataType 数据类型
     * @return 导入数据任务下标
     */
    public static String getImportDataIndex(String dataType) {
        return CacheKey.IMPORT_DATA_INDEX + dataType + KEY_SEPARATOR + getToday();
    }

    /**
     * 获取导入数据存放的map
     * @param dataType 数据类型
     * @return 导入数据存放的map
     */
    public static String getImportedDataMap(String dataType) {
        return CacheKey.DATA_MAP + dataType + KEY_SEPARATOR + getToday();
    }

    /**
     * 获取今天日期字符串
     * @return yyyyMMdd
     */
    private static String getToday() {
        return new SimpleDateFormat(DATE_FORMAT_PATTERN).format(new Date());
    }

}
