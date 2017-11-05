package org.hugoandrade.gymapp.data;

// imports

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.hugoandrade.gymapp.shared.User_Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class User_AndroidTest {

    private User mUserA;
    private User mUserB;

    static private User newA() {
        return User_Utils.newUser();
    }

    static private User newB() {
        return User_Utils.newSimpleUser();
    }

    @Before
    public void setUp() {
        mUserA = newA();
        mUserB = newB();
    }

    @Test
    public void testParcelable() {

        // Write the data.
        Parcel parcel = Parcel.obtain();
        mUserA.writeToParcel(parcel, mUserA.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        User resultingUser = User.CREATOR.createFromParcel(parcel);

        System.out.println(mUserA);
        System.out.println(resultingUser);

        // Verify that the received data is correct.
        User_Utils.checkUser(resultingUser);
    }

    @Test
    public void feed_ParcelableWriteReadArray() {
        // Write the data.
        Parcel parcel = Parcel.obtain();
        mUserA.writeToParcel(parcel, mUserA.describeContents());
        mUserB.writeToParcel(parcel, mUserB.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // create an array of appropriate size.
        User[] resultingArray = User.CREATOR.newArray(2);

        assertThat(resultingArray.length, is(2));

        // Read the data.
        resultingArray[0] = User.CREATOR.createFromParcel(parcel);
        resultingArray[1] = User.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        User_Utils.checkUser(resultingArray[0]);
        User_Utils.checkSimpleUser(resultingArray[1]);
    }


}



















