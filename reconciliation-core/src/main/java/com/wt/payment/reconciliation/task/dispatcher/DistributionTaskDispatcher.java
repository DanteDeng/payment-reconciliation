package com.wt.payment.reconciliation.task.dispatcher;

import com.wt.payment.reconciliation.constant.DistributionTaskKey;
import com.wt.payment.reconciliation.definitions.TaskDispatcher;
import com.wt.payment.reconciliation.model.ProcessInfo;
import com.wt.payment.reconciliation.task.executor.DataCheckExecutor;
import com.wt.payment.reconciliation.task.executor.DataImportExecutor;
import com.wt.payment.reconciliation.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 分布式任务分发
 */
public class DistributionTaskDispatcher implements TaskDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(DistributionTaskDispatcher.class);

    /**
     * 数据导入器
     */
    private DataImportExecutor dataImportExecutor;
    /**
     * 任务执行器
     */
    private DataCheckExecutor dataCheckExecutor;

    /**
     * 分发处理逻辑
     */
    public void dispatch() {
        // TODO 通过策略算法获取需要处理的对账过程的集合
        String processNo = "001";
        // 1.导入过程所需数据
        importDataOfProcess(processNo);
        // 2.执行对账过程
        dataCheckExecutor.execute(processNo);

        SleepUtil.sleepSeconds(3L); // todo 主线程阻塞三秒防止spring容器在子线程完成前关闭，实际web应用中不需要这个处理
    }

    /**
     * 导入过程所需数据
     * @param processNo 过程编号
     */
    private void importDataOfProcess(String processNo) {
        // 1.获取对账过程信息
        ProcessInfo processInfo = ReconciliationAssembler.getProcessInfoByProcessNo(processNo);
        if (processInfo != null) {
            // 2.获取过程需要处理的数据类型编号集合
            List<String> dataTypeNos = processInfo.getDataTypeNos();
            if (dataTypeNos != null) {
                // 3.循环导入数据
                for (String dataTypeNo : dataTypeNos) {
                    dataImportExecutor.execute(dataTypeNo);
                    // 3.1.合并过程所需处理的全部数据的唯一标识集合
                    mergeTaskKeys(processNo, dataTypeNo); // 合并keys
                }
            }
        }
    }

    /**
     * 合并不同数据类型需要处理的keys
     * @param processNo  对账过程编号
     * @param dataTypeNo 数据类型编号
     */
    private void mergeTaskKeys(String processNo, String dataTypeNo) {
        String mapKey = CacheKeyUtil.getImportedDataMap(dataTypeNo);
        DistributionExecuteUtil.synchronizeExecute(DistributionTaskKey.IMPORT_MERGE_KEYS_LOCK, 30L, 3, () -> {
            Set<String> keys = CacheUtil.getMapKeys(mapKey);
            String listKey = CacheKeyUtil.getReconciliationKeyList(processNo);
            List<Object> list = CacheUtil.getList(listKey);

            if (list != null) {
                Set<String> set = new HashSet<>();
                for (Object o : list) {
                    set.add((String) o);
                }
                keys.removeAll(set);
            }
            if (!keys.isEmpty()) {
                Collection<Object> newKeys = new ArrayList<>(keys);
                CacheUtil.addAllToList(listKey, newKeys);
            }
            return null;
        });
    }


}
