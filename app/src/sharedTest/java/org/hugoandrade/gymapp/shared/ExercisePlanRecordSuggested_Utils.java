package org.hugoandrade.gymapp.shared;

// imports

import org.hugoandrade.gymapp.data.ExercisePlanRecordSuggested;

import java.util.Calendar;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Shared ExercisePlanRecordSuggested testing Utils class
 */
@SuppressWarnings("SameParameterValue")
public class ExercisePlanRecordSuggested_Utils {

    static final public String TEST_ID_1 = "id_1";
    static final public String TEST_ID_2 = "id_2";
    static final private String TEST_STAFF_ID = "staff_id";
    static final private String TEST_MEMBER_ID = "member_id";
    static final private Calendar TEST_DATETIME = Calendar.getInstance();

    static public ExercisePlanRecordSuggested newExercisePlanRecordSuggested(String id) {
        return new ExercisePlanRecordSuggested(id, TEST_MEMBER_ID, TEST_STAFF_ID, TEST_DATETIME);
    }

    static public ExercisePlanRecordSuggested newExercisePlanRecordSuggested(String id, String memberID) {
        return new ExercisePlanRecordSuggested(id, memberID, TEST_STAFF_ID, TEST_DATETIME);
    }

    static public void checkExercisePlanRecordSuggested(ExercisePlanRecordSuggested testExercisePlanRecordSuggested, String id) {
        assertThat(testExercisePlanRecordSuggested.getID(), is(id));
        assertThat(testExercisePlanRecordSuggested.getStaffID(), is(TEST_STAFF_ID));
        assertThat(testExercisePlanRecordSuggested.getMemberID(), is(TEST_MEMBER_ID));
        assertThat(testExercisePlanRecordSuggested.getDatetime(), is(TEST_DATETIME));
    }

}
