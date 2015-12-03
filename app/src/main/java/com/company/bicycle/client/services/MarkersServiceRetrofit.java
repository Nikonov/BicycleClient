package com.company.bicycle.client.services;

import com.company.bicycle.client.modal.BicycleMarker;
import com.company.bicycle.client.modal.GetBicycleResponse;
import com.company.bicycle.client.modal.ResultAddedResponse;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by andrey on 03.12.15.
 */
public interface MarkersServiceRetrofit {
    @GET("/bicycle/{latitude}/{longitude}")
    Call<GetBicycleResponse> getNearestBicycleParking(@Path("latitude") double latitude, @Path("longitude") double longitude);

    @POST("/bicycle")
    Call<ResultAddedResponse> addNewMarker(@Body BicycleMarker marker);

    @POST("/bicycle/{id}")
    Call<GetBicycleResponse> updateMarker(@Body BicycleMarker marker, @Path("id") int id);
}
