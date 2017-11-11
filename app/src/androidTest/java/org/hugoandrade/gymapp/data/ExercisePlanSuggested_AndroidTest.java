package org.hugoandrade.gymapp.data;

// imports

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.hugoandrade.gymapp.shared.ExercisePlanSuggested_Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ExercisePlanSuggested_AndroidTest {

    private ExercisePlanSuggested mExercisePlanSuggestedA;
    private ExercisePlanSuggested mExercisePlanSuggestedB;

    static private ExercisePlanSuggested newA() {
        return ExercisePlanSuggested_Utils.newExercisePlanSuggested(ExercisePlanSuggested_Utils.TEST_ID_1);
    }

    static private ExercisePlanSuggested newB() {
        return ExercisePlanSuggested_Utils.newExercisePlanSuggested(ExercisePlanSuggested_Utils.TEST_ID_2);
    }

    @Before
    public void setUp() {
        mExercisePlanSuggestedA = newA();
        mExercisePlanSuggestedB = newB();
    }

    @Test
    public void testParcelable() {

        // Write the data.
        Parcel parcel = Parcel.obtain();
        mExercisePlanSuggestedA.writeToParcel(parcel, mExercisePlanSuggestedA.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        ExercisePlanSuggested resultingExercisePlanSuggested = ExercisePlanSuggested.CREATOR.createFromParcel(parcel);

        System.out.println(mExercisePlanSuggestedA);
        System.out.println(resultingExercisePlanSuggested);

        // Verify that the received data is correct.
        ExercisePlanSuggested_Utils
                .checkExercisePlanSuggested(resultingExercisePlanSuggested,
                                                  ExercisePlanSuggested_Utils.TEST_ID_1);
    }

    @Test
    public void feed_ParcelableWriteReadArray() {
        // Write the data.
        Parcel parcel = Parcel.obtain();
        mExercisePlanSuggestedA.writeToParcel(parcel, mExercisePlanSuggestedA.describeContents());
        mExercisePlanSuggestedB.writeToParcel(parcel, mExercisePlanSuggestedB.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // create an array of appropriate size.
        ExercisePlanSuggested[] resultingArray = ExercisePlanSuggested.CREATOR.newArray(2);

        assertThat(resultingArray.length, is(2));

        // Read the data.
        resultingArray[0] = ExercisePlanSuggested.CREATOR.createFromParcel(parcel);
        resultingArray[1] = ExercisePlanSuggested.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        ExercisePlanSuggested_Utils.checkExercisePlanSuggested(resultingArray[0], ExercisePlanSuggested_Utils.TEST_ID_1);
        ExercisePlanSuggested_Utils.checkExercisePlanSuggested(resultingArray[1], ExercisePlanSuggested_Utils.TEST_ID_2);
    }


}



















