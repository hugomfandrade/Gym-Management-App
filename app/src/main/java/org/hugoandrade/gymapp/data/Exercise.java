package org.hugoandrade.gymapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Exercise implements Parcelable {

    private String mID;
    private String mName;

    public static class Entry {

        public static final String TABLE_NAME = "Exercise";

        public static class Cols {
            public static final String ID = "id";
            public static final String NAME = "Name";
        }
    }

    public Exercise(String id) {
        mID = id;
    }

    public Exercise(String id, String name) {
        mID = id;
        mName = name;
    }

    public String getID() {
        return mID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Exercise) {
            Exercise u = (Exercise) obj;
            return Objects.equals(mID, u.mID) &&
                    Objects.equals(mName, u.mName);
        }
        return false;
    }

    protected Exercise(Parcel in) {
        mID = in.readString();
        mName = in.readString();
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mID);
        dest.writeString(mName);
    }
}
