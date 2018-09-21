package com.wt.payment.reconciliation.definitions;

import com.wt.payment.reconciliation.model.DataCheckParam;

import java.util.List;

/**
 * 对账数据导入器
 * @param <S> 源数据
 * @param <D> 目标数据
 */
public interface DataImporter<S, D extends DataCheckParam> {

    /**
     * 按照数据类型获取需要导入的数据总量
     * @param dataType 数据类型
     * @return 需要导入的数据总量
     */
    int getSourceDataTotal(String dataType);

    /**
     * 根据批次号获取相应数据类型的数据
     * @param dataType 数据类型
     * @param batchNo  批次号
     * @return 数据集合
     */
    List<S> batchGetSourceData(String dataType, int batchNo);

    /**
     * 源数据加工处理
     * @param sourceData 源数据
     * @return 处理后的数据
     */
    D processSourceData(S sourceData);

}
