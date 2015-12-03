package com.company.bicycle.client.modal;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by andrey on 03.12.15.
 */
public class ResultAddedResponse {
    @SerializedName("error")
    private int mResultCode;

    @SerializedName("message")
    private String mMessage;

    @SerializedName("data")
    private Data mData;

    private class Data {
        @SerializedName("id")
        private int id;

    }

    public int getResultCode() {
        return mResultCode;
    }
}
