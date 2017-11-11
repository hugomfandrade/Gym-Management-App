package org.hugoandrade.gymapp.webservice;

// imports

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.data.ExercisePlan;
import org.hugoandrade.gymapp.data.ExercisePlanSuggested;
import org.hugoandrade.gymapp.data.ExerciseRecord;
import org.hugoandrade.gymapp.data.ExerciseSet;
import org.hugoandrade.gymapp.data.StaffMember;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.data.WaitingUser;
import org.hugoandrade.gymapp.model.aidl.MobileClientDataJsonFormatter;
import org.hugoandrade.gymapp.model.aidl.MobileClientDataJsonParser;
import org.hugoandrade.gymapp.shared.ExercisePlanSuggested_Utils;
import org.hugoandrade.gymapp.shared.ExercisePlan_Utils;
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

        ExercisePlan obj = ExercisePlan_Utils.newExercisePlan(null, VAR_A);

        JsonObject jsonObject = formatter.getAsJsonObject(obj);

        assertTrue(obj.equals(parser.parseExercisePlan(jsonObject)));


        ExercisePlan objB = ExercisePlan_Utils.newExercisePlan(null, VAR_B);
        JsonObject jsonObjectB = formatter.getAsJsonObject(objB);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(jsonObjectB);
        jsonArray.add(jsonObject);

        List<ExercisePlan> objList = parser.parseExercisePlans(jsonArray);

        assertThat(objList.size(), is(2));
        assertTrue(objB.equals(objList.get(0)));
        assertTrue(obj.equals(objList.get(1)));
    }

    /**
     * Test parsing and formatting of JsonObject for ExercisePlanRecordSuggested
     */
    @Test
    public void testJsonParsingOfExercisePlanRecordSuggested() {

        ExercisePlanSuggested obj = ExercisePlanSuggested_Utils.newExercisePlanSuggested(null, VAR_A);

        JsonObject jsonObject = formatter.getAsJsonObject(obj);

        assertTrue(obj.equals(parser.parseExercisePlanSuggested(jsonObject)));


        ExercisePlanSuggested objB = ExercisePlanSuggested_Utils.newExercisePlanSuggested(null, VAR_B);
        JsonObject jsonObjectB = formatter.getAsJsonObject(objB);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(jsonObjectB);
        jsonArray.add(jsonObject);

        List<ExercisePlanSuggested> objList = parser.parseExercisePlanSuggesteds(jsonArray);

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



















