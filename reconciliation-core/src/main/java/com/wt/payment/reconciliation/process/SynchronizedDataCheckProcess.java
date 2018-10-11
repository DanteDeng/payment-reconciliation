package com.wt.payment.reconciliation.process;

import com.wt.payment.reconciliation.definitions.DataCheckNode;
import com.wt.payment.reconciliation.definitions.DataCheckProcess;
import com.wt.payment.reconciliation.model.DataCheckParam;
import com.wt.payment.reconciliation.model.NodeParam;
import com.wt.payment.reconciliation.utils.ReconciliationAssembler;
import com.wt.payment.reconciliation.utils.CacheKeyUtil;
import com.wt.payment.reconciliation.utils.CacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 同步对账处理过程
 */
public class SynchronizedDataCheckProcess implements DataCheckProcess<Void> {

    private static final Logger LOG = LoggerFactory.getLogger(SynchronizedDataCheckProcess.class);
    /**
     * 数据key：取数唯一标识
     */
    private String key;
    /**
     * 对账过程编号
     */
    private String processNo;
    /**
     * 流程具有的全部单元操作
     */
    private List<DataCheckNode> nodes;

    /**
     * 初始化
     * @param key 数据key：取数唯一标识
     */
    public SynchronizedDataCheckProcess(String processNo, String key) {
        this.processNo = processNo;
        this.key = key;
        nodes = ReconciliationAssembler.getReconciliationUnitsByProcessNo(processNo);
        if (nodes == null) {
            throw new RuntimeException("SynchronizedReconciliationProcess unit is not init yet process NO is " + processNo);
        }
    }

    /**
     * 执行对账过程（异常处理等逻辑可以自定义）
     * @return 执行结果
     */
    @Override
    public Void call() {
        boolean lock = CacheUtil.lock(key, 300L);// 锁定数据，幂等处理
        if (!lock) {
            LOG.error(String.format("reconciliation process lock failed process NO = %s key = %s", processNo, key));
            return null;
        }

        try {
            int nodeNo = 0;
            // 每个单元使用一个独立的事务处理，出现异常进行回滚
            for (DataCheckNode node : nodes) {
                nodeNo++;
                // 获取比对方数据
                DataCheckParam aSide = (DataCheckParam) CacheUtil.getHash(CacheKeyUtil.getImportedDataMap(node.getASideDataTypeNo()), key);
                // 获取被比对方数据
                DataCheckParam bSide = (DataCheckParam) CacheUtil.getHash(CacheKeyUtil.getImportedDataMap(node.getBSideDataTypeNo()), key);
                // 创建一个对账单元共享的参数对象
                NodeParam param = new NodeParam();
                param.setKey(key);

                param.setaSide(aSide);
                param.setbSide(bSide);
                // 进行数据比对
                if (aSide.getAmount().compareTo(bSide.getAmount()) == 0) {
                    param.setCheckResult(true); //数据比对成功设置结果为true
                }
                // 比对完成数据库操作
                node.execute(param);
                // 缓存中数据状态修改，成功失败总数修改等（期望原子操作）
                aSide.setNodeNo(nodeNo);
                bSide.setNodeNo(nodeNo);
                aSide.setCheckResult(param.getCheckResult());
                bSide.setCheckResult(param.getCheckResult());
                CacheUtil.setHash(CacheKeyUtil.getImportedDataMap(node.getASideDataTypeNo()), key, aSide);
                CacheUtil.setHash(CacheKeyUtil.getImportedDataMap(node.getBSideDataTypeNo()), key, bSide);
            }
            // 已处理任务数据总数+1
            CacheUtil.incrementAndGet(CacheKeyUtil.getReconciliationDataHandled(processNo), 1);
        } catch (Exception e) {
            // TODO 异常情况处理，异常数据使用补偿机制来做处理，概率性异常均认为是可以通过再次处理而做到修复的目的，但是如果多次处理无果，则进入异常池等待人工处理，这种情况仅限于最底层的异常数据
            LOG.error(String.format("reconciliation process error process NO = %s key = %s", processNo, key), e);
        } finally {
            CacheUtil.unlock(key);  // 释放锁
        }
        return null;
    }
}
