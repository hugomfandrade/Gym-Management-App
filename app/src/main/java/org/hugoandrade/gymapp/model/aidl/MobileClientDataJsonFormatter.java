package org.hugoandrade.gymapp.model.aidl;

import com.google.gson.JsonObject;

import org.hugoandrade.gymapp.data.User;

/**
 * Parses the objects to Json data.
 */
public class MobileClientDataJsonFormatter {


    public JsonObject getAsJsonObject(String username, String password) {

        return JsonObjectBuilder.instance()
                .addProperty(User.Entry.Cols.USERNAME, username)
                .addProperty(User.Entry.Cols.PASSWORD, password)
                .create();
    }

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
