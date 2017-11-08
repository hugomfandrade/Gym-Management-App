package org.hugoandrade.gymapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class ExerciseRecord implements Parcelable {

    private String mID;
    private String mExerciseSetID;
    private int mNumberOfRepetitions;
    private int mExerciseSetOrder;

    public static class Entry {

        public static final String TABLE_NAME = "ExerciseRecord";

        public static class Cols {
            public static final String ID = "id";
            public static final String EXERCISE_SET_ID = "ExerciseSetID";
            public static final String EXERCISE_SET_ORDER = "ExerciseSetOrder";
            public static final String NUMBER_OF_REPETITIONS = "NumberOfRepetitions";
        }
    }

    public ExerciseRecord(int numberOfRepetitions, int exerciseSetOrder) {
        mNumberOfRepetitions = numberOfRepetitions;
        mExerciseSetOrder = exerciseSetOrder;
    }

    public ExerciseRecord(String id, String exerciseSetID, int exerciseSetOrder, int numberOfRepetitions) {
        mID = id;
        mExerciseSetID = exerciseSetID;
        mExerciseSetOrder = exerciseSetOrder;
        mNumberOfRepetitions = numberOfRepetitions;
    }

    public String getID() {
        return mID;
    }

    public String getExerciseSetID() {
        return mExerciseSetID;
    }

    public void setNumberOfRepetitions(int numberOfRepetitions) {
        mNumberOfRepetitions = numberOfRepetitions;
    }

    public int getNumberOfRepetitions() {
        return mNumberOfRepetitions;
    }

    public void setExerciseSetID(String exerciseSetID) {
        mExerciseSetID = exerciseSetID;
    }

    public int getExerciseSetOrder() {
        return mExerciseSetOrder;
    }

    public void setExerciseSetOrder(int exerciseSetOrder) {
        mExerciseSetOrder = exerciseSetOrder;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ExerciseRecord) {
            ExerciseRecord u = (ExerciseRecord) obj;
            return Objects.equals(mID, u.mID) &&
                    Objects.equals(mExerciseSetID, u.mExerciseSetID) &&
                    Objects.equals(mNumberOfRepetitions, u.mNumberOfRepetitions) &&
                    Objects.equals(mExerciseSetOrder, u.mExerciseSetOrder) ;
        }
        return false;
    }

    protected ExerciseRecord(Parcel in) {
        mID = in.readString();
        mExerciseSetID = in.readString();
        mNumberOfRepetitions = in.readInt();
        mExerciseSetOrder = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mID);
        dest.writeString(mExerciseSetID);
        dest.writeInt(mNumberOfRepetitions);
        dest.writeInt(mExerciseSetOrder);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExerciseRecord> CREATOR = new Creator<ExerciseRecord>() {
        @Override
        public ExerciseRecord createFromParcel(Parcel in) {
            return new ExerciseRecord(in);
        }

        @Override
        public ExerciseRecord[] newArray(int size) {
            return new ExerciseRecord[size];
        }
    };
}
