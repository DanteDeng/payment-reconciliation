package com.wt.payment.reconciliation.definitions;

import com.wt.payment.reconciliation.model.NodeParam;

/**
 * 对账节点（底层操作接口，使用单例）
 */
public interface DataCheckNode {

    /**
     * 设置比对方数据类型编号
     * @param aSideDataTypeNo 比对方数据类型编号
     */
    void setASideDataTypeNo(String aSideDataTypeNo);

    /**
     * 获取比对方数据类型编号
     * @return 比对方数据类型编号
     */
    String getASideDataTypeNo();

    /**
     * 设置被比对方数据类型编号
     * @param bSideDataTypeNo 被比对方数据类型编号
     */
    void setBSideDataTypeNo(String bSideDataTypeNo);

    /**
     * 获取被比对方数据类型编号
     */
    String getBSideDataTypeNo();

    /**
     * 执行对账
     * @param param 单元操作参数
     */
    void execute(NodeParam param);

}
