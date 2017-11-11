package org.hugoandrade.gymapp.shared;

// imports

import org.hamcrest.core.IsNull;
import org.hugoandrade.gymapp.data.ExercisePlanSuggested;
import org.hugoandrade.gymapp.data.ExerciseSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Shared ExercisePlanSuggested testing Utils class
 */
@SuppressWarnings("SameParameterValue")
public class ExercisePlanSuggested_Utils {

    static final public String TEST_ID_1 = "id_1";
    static final public String TEST_ID_2 = "id_2";
    static final private String TEST_STAFF_ID = "staff_id";
    static final private String TEST_MEMBER_ID = "member_id";
    static final private Calendar TEST_DATETIME = Calendar.getInstance();

    static public ExercisePlanSuggested newExercisePlanSuggested(String id) {
        List<ExerciseSet> exerciseSetList = new ArrayList<>();
        ExerciseSet exerciseSetA = ExerciseSet_Utils.newExerciseSet(ExerciseSet_Utils.TEST_ID_1);
        ExerciseSet exerciseSetB = ExerciseSet_Utils.newExerciseSet(ExerciseSet_Utils.TEST_ID_2);
        exerciseSetList.add(exerciseSetA);
        exerciseSetList.add(exerciseSetB);
        ExercisePlanSuggested planSuggested = new ExercisePlanSuggested(id, TEST_MEMBER_ID, TEST_STAFF_ID, TEST_DATETIME);
        planSuggested.setExerciseSetList(exerciseSetList);
        return planSuggested;
    }

    static public ExercisePlanSuggested newExercisePlanSuggested(String id, String memberID) {
        List<ExerciseSet> exerciseSetList = new ArrayList<>();
        ExerciseSet exerciseSetA = ExerciseSet_Utils.newExerciseSet(ExerciseSet_Utils.TEST_ID_1);
        ExerciseSet exerciseSetB = ExerciseSet_Utils.newExerciseSet(ExerciseSet_Utils.TEST_ID_2);
        exerciseSetList.add(exerciseSetA);
        exerciseSetList.add(exerciseSetB);
        ExercisePlanSuggested planSuggested = new ExercisePlanSuggested(id, memberID, TEST_STAFF_ID, TEST_DATETIME);
        planSuggested.setExerciseSetList(exerciseSetList);
        return planSuggested;
    }

    static public void checkExercisePlanSuggested(ExercisePlanSuggested testExercisePlanSuggested, String id) {
        assertThat(testExercisePlanSuggested.getID(), is(id));
        assertThat(testExercisePlanSuggested.getStaffID(), is(TEST_STAFF_ID));
        assertThat(testExercisePlanSuggested.getMemberID(), is(TEST_MEMBER_ID));
        assertThat(testExercisePlanSuggested.getDatetime(), is(TEST_DATETIME));
        assertThat(testExercisePlanSuggested.getExerciseSetList(), is(IsNull.notNullValue()));
        assertThat(testExercisePlanSuggested.getExerciseSetList().size(), is(2));
        ExerciseSet_Utils.checkExerciseSet(testExercisePlanSuggested.getExerciseSetList().get(0),
                ExerciseSet_Utils.TEST_ID_1);
        ExerciseSet_Utils.checkExerciseSet(testExercisePlanSuggested.getExerciseSetList().get(1),
                ExerciseSet_Utils.TEST_ID_2);
    }

}
