package com.company.bicycle.client.services;

import android.location.Location;
import android.support.annotation.NonNull;

import com.company.bicycle.client.modal.BicycleMarker;
import com.company.bicycle.client.modal.GetBicycleResponse;
import com.company.bicycle.client.modal.ResultAddedResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.company.bicycle.client.utils.Logger.*;

/**
 * Created by Andrey Nikonov on 04.12.15.
 */
public class UrlConnectionMarkersProvider implements IDataMarkersProvider {
    private static final String LOG_DEBUG = makeLogTag(UrlConnectionMarkersProvider.class);

    @Override
    public List<BicycleMarker> getNearestMarkers(@NonNull Location myLocation) {
        String urlString = "https://quiet-spire-6100.herokuapp.com/bicycle/" + myLocation.getLatitude() + "/" + myLocation.getLongitude();
        HttpURLConnection connection = null;
        BufferedReader inputData = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Android");
            connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(10));
            connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(10));
            connection.setDoInput(true);

            connection.connect();

            inputData = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = inputData.readLine()) != null) {
                builder.append(line + "\n");
            }
            final int responseCode = connection.getResponseCode();
            logDebug(LOG_DEBUG, "Sending 'GET' request to URL : " + url + " responseCode: " + responseCode);
            String json = builder.toString();
            logDebug(LOG_DEBUG, " raw data: " + json);
            Gson gsonBuilder = new GsonBuilder().serializeNulls().create();
            GetBicycleResponse responseObject = gsonBuilder.fromJson(json, GetBicycleResponse.class);
            return responseObject == null ? new ArrayList<BicycleMarker>() : responseObject.getBicycleMarkers();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) connection.disconnect();
            try {
                if (inputData != null) {
                    inputData.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public int addNewMarker(@NonNull BicycleMarker newMarker) {
        HttpURLConnection connection = null;
        OutputStream outputStream = null;
        BufferedReader inputData = null;

        try {
            //constants
            URL url = new URL("https://quiet-spire-6100.herokuapp.com/bicycle/");
            Gson gsonSendData = new GsonBuilder().serializeNulls().create();
            String message = gsonSendData.toJson(newMarker, BicycleMarker.class);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(10));
            connection.setReadTimeout((int) TimeUnit.SECONDS.toMillis(10));
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(message.getBytes().length);

            //make some HTTP header nicety
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");

            //open
            connection.connect();

            //setup send
            outputStream = new BufferedOutputStream(connection.getOutputStream());
            outputStream.write(message.getBytes());
            //clean up
            outputStream.flush();
            inputData = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = inputData.readLine()) != null) {
                builder.append(line + "\n");
            }
            Gson gson = new GsonBuilder().serializeNulls().create();
            String json = builder.toString();
            ResultAddedResponse responseObject = gson.fromJson(json, ResultAddedResponse.class);
            return responseObject == null ? -1 : responseObject.getResultCode();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //clean up
            try {
                if (outputStream != null) outputStream.close();
                if (inputData != null) inputData.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null) connection.disconnect();
        }
        return -1;
    }

    @Override
    public int updateMarker(@NonNull BicycleMarker newMarker, int idMarker) {
        throw new UnsupportedOperationException("Method: updateMarker(@NonNull BicycleMarker newMarker, int idMarker) - unsupported");
    }
}
