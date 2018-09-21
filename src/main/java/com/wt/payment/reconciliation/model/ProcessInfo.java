package com.wt.payment.reconciliation.model;


import java.util.List;

/**
 * 对账过程信息
 */
public class ProcessInfo {
    /**
     * 过程编号(对应一种对账场景)
     */
    private String processNo;
    /**
     * 过程包含的对账操作单元集合
     */
    private List<NodeInfo> nodes;
    /**
     * 单元编号集合
     */
    private List<String> nodeNos;
    /**
     * 过程需要处理的数据类型集合
     */
    private List<String> dataTypeNos;
    /**
     * 出现异常数据需要处理的对账过程
     */
    private List<ProcessInfo> childProcesses;

    public String getProcessNo() {
        return processNo;
    }

    public void setProcessNo(String processNo) {
        this.processNo = processNo;
    }

    public List<NodeInfo> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeInfo> units) {
        this.nodes = units;
    }

    public List<String> getNodeNos() {
        return nodeNos;
    }

    public void setNodeNos(List<String> nodeNos) {
        this.nodeNos = nodeNos;
    }

    public List<String> getDataTypeNos() {
        return dataTypeNos;
    }

    public void setDataTypeNos(List<String> dataTypeNos) {
        this.dataTypeNos = dataTypeNos;
    }

    public List<ProcessInfo> getChildProcesses() {
        return childProcesses;
    }

    public void setChildProcesses(List<ProcessInfo> childProcesses) {
        this.childProcesses = childProcesses;
    }

    @Override
    public String toString() {
        return "ProcessInfo{" +
                "processNo='" + processNo + '\'' +
                ", nodes=" + nodes +
                ", nodeNos=" + nodeNos +
                ", dataTypeNos=" + dataTypeNos +
                ", childProcesses=" + childProcesses +
                '}';
    }
}
