package com.wt.payment.reconciliation.task.executor;

import com.wt.payment.reconciliation.constant.Constant;
import com.wt.payment.reconciliation.constant.DistributionTaskKey;
import com.wt.payment.reconciliation.definitions.DataImporter;
import com.wt.payment.reconciliation.model.DataCheckParam;
import com.wt.payment.reconciliation.model.ExecutorParam;
import com.wt.payment.reconciliation.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * 数据导入（对账操作前置处理，包括：数据分组、聚合、打包、格式化等）
 */
public class DataImportExecutor extends BaseDistributionExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(DataImportExecutor.class);

    /**
     * 数据导入器
     */
    private DataImporter dataImporter;

    /**
     * 初始化执行参数
     * @param dataTypeNo 数据类型编号
     */
    @Override
    protected void initExecutorParam(String dataTypeNo) {
        String machineIp = IpUtil.getLocalHostLANAddress();                                 // 获得注册机器编号
        String machineMapKey = CacheKeyUtil.getMachineMap();                                // 机器编号key
        String operateLockKey = DistributionTaskKey.IMPORT_TASK_NO_LOCK;                    // 导入数据任务锁key
        String taskNoKey = CacheKeyUtil.getImportDataIndex(dataTypeNo);                     // 导入数据任务编号key
        String maxExecuteTimeKey = CacheKeyUtil.getImportDataTaskMaxExecute(dataTypeNo);    // 任务最大执行时间key
        String handlingTaskMapKey = CacheKeyUtil.getImportDataHandlingTaskMap();            // 处理中任务map的key
        executorParam = new ExecutorParam();
        executorParam.setOperateNo(dataTypeNo);
        executorParam.setMachineIp(machineIp);
        executorParam.setMachineMapKey(machineMapKey);
        executorParam.setOperateLockKey(operateLockKey);
        executorParam.setTaskNoKey(taskNoKey);
        executorParam.setMaxExecuteTimeKey(maxExecuteTimeKey);
        executorParam.setHandlingTaskMapKey(handlingTaskMapKey);
        executorParam.setTaskSize(Constant.TASK_SIZE);

        // 按照数据类型编号装配数据导入器
        dataImporter = ReconciliationAssembler.getDataImporterByDataTypeNo(dataTypeNo);
    }

    /**
     * 销毁执行参数
     */
    @Override
    protected void destroyExecutorParam() {
        executorParam = null;
    }

    /**
     * 发送心跳
     */
    @Override
    protected void sendHeartBeat() {

    }

    /**
     * 计算任务数据总数
     * @return 任务数据总数
     */
    @Override
    public int calculateDataTotal() {
        String dataTypeNo = executorParam.getOperateNo();
        String dataImportTotalKey = CacheKeyUtil.getDataImportTotal(dataTypeNo);
        String lockKey = DistributionTaskKey.IMPORT_TASK_TOTAL_LOCK;
        return DistributionExecuteUtil.synchronizeExecute(lockKey, 1, () -> CacheUtil.getInt(dataImportTotalKey), () -> {
            Integer total = dataImporter.getSourceDataTotal(dataTypeNo); // 获取源数据数量
            CacheUtil.setInt(dataImportTotalKey, total);
            return total;
        });
    }

    /**
     * 执行新任务
     * @param dataTypeNo 操作编号
     * @param taskNo     任务编号
     * @param taskSize   任务size
     */
    @Override
    public void doNewTask(String dataTypeNo, int taskNo, int taskSize) {
        List<Future<Void>> futures = new ArrayList<>(); // 阻塞线程使用
        List sourceDataList = dataImporter.batchGetSourceData(dataTypeNo, taskNo);
        Map<String, DataCheckParam> processedMap = new ConcurrentHashMap<>(64);
        for (Object sourceData : sourceDataList) {
            Future<Void> future = executor.submit(() -> {
                DataCheckParam processedData = dataImporter.processSourceData(sourceData);  // 数据加工处理放置到map
                processedMap.put(processedData.getSerialNo(), processedData);
                return null;
            });
            futures.add(future);
        }
        for (Future<Void> future : futures) {   // 阻塞主线程直到所有的源数据已经加工处理完成
            try {
                future.get();
            } catch (Exception e) {
                LOG.info(String.format("import data get future error happens task NO %s data type NO %s", taskNo, dataTypeNo));
            }
        }

        CacheUtil.setHashAll(CacheKeyUtil.getImportedDataMap(dataTypeNo), processedMap);    // 数据加入到缓存
    }

    /**
     * 获取当前已处理数据总量
     * @param dataTypeNo 数据类型编号
     * @return 已处理数据数量
     */
    public Long getHandledDataCount(String dataTypeNo) {
        return CacheUtil.getMapSize(CacheKeyUtil.getImportedDataMap(dataTypeNo));
    }
}
