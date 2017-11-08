package org.hugoandrade.gymapp.data;

// imports

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.hugoandrade.gymapp.shared.ExercisePlanRecordSuggested_Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ExercisePlanRecordSuggested_AndroidTest {

    private ExercisePlanRecordSuggested mExercisePlanRecordSuggestedA;
    private ExercisePlanRecordSuggested mExercisePlanRecordSuggestedB;

    static private ExercisePlanRecordSuggested newA() {
        return ExercisePlanRecordSuggested_Utils.newExercisePlanRecordSuggested(ExercisePlanRecordSuggested_Utils.TEST_ID_1);
    }

    static private ExercisePlanRecordSuggested newB() {
        return ExercisePlanRecordSuggested_Utils.newExercisePlanRecordSuggested(ExercisePlanRecordSuggested_Utils.TEST_ID_2);
    }

    @Before
    public void setUp() {
        mExercisePlanRecordSuggestedA = newA();
        mExercisePlanRecordSuggestedB = newB();
    }

    @Test
    public void testParcelable() {

        // Write the data.
        Parcel parcel = Parcel.obtain();
        mExercisePlanRecordSuggestedA.writeToParcel(parcel, mExercisePlanRecordSuggestedA.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        ExercisePlanRecordSuggested resultingExercisePlanRecordSuggested = ExercisePlanRecordSuggested.CREATOR.createFromParcel(parcel);

        System.out.println(mExercisePlanRecordSuggestedA);
        System.out.println(resultingExercisePlanRecordSuggested);

        // Verify that the received data is correct.
        ExercisePlanRecordSuggested_Utils
                .checkExercisePlanRecordSuggested(resultingExercisePlanRecordSuggested,
                                                  ExercisePlanRecordSuggested_Utils.TEST_ID_1);
    }

    @Test
    public void feed_ParcelableWriteReadArray() {
        // Write the data.
        Parcel parcel = Parcel.obtain();
        mExercisePlanRecordSuggestedA.writeToParcel(parcel, mExercisePlanRecordSuggestedA.describeContents());
        mExercisePlanRecordSuggestedB.writeToParcel(parcel, mExercisePlanRecordSuggestedB.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // create an array of appropriate size.
        ExercisePlanRecordSuggested[] resultingArray = ExercisePlanRecordSuggested.CREATOR.newArray(2);

        assertThat(resultingArray.length, is(2));

        // Read the data.
        resultingArray[0] = ExercisePlanRecordSuggested.CREATOR.createFromParcel(parcel);
        resultingArray[1] = ExercisePlanRecordSuggested.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        ExercisePlanRecordSuggested_Utils.checkExercisePlanRecordSuggested(resultingArray[0], ExercisePlanRecordSuggested_Utils.TEST_ID_1);
        ExercisePlanRecordSuggested_Utils.checkExercisePlanRecordSuggested(resultingArray[1], ExercisePlanRecordSuggested_Utils.TEST_ID_2);
    }


}



















