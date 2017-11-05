package org.hugoandrade.gymapp.shared;

// imports


import org.hugoandrade.gymapp.data.ExerciseRecord;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Shared ExerciseRecord testing Utils class
 */
@SuppressWarnings("SameParameterValue")
public class ExerciseRecord_Utils {

    static final public String TEST_ID_1 = "id_1";
    static final public String TEST_ID_2 = "id_2";
    static final public String TEST_EXERCISE_SET_ID = "exercise_set_id";
    static final public int TEST_EXERCISE_SET_ORDER = 8;
    static final public int TEST_NUMBER_OF_REPETITIONS = 9;

    static public ExerciseRecord newExerciseRecord(String id) {
        return new ExerciseRecord(id, TEST_EXERCISE_SET_ID, TEST_EXERCISE_SET_ORDER, TEST_NUMBER_OF_REPETITIONS);
    }

    static public void checkExerciseRecord(ExerciseRecord testExerciseRecord, String id) {
        assertThat(testExerciseRecord.getID(), is(id));
        assertThat(testExerciseRecord.getExerciseSetID(), is(TEST_EXERCISE_SET_ID));
        assertThat(testExerciseRecord.getExerciseSetOrder(), is(TEST_EXERCISE_SET_ORDER));
        assertThat(testExerciseRecord.getNumberOfRepetitions(), is(TEST_NUMBER_OF_REPETITIONS));
    }

}
