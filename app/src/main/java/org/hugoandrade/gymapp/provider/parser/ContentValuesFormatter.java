package org.hugoandrade.gymapp.provider.parser;


import android.content.ContentValues;
import org.hugoandrade.gymapp.data.User;

/**
 * Parses the objects to ContentValues data.
 */
public class ContentValuesFormatter {

    public ContentValues getAsContentValues(User user) {
        return ContentValuesBuilder.instance()
                .put(User.Entry.Cols.USERNAME, user.getUsername())
                .put(User.Entry.Cols.PASSWORD, user.getPassword())
                .create();
    }

    private static class ContentValuesBuilder {

        private final ContentValues mContentValues;

        private static ContentValuesBuilder instance() {
            return new ContentValuesBuilder();
        }

        private ContentValuesBuilder() {
            mContentValues = new ContentValues();
        }

        ContentValuesBuilder put(String key, String value) {
            mContentValues.put(key, value);
            return this;
        }

        ContentValuesBuilder put(String key, Integer value) {
            mContentValues.put(key, value);
            return this;
        }

        ContentValuesBuilder put(String key, Boolean value) {
            mContentValues.put(key, value);
            return this;
        }

        ContentValues create() {
            return mContentValues;
        }
    }
}
