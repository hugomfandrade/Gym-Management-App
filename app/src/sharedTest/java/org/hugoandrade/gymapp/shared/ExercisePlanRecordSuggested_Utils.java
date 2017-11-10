package org.hugoandrade.gymapp.shared;

// imports

import org.hamcrest.core.IsNull;
import org.hugoandrade.gymapp.data.ExercisePlanRecordSuggested;
import org.hugoandrade.gymapp.data.ExerciseSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
        List<ExerciseSet> exerciseSetList = new ArrayList<>();
        ExerciseSet exerciseSetA = ExerciseSet_Utils.newExerciseSet(ExerciseSet_Utils.TEST_ID_1);
        ExerciseSet exerciseSetB = ExerciseSet_Utils.newExerciseSet(ExerciseSet_Utils.TEST_ID_2);
        exerciseSetList.add(exerciseSetA);
        exerciseSetList.add(exerciseSetB);
        ExercisePlanRecordSuggested planRecord = new ExercisePlanRecordSuggested(id, TEST_MEMBER_ID, TEST_STAFF_ID, TEST_DATETIME);
        planRecord.setExerciseSetList(exerciseSetList);
        return planRecord;
    }

    static public ExercisePlanRecordSuggested newExercisePlanRecordSuggested(String id, String memberID) {
        List<ExerciseSet> exerciseSetList = new ArrayList<>();
        ExerciseSet exerciseSetA = ExerciseSet_Utils.newExerciseSet(ExerciseSet_Utils.TEST_ID_1);
        ExerciseSet exerciseSetB = ExerciseSet_Utils.newExerciseSet(ExerciseSet_Utils.TEST_ID_2);
        exerciseSetList.add(exerciseSetA);
        exerciseSetList.add(exerciseSetB);
        ExercisePlanRecordSuggested planRecord = new ExercisePlanRecordSuggested(id, memberID, TEST_STAFF_ID, TEST_DATETIME);
        planRecord.setExerciseSetList(exerciseSetList);
        return planRecord;
    }

    static public void checkExercisePlanRecordSuggested(ExercisePlanRecordSuggested testExercisePlanRecordSuggested, String id) {
        assertThat(testExercisePlanRecordSuggested.getID(), is(id));
        assertThat(testExercisePlanRecordSuggested.getStaffID(), is(TEST_STAFF_ID));
        assertThat(testExercisePlanRecordSuggested.getMemberID(), is(TEST_MEMBER_ID));
        assertThat(testExercisePlanRecordSuggested.getDatetime(), is(TEST_DATETIME));
        assertThat(testExercisePlanRecordSuggested.getExerciseSetList(), is(IsNull.notNullValue()));
        assertThat(testExercisePlanRecordSuggested.getExerciseSetList().size(), is(2));
        ExerciseSet_Utils.checkExerciseSet(testExercisePlanRecordSuggested.getExerciseSetList().get(0),
                ExerciseSet_Utils.TEST_ID_1);
        ExerciseSet_Utils.checkExerciseSet(testExercisePlanRecordSuggested.getExerciseSetList().get(1),
                ExerciseSet_Utils.TEST_ID_2);
    }

}
