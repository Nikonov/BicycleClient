package com.company.bicycle.client.services;

import android.location.Location;
import android.support.annotation.NonNull;

import com.company.bicycle.client.modal.BicycleMarker;
import com.company.bicycle.client.modal.BicycleServiceResponse;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by andrey on 03.12.15.
 */
public class RetrofitMarkersProvider implements IDataMarkersProvider {
    private static final String BASE_API_URL = "https://quiet-spire-6100.herokuapp.com";

    @Override
    public List<BicycleMarker> getNearestMarkers(@NonNull Location myLocation) {
        MarkersServiceRetrofit serviceApi = createService();
        Call<BicycleServiceResponse> call = serviceApi.getNearestBicycleParking(myLocation.getLatitude(), myLocation.getLongitude());
        try {
            Response<BicycleServiceResponse> response = call.execute();
            return response.body().getBicycleMarkers();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    private MarkersServiceRetrofit createService() {
        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setReadTimeout(10, TimeUnit.SECONDS);
        httpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
        return retrofit.create(MarkersServiceRetrofit.class);
    }
}
