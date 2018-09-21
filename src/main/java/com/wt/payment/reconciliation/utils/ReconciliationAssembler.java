package com.wt.payment.reconciliation.utils;

import com.wt.payment.reconciliation.definitions.DataCheckNode;
import com.wt.payment.reconciliation.definitions.DataImporter;
import com.wt.payment.reconciliation.model.NodeInfo;
import com.wt.payment.reconciliation.model.ProcessInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 对账过程组装器
 */
public class ReconciliationAssembler {

    /**
     * 对账过程与处理单元映射关系map
     */
    private static final Map<String, List<DataCheckNode>> unitsMap = new HashMap<>();
    /**
     * 对账数据导入器map
     */
    private static final Map<String, DataImporter> importerMap = new HashMap<>();
    /**
     * 读写锁
     */
    private static final ReadWriteLock processLock = new ReentrantReadWriteLock();
    /**
     * 读写锁
     */
    private static final ReadWriteLock importerLock = new ReentrantReadWriteLock();

    /**
     * 根据过程编号组装对账过程
     * @param processNo 过程编号
     * @return 过程信息
     */
    public static ProcessInfo assembleProcessInfoByProcessNo(String processNo) {
        ProcessInfo processInfo = new ProcessInfo();
        processInfo.setProcessNo("001");
        // TODO 查库或者从配置读取信息
        NodeInfo unitInfo1 = new NodeInfo();
        unitInfo1.setUnitNo("001");
        unitInfo1.setASideDataTypeNo("001");
        unitInfo1.setBSideDataTypeNo("002");
        NodeInfo unitInfo2 = new NodeInfo();
        unitInfo2.setUnitNo("002");
        unitInfo2.setASideDataTypeNo("001");
        unitInfo2.setBSideDataTypeNo("002");
        NodeInfo unitInfo3 = new NodeInfo();
        unitInfo3.setUnitNo("003");
        unitInfo3.setASideDataTypeNo("001");
        unitInfo3.setBSideDataTypeNo("003");
        NodeInfo unitInfo4 = new NodeInfo();
        unitInfo4.setUnitNo("004");
        unitInfo4.setASideDataTypeNo("001");
        unitInfo4.setBSideDataTypeNo("004");
        List<NodeInfo> units = new ArrayList<>();
        units.add(unitInfo1);
        units.add(unitInfo2);
        units.add(unitInfo3);
        units.add(unitInfo4);
        processInfo.setNodes(units);
        List<String> unitNos = new ArrayList<>();
        List<String> dataTypeNos = new ArrayList<>();
        processInfo.setNodeNos(unitNos);
        processInfo.setDataTypeNos(dataTypeNos);
        for (NodeInfo unit : units) {
            unitNos.add(unit.getUnitNo());
            String aSideDataTypeNo = unit.getASideDataTypeNo();
            if (!dataTypeNos.contains(aSideDataTypeNo)) {
                dataTypeNos.add(aSideDataTypeNo);
            }
            String bSideDataTypeNo = unit.getBSideDataTypeNo();
            if (!dataTypeNos.contains(bSideDataTypeNo)) {
                dataTypeNos.add(bSideDataTypeNo);
            }
        }
        return processInfo;
    }


    /**
     * 根据对账过程编号获取对账操作单元集合
     * @param processNo 对账过程编号
     * @return 对账操作单元集合
     */
    public static List<DataCheckNode> getReconciliationUnitsByProcessNo(String processNo) {

        List<DataCheckNode> reconciliationUnits = unitsMap.get(processNo);
        if (reconciliationUnits == null) {
            Lock writeLock = processLock.writeLock();
            writeLock.lock();
            try {
                ProcessInfo processInfo = assembleProcessInfoByProcessNo(processNo);
                if (processInfo != null) {
                    reconciliationUnits = new ArrayList<>();
                    List<NodeInfo> units = processInfo.getNodes();
                    for (NodeInfo unit : units) { // 根据单元编号找到相应的操作单元实例进行实例获取
                        DataCheckNode bean = SpringContextUtil.getBean("reconciliationUnit" + unit.getUnitNo(), DataCheckNode.class);
                        // 对账数据类型编号设置
                        bean.setASideDataTypeNo(unit.getASideDataTypeNo());
                        bean.setBSideDataTypeNo(unit.getBSideDataTypeNo());
                        reconciliationUnits.add(bean);
                    }
                    unitsMap.put(processNo, reconciliationUnits);   //units存放至map
                }
            } finally {
                writeLock.unlock();
            }
        }

        return reconciliationUnits;
    }

    /**
     * 根据数据类型获取数据导入器
     * @param dataTypeNo 数据类型
     * @return 数据导入器
     */
    public static DataImporter getDataImporterByDataTypeNo(String dataTypeNo) {

        DataImporter dataImporter = importerMap.get(dataTypeNo);
        if (dataImporter == null) {
            Lock writeLock = importerLock.writeLock();
            writeLock.lock();
            try {
                dataImporter = SpringContextUtil.getBean("dataImporter" + dataTypeNo, DataImporter.class);
                importerMap.put(dataTypeNo, dataImporter);   //dataImporter存放至map
            } finally {
                writeLock.unlock();
            }
        }
        return dataImporter;
    }

}
