package org.hugoandrade.gymapp.data;

public class StaffMember {

    private final String mStaffID;
    private final String mMemberID;

    public static class Entry {

        public static final String TABLE_NAME  = "StaffMember";

        public static class Cols {

            public static final String STAFF_ID = "StaffID";
            public static final String MEMBER_ID = "MemberID";
        }
    }

    public StaffMember(String staffID, String memberID) {
        mStaffID = staffID;
        mMemberID = memberID;
    }

    public String getStaffID() {
        return mStaffID;
    }

    public String getMemberID() {
        return mMemberID;
    }
}
