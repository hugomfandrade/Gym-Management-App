package org.hugoandrade.gymapp.model.aidl;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.hugoandrade.gymapp.data.User;

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

    public User parseUser(JsonObject jsonObject) {
        return new User(
                getJsonPrimitive(jsonObject, User.Entry.Cols.ID, null),
                getJsonPrimitive(jsonObject, User.Entry.Cols.USERNAME, null),
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

    public float getJsonPrimitive(JsonObject jsonObject, String jsonMemberName, float defaultValue) {
        try {
            return jsonObject.getAsJsonPrimitive(jsonMemberName).getAsFloat();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public int getJsonPrimitive(JsonObject jsonObject, String jsonMemberName, int defaultValue) {
        try {
            return (int) jsonObject.getAsJsonPrimitive(jsonMemberName).getAsFloat();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public double getJsonPrimitive(JsonObject jsonObject, String jsonMemberName, double defaultValue) {
        try {
            return (double) jsonObject.getAsJsonPrimitive(jsonMemberName).getAsFloat();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public String getJsonPrimitive(JsonObject jsonObject, String jsonMemberName, String defaultValue) {
        try {
            return jsonObject.getAsJsonPrimitive(jsonMemberName).getAsString();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public boolean getJsonPrimitive(JsonObject jsonObject, String jsonMemberName, boolean defaultValue) {
        try {
            return jsonObject.getAsJsonPrimitive(jsonMemberName).getAsBoolean();
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
