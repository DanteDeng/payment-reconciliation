package com.wt.payment.reconciliation.importer;

import com.wt.payment.reconciliation.constant.Constant;
import com.wt.payment.reconciliation.constant.DistributionTaskKey;
import com.wt.payment.reconciliation.definitions.DataImporter;
import com.wt.payment.reconciliation.model.DataCheckParam;
import com.wt.payment.reconciliation.utils.DistributionExecuteUtil;
import com.wt.payment.reconciliation.utils.RedisKeyUtil;
import com.wt.payment.reconciliation.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TestDataImporter implements DataImporter<DataCheckParam, DataCheckParam> {

    private static final Logger LOG = LoggerFactory.getLogger(TestDataImporter.class);

    @Override
    public int getSourceDataTotal(String dataType) {

        return 1000000;
    }

    @Override
    public List<DataCheckParam> batchGetSourceData(String dataType, int batchNo) {
        LOG.info("batch get source data start");
        List<DataCheckParam> list = new ArrayList<>();
        int start = batchNo * Constant.TASK_SIZE;
        int end = (batchNo + 1) * Constant.TASK_SIZE;
        List<Object> tempList = RedisUtil.subList("aTempList", start, end);
        if (tempList != null) {
            for (Object o : tempList) {
                list.add((DataCheckParam) o);
            }
        }

        LOG.info("batch get source data end list size = " + list.size());
        return list;
    }

    @Override
    public DataCheckParam processSourceData(DataCheckParam sourceData) {
        return sourceData;
    }
}
