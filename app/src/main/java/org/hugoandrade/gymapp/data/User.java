package org.hugoandrade.gymapp.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import org.hugoandrade.gymapp.provider.StorageProvider;

import java.util.Objects;

public class User implements Parcelable {

    private String mID;
    private String mUsername;
    private String mPassword;
    private String mCredential;

    private String mUserID;
    private String mToken;

    public static class Credential {

        public static final String ADMIN = "Admin";
        public static final String STAFF = "Staff";
        public static final String MEMBER = "Member";

        public static class Cols {
            public static final String USER_ID = "UserID";
        }
    }

    public static class Entry {

        public static final String TABLE_NAME = "User";

        public static class Cols {
            public static final String ID = "id";
            public static final String USERNAME = "Username";
            public static final String PASSWORD = "Password";
            public static final String USER_ID = "UserID";
            public static final String TOKEN = "Token";
        }

        public static final String REQUEST_TYPE = "RequestType";
        public static final String SIGN_UP = "SignUp";


        // SQLite table User
        // PATH & TOKEN for entire table
        public static final String PATH = TABLE_NAME;
        /**
         * The code that is returned when a URI for more than 1 items is
         * matched against the given components.  Must be positive.
         */
        public static final int PATH_TOKEN = 110;

        // PATH & TOKEN for single row of table
        public static final String PATH_FOR_ID = PATH + "/#";

        /**
         * The code that is returned when a URI for exactly 1 item is
         * matched against the given components.  Must be positive.
         */
        public static final int PATH_FOR_ID_TOKEN = 120;

        // CONTENT/MIME TYPE for this content
        private final static String MIME_TYPE_END = PATH;
        public static final String CONTENT_TYPE_DIR = StorageProvider.ORGANIZATIONAL_NAME
                + ".cursor.dir/" + StorageProvider.ORGANIZATIONAL_NAME + "." + MIME_TYPE_END;
        public static final String CONTENT_ITEM_TYPE = StorageProvider.ORGANIZATIONAL_NAME
                + ".cursor.item/" + StorageProvider.ORGANIZATIONAL_NAME + "." + MIME_TYPE_END;

        public static final Uri CONTENT_URI = StorageProvider.BASE_URI.buildUpon()
                .appendPath(PATH).build();
    }

    public User() {
    }

    public User(String id) {
        mID = id;
    }

    public User(String id, String username, String password, String userID, String token) {
        mID = id;
        mUsername = username;
        mPassword = password;
        mUserID = userID;
        mToken = token;
    }

    public User(String username, String password) {
        mUsername = username;
        mPassword = password;
    }

    public String getID() {
        return mID;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setCredential(String credential) {
        mCredential = credential;
    }

    public String getCredential() {
        return mCredential;
    }

    public String getUserID() {
        return mUserID;
    }

    public String getToken() {
        return mToken;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof User) {
            User u = (User) obj;
            return Objects.equals(mID, u.mID) &&
                    Objects.equals(mUserID, u.mUserID) &&
                    Objects.equals(mCredential, u.mCredential) &&
                    Objects.equals(mUsername, u.mUsername) &&
                    Objects.equals(mPassword, u.mPassword) &&
                    Objects.equals(mToken, u.mToken);
        }
        return false;
    }

    protected User(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        mID = in.readString();
        mUsername = in.readString();
        mPassword = in.readString();
        mCredential = in.readString();
        mUserID = in.readString();
        mToken = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mID);
        parcel.writeString(mUsername);
        parcel.writeString(mPassword);
        parcel.writeString(mCredential);
        parcel.writeString(mUserID);
        parcel.writeString(mToken);
    }

    @Override
    public String toString() {
        return "User{" +
                "mID='" + mID + '\'' +
                ", mUsername='" + mUsername + '\'' +
                ", mPassword='" + mPassword + '\'' +
                ", mCredential='" + mCredential + '\'' +
                ", mUserID='" + mUserID + '\'' +
                ", mToken='" + mToken + '\'' +
                '}';
    }
}
