package com.wt.payment.reconciliation.importer;

import com.wt.payment.reconciliation.constant.DistributionTaskKey;
import com.wt.payment.reconciliation.utils.DistributionExecuteUtil;
import com.wt.payment.reconciliation.utils.RedisKeyUtil;
import com.wt.payment.reconciliation.utils.RedisUtil;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("dataImporter002")
public class BBBDataImporter extends TestDataImporter {

    @Override
    public int getSourceDataTotal(String dataType) {
        String lockKey = DistributionTaskKey.IMPORT_TEMP_LIST_LOCK;
        DistributionExecuteUtil.synchronizeExecute(lockKey, 1, () -> {
            Long size = RedisUtil.getListSize("aTempList");
            if (size != null && size.equals(1000000L)) {
                return 1;
            }
            return null;
        }, () -> {
            List<Object> list = RedisUtil.getMapValues(RedisKeyUtil.getImportedDataMap("001"));
            RedisUtil.addAllToList("aTempList", list);
            return list.size();
        });
        return 1000000;
    }

}
