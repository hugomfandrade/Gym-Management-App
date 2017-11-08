package org.hugoandrade.gymapp.model.aidl;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.data.ExercisePlanRecord;
import org.hugoandrade.gymapp.data.ExercisePlanRecordSuggested;
import org.hugoandrade.gymapp.data.ExerciseRecord;
import org.hugoandrade.gymapp.data.ExerciseSet;
import org.hugoandrade.gymapp.data.StaffMember;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.data.WaitingUser;
import org.hugoandrade.gymapp.utils.ISO8601Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the Json data returned from the Mobile Service Client API
 * and returns the objects that contain this data.
 */
public class MobileClientDataJsonParser {

    /**
     * Used for logging purposes.
     */
    private final String TAG =
            getClass().getSimpleName();


    public List<WaitingUser> parseWaitingUsers(JsonElement jsonElement) {
        List<WaitingUser> waitingUserList = new ArrayList<>();

        for (JsonElement item : jsonElement.getAsJsonArray()) {
            try {
                waitingUserList.add(parseWaitingUser(item.getAsJsonObject()));
            } catch (ClassCastException e) {
                Log.e(TAG, "Exception caught when parsing WaitingUser" +
                        " data from azure table: " + e.getMessage());
            }
        }
        return waitingUserList;
    }

    public WaitingUser parseWaitingUser(JsonObject jsonObject) {
        return new WaitingUser(
                getJsonPrimitive(jsonObject, WaitingUser.Entry.Cols.ID, null),
                getJsonPrimitive(jsonObject, WaitingUser.Entry.Cols.USERNAME, null),
                getJsonPrimitive(jsonObject, WaitingUser.Entry.Cols.CREDENTIAL, null),
                getJsonPrimitive(jsonObject, WaitingUser.Entry.Cols.CODE, null));
    }

    public List<StaffMember> parseStaffMembers(JsonElement jsonElement) {
        List<StaffMember> staffMemberList = new ArrayList<>();

        for (JsonElement item : jsonElement.getAsJsonArray()) {
            try {
                staffMemberList.add(parseStaffMember(item.getAsJsonObject()));
            } catch (ClassCastException e) {
                Log.e(TAG, "Exception caught when parsing WaitingUser" +
                        " data from azure table: " + e.getMessage());
            }
        }
        return staffMemberList;
    }

    public StaffMember parseStaffMember(JsonObject jsonObject) {
        return new StaffMember(
                getJsonPrimitive(jsonObject, StaffMember.Entry.Cols.STAFF_ID, null),
                getJsonPrimitive(jsonObject, StaffMember.Entry.Cols.MEMBER_ID, null));
    }

    public List<User> parseUsers(JsonElement jsonElement) {
        List<User> userList = new ArrayList<>();

        for (JsonElement item : jsonElement.getAsJsonArray()) {
            try {
                userList.add(parseUser(item.getAsJsonObject()));
            } catch (ClassCastException e) {
                Log.e(TAG, "Exception caught when parsing User" +
                        " data from azure table: " + e.getMessage());
            }
        }
        return userList;
    }

    public User parseUser(JsonObject jsonObject) {
        return new User(
                getJsonPrimitive(jsonObject, User.Entry.Cols.ID, null),
                getJsonPrimitive(jsonObject, User.Entry.Cols.USERNAME, null),
                getJsonPrimitive(jsonObject, User.Entry.Cols.PASSWORD, null),
                getJsonPrimitive(jsonObject, User.Entry.Cols.USER_ID, null),
                getJsonPrimitive(jsonObject, User.Entry.Cols.TOKEN, null));
    }

    public List<String> parseStrings(JsonElement jsonElement, String columnName) {
        List<String> stringList = new ArrayList<>();

        for (JsonElement item : jsonElement.getAsJsonArray()) {
            try {
                stringList.add(parseString(item.getAsJsonObject(), columnName));
            } catch (ClassCastException e) {
                Log.e(TAG, "Exception caught when parsing String" +
                        " data from azure table: " + e.getMessage());
            }
        }
        return stringList;
    }

    public String parseString(JsonObject result, String columnName) {
        return getJsonPrimitive(result, columnName, null);
    }

    public List<Exercise> parseExercises(JsonElement jsonElement) {
        List<Exercise> exerciseList = new ArrayList<>();

        for (JsonElement item : jsonElement.getAsJsonArray()) {
            try {
                exerciseList.add(parseExercise(item.getAsJsonObject()));
            } catch (ClassCastException e) {
                Log.e(TAG, "Exception caught when parsing Exercise" +
                        " data from azure table: " + e.getMessage());
            }
        }
        return exerciseList;
    }

    public Exercise parseExercise(JsonObject jsonObject) {
        return new Exercise(
                getJsonPrimitive(jsonObject, Exercise.Entry.Cols.ID, null),
                getJsonPrimitive(jsonObject, Exercise.Entry.Cols.NAME, null));
    }

    public List<ExerciseRecord> parseExerciseRecords(JsonElement jsonElement) {
        List<ExerciseRecord> exerciseRecordList = new ArrayList<>();

        for (JsonElement item : jsonElement.getAsJsonArray()) {
            try {
                exerciseRecordList.add(parseExerciseRecord(item.getAsJsonObject()));
            } catch (ClassCastException e) {
                Log.e(TAG, "Exception caught when parsing ExerciseRecord" +
                        " data from azure table: " + e.getMessage());
            }
        }
        return exerciseRecordList;
    }

    public ExerciseRecord parseExerciseRecord(JsonObject jsonObject) {
        return new ExerciseRecord(
                getJsonPrimitive(jsonObject, ExerciseRecord.Entry.Cols.ID, null),
                getJsonPrimitive(jsonObject, ExerciseRecord.Entry.Cols.EXERCISE_SET_ID, null),
                getJsonPrimitive(jsonObject, ExerciseRecord.Entry.Cols.EXERCISE_SET_ORDER, -1),
                getJsonPrimitive(jsonObject, ExerciseRecord.Entry.Cols.NUMBER_OF_REPETITIONS, -1));
    }

    public List<ExerciseSet> parseExerciseSets(JsonElement jsonElement) {
        List<ExerciseSet> exerciseSetList = new ArrayList<>();

        for (JsonElement item : jsonElement.getAsJsonArray()) {
            try {
                exerciseSetList.add(parseExerciseSet(item.getAsJsonObject()));
            } catch (ClassCastException e) {
                Log.e(TAG, "Exception caught when parsing ExerciseSet" +
                        " data from azure table: " + e.getMessage());
            }
        }
        return exerciseSetList;
    }

    public ExerciseSet parseExerciseSet(JsonObject jsonObject) {
        return new ExerciseSet(
                getJsonPrimitive(jsonObject, ExerciseSet.Entry.Cols.ID, null),
                getJsonPrimitive(jsonObject, ExerciseSet.Entry.Cols.EXERCISE_ID, null),
                getJsonPrimitive(jsonObject, ExerciseSet.Entry.Cols.EXERCISE_PLAN_RECORD_ID, null),
                getJsonPrimitive(jsonObject, ExerciseSet.Entry.Cols.EXERCISE_PLAN_RECORD_ORDER, -1));
    }

    public List<ExercisePlanRecord> parseExercisePlanRecords(JsonElement jsonElement) {
        List<ExercisePlanRecord> exercisePlanRecordList = new ArrayList<>();

        for (JsonElement item : jsonElement.getAsJsonArray()) {
            try {
                exercisePlanRecordList.add(parseExercisePlanRecord(item.getAsJsonObject()));
            } catch (ClassCastException e) {
                Log.e(TAG, "Exception caught when parsing ExercisePlanRecord" +
                        " data from azure table: " + e.getMessage());
            }
        }
        return exercisePlanRecordList;
    }

    public ExercisePlanRecord parseExercisePlanRecord(JsonObject jsonObject) {
        return new ExercisePlanRecord(
                getJsonPrimitive(jsonObject, ExercisePlanRecord.Entry.Cols.ID, null),
                getJsonPrimitive(jsonObject, ExercisePlanRecord.Entry.Cols.MEMBER_ID, null),
                ISO8601Utils.toCalendar(getJsonPrimitive(jsonObject, ExercisePlanRecord.Entry.Cols.DATETIME, null)));
    }

    public List<ExercisePlanRecordSuggested> parseExercisePlanRecordSuggesteds(JsonElement jsonElement) {
        List<ExercisePlanRecordSuggested> exercisePlanRecordSuggestedList = new ArrayList<>();

        for (JsonElement item : jsonElement.getAsJsonArray()) {
            try {
                exercisePlanRecordSuggestedList.add(parseExercisePlanRecordSuggested(item.getAsJsonObject()));
            } catch (ClassCastException e) {
                Log.e(TAG, "Exception caught when parsing ExercisePlanRecordSuggested" +
                        " data from azure table: " + e.getMessage());
            }
        }
        return exercisePlanRecordSuggestedList;
    }

    public ExercisePlanRecordSuggested parseExercisePlanRecordSuggested(JsonObject jsonObject) {
        return new ExercisePlanRecordSuggested(
                getJsonPrimitive(jsonObject, ExercisePlanRecordSuggested.Entry.Cols.ID, null),
                getJsonPrimitive(jsonObject, ExercisePlanRecordSuggested.Entry.Cols.MEMBER_ID, null),
                getJsonPrimitive(jsonObject, ExercisePlanRecordSuggested.Entry.Cols.STAFF_ID, null),
                ISO8601Utils.toCalendar(getJsonPrimitive(jsonObject, ExercisePlanRecordSuggested.Entry.Cols.DATETIME, null)));
    }

    private int getJsonPrimitive(JsonObject jsonObject, String jsonMemberName, int defaultValue) {
        try {
            return (int) jsonObject.getAsJsonPrimitive(jsonMemberName).getAsFloat();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String getJsonPrimitive(JsonObject jsonObject, String jsonMemberName, @SuppressWarnings("SameParameterValue") String defaultValue) {
        try {
            return jsonObject.getAsJsonPrimitive(jsonMemberName).getAsString();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @SuppressWarnings("unused")
    private boolean getJsonPrimitive(JsonObject jsonObject, String jsonMemberName, boolean defaultValue) {
        try {
            return jsonObject.getAsJsonPrimitive(jsonMemberName).getAsBoolean();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @SuppressWarnings("unused")
    private double getJsonPrimitive(JsonObject jsonObject, String jsonMemberName, double defaultValue) {
        try {
            return (double) jsonObject.getAsJsonPrimitive(jsonMemberName).getAsFloat();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @SuppressWarnings("unused")
    private float getJsonPrimitive(JsonObject jsonObject, String jsonMemberName, float defaultValue) {
        try {
            return jsonObject.getAsJsonPrimitive(jsonMemberName).getAsFloat();
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
