package org.hugoandrade.gymapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExercisePlanRecord implements Parcelable {

    private String mID;
    private String mMemberID;
    private Calendar mDatetime;

    private User mMember;
    private List<ExerciseSet> mExerciseSetList;

    public static class Entry {

        public static final String TABLE_NAME = "ExercisePlanRecord";

        public static class Cols {
            public static final String ID = "id";
            public static final String MEMBER_ID = "MemberID";
            public static final String DATETIME = "Datetime";
        }
    }

    public static ExercisePlanRecord empty(User user, Calendar datetime) {
        ExercisePlanRecord exercisePlanRecord = new ExercisePlanRecord();
        exercisePlanRecord.setMember(user);
        exercisePlanRecord.setDatetime(datetime);
        exercisePlanRecord.setExerciseSetList(new ArrayList<ExerciseSet>());
        return exercisePlanRecord;
    }

    private ExercisePlanRecord() {
        mID = null;
        mMemberID = null;
    }

    public ExercisePlanRecord(String id, String memberID, Calendar datetime) {
        mID = id;
        mMemberID = memberID;
        mDatetime = datetime;
    }

    public String getID() {
        return mID;
    }

    public String getMemberID() {
        return mMemberID;
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

    public void addExercises(List<Exercise> exerciseList) {
        for (Exercise e : exerciseList) {
            mExerciseSetList.add(new ExerciseSet(e, mExerciseSetList.size() + 1));
        }
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

    protected ExercisePlanRecord(Parcel in) {
        mID = in.readString();
        mMemberID = in.readString();
        mMember = in.readParcelable(User.class.getClassLoader());
        mDatetime = (Calendar) in.readSerializable();

        mExerciseSetList = new ArrayList<>();
        in.readTypedList(mExerciseSetList, ExerciseSet.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mID);
        dest.writeString(mMemberID);
        dest.writeParcelable(mMember, flags);
        dest.writeSerializable(mDatetime);
        dest.writeTypedList(mExerciseSetList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExercisePlanRecord> CREATOR = new Creator<ExercisePlanRecord>() {
        @Override
        public ExercisePlanRecord createFromParcel(Parcel in) {
            return new ExercisePlanRecord(in);
        }

        @Override
        public ExercisePlanRecord[] newArray(int size) {
            return new ExercisePlanRecord[size];
        }
    };
}
