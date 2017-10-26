package org.hugoandrade.gymapp.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.LoginModel;
import org.hugoandrade.gymapp.provider.parser.ContentValuesFormatter;
import org.hugoandrade.gymapp.provider.parser.CursorParser;

/**
 * Helper class that consolidates and simplifies operations on the
 * StorageProvider.
 */
public class StorageOps {
    /**
     * Reference back to the LoginModel.
     */
    private final LoginModel mLoginModel;

    /**
     * Define the Proxy for accessing the StorageProvider.
     */
    private ContentResolver mContentResolver;


    private CursorParser mParser = new CursorParser();
    private ContentValuesFormatter cvFormatter = new ContentValuesFormatter();

    /**
     * Constructor initializes the fields.
     */
    public StorageOps(Context context, LoginModel loginModel) {
        mLoginModel = loginModel;
        mContentResolver = context.getContentResolver();
    }

    public Uri insertLastLogin(User user) throws RemoteException{

        // Insert the content at the designated URI.
        return insert(User.Entry.CONTENT_URI,
                      cvFormatter.getAsContentValues(user));
    }

    /**
     * Insert @a ContentValues into the StorageProvider at
     * the @a uri.
     */
    protected Uri insert(Uri uri, ContentValues cvs) {
        return mContentResolver.insert(uri, cvs);
    }

    public void getLastLoginUser() throws RemoteException {

        // Query for all users in the StorageProvider by their race.
        Cursor c = query(User.Entry.CONTENT_URI,
                new String[] {
                        User.Entry.Cols.USERNAME,
                        User.Entry.Cols.PASSWORD
                }, null, null, null);

        if (c.moveToFirst()) {
            mLoginModel.displayLastLogin(mParser.parseUser(c));
        }
        else
            mLoginModel.displayLastLogin(new User());
    }

    /**
     * Return a Cursor from a query on the StorageProvider at
     * the @a uri.
     */
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        return mContentResolver.query(uri,
                         projection,
                         selection,
                         selectionArgs,
                         sortOrder);

    }

    /**
     * Delete all users from the StorageProvider.
     */
    public int deleteAll()
            throws RemoteException {

        return delete(User.Entry.CONTENT_URI,
                null,
                null);
    }

    /**
     * Delete the @a selection and @a selectionArgs from the
     * StorageProvider at the @a uri.
     */
    protected int delete(Uri uri,
                         String selection,
                         String[] selectionArgs) {
        return mContentResolver.delete
            (uri,
             selection,
             selectionArgs);
    }
}
