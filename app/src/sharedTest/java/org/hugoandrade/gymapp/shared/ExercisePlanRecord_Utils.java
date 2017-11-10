package org.hugoandrade.gymapp.shared;

// imports

import org.hamcrest.core.IsNull;
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.data.ExercisePlanRecord;
import org.hugoandrade.gymapp.data.ExerciseSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
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
        List<ExerciseSet> exerciseSetList = new ArrayList<>();
        ExerciseSet exerciseSetA = ExerciseSet_Utils.newExerciseSet(ExerciseSet_Utils.TEST_ID_1);
        ExerciseSet exerciseSetB = ExerciseSet_Utils.newExerciseSet(ExerciseSet_Utils.TEST_ID_2);
        exerciseSetList.add(exerciseSetA);
        exerciseSetList.add(exerciseSetB);
        ExercisePlanRecord planRecord = new ExercisePlanRecord(id, TEST_MEMBER_ID, TEST_DATETIME);
        planRecord.setExerciseSetList(exerciseSetList);
        return planRecord;
    }

    static public ExercisePlanRecord newExercisePlanRecord(String id, String memberID) {
        List<ExerciseSet> exerciseSetList = new ArrayList<>();
        ExerciseSet exerciseSetA = ExerciseSet_Utils.newExerciseSet(ExerciseSet_Utils.TEST_ID_1);
        ExerciseSet exerciseSetB = ExerciseSet_Utils.newExerciseSet(ExerciseSet_Utils.TEST_ID_2);
        exerciseSetList.add(exerciseSetA);
        exerciseSetList.add(exerciseSetB);
        ExercisePlanRecord planRecord = new ExercisePlanRecord(id, memberID, TEST_DATETIME);
        planRecord.setExerciseSetList(exerciseSetList);
        return planRecord;
    }

    static public void checkExercisePlanRecord(ExercisePlanRecord testExercisePlanRecord, String id) {
        assertThat(testExercisePlanRecord.getID(), is(id));
        assertThat(testExercisePlanRecord.getMemberID(), is(TEST_MEMBER_ID));
        assertThat(testExercisePlanRecord.getDatetime(), is(TEST_DATETIME));
        assertThat(testExercisePlanRecord.getExerciseSetList(), is(IsNull.notNullValue()));
        assertThat(testExercisePlanRecord.getExerciseSetList().size(), is(2));
        ExerciseSet_Utils.checkExerciseSet(testExercisePlanRecord.getExerciseSetList().get(0),
                ExerciseSet_Utils.TEST_ID_1);
        ExerciseSet_Utils.checkExerciseSet(testExercisePlanRecord.getExerciseSetList().get(1),
                ExerciseSet_Utils.TEST_ID_2);
    }

}
