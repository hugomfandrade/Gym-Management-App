package org.hugoandrade.gymapp;

import org.hugoandrade.gymapp.suites.Data_IntegrationTests_Suite;
import org.hugoandrade.gymapp.suites.Provider_IntegrationTests_Suite;
import org.hugoandrade.gymapp.suites.Sanity_IntegrationTests_Suite;
import org.hugoandrade.gymapp.suites.View_IntegrationTests_Suite;
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
        Sanity_IntegrationTests_Suite.class,
        Data_IntegrationTests_Suite.class,
        Provider_IntegrationTests_Suite.class,
        View_IntegrationTests_Suite.class,
})


public class All_IntegrationTests_Suites {
}
