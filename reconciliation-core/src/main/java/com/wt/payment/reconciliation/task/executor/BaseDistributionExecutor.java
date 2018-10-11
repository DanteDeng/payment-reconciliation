package com.wt.payment.reconciliation.task.executor;

import com.wt.payment.reconciliation.constant.HandleStatus;
import com.wt.payment.reconciliation.definitions.DistributionExecutor;
import com.wt.payment.reconciliation.model.ExecutorParam;
import com.wt.payment.reconciliation.utils.DistributionExecuteUtil;
import com.wt.payment.reconciliation.utils.CacheUtil;
import com.wt.payment.reconciliation.utils.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 分布式对账任务执行器
 */
public abstract class BaseDistributionExecutor implements DistributionExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(BaseDistributionExecutor.class);
    /**
     * 待导入数据总数
     */
    private Integer dataTotal;
    /**
     * 任务总数
     */
    private Integer taskTotal;
    /**
     * 处理器状态
     */
    private int status;
    /**
     * 读写锁
     */
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    /**
     * 执行器参数信息
     */
    protected ExecutorParam executorParam;
    /**
     * 线程池
     */
    protected ThreadPoolExecutor executor = new ThreadPoolExecutor(10,
            20,
            1,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());
    /**
     * 分发定时处理间隔时间
     */
    protected static final int TASK_PERIOD = 1;

    /**
     * 导入逻辑
     * @param operateNo 操作编号
     */
    @Override
    public void execute(String operateNo) {

        LOG.info(String.format("distribution execute start operate NO %s", operateNo));
        // 初始化执行器参数
        initExecutorParam(operateNo);

        // 设置任务数据总量
        setDataTotal(calculateDataTotal());

        // 设置任务总数
        setTaskTotal(calculateTaskTotal());

        // 定时执行心跳发送 以及 任务分发
        ScheduledThreadPoolExecutor schedule = new ScheduledThreadPoolExecutor(1);
        schedule.scheduleAtFixedRate(() -> {
            LOG.info(String.format("schedule new check task start operate NO %s", operateNo));
            try {
                // 发送心跳
                sendHeartBeat();

                // 开启一个新任务
                triggerANewTask();

                LOG.info(String.format("schedule new check task end operate NO %s", operateNo));
            } catch (Exception e) {
                LOG.error(String.format("schedule new check task error operate NO %s", operateNo), e);
            }
        }, TASK_PERIOD, TASK_PERIOD, TimeUnit.SECONDS);

        Long dataHandled = joinUntilAllDone(schedule);  // 阻塞线程直到全部处理完成
        LOG.info(String.format("distribution execute end operate NO %s data handled %s", operateNo, dataHandled));
    }

    /**
     * 计算任务总数
     * @return 任务总数
     */
    private int calculateTaskTotal() {
        int taskSize = executorParam.getTaskSize();
        if (taskSize <= 0) {
            throw new RuntimeException(String.format("distribution executor param illegal %s", executorParam));
        }
        int dataTotal = getDataTotal();
        int taskTotal = dataTotal / taskSize + (dataTotal % taskSize == 0 ? 0 : 1);
        LOG.info(String.format("calculate task total %s", taskTotal));
        return taskTotal;
    }

    /**
     * 获取执行超时任务编号
     * @param taskMapKey        任务map key
     * @param maxExecuteTimeKey 最大执行时间key
     * @return 执行超时任务编号
     */
    private Integer getOutOfTimeTaskNo(String taskMapKey, String maxExecuteTimeKey) {
        Integer taskNo = null;
        Map<String, Object> handlingTaskMap = CacheUtil.getMap(taskMapKey);
        Long maxExecute = CacheUtil.get(maxExecuteTimeKey, Long.class);
        long nowTime = new Date().getTime();
        for (Map.Entry<String, Object> entry : handlingTaskMap.entrySet()) {
            Date doneTime = (Date) entry.getValue();
            LOG.info(String.format("task executor maxExecute = %s nowTime = %s doneTime = %s", maxExecute, nowTime, doneTime));
            if (doneTime != null) {
                long takes = nowTime - doneTime.getTime();
                if (maxExecute != null && takes > maxExecute * 2) {   // 超过最大执行时长两倍的任务当作异常任务重跑
                    taskNo = Integer.getInteger(entry.getKey());
                    break;
                }
            }
        }
        return taskNo;
    }

    /**
     * 触发新任务
     */
    private void triggerANewTask() {
        String operateNo = executorParam.getOperateNo();
        String operateLockKey = executorParam.getOperateLockKey();
        String taskNoKey = executorParam.getTaskNoKey();
        int taskSize = executorParam.getTaskSize();
        String handlingTaskMapKey = executorParam.getHandlingTaskMapKey();
        String maxExecuteTimeKey = executorParam.getMaxExecuteTimeKey();
        LOG.info(String.format("start new task start operate NO %s lock key %s task size %s", operateNo, operateLockKey, taskSize));
        int status = this.getStatus();
        if (status != HandleStatus.ING) { //当前执行器中任务还在处理中

            // 计算任务编号
            Integer taskNo = calculateTaskNo(operateLockKey, taskNoKey, handlingTaskMapKey, maxExecuteTimeKey);
            if (taskNo != null) { // 任务编号为空表示任务已经全部处理完成
                // 开启新任务
                setStatus(HandleStatus.ING);    //执行器状态锁定
                try {
                    long startMillis = new Date().getTime();

                    doNewTask(operateNo, taskNo, taskSize);

                    CacheUtil.removeHash(handlingTaskMapKey, String.valueOf(taskNo));   //处理完成从处理中map中移除
                    LOG.info(String.format("new reconciliation task end process NO %s task size %s task NO %s", operateNo, taskSize, taskNo));
                    long endMillis = new Date().getTime();
                    long takes = startMillis - endMillis;
                    // 获取原有最大任务处理时间
                    Long oldTakes = CacheUtil.get(maxExecuteTimeKey, Long.class);
                    if (oldTakes == null || oldTakes < takes) { // 如果本次处理时间大过已有最大处理时间或者还未设置最大处理时间则更新最大处理时间
                        CacheUtil.set(maxExecuteTimeKey, takes);
                    }
                } finally { // 执行完成执行器状态设置为就绪
                    setStatus(HandleStatus.INIT);
                }
            }
        }
    }


    /**
     * 处理任务编号计算(分布式同步操作)
     * @param operateLockKey    操作锁定key
     * @param taskNoKey         任务编号key
     * @param taskMapKey        任务map集合key
     * @param maxExecuteTimeKey 最大执行时间key
     * @return 任务编号
     */
    private Integer calculateTaskNo(String operateLockKey, String taskNoKey, String taskMapKey, String maxExecuteTimeKey) {

        return DistributionExecuteUtil.synchronizeExecute(operateLockKey, 30L, 3, () -> {
            Integer handled = CacheUtil.getInt(taskNoKey); // 当前已处理任务数
            //LOG.info(String.format("task no %s key %s", handled, taskNoKey));
            if (handled == null) {  // 首次获取
                handled = 0;
            }
            if (handled >= getTaskTotal()) { // 当前已处理任务数量已经大于等于总任务数量
                handled = getOutOfTimeTaskNo(taskMapKey, maxExecuteTimeKey);
            }
            if (handled != null) {
                CacheUtil.setHash(taskMapKey, String.valueOf(handled), new Date()); //开始处理放置到处理中任务map中
                CacheUtil.incrementAndGet(taskNoKey, 1);   //任务下标+1
            }
            LOG.info(String.format("distribution calculate task NO end handled %s", handled));
            return handled;
        });
    }

    /**
     * 阻塞线程直到全部数据处理完成
     * @param schedule 定时器
     */
    private Long joinUntilAllDone(ExecutorService schedule) {
        String operateNo = executorParam.getOperateNo();
        String handlingTaskMapKey = executorParam.getHandlingTaskMapKey();
        String maxExecuteTimeKey = executorParam.getMaxExecuteTimeKey();
        Long handled;
        while (true) { // 阻塞线程
            // 任务全部处理完则停止任务，（发送消息通知对账任务全部处理完成，暂时省略此逻辑）
            handled = getHandledDataCount(operateNo);
            if (handled != null && getDataTotal() <= handled.intValue() &&
                    getOutOfTimeTaskNo(handlingTaskMapKey, maxExecuteTimeKey) == null) {
                if (!schedule.isShutdown()) {
                    schedule.shutdown(); // 到这一步说明全部任务已经完成所以直接实时关闭
                } else {
                    if (getStatus() != HandleStatus.ING) {  // 只有执行器状态是没有任务正在执行才可以完成执行器参数销毁等动作
                        this.setDataTotal(null);
                        this.setTaskTotal(null);
                        this.destroyExecutorParam();
                        break;
                    }
                }
            }
            SleepUtil.sleepSeconds(1L);
        }
        return handled;
    }


    /**
     * 设置状态
     * @param status 状态
     */
    private void setStatus(int status) {
        Lock writeLock = this.lock.writeLock();
        writeLock.lock();
        try {
            this.status = status;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 获取状态
     * @return 状态
     */
    private int getStatus() {
        Lock readLock = this.lock.readLock();
        readLock.lock();
        try {
            return status;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 获取任务处理数据总数
     * @return 任务处理数据总数
     */
    private Integer getDataTotal() {
        return dataTotal;
    }

    /**
     * 设置任务处理数据总数
     * @param dataTotal 任务处理数据总数
     */
    private void setDataTotal(Integer dataTotal) {
        this.dataTotal = dataTotal;
    }

    /**
     * 获取任务总数
     * @return 任务总数
     */
    private Integer getTaskTotal() {
        return taskTotal;
    }

    /**
     * 设置任务总数
     * @param taskTotal 任务总数
     */
    private void setTaskTotal(Integer taskTotal) {
        this.taskTotal = taskTotal;
    }

    /**
     * 计算任务数据总数
     * @return 任务数据总数
     */
    protected abstract int calculateDataTotal();

    /**
     * 初始化执行器参数
     */
    protected abstract void initExecutorParam(String operateNo);

    /**
     * 销毁执行器参数
     */
    protected abstract void destroyExecutorParam();

    /**
     * 发送心跳
     */
    protected abstract void sendHeartBeat();

    /**
     * 获取当前已处理数据总量
     * @param operateNo 过程编号
     * @return 已处理数据数量
     */
    protected abstract Long getHandledDataCount(String operateNo);

    /**
     * 执行新任务的逻辑
     * @param operateNo 执行编号
     * @param taskNo    任务编号
     * @param taskSize  任务size
     */
    protected abstract void doNewTask(String operateNo, int taskNo, int taskSize);

}
