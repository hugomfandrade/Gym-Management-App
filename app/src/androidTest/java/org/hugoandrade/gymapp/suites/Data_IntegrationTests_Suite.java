package org.hugoandrade.gymapp.suites;

import org.hugoandrade.gymapp.data.Credential_AndroidTest;
import org.hugoandrade.gymapp.data.ExercisePlanRecord_AndroidTest;
import org.hugoandrade.gymapp.data.ExerciseRecord_AndroidTest;
import org.hugoandrade.gymapp.data.ExerciseSet_AndroidTest;
import org.hugoandrade.gymapp.data.Exercise_AndroidTest;
import org.hugoandrade.gymapp.data.User_AndroidTest;
import org.hugoandrade.gymapp.data.WaitingUser_AndroidTest;
import org.hugoandrade.gymapp.webservice.JsonParserFormatter_AndroidTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 */
// Runs all unit tests.
@RunWith(Suite.class)
@Suite.SuiteClasses({
        Credential_AndroidTest.class,
        Exercise_AndroidTest.class,
        ExercisePlanRecord_AndroidTest.class,
        ExerciseRecord_AndroidTest.class,
        ExerciseSet_AndroidTest.class,
        User_AndroidTest.class,
        WaitingUser_AndroidTest.class,
        JsonParserFormatter_AndroidTest.class
})


public class Data_IntegrationTests_Suite {
}
