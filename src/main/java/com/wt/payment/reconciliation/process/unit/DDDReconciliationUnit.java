package com.wt.payment.reconciliation.process.unit;

import com.wt.payment.reconciliation.definitions.NodePostProcessor;
import com.wt.payment.reconciliation.model.NodeParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 示例对账流程
 */
@Component("reconciliationUnit004")
public class DDDReconciliationUnit extends BaseReconciliationUnit {

    /**
     * 后置处理器
     */
    @Autowired
    private NodePostProcessor postProcessor;

    /**
     * 执行对账操作单元
     * @param param 单元操作参数
     */
    @Override
    public void execute(NodeParam param) {

        postProcessor.postHandle(param); // TODO 前后植入逻辑做好异常处理、事务控制等操作
    }

}
