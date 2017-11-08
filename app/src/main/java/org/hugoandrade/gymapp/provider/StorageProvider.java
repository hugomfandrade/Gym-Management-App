package org.hugoandrade.gymapp.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.hugoandrade.gymapp.data.User;


public class StorageProvider extends ContentProvider {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = StorageProvider.class.getSimpleName();

    public static final String ORGANIZATIONAL_NAME = "org.hugoandrade";

    private static final String PROJECT_NAME = "gymapp";

    public static final String AUTHORITY = ORGANIZATIONAL_NAME + "." + PROJECT_NAME + ".provider";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    /**
     * The URI Matcher used by this content provider.
     */
    static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * Helper method that matches each URI to the integer
     * constants defined above.
     *
     * @return UriMatcher
     */
    private static UriMatcher buildUriMatcher() {
        // add default 'no match' result to matcher
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        // Entries URIs
        matcher.addURI(AUTHORITY, User.Entry.PATH, User.Entry.PATH_TOKEN);
        matcher.addURI(AUTHORITY, User.Entry.PATH_FOR_ID, User.Entry.PATH_FOR_ID_TOKEN);

        return matcher;
    }

    /**
     * Use StorageDatabaseHelper to manage database creation and version
     * management.
     */
    private StorageDatabaseHelper mOpenHelper;

    /**
     * Context for the Content Provider.
     */
    private Context mContext;

    /**
     * Hook method returns true if successfully started.
     */
    @Override
    public boolean onCreate() {
        mContext = getContext();

        // Select the concrete implementor.
        // Create the StorageDatabaseHelper.
        mOpenHelper =
                new StorageDatabaseHelper(mContext);
        return true;
    }

    /**
     * Method called to handle type requests from client applications.
     * It returns the MIME type of the data associated with each
     * URI.  
     */
    @Override
    public String getType(@NonNull Uri uri) {
        // Match the id returned by UriMatcher to return appropriate
        // MIME_TYPE.
        switch (sUriMatcher.match(uri)) {
            case User.Entry.PATH_TOKEN:
                return User.Entry.CONTENT_TYPE_DIR;
            case User.Entry.PATH_FOR_ID_TOKEN:
                return User.Entry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: "
                        + uri);
        }
    }

    /**
     * Method called to handle insert requests from client apps.
     */
    @Override
    public Uri insert(@NonNull Uri uri,
                      ContentValues cvs) {
        Uri returnUri;

        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If there's a match insert a new
        // row.
        switch (sUriMatcher.match(uri)) {
        case User.Entry.PATH_TOKEN:
            returnUri = insertUser(uri,
                                   cvs);
            break;
        default:
            throw new UnsupportedOperationException("Unknown uri: "
                                                    + uri);
        }

        // Notifies registered observers that a row was inserted.
        mContext.getContentResolver().notifyChange(uri, 
                                                   null);
        return returnUri;
    }

    private Uri insertUser(Uri uri,
                           ContentValues cvs) {
        final SQLiteDatabase db =
            mOpenHelper.getWritableDatabase();

        // delete all before inserting
        db.delete(User.Entry.TABLE_NAME,
                null,
                null);

        long id =
            db.insert(User.Entry.TABLE_NAME,
                      null,
                      cvs);

        // Check if a new row is inserted or not.
        if (id > 0)
            return ContentUris.withAppendedId(User.Entry.CONTENT_URI, id);
        else
            throw new android.database.SQLException
                ("Failed to insert row into " 
                 + uri);
    }

    /**
     * Method called to handle query requests from client
     * applications. Query operations only available for querying
     * all table.
     */
    @Override
    public Cursor query(@NonNull Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor;

        // Match the id returned by UriMatcher to query appropriate
        // rows.
        switch (sUriMatcher.match(uri)) {
        case User.Entry.PATH_TOKEN:
            cursor = queryUsers(uri,
                                projection,
                                selection,
                                selectionArgs,
                                sortOrder);
            break;
        case User.Entry.PATH_FOR_ID_TOKEN:
        default:
            throw new UnsupportedOperationException("Unknown uri: "
                                                    + uri);
        }

        // Register to watch a content URI for changes.
        cursor.setNotificationUri(mContext.getContentResolver(), 
                                  uri);
        return cursor;
    }

    /**
     * Method called to handle query requests from client
     * applications.
     */
    private Cursor queryUsers(Uri uri,
                              String[] projection,
                              String selection,
                              String[] selectionArgs,
                              String sortOrder) {
        // Expand the selection if necessary.
        selection = addSelectionArgs(selection, 
                                     selectionArgs,
                                     "OR");
        return mOpenHelper.getReadableDatabase().query(
                User.Entry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    /**
     * Method called to handle update requests from client
     * applications. Update operations not available.
     */
    @Override
    public int update(@NonNull Uri uri,
                      ContentValues cvs,
                      String selection,
                      String[] selectionArgs) {

        //int returnCount;

        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If there's a match update rows.
        switch (sUriMatcher.match(uri)) {
        case User.Entry.PATH_TOKEN:
        case User.Entry.PATH_FOR_ID_TOKEN:
        default:
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Method called to handle delete requests from client
     * applications. Delete operations only available for querying
     * all table.
     */
    @Override
    public int delete(@NonNull Uri uri,
                      String selection,
                      String[] selectionArgs) {
        int returnCount;

        // Try to match against the path in a url.  It returns the
        // code for the matched node (added using addURI), or -1 if
        // there is no matched node.  If there's a match delete rows.
        switch (sUriMatcher.match(uri)) {
        case User.Entry.PATH_TOKEN:
            returnCount = deleteUsers(uri,
                                      selection,
                                      selectionArgs);
            break;
        case User.Entry.PATH_FOR_ID_TOKEN:
        default:
            throw new UnsupportedOperationException();
        }

        if (selection == null
            || returnCount > 0)
            // Notifies registered observers that row(s) were deleted.
            mContext.getContentResolver().notifyChange(uri, 
                                                       null);

        return returnCount;
    }

    /**
     * Method called to handle delete requests from client
     * applications.  
     */
    private int deleteUsers(Uri uri,
                            String selection,
                            String[] selectionArgs) {
        // Expand the selection if necessary.
        selection = addSelectionArgs(selection, 
                                     selectionArgs,
                                     " OR ");
        return mOpenHelper.getWritableDatabase().delete
            (User.Entry.TABLE_NAME,
             selection,
             selectionArgs);
    }

    /**
     * Return a selection string that concatenates all the
     * @a selectionArgs for a given @a selection using the given @a
     * operation.
     */
    private String addSelectionArgs(String selection,
                                    String[] selectionArgs,
                                    String operation) {
        // Handle the "null" case.
        if (selection == null
            || selectionArgs == null)
            return null;
        else {
            String selectionResult = "";

            // Properly add the selection args to the selectionResult.
            for (int i = 0;
                 i < selectionArgs.length - 1;
                 ++i)
                selectionResult += (selection 
                           + " = ? " 
                           + operation 
                           + " ");
            
            // Handle the final selection case.
            selectionResult += (selection
                       + " = ?");

            return selectionResult;
        }
    }
}
