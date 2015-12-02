package com.company.bicycle.client.modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by andrey on 03.12.15.
 */
public class BicycleMarker implements Parcelable {
    @SerializedName("id")
    private int mId;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("findDescription")
    private String mFindDescription;

    @SerializedName("latitude")
    private double mLatitude;

    @SerializedName("longitude")
    private double mLongitude;

    public static final Parcelable.Creator<BicycleMarker> CREATOR
            = new Parcelable.Creator<BicycleMarker>() {
        public BicycleMarker createFromParcel(Parcel in) {
            return new BicycleMarker(in);
        }

        public BicycleMarker[] newArray(int size) {
            return new BicycleMarker[size];
        }
    };

    private BicycleMarker(Parcel in) {
        mId = in.readInt();
        mDescription = in.readString();
        mFindDescription = in.readString();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mDescription);
        dest.writeString(mFindDescription);
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
    }

    public int getId() {
        return mId;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getFindDescription() {
        return mFindDescription;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }
}
