package com.company.bicycle.client.modal;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by andrey on 03.12.15.
 */
public class BicycleServiceResponse {
    @SerializedName("error")
    private int mError;

    @SerializedName("message")
    private String mMessage;

    @SerializedName("data")
    private ArrayList<BicycleMarker> bicycleMarkers;

    public int getError() {
        return mError;
    }

    public String getMessage() {
        return mMessage;
    }

    public ArrayList<BicycleMarker> getBicycleMarkers() {
        return bicycleMarkers;
    }
}
