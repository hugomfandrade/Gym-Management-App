package org.hugoandrade.gymapp.data;

// imports

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.hugoandrade.gymapp.shared.Credential_Utils;
import org.hugoandrade.gymapp.shared.User_Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class Credential_AndroidTest {

    private Credential mCredentialA;
    private Credential mCredentialB;

    static private Credential newA() {
        return Credential_Utils.newCredential(Credential_Utils.TEST_ID_1);
    }

    static private Credential newB() {
        return Credential_Utils.newCredential(Credential_Utils.TEST_ID_2);
    }

    @Before
    public void setUp() {
        mCredentialA = newA();
        mCredentialB = newB();
    }

    @Test
    public void testParcelable() {

        // Write the data.
        Parcel parcel = Parcel.obtain();
        mCredentialA.writeToParcel(parcel, mCredentialA.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        Credential resultingCredential = Credential.CREATOR.createFromParcel(parcel);

        System.out.println(mCredentialA);
        System.out.println(resultingCredential);

        // Verify that the received data is correct.
        Credential_Utils.checkCredential(resultingCredential, Credential_Utils.TEST_ID_1);
    }

    @Test
    public void feed_ParcelableWriteReadArray() {
        // Write the data.
        Parcel parcel = Parcel.obtain();
        mCredentialA.writeToParcel(parcel, mCredentialA.describeContents());
        mCredentialB.writeToParcel(parcel, mCredentialB.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // create an array of appropriate size.
        Credential[] resultingArray = Credential.CREATOR.newArray(2);

        assertThat(resultingArray.length, is(2));

        // Read the data.
        resultingArray[0] = Credential.CREATOR.createFromParcel(parcel);
        resultingArray[1] = Credential.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        Credential_Utils.checkCredential(resultingArray[0], Credential_Utils.TEST_ID_1);
        Credential_Utils.checkCredential(resultingArray[1], Credential_Utils.TEST_ID_2);
    }


}



















