package org.hugoandrade.gymapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class WaitingUser implements Parcelable {

    private String mID;
    private String mUsername;
    private String mCredential;
    private String mCode;

    public static class Entry {

        public static final String TABLE_NAME = "WaitingUser";

        public static class Cols {
            public static final String ID = "id";
            public static final String USERNAME   = "Username";
            public static final String CREDENTIAL = "Credential";
            public static final String CODE       = "Code";
        }
    }

    public WaitingUser(String id, String username, String credential, String code) {
        mID = id;
        mUsername = username;
        mCredential = credential;
        mCode = code;
    }

    public WaitingUser(String username, String credential) {
        mUsername = username;
        mCredential = credential;
    }

    public String getID() {
        return mID;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getCredential() {
        return mCredential;
    }

    public String getCode() {
        return mCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof WaitingUser) {
            WaitingUser u = (WaitingUser) obj;
            return Objects.equals(mID, u.mID) &&
                    Objects.equals(mCode, u.mCode) &&
                    Objects.equals(mCredential, u.mCredential) &&
                    Objects.equals(mUsername, u.mUsername);
        }
        return false;
    }

    protected WaitingUser(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        mID = in.readString();
        mUsername = in.readString();
        mCredential = in.readString();
        mCode = in.readString();
    }

    public static final Creator<WaitingUser> CREATOR = new Creator<WaitingUser>() {
        @Override
        public WaitingUser createFromParcel(Parcel in) {
            return new WaitingUser(in);
        }

        @Override
        public WaitingUser[] newArray(int size) {
            return new WaitingUser[size];
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
        parcel.writeString(mCredential);
        parcel.writeString(mCode);
    }
}
