package org.hugoandrade.gymapp.webservice;

// imports

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.data.ExercisePlanRecord;
import org.hugoandrade.gymapp.data.ExercisePlanRecordSuggested;
import org.hugoandrade.gymapp.data.ExerciseRecord;
import org.hugoandrade.gymapp.data.ExerciseSet;
import org.hugoandrade.gymapp.data.StaffMember;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.data.WaitingUser;
import org.hugoandrade.gymapp.model.aidl.MobileClientDataJsonFormatter;
import org.hugoandrade.gymapp.model.aidl.MobileClientDataJsonParser;
import org.hugoandrade.gymapp.shared.ExercisePlanRecordSuggested_Utils;
import org.hugoandrade.gymapp.shared.ExercisePlanRecord_Utils;
import org.hugoandrade.gymapp.shared.ExerciseRecord_Utils;
import org.hugoandrade.gymapp.shared.ExerciseSet_Utils;
import org.hugoandrade.gymapp.shared.Exercise_Utils;
import org.hugoandrade.gymapp.shared.StaffMember_Utils;
import org.hugoandrade.gymapp.shared.WaitingUser_Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class JsonParserFormatter_AndroidTest {

    private MobileClientDataJsonParser parser = new MobileClientDataJsonParser();
    private MobileClientDataJsonFormatter formatter = new MobileClientDataJsonFormatter();

    private static final String VAR_A = "varA";
    private static final String VAR_B = "varA";

    /**
     * Test parsing and formatting of JsonObject for WaitingUser
     */
    @Test
    public void testJsonParsingOfWaitingUser() {

        WaitingUser obj = WaitingUser_Utils.newWaitingUser(null, VAR_A);

        JsonObject jsonObject = formatter.getAsJsonObject(obj);

        assertTrue(obj.equals(parser.parseWaitingUser(jsonObject)));


        WaitingUser objB = WaitingUser_Utils.newWaitingUser(null, VAR_B);
        JsonObject jsonObjectB = formatter.getAsJsonObject(objB);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(jsonObjectB);
        jsonArray.add(jsonObject);

        List<WaitingUser> objList = parser.parseWaitingUsers(jsonArray);

        assertThat(objList.size(), is(2));
        assertTrue(objB.equals(objList.get(0)));
        assertTrue(obj.equals(objList.get(1)));
    }

    /**
     * Test parsing and formatting of JsonObject for User
     */
    @Test
    public void testJsonParsingOfUser() {

        User obj = new User(VAR_A, "password");

        JsonObject jsonObject = formatter.getAsJsonObject(obj.getUsername(), obj.getPassword());

        assertTrue(obj.equals(parser.parseUser(jsonObject)));


        User objB = new User(VAR_B, "password");
        JsonObject jsonObjectB = formatter.getAsJsonObject(obj.getUsername(), obj.getPassword());

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(jsonObjectB);
        jsonArray.add(jsonObject);

        List<User> objList = parser.parseUsers(jsonArray);

        assertThat(objList.size(), is(2));
        assertTrue(objB.equals(objList.get(0)));
        assertTrue(obj.equals(objList.get(1)));
    }

    /**
     * Test parsing and formatting of JsonObject for Exercise
     */
    @Test
    public void testJsonParsingOfExercise() {

        Exercise obj = Exercise_Utils.newExercise(null, VAR_A);

        JsonObject jsonObject = formatter.getAsJsonObject(obj);

        assertTrue(obj.equals(parser.parseExercise(jsonObject)));


        Exercise objB = Exercise_Utils.newExercise(null, VAR_B);
        JsonObject jsonObjectB = formatter.getAsJsonObject(objB);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(jsonObjectB);
        jsonArray.add(jsonObject);

        List<Exercise> objList = parser.parseExercises(jsonArray);

        assertThat(objList.size(), is(2));
        assertTrue(objB.equals(objList.get(0)));
        assertTrue(obj.equals(objList.get(1)));
    }

    /**
     * Test parsing and formatting of JsonObject for ExercisePlanRecord
     */
    @Test
    public void testJsonParsingOfExercisePlanRecord() {

        ExercisePlanRecord obj = ExercisePlanRecord_Utils.newExercisePlanRecord(null, VAR_A);

        JsonObject jsonObject = formatter.getAsJsonObject(obj);

        assertTrue(obj.equals(parser.parseExercisePlanRecord(jsonObject)));


        ExercisePlanRecord objB = ExercisePlanRecord_Utils.newExercisePlanRecord(null, VAR_B);
        JsonObject jsonObjectB = formatter.getAsJsonObject(objB);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(jsonObjectB);
        jsonArray.add(jsonObject);

        List<ExercisePlanRecord> objList = parser.parseExercisePlanRecords(jsonArray);

        assertThat(objList.size(), is(2));
        assertTrue(objB.equals(objList.get(0)));
        assertTrue(obj.equals(objList.get(1)));
    }

    /**
     * Test parsing and formatting of JsonObject for ExercisePlanRecordSuggested
     */
    @Test
    public void testJsonParsingOfExercisePlanRecordSuggested() {

        ExercisePlanRecordSuggested obj = ExercisePlanRecordSuggested_Utils.newExercisePlanRecordSuggested(null, VAR_A);

        JsonObject jsonObject = formatter.getAsJsonObject(obj);

        assertTrue(obj.equals(parser.parseExercisePlanRecordSuggested(jsonObject)));


        ExercisePlanRecordSuggested objB = ExercisePlanRecordSuggested_Utils.newExercisePlanRecordSuggested(null, VAR_B);
        JsonObject jsonObjectB = formatter.getAsJsonObject(objB);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(jsonObjectB);
        jsonArray.add(jsonObject);

        List<ExercisePlanRecordSuggested> objList = parser.parseExercisePlanRecordSuggesteds(jsonArray);

        assertThat(objList.size(), is(2));
        assertTrue(objB.equals(objList.get(0)));
        assertTrue(obj.equals(objList.get(1)));
    }

    /**
     * Test parsing and formatting of JsonObject for ExerciseSet
     */
    @Test
    public void testJsonParsingOfExerciseSet() {

        ExerciseSet obj = ExerciseSet_Utils.newExerciseSet(null, VAR_A);

        JsonObject jsonObject = formatter.getAsJsonObject(obj);

        assertTrue(obj.equals(parser.parseExerciseSet(jsonObject)));


        ExerciseSet objB = ExerciseSet_Utils.newExerciseSet(null, VAR_B);
        JsonObject jsonObjectB = formatter.getAsJsonObject(objB);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(jsonObjectB);
        jsonArray.add(jsonObject);

        List<ExerciseSet> objList = parser.parseExerciseSets(jsonArray);

        assertThat(objList.size(), is(2));
        assertTrue(objB.equals(objList.get(0)));
        assertTrue(obj.equals(objList.get(1)));
    }

    /**
     * Test parsing and formatting of JsonObject for ExerciseRecord
     */
    @Test
    public void testJsonParsingOfExerciseRecord() {

        ExerciseRecord obj = ExerciseRecord_Utils.newExerciseRecord(null, VAR_A);
        JsonObject jsonObject = formatter.getAsJsonObject(obj);

        assertTrue(obj.equals(parser.parseExerciseRecord(jsonObject)));


        ExerciseRecord objB = ExerciseRecord_Utils.newExerciseRecord(null, VAR_B);
        JsonObject jsonObjectB = formatter.getAsJsonObject(objB);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(jsonObjectB);
        jsonArray.add(jsonObject);

        List<ExerciseRecord> objList = parser.parseExerciseRecords(jsonArray);

        assertThat(objList.size(), is(2));
        assertTrue(objB.equals(objList.get(0)));
        assertTrue(obj.equals(objList.get(1)));
    }

    /**
     * Test parsing and formatting of JsonObject for StaffMember
     */
    @Test
    public void testJsonParsingOfStaffMember() {

        StaffMember obj = StaffMember_Utils.newStaffMember(VAR_A, VAR_A);
        JsonObject jsonObject = formatter.getAsJsonObject(obj);

        assertTrue(obj.equals(parser.parseStaffMember(jsonObject)));


        StaffMember objB = StaffMember_Utils.newStaffMember(VAR_A, VAR_B);
        JsonObject jsonObjectB = formatter.getAsJsonObject(objB);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(jsonObjectB);
        jsonArray.add(jsonObject);

        List<StaffMember> objList = parser.parseStaffMembers(jsonArray);

        assertThat(objList.size(), is(2));
        assertTrue(objB.equals(objList.get(0)));
        assertTrue(obj.equals(objList.get(1)));
    }
}



















