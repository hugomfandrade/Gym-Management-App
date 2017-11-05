package org.hugoandrade.gymapp.data;

// imports

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.hugoandrade.gymapp.shared.Exercise_Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.hugoandrade.gymapp.shared.Exercise_Utils.TEST_ID_1;
import static org.hugoandrade.gymapp.shared.Exercise_Utils.TEST_ID_2;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class Exercise_AndroidTest {

    private Exercise mExerciseA;
    private Exercise mExerciseB;

    static private Exercise newA() {
        return Exercise_Utils.newExercise(TEST_ID_1);
    }

    static private Exercise newB() {
        return Exercise_Utils.newExercise(TEST_ID_2);
    }

    @Before
    public void setUp() {
        mExerciseA = newA();
        mExerciseB = newB();
    }

    @Test
    public void testParcelable() {

        // Write the data.
        Parcel parcel = Parcel.obtain();
        mExerciseA.writeToParcel(parcel, mExerciseA.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        Exercise resultingExercise = Exercise.CREATOR.createFromParcel(parcel);

        System.out.println(mExerciseA);
        System.out.println(resultingExercise);

        // Verify that the received data is correct.
        Exercise_Utils.checkExercise(resultingExercise, TEST_ID_1);
    }

    @Test
    public void feed_ParcelableWriteReadArray() {
        // Write the data.
        Parcel parcel = Parcel.obtain();
        mExerciseA.writeToParcel(parcel, mExerciseA.describeContents());
        mExerciseB.writeToParcel(parcel, mExerciseB.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // create an array of appropriate size.
        Exercise[] resultingArray = Exercise.CREATOR.newArray(2);

        assertThat(resultingArray.length, is(2));

        // Read the data.
        resultingArray[0] = Exercise.CREATOR.createFromParcel(parcel);
        resultingArray[1] = Exercise.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        Exercise_Utils.checkExercise(resultingArray[0], TEST_ID_1);
        Exercise_Utils.checkExercise(resultingArray[1], TEST_ID_2);
    }


}



















