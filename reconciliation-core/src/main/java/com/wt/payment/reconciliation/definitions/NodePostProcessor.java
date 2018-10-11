package com.wt.payment.reconciliation.definitions;

import com.wt.payment.reconciliation.model.NodeParam;

/**
 * 对账节点后置处理器（底层操作接口，使用单例）
 */
public interface NodePostProcessor {

    /**
     * 后置处理
     * @param param 对账参数
     */
    void postHandle(NodeParam param);

}
