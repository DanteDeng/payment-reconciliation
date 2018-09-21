package com.wt.payment.reconciliation.importer;

import org.springframework.stereotype.Component;

@Component("dataImporter004")
public class DDDDataImporter extends TestDataImporter {

    @Override
    public int getSourceDataTotal(String dataType) {
        return 1000000;
    }

}
