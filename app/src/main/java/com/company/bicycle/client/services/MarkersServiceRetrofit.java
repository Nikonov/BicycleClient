package com.company.bicycle.client.services;

import com.company.bicycle.client.modal.BicycleServiceResponse;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by andrey on 03.12.15.
 */
public interface MarkersServiceRetrofit {
    @GET("/bicycle/{latitude}/{longitude}")
    Call<BicycleServiceResponse> getNearestBicycleParking(@Path("latitude") double latitude, @Path("longitude") double longitude);
}
