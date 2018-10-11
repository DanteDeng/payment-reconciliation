package com.wt.payment.reconciliation.model;

import java.math.BigDecimal;

/**
 * 对账参数
 */
public class DataCheckParam {

    /**
     * 流水号
     */
    private String serialNo;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 处理到的节点编号
     */
    private int nodeNo;
    /**
     * 比对结果
     */
    private boolean checkResult;

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getNodeNo() {
        return nodeNo;
    }

    public void setNodeNo(int nodeNo) {
        this.nodeNo = nodeNo;
    }

    public boolean getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(boolean checkResult) {
        this.checkResult = checkResult;
    }

    @Override
    public String toString() {
        return "DataCheckParam{" +
                "serialNo='" + serialNo + '\'' +
                ", amount=" + amount +
                ", nodeNo='" + nodeNo + '\'' +
                ", checkResult=" + checkResult +
                '}';
    }
}
