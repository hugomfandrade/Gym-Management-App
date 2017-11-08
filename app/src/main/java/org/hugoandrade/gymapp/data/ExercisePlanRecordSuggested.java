package org.hugoandrade.gymapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ExercisePlanRecordSuggested implements Parcelable {

    private String mID;
    private String mMemberID;
    private String mStaffID;
    private Calendar mDatetime;

    private User mMember;
    private User mStaff;
    private List<ExerciseSet> mExerciseSetList;

    public static class Entry {

        public static final String TABLE_NAME = "ExercisePlanRecordSuggested";

        public static class Cols {
            public static final String ID = "id";
            public static final String MEMBER_ID = "MemberID";
            public static final String STAFF_ID = "StaffID";
            public static final String DATETIME = "Datetime";
        }
    }

    public ExercisePlanRecordSuggested(User staff, ExercisePlanRecord exercisePlanRecord) {
        mStaff = staff;
        mStaffID = staff.getID();

        mID = exercisePlanRecord.getID();
        mMember = exercisePlanRecord.getMember();
        mMemberID = exercisePlanRecord.getMemberID();
        mDatetime = exercisePlanRecord.getDatetime();
        mExerciseSetList = exercisePlanRecord.getExerciseSetList();
    }

    public ExercisePlanRecordSuggested(String id, String memberID, String staffID, Calendar datetime) {

        mID = id;
        mMember = new User(memberID);
        mMemberID = memberID;
        mStaff = new User(staffID);
        mStaffID = staffID;
        mDatetime = datetime;
        mExerciseSetList = new ArrayList<>();
    }

    public ExercisePlanRecord getAsExercisePlan() {
        ExercisePlanRecord exercisePlanRecord = new ExercisePlanRecord(mID, mMemberID, mDatetime);
        exercisePlanRecord.setMember(mMember);
        exercisePlanRecord.setExerciseSetList(mExerciseSetList);
        return exercisePlanRecord;
    }

    public String getID() {
        return mID;
    }

    public String getMemberID() {
        return mMemberID;
    }

    public String getStaffID() {
        return mStaffID;
    }

    public User getStaff() {
        return mStaff;
    }

    public Calendar getDatetime() {
        return mDatetime;
    }

    public void setDatetime(Calendar datetime) {
        mDatetime = datetime;
    }

    public List<ExerciseSet> getExerciseSetList() {
        return mExerciseSetList;
    }

    public void setExerciseSetList(List<ExerciseSet> exerciseSetList) {
        mExerciseSetList = exerciseSetList;
    }

    public void setMember(User member) {
        mMember = member;
        mMemberID = member.getID();
    }

    public void setStaff(User staff) {
        mStaff = staff;
        mStaffID = staff.getID();
    }

    public void addExerciseSet(ExerciseSet exerciseSet) {
        if (mExerciseSetList == null)
            mExerciseSetList = new ArrayList<>();

        mExerciseSetList.add(exerciseSet);
        Collections.sort(mExerciseSetList, new Comparator<ExerciseSet>() {
            @Override
            public int compare(ExerciseSet o1, ExerciseSet o2) {
                return o1.getExercisePlanRecordOrder() - o2.getExercisePlanRecordOrder();
            }
        });
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ExercisePlanRecordSuggested) {
            ExercisePlanRecordSuggested u = (ExercisePlanRecordSuggested) obj;
            return Objects.equals(mID, u.mID) &&
                    Objects.equals(mMember, u.mMember) &&
                    Objects.equals(mMemberID, u.mMemberID) &&
                    Objects.equals(mStaff, u.mStaff) &&
                    Objects.equals(mStaffID, u.mStaffID);// && Objects.equals(mDatetime, u.mDatetime) ;
        }
        return false;
    }

    protected ExercisePlanRecordSuggested(Parcel in) {
        mID = in.readString();
        mMemberID = in.readString();
        mMember = in.readParcelable(User.class.getClassLoader());
        mStaffID = in.readString();
        mStaff = in.readParcelable(User.class.getClassLoader());
        mDatetime = (Calendar) in.readSerializable();

        mExerciseSetList = new ArrayList<>();
        in.readTypedList(mExerciseSetList, ExerciseSet.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mID);
        dest.writeString(mMemberID);
        dest.writeParcelable(mMember, flags);
        dest.writeString(mStaffID);
        dest.writeParcelable(mStaff, flags);
        dest.writeSerializable(mDatetime);
        dest.writeTypedList(mExerciseSetList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExercisePlanRecordSuggested> CREATOR = new Creator<ExercisePlanRecordSuggested>() {
        @Override
        public ExercisePlanRecordSuggested createFromParcel(Parcel in) {
            return new ExercisePlanRecordSuggested(in);
        }

        @Override
        public ExercisePlanRecordSuggested[] newArray(int size) {
            return new ExercisePlanRecordSuggested[size];
        }
    };
}
