package org.hugoandrade.gymapp.model.service;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.table.query.ExecutableJsonQuery;

/**
 * Helper class that is used to help build ExecutableJsonQuery. Particularly
 * used to build "where" conditions with multiple "or".
 */
public class MobileServiceJsonTableBuilder {

    public static MobileServiceJsonTableBuilder instance(String name, MobileServiceClient client) {
        return new MobileServiceJsonTableBuilder(name, client);
    }

    private MobileServiceJsonTable mMobileServiceJsonTable;

    private MobileServiceJsonTableBuilder(String name, MobileServiceClient client) {
        mMobileServiceJsonTable = new MobileServiceJsonTable(name, client);
    }

    ExecutableJsonQuery where(String field, String... values) {
        ExecutableJsonQuery query = new ExecutableJsonQuery();
        if (mMobileServiceJsonTable != null)
            query.setTable(mMobileServiceJsonTable);

        if (values.length == 0) {
            return query;
        }

        for (int i = 0 ; i < values.length ; i++) {
            if (i != 0)
                query.or();
            query.field(field).eq(values[i]);
        }

        return query;
    }
}
