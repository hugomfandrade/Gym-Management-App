package org.hugoandrade.gymapp;

import org.hugoandrade.gymapp.misc.SanityTest_InstrumentedTest;
import org.hugoandrade.gymapp.suites.Data_IntegrationTests_Suite;
import org.hugoandrade.gymapp.suites.Provider_IntegrationTests_Suite;
import org.hugoandrade.gymapp.view.Login_Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 */
// Runs all unit tests.
@SuppressWarnings("unused")
@RunWith(Suite.class)
@Suite.SuiteClasses({
        // just a sanity check to make sure integration tests running properly
        SanityTest_InstrumentedTest.class,
        Data_IntegrationTests_Suite.class,
        Provider_IntegrationTests_Suite.class,
        Login_Test.class,
})


public class All_IntegrationTests_Suites {
}
