package org.hugoandrade.gymapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hugo Andrade on 22/10/2017.
 */

public class User implements Parcelable {

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
    }

    private String mID;
    private String mUsername;
    private String mPassword;
    private String mCredential;

    private String mUserID;
    private String mToken;

    public User(String id, String username, String userID, String token) {
        mID = id;
        mUsername = username;
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

    protected User(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        mID = in.readString();
        mUsername = in.readString();
        mPassword = in.readString();
        mCredential = in.readString();
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
        parcel.writeString(mToken);
    }
}
