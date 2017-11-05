package org.hugoandrade.gymapp.data;

// imports

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.hugoandrade.gymapp.shared.ExerciseSet_Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ExerciseSet_AndroidTest {

    private ExerciseSet mExerciseSetA;
    private ExerciseSet mExerciseSetB;

    static private ExerciseSet newA() {
        return ExerciseSet_Utils.newExerciseSet(ExerciseSet_Utils.TEST_ID_1);
    }

    static private ExerciseSet newB() {
        return ExerciseSet_Utils.newExerciseSet(ExerciseSet_Utils.TEST_ID_2);
    }

    @Before
    public void setUp() {
        mExerciseSetA = newA();
        mExerciseSetB = newB();
    }

    @Test
    public void testParcelable() {

        // Write the data.
        Parcel parcel = Parcel.obtain();
        mExerciseSetA.writeToParcel(parcel, mExerciseSetA.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        ExerciseSet resultingExerciseSet = ExerciseSet.CREATOR.createFromParcel(parcel);

        System.out.println(mExerciseSetA);
        System.out.println(resultingExerciseSet);

        // Verify that the received data is correct.
        ExerciseSet_Utils.checkExerciseSet(resultingExerciseSet, ExerciseSet_Utils.TEST_ID_1);
    }

    @Test
    public void feed_ParcelableWriteReadArray() {
        // Write the data.
        Parcel parcel = Parcel.obtain();
        mExerciseSetA.writeToParcel(parcel, mExerciseSetA.describeContents());
        mExerciseSetB.writeToParcel(parcel, mExerciseSetB.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // create an array of appropriate size.
        ExerciseSet[] resultingArray = ExerciseSet.CREATOR.newArray(2);

        assertThat(resultingArray.length, is(2));

        // Read the data.
        resultingArray[0] = ExerciseSet.CREATOR.createFromParcel(parcel);
        resultingArray[1] = ExerciseSet.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        ExerciseSet_Utils.checkExerciseSet(resultingArray[0], ExerciseSet_Utils.TEST_ID_1);
        ExerciseSet_Utils.checkExerciseSet(resultingArray[1], ExerciseSet_Utils.TEST_ID_2);
    }


}



















