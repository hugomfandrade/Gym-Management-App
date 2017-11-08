package org.hugoandrade.gymapp.shared;

// imports

import org.hugoandrade.gymapp.data.StaffMember;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Shared StaffMember testing Utils class
 */
public class StaffMember_Utils {

    static final public String TEST_MEMBER_ID_1 = "member_id_1";
    static final public String TEST_MEMBER_ID_2 = "member_id_2";
    static final public String TEST_STAFF_ID = "staff_id";

    static public StaffMember newStaffMember(String memberID) {
        return new StaffMember(TEST_STAFF_ID, memberID);
    }

    static public StaffMember newStaffMember(String staffID, String memberID) {
        return new StaffMember(staffID, memberID);
    }

    static public void checkStaffMember(StaffMember testStaffMember, String memberID) {
        assertThat(testStaffMember.getMemberID(), is(memberID));
        assertThat(testStaffMember.getStaffID(), is(TEST_STAFF_ID));
    }

}
