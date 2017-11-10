package org.hugoandrade.gymapp.data;

// imports

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.hugoandrade.gymapp.shared.ExercisePlanRecord_Utils;
import org.hugoandrade.gymapp.shared.ExerciseSet_Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ExercisePlanRecord_AndroidTest {

    private ExercisePlanRecord mExercisePlanRecordA;
    private ExercisePlanRecord mExercisePlanRecordB;

    static private ExercisePlanRecord newA() {
        return ExercisePlanRecord_Utils.newExercisePlanRecord(ExercisePlanRecord_Utils.TEST_ID_1);
    }

    static private ExercisePlanRecord newB() {
        return ExercisePlanRecord_Utils.newExercisePlanRecord(ExercisePlanRecord_Utils.TEST_ID_2);
    }

    @Before
    public void setUp() {
        mExercisePlanRecordA = newA();
        mExercisePlanRecordB = newB();
    }

    @Test
    public void testParcelable() {

        // Write the data.
        Parcel parcel = Parcel.obtain();
        mExercisePlanRecordA.writeToParcel(parcel, mExercisePlanRecordA.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        ExercisePlanRecord resultingExercisePlanRecord = ExercisePlanRecord.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        ExercisePlanRecord_Utils.checkExercisePlanRecord(resultingExercisePlanRecord, ExercisePlanRecord_Utils.TEST_ID_1);
    }

    @Test
    public void feed_ParcelableWriteReadArray() {
        // Write the data.
        Parcel parcel = Parcel.obtain();
        mExercisePlanRecordA.writeToParcel(parcel, mExercisePlanRecordA.describeContents());
        mExercisePlanRecordB.writeToParcel(parcel, mExercisePlanRecordB.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // create an array of appropriate size.
        ExercisePlanRecord[] resultingArray = ExercisePlanRecord.CREATOR.newArray(2);

        assertThat(resultingArray.length, is(2));

        // Read the data.
        resultingArray[0] = ExercisePlanRecord.CREATOR.createFromParcel(parcel);
        resultingArray[1] = ExercisePlanRecord.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        ExercisePlanRecord_Utils.checkExercisePlanRecord(resultingArray[0], ExercisePlanRecord_Utils.TEST_ID_1);
        ExercisePlanRecord_Utils.checkExercisePlanRecord(resultingArray[1], ExercisePlanRecord_Utils.TEST_ID_2);
    }
}



















