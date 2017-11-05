package org.hugoandrade.gymapp.data;

// imports

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.hugoandrade.gymapp.shared.WaitingUser_Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class WaitingUser_AndroidTest {

    private WaitingUser mWaitingUserA;
    private WaitingUser mWaitingUserB;

    static private WaitingUser newA() {
        return WaitingUser_Utils.newWaitingUser(WaitingUser_Utils.TEST_ID_1);
    }

    static private WaitingUser newB() {
        return WaitingUser_Utils.newWaitingUser(WaitingUser_Utils.TEST_ID_2);
    }

    @Before
    public void setUp() {
        mWaitingUserA = newA();
        mWaitingUserB = newB();
    }

    @Test
    public void testParcelable() {

        // Write the data.
        Parcel parcel = Parcel.obtain();
        mWaitingUserA.writeToParcel(parcel, mWaitingUserA.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        WaitingUser resultingWaitingUser = WaitingUser.CREATOR.createFromParcel(parcel);

        System.out.println(mWaitingUserA);
        System.out.println(resultingWaitingUser);

        // Verify that the received data is correct.
        WaitingUser_Utils.checkWaitingUser(resultingWaitingUser, WaitingUser_Utils.TEST_ID_1);
    }

    @Test
    public void feed_ParcelableWriteReadArray() {
        // Write the data.
        Parcel parcel = Parcel.obtain();
        mWaitingUserA.writeToParcel(parcel, mWaitingUserA.describeContents());
        mWaitingUserB.writeToParcel(parcel, mWaitingUserB.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // create an array of appropriate size.
        WaitingUser[] resultingArray = WaitingUser.CREATOR.newArray(2);

        assertThat(resultingArray.length, is(2));

        // Read the data.
        resultingArray[0] = WaitingUser.CREATOR.createFromParcel(parcel);
        resultingArray[1] = WaitingUser.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        WaitingUser_Utils.checkWaitingUser(resultingArray[0], WaitingUser_Utils.TEST_ID_1);
        WaitingUser_Utils.checkWaitingUser(resultingArray[1], WaitingUser_Utils.TEST_ID_2);
    }


}



















