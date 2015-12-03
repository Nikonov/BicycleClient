package com.company.bicycle.client;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.company.bicycle.client.modal.BicycleMarker;
import com.company.bicycle.client.services.MarkersIntentService;
import com.company.bicycle.client.ui.DescriptionContainerFragment;
import com.company.bicycle.client.ui.DescriptionMarkerFragment;
import com.company.bicycle.client.ui.NewBicycleMarkerFragment;
import com.company.bicycle.client.utils.PermissionUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.company.bicycle.client.utils.Logger.*;

public class MainMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationChangeListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener, NewBicycleMarkerFragment.OnNewMarker {
    private static final String LOG_DEBUG = "MainMapsActivity";
    private static final int MINIMUM_DISTANCE_METER = 30;
    private BroadcastReceiver mMarkersReceiver;
    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private HashMap<Marker, BicycleMarker> mMapMarkers = new HashMap<>(10);
    private FrameLayout mContentPanel;
    private ViewGroup mRootLayout;
    private Location mLastLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_maps);
        mContentPanel = (FrameLayout) findViewById(R.id.content_container);
        mRootLayout = (ViewGroup) findViewById(R.id.root_layout);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //enabled my location function
        mMap.setOnMyLocationChangeListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapLongClickListener(this);
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
        if (isNeedNewRequest(location, mLastLocationRequest)) {
            MarkersIntentService.executeActionGetNearestMarkers(this, location);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter markersFilter = new IntentFilter(MarkersIntentService.ACTION_FOUND_MARKERS);
        markersFilter.addAction(MarkersIntentService.ACTION_ADD_MARKER);
        mMarkersReceiver = new MarkersReceiver();
        registerReceiver(mMarkersReceiver, markersFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mMarkersReceiver);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //unused
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        logDebug(LOG_DEBUG, " onInfoWindowClick ");
        final String tagDescriptionFragment = getString(R.string.tag_fragment_content_container);
        DescriptionContainerFragment fragment = (DescriptionContainerFragment) getSupportFragmentManager().
                findFragmentByTag(tagDescriptionFragment);

        BicycleMarker currentBicycleMarker = mMapMarkers.get(marker);
        ArrayList<BicycleMarker> allMarkers = new ArrayList<>(mMapMarkers.values());
        final int currentPosition = currentBicycleMarker == null ? 0 : allMarkers.indexOf(currentBicycleMarker);

        if (fragment == null) {
            Fragment descriptionContainer = DescriptionContainerFragment.newInstance(allMarkers, currentPosition);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, descriptionContainer, tagDescriptionFragment)
                    .commit();
            getSupportFragmentManager().executePendingTransactions();
        } else {
            fragment.updateData(allMarkers, currentPosition);
        }
        //show panel if need
        if (!isDescriptionPanelVisible()) {
            showOrHidePanel(true);
        }

    }

    private void showOrHidePanel(final boolean show) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final int y = (int) (metrics.heightPixels * 0.4);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mContentPanel, View.TRANSLATION_Y,
                show ? metrics.heightPixels : y, show ? y : metrics.heightPixels);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (show) mContentPanel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!show) mContentPanel.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    private boolean isDescriptionPanelVisible() {
        return mContentPanel.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        logDebug(LOG_DEBUG, " onMapLongClick ");
        final String tagNewAddedFragment = getString(R.string.tag_fragment_new_bicycle_marker);
        Fragment newAddMarker = NewBicycleMarkerFragment.newInstance(latLng);
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.content_container, newAddMarker, tagNewAddedFragment)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
        //show panel if need
        if (!isDescriptionPanelVisible()) {
            showOrHidePanel(true);
        }
    }


    private class MarkersReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                switch (action) {
                    case MarkersIntentService.ACTION_ADD_MARKER:
                        int result = intent.getIntExtra(MarkersIntentService.EXTRA_RESULT, -1);
                        int resMessage = MarkersIntentService.isSuccess(result) ? R.string.added_successfully : R.string.added_error;
                        Snackbar.make(mRootLayout, resMessage,
                                Snackbar.LENGTH_SHORT)
                                .show();
                        break;
                    case MarkersIntentService.ACTION_FOUND_MARKERS:
                        ArrayList<BicycleMarker> markers = intent.getParcelableArrayListExtra(MarkersIntentService.EXTRA_MARKERS);
                        addMarkersToMap(markers);
                        mLastLocationRequest = intent.getParcelableExtra(MarkersIntentService.EXTRA_CURRENT_LOCATION);
                        break;
                    default:
                        logDebug(LOG_DEBUG, " Unknown action: " + action);
                }
            }
        }
    }

    private void addMarkersToMap(List<BicycleMarker> markers) {
        if (markers == null) return;
        Resources resources = getResources();
        String titleFormat = resources.getString(R.string.title_marker_bicycle_parking);
        for (BicycleMarker bicycleMarker : markers) {
            double latitude = bicycleMarker.getLatitude();
            double longitude = bicycleMarker.getLongitude();
            final LatLng position = new LatLng(latitude, longitude);
            Marker markerOnTheMap = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(String.format(titleFormat, bicycleMarker.getId()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bike_grey600_24dp)));
            //save
            mMapMarkers.put(markerOnTheMap, bicycleMarker);
        }
    }

    @Override
    public void onAddNewMarker(BicycleMarker marker) {
        MarkersIntentService.executeActionAddMarker(this, marker);
        showOrHidePanel(false);
    }

    @Override
    public void onBackPressed() {
        if (isDescriptionPanelVisible()) {
            showOrHidePanel(false);
            return;
        }
        super.onBackPressed();
    }

    private boolean isNeedNewRequest(Location currentLocation, Location lastLocation) {
        if (lastLocation == null) return true;
        float[] holderResult = new float[4];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                lastLocation.getLatitude(), lastLocation.getLongitude(), holderResult);
        final float distance = holderResult[0];
        logDebug(LOG_DEBUG, " distance: " + distance);
        return distance > MINIMUM_DISTANCE_METER;
    }
}
