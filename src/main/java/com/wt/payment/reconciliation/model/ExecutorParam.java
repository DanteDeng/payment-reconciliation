package com.wt.payment.reconciliation.model;

/**
 * 执行器参数
 */
public class ExecutorParam {

    /**
     * 操作编号
     */
    private String operateNo;
    /**
     * 获得注册机器编号
     */
    private String machineIp;
    /**
     * 机器编号key
     */
    private String machineMapKey;
    /**
     * 处理中任务map的key
     */
    private String handlingTaskMapKey;
    /**
     * 任务最大执行时间key
     */
    private String maxExecuteTimeKey;
    /**
     * 对账任务锁key
     */
    private String operateLockKey;
    /**
     * 任务编号key
     */
    private String taskNoKey;
    /**
     * 任务size
     */
    private int taskSize;

    public String getOperateNo() {
        return operateNo;
    }

    public void setOperateNo(String operateNo) {
        this.operateNo = operateNo;
    }

    public String getMachineIp() {
        return machineIp;
    }

    public void setMachineIp(String machineIp) {
        this.machineIp = machineIp;
    }

    public String getMachineMapKey() {
        return machineMapKey;
    }

    public void setMachineMapKey(String machineMapKey) {
        this.machineMapKey = machineMapKey;
    }

    public String getHandlingTaskMapKey() {
        return handlingTaskMapKey;
    }

    public void setHandlingTaskMapKey(String handlingTaskMapKey) {
        this.handlingTaskMapKey = handlingTaskMapKey;
    }

    public String getMaxExecuteTimeKey() {
        return maxExecuteTimeKey;
    }

    public void setMaxExecuteTimeKey(String maxExecuteTimeKey) {
        this.maxExecuteTimeKey = maxExecuteTimeKey;
    }

    public String getOperateLockKey() {
        return operateLockKey;
    }

    public void setOperateLockKey(String operateLockKey) {
        this.operateLockKey = operateLockKey;
    }

    public String getTaskNoKey() {
        return taskNoKey;
    }

    public void setTaskNoKey(String taskNoKey) {
        this.taskNoKey = taskNoKey;
    }

    public int getTaskSize() {
        return taskSize;
    }

    public void setTaskSize(int taskSize) {
        this.taskSize = taskSize;
    }
}
