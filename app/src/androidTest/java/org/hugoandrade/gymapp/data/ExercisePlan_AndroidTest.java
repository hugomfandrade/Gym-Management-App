package org.hugoandrade.gymapp.data;

// imports

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.hugoandrade.gymapp.shared.ExercisePlan_Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ExercisePlan_AndroidTest {

    private ExercisePlan mExercisePlanA;
    private ExercisePlan mExercisePlanB;

    static private ExercisePlan newA() {
        return ExercisePlan_Utils.newExercisePlan(ExercisePlan_Utils.TEST_ID_1);
    }

    static private ExercisePlan newB() {
        return ExercisePlan_Utils.newExercisePlan(ExercisePlan_Utils.TEST_ID_2);
    }

    @Before
    public void setUp() {
        mExercisePlanA = newA();
        mExercisePlanB = newB();
    }

    @Test
    public void testParcelable() {

        // Write the data.
        Parcel parcel = Parcel.obtain();
        mExercisePlanA.writeToParcel(parcel, mExercisePlanA.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        ExercisePlan resultingExercisePlan = ExercisePlan.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        ExercisePlan_Utils.checkExercisePlan(resultingExercisePlan, ExercisePlan_Utils.TEST_ID_1);
    }

    @Test
    public void feed_ParcelableWriteReadArray() {
        // Write the data.
        Parcel parcel = Parcel.obtain();
        mExercisePlanA.writeToParcel(parcel, mExercisePlanA.describeContents());
        mExercisePlanB.writeToParcel(parcel, mExercisePlanB.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // create an array of appropriate size.
        ExercisePlan[] resultingArray = ExercisePlan.CREATOR.newArray(2);

        assertThat(resultingArray.length, is(2));

        // Read the data.
        resultingArray[0] = ExercisePlan.CREATOR.createFromParcel(parcel);
        resultingArray[1] = ExercisePlan.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        ExercisePlan_Utils.checkExercisePlan(resultingArray[0], ExercisePlan_Utils.TEST_ID_1);
        ExercisePlan_Utils.checkExercisePlan(resultingArray[1], ExercisePlan_Utils.TEST_ID_2);
    }
}



















