package com.wt.payment.reconciliation.importer;

import org.springframework.stereotype.Component;

@Component("dataImporter003")
public class CCCDataImporter extends TestDataImporter {

    @Override
    public int getSourceDataTotal(String dataType) {
        return 1000000;
    }

}
