package org.hugoandrade.gymapp.model.aidl;

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

/**
 * Parses the objects to Json data.
 */
public class MobileClientDataJsonFormatter {

    /**
     * Parses a username-password combo to a JsonObject
     */
    public JsonObject getAsJsonObject(String username, String password) {

        return JsonObjectBuilder.instance()
                .addProperty(User.Entry.Cols.USERNAME, username)
                .addProperty(User.Entry.Cols.PASSWORD, password)
                .create();
    }

    /**
     * Parses a WaitingUser object to a JsonObject
     */
    public JsonObject getAsJsonObject(WaitingUser waitingUser) {

        return JsonObjectBuilder.instance()
                .addProperty(WaitingUser.Entry.Cols.USERNAME, waitingUser.getUsername())
                .addProperty(WaitingUser.Entry.Cols.CREDENTIAL, waitingUser.getCredential())
                .addProperty(WaitingUser.Entry.Cols.CODE, waitingUser.getCode())
                .create();
    }

    /**
     * Parses an Exercise object to a JsonObject
     */
    public JsonObject getAsJsonObject(Exercise exercise) {

        return JsonObjectBuilder.instance()
                .addProperty(Exercise.Entry.Cols.NAME, exercise.getName())
                .create();
    }

    /**
     * Parses an ExercisePlanRecord object to a JsonObject
     */
    public JsonObject getAsJsonObject(ExercisePlanRecord exercisePlanRecord) {

        return JsonObjectBuilder.instance()
                .addProperty(ExercisePlanRecord.Entry.Cols.MEMBER_ID, exercisePlanRecord.getMemberID())
                .addProperty(ExercisePlanRecord.Entry.Cols.DATETIME, ISO8601Utils.fromCalendar(exercisePlanRecord.getDatetime()))
                .create();
    }

    /**
     * Parses an ExercisePlanRecordSuggested object to a JsonObject
     */
    public JsonObject getAsJsonObject(ExercisePlanRecordSuggested exercisePlanRecordSuggested) {

        return JsonObjectBuilder.instance()
                .addProperty(ExercisePlanRecordSuggested.Entry.Cols.MEMBER_ID, exercisePlanRecordSuggested.getMemberID())
                .addProperty(ExercisePlanRecordSuggested.Entry.Cols.STAFF_ID, exercisePlanRecordSuggested.getStaffID())
                .addProperty(ExercisePlanRecordSuggested.Entry.Cols.DATETIME, ISO8601Utils.fromCalendar(exercisePlanRecordSuggested.getDatetime()))
                .create();
    }

    /**
     * Parses an ExerciseSet object to a JsonObject
     */
    public JsonObject getAsJsonObject(ExerciseSet exerciseSet) {

        return JsonObjectBuilder.instance()
                .addProperty(ExerciseSet.Entry.Cols.EXERCISE_ID, exerciseSet.getExercise().getID())
                .addProperty(ExerciseSet.Entry.Cols.EXERCISE_PLAN_RECORD_ID, exerciseSet.getExercisePlanRecordID())
                .addProperty(ExerciseSet.Entry.Cols.EXERCISE_PLAN_RECORD_ORDER, exerciseSet.getExercisePlanRecordOrder())
                .create();
    }

    /**
     * Parses an ExerciseRecord object to a JsonObject
     */
    public JsonObject getAsJsonObject(ExerciseRecord exerciseRecord) {

        return JsonObjectBuilder.instance()
                .addProperty(ExerciseRecord.Entry.Cols.EXERCISE_SET_ID, exerciseRecord.getExerciseSetID())
                .addProperty(ExerciseRecord.Entry.Cols.EXERCISE_SET_ORDER, exerciseRecord.getExerciseSetOrder())
                .addProperty(ExerciseRecord.Entry.Cols.NUMBER_OF_REPETITIONS, exerciseRecord.getNumberOfRepetitions())
                .create();
    }

    /**
     * Parses an StaffMember object to a JsonObject
     */
    public JsonObject getAsJsonObject(StaffMember staffMember) {
        return JsonObjectBuilder.instance()
                .addProperty(StaffMember.Entry.Cols.STAFF_ID, staffMember.getStaffID())
                .addProperty(StaffMember.Entry.Cols.MEMBER_ID, staffMember.getMemberID())
                .create();
    }

    /**
     * Builder class used to implement the builder pattern throughout this instance
     */
    private static class JsonObjectBuilder {

        private final JsonObject mJsonObject;

        private static JsonObjectBuilder instance() {
            return new JsonObjectBuilder();
        }

        private JsonObjectBuilder() {
            mJsonObject = new JsonObject();
        }

        JsonObjectBuilder addProperty(String property, String value) {
            mJsonObject.addProperty(property, value);
            return this;
        }

        JsonObjectBuilder addProperty(String property, Number value) {
            mJsonObject.addProperty(property, value);
            return this;
        }

        JsonObjectBuilder addProperty(String property, Boolean value) {
            mJsonObject.addProperty(property, value);
            return this;
        }

        JsonObject create() {
            return mJsonObject;
        }
    }
}
