package org.hugoandrade.gymapp.suites;

import org.hugoandrade.gymapp.provider.StorageDBAdapter_AndroidTest;
import org.hugoandrade.gymapp.provider.StorageProvider_AndroidTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        StorageProvider_AndroidTest.class,
        StorageDBAdapter_AndroidTest.class,
})


public class Provider_IntegrationTests_Suite {
}
