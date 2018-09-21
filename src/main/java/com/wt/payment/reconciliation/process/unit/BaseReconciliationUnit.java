package com.wt.payment.reconciliation.process.unit;

import com.wt.payment.reconciliation.definitions.DataCheckNode;

/**
 * 对账单元模板类
 */
public abstract class BaseReconciliationUnit implements DataCheckNode {

    private String aSideDataTypeNo;

    private String bSideDataTypeNo;

    @Override
    public void setASideDataTypeNo(String aSideDataTypeNo) {
        this.aSideDataTypeNo = aSideDataTypeNo;
    }

    @Override
    public String getASideDataTypeNo() {
        return aSideDataTypeNo;
    }

    @Override
    public void setBSideDataTypeNo(String bSideDataTypeNo) {
        this.bSideDataTypeNo = bSideDataTypeNo;
    }

    @Override
    public String getBSideDataTypeNo() {
        return bSideDataTypeNo;
    }

}