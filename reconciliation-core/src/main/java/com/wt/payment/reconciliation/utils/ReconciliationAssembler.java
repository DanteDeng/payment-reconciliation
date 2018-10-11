package com.wt.payment.reconciliation.utils;

import com.wt.payment.reconciliation.definitions.BeanManager;
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

    private static BeanManager beanManager;

    /**
     * 对账过程与处理单元映射关系map
     */
    private static final Map<String, List<DataCheckNode>> unitsMap = new HashMap<>();
    /**
     * 数据类型与数据导入器映射map
     */
    private static Map<String, ProcessInfo> processNoMapProcessInfo;
    /**
     * 对账数据导入器map
     */
    private static final Map<String, DataImporter> importerMap = new HashMap<>();
    /**
     * 数据类型与数据导入器映射map
     */
    private static Map<String, String> dataMapImporter;
    /**
     * 读写锁
     */
    private static final ReadWriteLock processLock = new ReentrantReadWriteLock();
    /**
     * 读写锁
     */
    private static final ReadWriteLock importerLock = new ReentrantReadWriteLock();

    public static BeanManager getBeanManager() {
        if (beanManager == null) {
            throw new NullPointerException("please init bean manager first");
        }
        return beanManager;
    }

    public static void setBeanManager(BeanManager beanManager) {
        ReconciliationAssembler.beanManager = beanManager;
    }

    public static Map<String, ProcessInfo> getProcessNoMapProcessInfo() {
        if (beanManager == null) {
            throw new NullPointerException("please init bean manager first");
        }
        return processNoMapProcessInfo;
    }

    public static void setProcessNoMapProcessInfo(Map<String, ProcessInfo> processNoMapProcessInfo) {
        ReconciliationAssembler.processNoMapProcessInfo = processNoMapProcessInfo;
    }

    public static Map<String, String> getDataMapImporter() {
        return dataMapImporter;
    }

    public static void setDataMapImporter(Map<String, String> dataMapImporter) {
        ReconciliationAssembler.dataMapImporter = dataMapImporter;
    }

    /**
     * 根据过程编号组装对账过程
     * @param processNo 过程编号
     * @return 过程信息
     */
    public static ProcessInfo getProcessInfoByProcessNo(String processNo) {
        ProcessInfo processInfo = getProcessNoMapProcessInfo().get(processNo);
        if (processInfo == null) {
            throw new RuntimeException(String.format("there is no process info mapped by process NO %s", processNo));
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
                ProcessInfo processInfo = getProcessInfoByProcessNo(processNo);
                if (processInfo != null) {
                    reconciliationUnits = new ArrayList<>();
                    List<NodeInfo> units = processInfo.getNodes();
                    for (NodeInfo unit : units) { // 根据单元编号找到相应的操作单元实例进行实例获取
                        DataCheckNode bean = getBeanManager().getBean(unit.getUnitNo(), DataCheckNode.class);
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
                String importerNo = getDataMapImporter().get(dataTypeNo);
                if (importerNo == null || importerNo.isEmpty()) {
                    throw new RuntimeException(String.format("there is no importer no mapped by data type NO %s", dataTypeNo));
                }
                dataImporter = getBeanManager().getBean(importerNo, DataImporter.class);   // 若是找不到相关的bean，spring容器会抛出异常

                importerMap.put(dataTypeNo, dataImporter);   //dataImporter存放至map
            } finally {
                writeLock.unlock();
            }
        }
        return dataImporter;
    }

}
