package com.wt.payment.reconciliation.model;

/**
 * 对账单元参数
 */
public class NodeParam {
    /**
     * 对账取数数据的key
     */
    private String key;
    /**
     * 比对方
     */
    private DataCheckParam aSide;
    /**
     * 被比对方
     */
    private DataCheckParam bSide;
    /**
     * 比对结果
     */
    private boolean checkResult;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DataCheckParam getaSide() {
        return aSide;
    }

    public void setaSide(DataCheckParam aSide) {
        this.aSide = aSide;
    }

    public DataCheckParam getbSide() {
        return bSide;
    }

    public void setbSide(DataCheckParam bSide) {
        this.bSide = bSide;
    }

    public boolean getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(boolean checkResult) {
        this.checkResult = checkResult;
    }

    @Override
    public String toString() {
        return "NodeParam{" +
                "key=" + key +
                ", aSide=" + aSide +
                ", bSide=" + bSide +
                ", checkResult=" + checkResult +
                '}';
    }
}
