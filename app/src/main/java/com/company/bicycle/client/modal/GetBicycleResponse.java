package com.company.bicycle.client.modal;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by andrey on 03.12.15.
 */
public class GetBicycleResponse {
    @SerializedName("error")
    private int mResultCode;

    @SerializedName("message")
    private String mMessage;

    @SerializedName("data")
    private ArrayList<BicycleMarker> bicycleMarkers;

    public int getResultCode() {
        return mResultCode;
    }

    public String getMessage() {
        return mMessage;
    }

    public ArrayList<BicycleMarker> getBicycleMarkers() {
        return bicycleMarkers;
    }
}
