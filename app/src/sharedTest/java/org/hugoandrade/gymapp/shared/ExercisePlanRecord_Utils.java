package org.hugoandrade.gymapp.shared;

// imports

import org.hugoandrade.gymapp.data.ExercisePlanRecord;

import java.util.Calendar;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Shared ExercisePlanRecord testing Utils class
 */
@SuppressWarnings("SameParameterValue")
public class ExercisePlanRecord_Utils {

    static final public String TEST_ID_1 = "id_1";
    static final public String TEST_ID_2 = "id_2";
    static final public String TEST_MEMBER_ID = "member_id";
    static final public Calendar TEST_DATETIME = Calendar.getInstance();

    static public ExercisePlanRecord newExercisePlanRecord(String id) {
        return new ExercisePlanRecord(id, TEST_MEMBER_ID, TEST_DATETIME);
    }

    static public ExercisePlanRecord newExercisePlanRecord(String id, String memberID) {
        return new ExercisePlanRecord(id, memberID, TEST_DATETIME);
    }

    static public void checkExercisePlanRecord(ExercisePlanRecord testExercisePlanRecord, String id) {
        assertThat(testExercisePlanRecord.getID(), is(id));
        assertThat(testExercisePlanRecord.getMemberID(), is(TEST_MEMBER_ID));
        assertThat(testExercisePlanRecord.getDatetime(), is(TEST_DATETIME));
    }

}
