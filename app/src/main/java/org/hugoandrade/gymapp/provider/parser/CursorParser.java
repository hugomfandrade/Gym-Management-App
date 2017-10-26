package org.hugoandrade.gymapp.provider.parser;

import android.content.ContentValues;
import android.database.Cursor;

import org.hugoandrade.gymapp.data.User;

public class CursorParser {

    public User parseUser(Cursor c) {
        return new User(
                getColumnValue(c, User.Entry.Cols.USERNAME, null),
                getColumnValue(c, User.Entry.Cols.PASSWORD, null)
        );
    }

    private int getColumnValue(Cursor cursor, String columnName, int defaultValue) {
        try {
            return cursor.getInt(cursor.getColumnIndex(columnName));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String getColumnValue(Cursor cursor, String columnName, String defaultValue) {
        try {
            return cursor.getString(cursor.getColumnIndex(columnName));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private int getColumnValue(ContentValues values, String columnName, int defaultValue) {
        Integer i = values.getAsInteger(columnName);

        return i == null? defaultValue : i;
    }

    private String getColumnValue(ContentValues values, String columnName, String defaultValue) {
        String s = values.getAsString(columnName);

        return s == null? defaultValue : s;
    }
}
