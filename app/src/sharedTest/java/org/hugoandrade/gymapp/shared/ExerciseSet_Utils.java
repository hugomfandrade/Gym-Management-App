package org.hugoandrade.gymapp.shared;

// imports

import org.hugoandrade.gymapp.data.ExerciseSet;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Shared ExerciseSet testing Utils class
 */
@SuppressWarnings("SameParameterValue")
public class ExerciseSet_Utils {

    static final public String TEST_ID_1 = "id_1";
    static final public String TEST_ID_2 = "id_2";
    static final public String TEST_EXERCISE_ID = "exercise_id";
    static final public String TEST_EXERCISE_PLAN_RECORD_ID = "exercise_plan_record_id";
    static final public int TEST_EXERCISE_PLAN_RECORD_ORDER = 8;

    static public ExerciseSet newExerciseSet(String id) {
        return new ExerciseSet(id, TEST_EXERCISE_ID, TEST_EXERCISE_PLAN_RECORD_ID, TEST_EXERCISE_PLAN_RECORD_ORDER);
    }

    static public ExerciseSet newExerciseSet(String id, String exerciseID) {
        return new ExerciseSet(id, exerciseID, TEST_EXERCISE_PLAN_RECORD_ID, TEST_EXERCISE_PLAN_RECORD_ORDER);
    }

    static public void checkExerciseSet(ExerciseSet testExerciseSet, String id) {
        assertThat(testExerciseSet.getID(), is(id));
        assertThat(testExerciseSet.getExerciseID(), is(TEST_EXERCISE_ID));
        assertThat(testExerciseSet.getExercisePlanRecordID(), is(TEST_EXERCISE_PLAN_RECORD_ID));
        assertThat(testExerciseSet.getExercisePlanRecordOrder(), is(TEST_EXERCISE_PLAN_RECORD_ORDER));
    }


}
