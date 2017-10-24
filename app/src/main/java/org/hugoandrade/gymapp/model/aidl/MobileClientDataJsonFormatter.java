package org.hugoandrade.gymapp.model.aidl;

import com.google.gson.JsonObject;

import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.data.WaitingUser;

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

    public JsonObject getAsJsonObject(WaitingUser waitingUser) {

        return JsonObjectBuilder.instance()
                .addProperty(WaitingUser.Entry.Cols.USERNAME, waitingUser.getUsername())
                .addProperty(WaitingUser.Entry.Cols.CREDENTIAL, waitingUser.getCredential())
                .addProperty(WaitingUser.Entry.Cols.CODE, waitingUser.getCode())
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
