package com.wt.payment.reconciliation.task.executor;

import com.wt.payment.reconciliation.constant.Constant;
import com.wt.payment.reconciliation.constant.DistributionTaskKey;
import com.wt.payment.reconciliation.model.ExecutorParam;
import com.wt.payment.reconciliation.process.SynchronizedDataCheckProcess;
import com.wt.payment.reconciliation.utils.IpUtil;
import com.wt.payment.reconciliation.utils.RedisKeyUtil;
import com.wt.payment.reconciliation.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Future;

/**
 * 分布式对账任务执行器
 */
@Component
public class DataCheckExecutor extends BaseDistributionExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(DataCheckExecutor.class);

    /**
     * 初始化执行器参数
     * @param processNo 对账过程编号
     */
    @Override
    public void initExecutorParam(String processNo) {
        String machineIp = IpUtil.getLocalHostLANAddress(); // 获得注册机器编号
        LOG.info(String.format("distribution reconciliation executor machine ip %s", machineIp));
        if (machineIp == null) {
            throw new RuntimeException("get machine no error");
        }
        String machineMapKey = RedisKeyUtil.getMachineMap();    // 机器编号key
        String handlingTaskMapKey = RedisKeyUtil.getReconciliationHandlingTaskMap();    // 处理中任务map的key
        String maxExecuteTimeKey = RedisKeyUtil.getReconciliationTaskMaxExecute(processNo); // 任务最大执行时间key
        String operateLockKey = DistributionTaskKey.CHECK_TASK_NO_LOCK; // 对账任务锁key
        String taskNoKey = RedisKeyUtil.getReconciliationTaskIndex(processNo);  // 任务编号key
        executorParam = new ExecutorParam();
        executorParam.setOperateNo(processNo);
        executorParam.setMachineIp(machineIp);
        executorParam.setMachineMapKey(machineMapKey);
        executorParam.setHandlingTaskMapKey(handlingTaskMapKey);
        executorParam.setMaxExecuteTimeKey(maxExecuteTimeKey);
        executorParam.setOperateLockKey(operateLockKey);
        executorParam.setTaskNoKey(taskNoKey);
        executorParam.setTaskSize(Constant.TASK_SIZE);
    }

    /**
     * 销毁执行器参数
     */
    @Override
    public void destroyExecutorParam() {
        executorParam = null;
    }

    /**
     * 发送心跳
     */
    @Override
    public void sendHeartBeat() {
        RedisUtil.setHash(executorParam.getMachineMapKey(), executorParam.getMachineIp(), new Date()); // TODO 心跳发送，监控需要使用
    }

    /**
     * 计算任务数据总数
     * @return 任务数据总数
     */
    @Override
    public int calculateDataTotal() {

        Long dataTotal = RedisUtil.getListSize(RedisKeyUtil.getReconciliationKeyList(executorParam.getOperateNo()));    // 数据总数
        if (dataTotal == null) {
            throw new RuntimeException("data is not imported");
        }

        return dataTotal.intValue();
    }

    /**
     * 执行新任务
     * @param processNo 过程编号
     * @param taskNo    任务编号
     * @param taskSize  任务size
     */
    @Override
    public void doNewTask(String processNo, int taskNo, int taskSize) {

        List<Future<Void>> futures = new ArrayList<>(); // 阻塞线程获取结果使用
        // 1.计算取数的开始与结束值
        int start = taskNo * taskSize;
        int end = (taskNo + 1) * taskSize;
        // 2.从redis中取出所需处理的数据
        List<Object> list = RedisUtil.subList(RedisKeyUtil.getReconciliationKeyList(processNo), start, end);
        HashSet<String> tasks = new HashSet<>();
        for (Object obj : list) {
            if (obj instanceof String) {
                tasks.add((String) obj);
            }
        }
        // 3.执行器处理数据
        for (String taskId : tasks) { // 多线程处理分配到的任务
            SynchronizedDataCheckProcess process = new SynchronizedDataCheckProcess(processNo, taskId);
            futures.add(executor.submit(process));
        }

        for (Future<Void> future : futures) { // 阻塞线程（整的一个列表的任务执行完才可以去更新任务状态）
            try {
                future.get();
            } catch (Exception e) {
                LOG.error(String.format("new reconciliation task get future error future is %s", future));
            }
        }

    }

    /**
     * 获取当前已处理数据总量
     * @param processNo 过程编号
     * @return 已处理数据数量
     */
    @Override
    public Long getHandledDataCount(String processNo) {
        return RedisUtil.getLong(RedisKeyUtil.getReconciliationDataHandled(processNo));
    }

}
