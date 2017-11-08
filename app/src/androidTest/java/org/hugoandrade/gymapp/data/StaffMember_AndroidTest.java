package org.hugoandrade.gymapp.data;

// imports

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.hugoandrade.gymapp.shared.StaffMember_Utils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.hugoandrade.gymapp.shared.StaffMember_Utils.TEST_MEMBER_ID_1;
import static org.hugoandrade.gymapp.shared.StaffMember_Utils.TEST_MEMBER_ID_2;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class StaffMember_AndroidTest {

    private StaffMember mStaffMemberA;
    private StaffMember mStaffMemberB;

    static private StaffMember newA() {
        return StaffMember_Utils.newStaffMember(TEST_MEMBER_ID_1);
    }

    static private StaffMember newB() {
        return StaffMember_Utils.newStaffMember(TEST_MEMBER_ID_2);
    }

    @Before
    public void setUp() {
        mStaffMemberA = newA();
        mStaffMemberB = newB();
    }

    @Test
    public void testParcelable() {

        // Write the data.
        Parcel parcel = Parcel.obtain();
        mStaffMemberA.writeToParcel(parcel, mStaffMemberA.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Read the data.
        StaffMember resultingStaffMember = StaffMember.CREATOR.createFromParcel(parcel);

        System.out.println(mStaffMemberA);
        System.out.println(resultingStaffMember);

        // Verify that the received data is correct.
        StaffMember_Utils.checkStaffMember(resultingStaffMember, TEST_MEMBER_ID_1);
    }

    @Test
    public void feed_ParcelableWriteReadArray() {
        // Write the data.
        Parcel parcel = Parcel.obtain();
        mStaffMemberA.writeToParcel(parcel, mStaffMemberA.describeContents());
        mStaffMemberB.writeToParcel(parcel, mStaffMemberB.describeContents());

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // create an array of appropriate size.
        StaffMember[] resultingArray = StaffMember.CREATOR.newArray(2);

        assertThat(resultingArray.length, is(2));

        // Read the data.
        resultingArray[0] = StaffMember.CREATOR.createFromParcel(parcel);
        resultingArray[1] = StaffMember.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        StaffMember_Utils.checkStaffMember(resultingArray[0], TEST_MEMBER_ID_1);
        StaffMember_Utils.checkStaffMember(resultingArray[1], TEST_MEMBER_ID_2);
    }


}



















