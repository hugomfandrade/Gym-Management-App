package org.hugoandrade.gymapp.data;

import android.os.Parcel;

import java.util.Calendar;
import java.util.Objects;

public class ExercisePlanSuggested extends ExercisePlan {

    private User mStaff;

    public static class Entry {

        public static final String TABLE_NAME = "ExercisePlanRecordSuggested";

        public static class Cols {
            public static final String ID = "id";
            public static final String MEMBER_ID = "MemberID";
            public static final String STAFF_ID = "StaffID";
            public static final String DATETIME = "Datetime";
        }
    }

    public ExercisePlanSuggested(User staff, ExercisePlan exercisePlan) {
        super(exercisePlan.getID(), exercisePlan.getMember().getID(), exercisePlan.getDatetime());
        mStaff = staff;

        setMember(exercisePlan.getMember());
        setExerciseSetList(exercisePlan.getExerciseSetList());
    }

    public ExercisePlanSuggested(String id, String memberID, String staffID, Calendar datetime) {
        super(id, memberID, datetime);
        mStaff = new User(staffID);
    }

    public String getStaffID() {
        return mStaff.getID();
    }

    public User getStaff() {
        return mStaff;
    }

    public void setStaff(User staff) {
        mStaff = staff;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ExercisePlanSuggested) {
            ExercisePlanSuggested u = (ExercisePlanSuggested) obj;
            return super.equals(obj) &&
                    Objects.equals(mStaff, u.mStaff);// && Objects.equals(mDatetime, u.mDatetime) ;
        }
        return false;
    }

    protected ExercisePlanSuggested(Parcel in) {
        super(in);
        mStaff = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(mStaff, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExercisePlanSuggested> CREATOR = new Creator<ExercisePlanSuggested>() {
        @Override
        public ExercisePlanSuggested createFromParcel(Parcel in) {
            return new ExercisePlanSuggested(in);
        }

        @Override
        public ExercisePlanSuggested[] newArray(int size) {
            return new ExercisePlanSuggested[size];
        }
    };
}
