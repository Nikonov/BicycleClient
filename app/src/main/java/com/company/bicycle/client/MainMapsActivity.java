package com.company.bicycle.client;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.company.bicycle.client.modal.BicycleMarker;
import com.company.bicycle.client.services.MarkersIntentService;
import com.company.bicycle.client.utils.PermissionUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import static com.company.bicycle.client.utils.Logger.*;

public class MainMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationChangeListener {
    private static final String LOG_DEBUG = "MainMapsActivity";
    private BroadcastReceiver mMarkersReceiver;
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //enabled my location function
        mMap.setOnMyLocationChangeListener(this);
        enableMyLocation();
    }


    private void enableMyLocation() {
        boolean locationPermissionDenied = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED;
        if (locationPermissionDenied) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else {
            if (isMapReady()) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isMapReady() {
        return mMap != null;
    }


    @Override
    public void onMyLocationChange(Location location) {
        if (isDebugLevel()) {
            Log.d(LOG_DEBUG, " onMyLocationChange location: " + location);
        }
        MarkersIntentService.executeActionGetNearestMarkers(this, location);
        if (isMapReady()) mMap.setMyLocationEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter markersFilter = new IntentFilter(MarkersIntentService.ACTION_FOUND_MARKERS);
        mMarkersReceiver = new MarkersReceiver();
        registerReceiver(mMarkersReceiver, markersFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mMarkersReceiver);
    }

    private class MarkersReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<BicycleMarker> markers = intent.getParcelableArrayListExtra(MarkersIntentService.EXTRA_MARKERS);
            addMarkersToMap(markers);
        }
    }

    private void addMarkersToMap(List<BicycleMarker> markers) {
        if (markers == null) return;
        Resources resources = getResources();
        String titleFormat = resources.getString(R.string.title_marker_bicycle_parking);
        for (BicycleMarker marker : markers) {
            double latitude = marker.getLatitude();
            double longitude = marker.getLongitude();
            final LatLng position = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(String.format(titleFormat, marker.getId()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bike_grey600_24dp)));
        }
    }

}
