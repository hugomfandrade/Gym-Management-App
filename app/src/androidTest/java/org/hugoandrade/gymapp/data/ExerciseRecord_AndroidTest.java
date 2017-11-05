package org.hugoandrade.gymapp.data;

// imports

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.hugoandrade.gymapp.shared.ExerciseRecord_Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ExerciseRecord_AndroidTest {

    private ExerciseRecord mExerciseRecordA;
    private ExerciseRecord mExerciseRecordB;

    static private ExerciseRecord newA() {
        return ExerciseRecord_Utils.newExerciseRecord(ExerciseRecord_Utils.TEST_ID_1);
    }

    static private ExerciseRecord newB() {
        return ExerciseRecord_Utils.newExerciseRecord(ExerciseRecord_Utils.TEST_ID_2);
    }

    @Before
    public void setUp() {
        mExerciseRecordA = newA();
        mExerciseRecordB = newB();
    }

    @Test
    public void testParcelable() {

        // Write the data.
        Parcel parcel = Parcel.obtain();
        mExerciseRecordA.writeToParcel(parcel, mExerciseRecordA.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        ExerciseRecord resultingExerciseRecord = ExerciseRecord.CREATOR.createFromParcel(parcel);

        System.out.println(mExerciseRecordA);
        System.out.println(resultingExerciseRecord);

        // Verify that the received data is correct.
        ExerciseRecord_Utils.checkExerciseRecord(resultingExerciseRecord, ExerciseRecord_Utils.TEST_ID_1);
    }

    @Test
    public void feed_ParcelableWriteReadArray() {
        // Write the data.
        Parcel parcel = Parcel.obtain();
        mExerciseRecordA.writeToParcel(parcel, mExerciseRecordA.describeContents());
        mExerciseRecordB.writeToParcel(parcel, mExerciseRecordB.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // create an array of appropriate size.
        ExerciseRecord[] resultingArray = ExerciseRecord.CREATOR.newArray(2);

        assertThat(resultingArray.length, is(2));

        // Read the data.
        resultingArray[0] = ExerciseRecord.CREATOR.createFromParcel(parcel);
        resultingArray[1] = ExerciseRecord.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        ExerciseRecord_Utils.checkExerciseRecord(resultingArray[0], ExerciseRecord_Utils.TEST_ID_1);
        ExerciseRecord_Utils.checkExerciseRecord(resultingArray[1], ExerciseRecord_Utils.TEST_ID_2);
    }


}



















