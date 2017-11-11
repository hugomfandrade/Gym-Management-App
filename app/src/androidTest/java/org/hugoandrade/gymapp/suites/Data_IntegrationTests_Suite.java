package org.hugoandrade.gymapp.suites;

import org.hugoandrade.gymapp.data.ExercisePlan_AndroidTest;
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
        Exercise_AndroidTest.class,
        ExercisePlan_AndroidTest.class,
        ExerciseRecord_AndroidTest.class,
        ExerciseSet_AndroidTest.class,
        User_AndroidTest.class,
        WaitingUser_AndroidTest.class,
        JsonParserFormatter_AndroidTest.class
})


public class Data_IntegrationTests_Suite {
}
