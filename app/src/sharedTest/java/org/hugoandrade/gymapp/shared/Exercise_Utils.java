package org.hugoandrade.gymapp.shared;

// imports

import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.data.User;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Shared Exercise testing Utils class
 */
@SuppressWarnings("SameParameterValue")
public class Exercise_Utils {

    static final public String TEST_ID_1 = "id_1";
    static final public String TEST_ID_2 = "id_2";
    static final public String TEST_NAME = "name";

    static public Exercise newExercise(String id) {
        return new Exercise(id, TEST_NAME);
    }

    static public Exercise newExercise(String id, String name) {
        return new Exercise(id, name);
    }

    static public void checkExercise(Exercise testExercise, String id) {
        assertThat(testExercise.getID(), is(id));
        assertThat(testExercise.getName(), is(TEST_NAME));
    }

}
