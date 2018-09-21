package com.wt.payment.reconciliation.importer;

import com.wt.payment.reconciliation.constant.Constant;
import com.wt.payment.reconciliation.definitions.DataImporter;
import com.wt.payment.reconciliation.model.DataCheckParam;
import com.wt.payment.reconciliation.utils.UidUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component("dataImporter001")
public class AAADataImporter implements DataImporter<DataCheckParam, DataCheckParam> {

    /**
     * 随机器
     */
    private Random random = new Random(1);

    @Override
    public int getSourceDataTotal(String dataType) {
        return 1000000;
    }

    @Override
    public List<DataCheckParam> batchGetSourceData(String dataType, int batchNo) {
        List<DataCheckParam> list = new ArrayList<>();
        for (int i = 0; i < Constant.TASK_SIZE; i++) {
            String serialNo = UidUtil.generate();   // 数据序列号生成
            double d = random.nextDouble(); // 生成金额的小数部分
            int in = random.nextInt(10000); // 生成金额的整数部分
            d = d + in; // 金额
            DataCheckParam aSide = new DataCheckParam();
            aSide.setSerialNo(serialNo);
            aSide.setAmount(new BigDecimal(d));
            list.add(aSide);
        }
        return list;
    }

    @Override
    public DataCheckParam processSourceData(DataCheckParam sourceData) {
        return sourceData;
    }
}
