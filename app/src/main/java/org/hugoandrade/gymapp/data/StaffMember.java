package org.hugoandrade.gymapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class StaffMember implements Parcelable {

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

    protected StaffMember(Parcel in) {
        mStaffID = in.readString();
        mMemberID = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mStaffID);
        dest.writeString(mMemberID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StaffMember> CREATOR = new Creator<StaffMember>() {
        @Override
        public StaffMember createFromParcel(Parcel in) {
            return new StaffMember(in);
        }

        @Override
        public StaffMember[] newArray(int size) {
            return new StaffMember[size];
        }
    };

    public String getStaffID() {
        return mStaffID;
    }

    public String getMemberID() {
        return mMemberID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof StaffMember) {
            StaffMember u = (StaffMember) obj;
            return Objects.equals(mStaffID, u.mStaffID) &&
                    Objects.equals(mMemberID, u.mMemberID) ;
        }
        return false;
    }
}
