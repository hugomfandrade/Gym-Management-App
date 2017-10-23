package org.hugoandrade.gymapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hugo Andrade on 22/10/2017.
 */

public class Credential implements Parcelable {

    public static final String ADMIN = "Admin";
    public static final String STAFF = "Staff";
    public static final String MEMBER = "Member";

    public static class Entry {

        public static final String TABLE_NAME = "User";

        public static class Cols {
            public static final String ID = "id";
            public static final String USERNAME = "Username";
            public static final String PASSWORD = "Password";
        }
    }

    private String mID;
    private String mUsername;
    private String mCredential;

    public Credential(String id, String username, String credential) {
        mID = id;
        mUsername = username;
        mCredential = credential;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getCredential() {
        return mCredential;
    }

    protected Credential(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        mID = in.readString();
        mUsername = in.readString();
        mCredential = in.readString();
    }

    public static final Creator<Credential> CREATOR = new Creator<Credential>() {
        @Override
        public Credential createFromParcel(Parcel in) {
            return new Credential(in);
        }

        @Override
        public Credential[] newArray(int size) {
            return new Credential[size];
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
    }
}
