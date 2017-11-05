package org.hugoandrade.gymapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExerciseSet implements Parcelable {

    private String mID;
    private String mExerciseID;
    private String mExercisePlanRecordID;
    private int mExercisePlanRecordOrder;

    private Exercise mExercise;
    private List<ExerciseRecord> mExerciseRecordList;

    public static class Entry {

        public static final String TABLE_NAME = "ExerciseSet";

        public static class Cols {
            public static final String ID = "id";
            public static final String EXERCISE_PLAN_RECORD_ID = "ExercisePlanRecordID";
            public static final String EXERCISE_PLAN_RECORD_ORDER = "ExercisePlanRecordOrder";
            public static final String EXERCISE_ID = "ExerciseID";
        }
    }

    public ExerciseSet(String id, String exerciseID, String exercisePlanRecordID, int exercisePlanRecordOrder) {
        mID = id;
        mExerciseID = exerciseID;
        mExercisePlanRecordID = exercisePlanRecordID;
        mExercisePlanRecordOrder = exercisePlanRecordOrder;
    }

    public ExerciseSet(Exercise exercise, int exercisePlanRecordOrder) {
        mExercise = exercise;
        mExerciseID = exercise.getID();
        mExercisePlanRecordOrder = exercisePlanRecordOrder;
        mExerciseRecordList = new ArrayList<>();
        mExerciseRecordList.add(new ExerciseRecord(10, 1));
    }

    public String getID() {
        return mID;
    }

    public void setExercise(Exercise exercise) {
        mExercise = exercise;
    }

    public Exercise getExercise() {
        return mExercise;
    }

    public String getExerciseID() {
        return mExerciseID;
    }

    public void addExerciseRecord(ExerciseRecord exerciseRecord) {
        if (mExerciseRecordList == null)
            mExerciseRecordList = new ArrayList<>();

        mExerciseRecordList.add(exerciseRecord);
        Collections.sort(mExerciseRecordList, new Comparator<ExerciseRecord>() {
            @Override
            public int compare(ExerciseRecord o1, ExerciseRecord o2) {
                return o1.getExerciseSetOrder() - o2.getExerciseSetOrder();
            }
        });
    }

    public List<ExerciseRecord> getExerciseRecordList() {
        return mExerciseRecordList;
    }

    public void setExercisePlanRecordID(String exercisePlanRecordID) {
        mExercisePlanRecordID = exercisePlanRecordID;
    }

    public void setExercisePlanRecordOrder(int exercisePlanRecordOrder) {
        mExercisePlanRecordOrder = exercisePlanRecordOrder;
    }

    public int getExercisePlanRecordOrder() {
        return mExercisePlanRecordOrder;
    }

    public String getExercisePlanRecordID() {
        return mExercisePlanRecordID;
    }

    protected ExerciseSet(Parcel in) {
        mID = in.readString();
        mExerciseID = in.readString();
        mExercisePlanRecordID = in.readString();
        mExercisePlanRecordOrder = in.readInt();
        mExercise = in.readParcelable(Exercise.class.getClassLoader());

        mExerciseRecordList = new ArrayList<>();
        in.readTypedList(mExerciseRecordList, ExerciseRecord.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mID);
        dest.writeString(mExerciseID);
        dest.writeString(mExercisePlanRecordID);
        dest.writeInt(mExercisePlanRecordOrder);
        dest.writeParcelable(mExercise, flags);

        dest.writeTypedList(mExerciseRecordList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExerciseSet> CREATOR = new Creator<ExerciseSet>() {
        @Override
        public ExerciseSet createFromParcel(Parcel in) {
            return new ExerciseSet(in);
        }

        @Override
        public ExerciseSet[] newArray(int size) {
            return new ExerciseSet[size];
        }
    };
}
