package com.wt.payment.reconciliation.model;

/**
 * 对账操作单元信息
 */
public class NodeInfo {
    /**
     * 单元编号
     */
    private String unitNo;
    /**
     * 比对方数据类型编号
     */
    private String aSideDataTypeNo;
    /**
     * 被比对方数据类型编号
     */
    private String bSideDataTypeNo;

    public String getUnitNo() {
        return unitNo;
    }

    public void setUnitNo(String unitNo) {
        this.unitNo = unitNo;
    }

    public String getASideDataTypeNo() {
        return aSideDataTypeNo;
    }

    public void setASideDataTypeNo(String aSideDataTypeNo) {
        this.aSideDataTypeNo = aSideDataTypeNo;
    }

    public String getBSideDataTypeNo() {
        return bSideDataTypeNo;
    }

    public void setBSideDataTypeNo(String bSideDataTypeNo) {
        this.bSideDataTypeNo = bSideDataTypeNo;
    }

    @Override
    public String toString() {
        return "UnitInfo{" +
                "unitNo='" + unitNo + '\'' +
                ", aSideDataTypeNo='" + aSideDataTypeNo + '\'' +
                ", bSideDataTypeNo='" + bSideDataTypeNo + '\'' +
                '}';
    }
}
