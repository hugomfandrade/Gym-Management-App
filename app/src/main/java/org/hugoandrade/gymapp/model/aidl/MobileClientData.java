package org.hugoandrade.gymapp.model.aidl;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.data.ExercisePlan;
import org.hugoandrade.gymapp.data.ExercisePlanSuggested;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.data.WaitingUser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class MobileClientData implements Parcelable {

    /*
     * These fields store the MobileClientData's state.
     */
    private final int mOperationType;
    private final int mOperationResult;
    private User mUser;
    private List<User> mUserList;
    private Exercise mExercise;
    private List<Exercise> mExerciseList;
    private WaitingUser mWaitingUser;
    private ExercisePlanSuggested mExercisePlanSuggested;
    private ExercisePlan mExercisePlan;
    private List<ExercisePlan> mExercisePlanList;
    private List<ExercisePlanSuggested> mExercisePlanSuggestedList;
    private String mErrorMessage;

    public static final int OPERATION_LOGIN = 3;
    public static final int OPERATION_GET_ALL_USER = 4;
    public static final int OPERATION_CREATE_USER = 5;
    public static final int OPERATION_VALIDATE = 6;
    public static final int OPERATION_SIGN_UP = 7;
    public static final int OPERATION_GET_MY_MEMBERS = 8;
    public static final int OPERATION_GET_MEMBERS_EXCEPT_MINE = 9;
    public static final int OPERATION_ADD_MEMBER_TO_MY_MEMBERS = 10;
    public static final int OPERATION_GET_MY_STAFF = 11;
    public static final int OPERATION_GET_ALL_EXERCISES = 12;
    public static final int OPERATION_CREATE_EXERCISE = 13;
    public static final int OPERATION_CREATE_EXERCISE_PLAN = 14;
    public static final int OPERATION_GET_HISTORY = 15;
    public static final int OPERATION_CREATE_SUGGESTED_EXERCISE_PLAN = 16;
    public static final int OPERATION_GET_HISTORY_SUGGESTED = 17;
    public static final int OPERATION_DISMISS_SUGGESTED_PLAN = 18;

    public static final int OPERATION_SUCCESS = 1;
    public static final int OPERATION_FAILURE = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({OPERATION_LOGIN, OPERATION_GET_ALL_USER, OPERATION_CREATE_USER,
            OPERATION_VALIDATE, OPERATION_SIGN_UP, OPERATION_GET_MY_MEMBERS,
            OPERATION_GET_MEMBERS_EXCEPT_MINE, OPERATION_ADD_MEMBER_TO_MY_MEMBERS,
            OPERATION_GET_MY_STAFF, OPERATION_GET_ALL_EXERCISES, OPERATION_CREATE_EXERCISE,
            OPERATION_CREATE_EXERCISE_PLAN, OPERATION_GET_HISTORY,
            OPERATION_CREATE_SUGGESTED_EXERCISE_PLAN, OPERATION_GET_HISTORY_SUGGESTED,
            OPERATION_DISMISS_SUGGESTED_PLAN
    })
    @interface OpType {}

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({OPERATION_SUCCESS, OPERATION_FAILURE})
    @interface OpResult {}

    /**
     * Default constructor that initializes the POJO.
     * @param operationType operation type
     * @param operationResult operation result
     */
    public MobileClientData(@OpType int operationType, @OpResult int operationResult) {
        mOperationType = operationType;
        mOperationResult = operationResult;
    }

    public int getOperationType() {
        return mOperationType;
    }

    public int getOperationResult() {
        return mOperationResult;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public List<User> getUserList() {
        return mUserList;
    }

    public void setUserList(List<User> userList) {
        mUserList = userList;
    }

    public WaitingUser getWaitingUser() {
        return mWaitingUser;
    }

    public void setWaitingUser(WaitingUser waitingUser) {
        mWaitingUser = waitingUser;
    }

    public Exercise getExercise() {
        return mExercise;
    }

    public void setExercise(Exercise exercise) {
        mExercise = exercise;
    }

    public List<Exercise> getExerciseList() {
        return mExerciseList;
    }

    public void setExerciseList(List<Exercise> exerciseList) {
        mExerciseList = exerciseList;
    }

    public ExercisePlan getExercisePlan() {
        return mExercisePlan;
    }

    public void setExercisePlan(ExercisePlan exercisePlan) {
        mExercisePlan = exercisePlan;
    }

    public ExercisePlanSuggested getExercisePlanSuggested() {
        return mExercisePlanSuggested;
    }

    public void setExercisePlanSuggested(ExercisePlanSuggested exercisePlanSuggested) {
        mExercisePlanSuggested = exercisePlanSuggested;
    }

    public List<ExercisePlan> getExercisePlanList() {
        return mExercisePlanList;
    }

    public void setExercisePlanList(List<ExercisePlan> exercisePlanList) {
        mExercisePlanList = exercisePlanList;
    }

    public List<ExercisePlanSuggested> getExercisePlanSuggestedList() {
        return mExercisePlanSuggestedList;
    }

    public void setExercisePlanSuggestedList(List<ExercisePlanSuggested> exercisePlanSuggestedList) {
        mExercisePlanSuggestedList = exercisePlanSuggestedList;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        mErrorMessage = errorMessage;
    }

    /**
     * A bitmask indicating the set of special object types marshaled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write this instance out to byte contiguous memory.
     */
    @Override
    public void writeToParcel(Parcel dest,
                              int flags) {
        dest.writeInt(mOperationType);
        dest.writeInt(mOperationResult);
        dest.writeParcelable(mUser, flags);
        dest.writeTypedList(mUserList);
        dest.writeParcelable(mExercise, flags);
        dest.writeTypedList(mExerciseList);
        dest.writeParcelable(mWaitingUser, flags);
        dest.writeParcelable(mExercisePlanSuggested, flags);
        dest.writeParcelable(mExercisePlan, flags);
        dest.writeTypedList(mExercisePlanList);
        dest.writeTypedList(mExercisePlanSuggestedList);
        dest.writeString(mErrorMessage);
    }

    /**
     * Private constructor provided for the CREATOR interface, which
     * is used to de-marshal a MobileClientData from the Parcel of data.
     * <p>
     * The order of reading in variables HAS TO MATCH the order in
     * writeToParcel(Parcel, int)
     *
     * @param in parcel
     */
    private MobileClientData(Parcel in) {

        mOperationType = in.readInt();
        mOperationResult = in.readInt();
        mUser = in.readParcelable(User.class.getClassLoader());
        mUserList = new ArrayList<>();
        in.readTypedList(mUserList, User.CREATOR);
        mExercise = in.readParcelable(Exercise.class.getClassLoader());
        mExerciseList = new ArrayList<>();
        in.readTypedList(mExerciseList, Exercise.CREATOR);
        mWaitingUser = in.readParcelable(WaitingUser.class.getClassLoader());
        mExercisePlanSuggested = in.readParcelable(ExercisePlanSuggested.class.getClassLoader());
        mExercisePlan = in.readParcelable(ExercisePlan.class.getClassLoader());
        mExercisePlanList = new ArrayList<>();
        in.readTypedList(mExercisePlanList, ExercisePlan.CREATOR);
        mExercisePlanSuggestedList = new ArrayList<>();
        in.readTypedList(mExercisePlanSuggestedList, ExercisePlanSuggested.CREATOR);
        mErrorMessage = in.readString();
    }

    /**
     * public Parcelable.Creator for WeatherData, which is an
     * interface that must be implemented and provided as a public
     * CREATOR field that generates instances of your Parcelable class
     * from a Parcel.
     */
    public static final Creator<MobileClientData> CREATOR =
            new Creator<MobileClientData>() {
                public MobileClientData createFromParcel(Parcel in) {
                    return new MobileClientData(in);
                }

                public MobileClientData[] newArray(int size) {
                    return new MobileClientData[size];
                }
            };
}
