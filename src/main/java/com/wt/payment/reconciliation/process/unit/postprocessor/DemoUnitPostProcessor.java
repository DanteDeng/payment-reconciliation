package com.wt.payment.reconciliation.process.unit.postprocessor;

import com.wt.payment.reconciliation.definitions.NodePostProcessor;
import com.wt.payment.reconciliation.model.NodeParam;
import org.springframework.stereotype.Component;

/**
 * 示例对账后置处理器
 */
@Component
public class DemoUnitPostProcessor implements NodePostProcessor {

    /**
     * 后置处理
     * @param param 对账参数
     */
    @Override
    public void postHandle(NodeParam param) {
       /* boolean checkResult = param.getCheckResult();
        ReconciliationParam aSide = param.getaSide();
        ReconciliationParam bSide = param.getbSide();
        if (checkResult) {  // 回写对账结果
            aSide.setStatus("Y");
            bSide.setStatus("Y");
            RedisUtil.incrementAndGet(RedisKeyUtil.getReconciliationSuccessTotal("0"), 1); //成功量统计
        } else {
            aSide.setStatus("F");
            bSide.setStatus("F");
            //RedisUtil.incrementAndGet(RedisKeyUtil.getFailureTotalByType("0"), 1); // TODO 失败量统计
            RedisUtil.addToSet(RedisKeyUtil.getReconciliationFailureList("0"), param.getKey()); // TODO 失败数据记录
        }
        RedisUtil.incrementAndGet(RedisKeyUtil.getReconciliationDataHandled("0"), 1); // TODO 已处理数据总数+1*/
        //System.out.println(String.format("post handle compare result = %s key = %s ", checkResult, param.getKey()));
    }
}
