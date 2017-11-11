package org.hugoandrade.gymapp.shared;

// imports

import org.hamcrest.core.IsNull;
import org.hugoandrade.gymapp.data.ExercisePlan;
import org.hugoandrade.gymapp.data.ExerciseSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * Shared ExercisePlan testing Utils class
 */
@SuppressWarnings("SameParameterValue")
public class ExercisePlan_Utils {

    static final public String TEST_ID_1 = "id_1";
    static final public String TEST_ID_2 = "id_2";
    static final private String TEST_MEMBER_ID = "member_id";
    static final private Calendar TEST_DATETIME = Calendar.getInstance();

    static public ExercisePlan newExercisePlan(String id) {
        List<ExerciseSet> exerciseSetList = new ArrayList<>();
        ExerciseSet exerciseSetA = ExerciseSet_Utils.newExerciseSet(ExerciseSet_Utils.TEST_ID_1);
        ExerciseSet exerciseSetB = ExerciseSet_Utils.newExerciseSet(ExerciseSet_Utils.TEST_ID_2);
        exerciseSetList.add(exerciseSetA);
        exerciseSetList.add(exerciseSetB);
        ExercisePlan plan = new ExercisePlan(id, TEST_MEMBER_ID, TEST_DATETIME);
        plan.setExerciseSetList(exerciseSetList);
        return plan;
    }

    static public ExercisePlan newExercisePlan(String id, String memberID) {
        List<ExerciseSet> exerciseSetList = new ArrayList<>();
        ExerciseSet exerciseSetA = ExerciseSet_Utils.newExerciseSet(ExerciseSet_Utils.TEST_ID_1);
        ExerciseSet exerciseSetB = ExerciseSet_Utils.newExerciseSet(ExerciseSet_Utils.TEST_ID_2);
        exerciseSetList.add(exerciseSetA);
        exerciseSetList.add(exerciseSetB);
        ExercisePlan planRecord = new ExercisePlan(id, memberID, TEST_DATETIME);
        planRecord.setExerciseSetList(exerciseSetList);
        return planRecord;
    }

    static public void checkExercisePlan(ExercisePlan testExercisePlan, String id) {
        assertThat(testExercisePlan.getID(), is(id));
        assertThat(testExercisePlan.getMemberID(), is(TEST_MEMBER_ID));
        assertThat(testExercisePlan.getDatetime(), is(TEST_DATETIME));
        assertThat(testExercisePlan.getExerciseSetList(), is(IsNull.notNullValue()));
        assertThat(testExercisePlan.getExerciseSetList().size(), is(2));
        ExerciseSet_Utils.checkExerciseSet(testExercisePlan.getExerciseSetList().get(0),
                ExerciseSet_Utils.TEST_ID_1);
        ExerciseSet_Utils.checkExerciseSet(testExercisePlan.getExerciseSetList().get(1),
                ExerciseSet_Utils.TEST_ID_2);
    }

}
