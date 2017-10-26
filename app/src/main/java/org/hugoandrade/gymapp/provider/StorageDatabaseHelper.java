package org.hugoandrade.gymapp.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.hugoandrade.gymapp.data.User;

import java.io.File;

/**
 * The database helper used by the StorageProvider to create
 * and manage its underlying SQLite database.
 */
public class StorageDatabaseHelper
       extends SQLiteOpenHelper {
    /**
     * Database name.
     */
    private static final String DATABASE_NAME =
        "GymApp";

    /**
     * Database version number, which is updated with each schema
     * change.
     */
    private static int DATABASE_VERSION = 1;

    /*
     * SQL create table statements.
     */

    /**
     * SQL statement used to create the User table.
     */
    final String SQL_CREATE_USER_TABLE =
        "CREATE TABLE "
        + User.Entry.TABLE_NAME + " ("
        + "_" + User.Entry.Cols.ID + " INTEGER PRIMARY KEY, "
        + User.Entry.Cols.USERNAME + " TEXT NOT NULL, "
        + User.Entry.Cols.PASSWORD + " TEXT NOT NULL "
        + " );";

     /**
     * Constructor - initialize database name and version, but don't
     * actually construct the database (which is done in the
     * onCreate() hook method). It places the database in the
     * application's cache directory, which will be automatically
     * cleaned up by Android if the device runs low on storage space.
     *
     * @param context Any context
     */
    public StorageDatabaseHelper(Context context) {
        super(context, 
              context.getCacheDir()
              + File.separator
              + DATABASE_NAME, 
              null,
              DATABASE_VERSION);
    }

    /**
     * Hook method called when the database is created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table.
        db.execSQL(SQL_CREATE_USER_TABLE);
    }

    /**
     * Hook method called when the database is upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {
        // Delete the existing tables.
        db.execSQL("DROP TABLE IF EXISTS "
                   + User.Entry.TABLE_NAME);
        // Create the new tables.
        onCreate(db);
    }
}
