package org.hugoandrade.gymapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ExerciseSet implements Parcelable {

    private String mID;
    private String mExercisePlanID;
    private int mExercisePlanOrder;

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

    public ExerciseSet(String id, String exerciseID, String exercisePlanID, int exercisePlanOrder) {
        mID = id;
        mExercise = new Exercise(exerciseID);
        mExercisePlanID = exercisePlanID;
        mExercisePlanOrder = exercisePlanOrder;
    }

    public ExerciseSet(Exercise exercise, int exercisePlanOrder) {
        mExercise = exercise;
        mExercisePlanOrder = exercisePlanOrder;
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
        return mExercise.getID();
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

    public void setExercisePlanID(String exercisePlanID) {
        mExercisePlanID = exercisePlanID;
    }

    public void setExercisePlanOrder(int exercisePlanOrder) {
        mExercisePlanOrder = exercisePlanOrder;
    }

    public int getExercisePlanOrder() {
        return mExercisePlanOrder;
    }

    public String getExercisePlanID() {
        return mExercisePlanID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ExerciseSet) {
            ExerciseSet u = (ExerciseSet) obj;
            return Objects.equals(mID, u.mID) &&
                    Objects.equals(mExercise, u.mExercise) &&
                    Objects.equals(mExercisePlanID, u.mExercisePlanID) &&
                    Objects.equals(mExercisePlanOrder, u.mExercisePlanOrder) ;
        }
        return false;
    }

    protected ExerciseSet(Parcel in) {
        mID = in.readString();
        mExercisePlanID = in.readString();
        mExercisePlanOrder = in.readInt();
        mExercise = in.readParcelable(Exercise.class.getClassLoader());

        mExerciseRecordList = new ArrayList<>();
        in.readTypedList(mExerciseRecordList, ExerciseRecord.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mID);
        dest.writeString(mExercisePlanID);
        dest.writeInt(mExercisePlanOrder);
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
