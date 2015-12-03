package com.company.bicycle.client.services;

import android.location.Location;
import android.support.annotation.NonNull;

import com.company.bicycle.client.modal.BicycleMarker;

import java.util.List;

/**
 * Created by andrey on 03.12.15.
 */
public interface IDataMarkersProvider {

    List<BicycleMarker> getNearestMarkers(@NonNull Location myLocation);

    int addNewMarker(@NonNull BicycleMarker newMarker);

    int updateMarker(@NonNull BicycleMarker newMarker, int idMarker);
}
