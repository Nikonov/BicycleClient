package com.company.bicycle.client.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.nsd.NsdServiceInfo;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.company.bicycle.client.modal.BicycleMarker;

import java.util.ArrayList;
import java.util.List;

import static com.company.bicycle.client.utils.Logger.*;

/**
 * Created by andrey on 03.12.15.
 */
public class MarkersIntentService extends IntentService {
    private static final String LOG_DEBUG = makeLogTag(MarkersIntentService.class);
    private static final String ACTION_GET_NEAREST_MARKERS = "com.company.bicycle.client.action.GET_NEAREST_MARKERS";
    public static final String ACTION_ADD_MARKER = "com.company.bicycle.client.action.ADD_MARKER";
    public static final String ACTION_FOUND_MARKERS = "com.company.bicycle.client.action.FOUND_MARKERS";
    public static final String EXTRA_MARKERS = "extra_markers";
    public static final String EXTRA_MARKER = "extra_marker";
    public static final String EXTRA_CURRENT_LOCATION = "extra_current_location";
    public static final String EXTRA_RESULT = "extra_result";
    private IDataMarkersProvider mDataProvider = new RetrofitMarkersProvider();

    public MarkersIntentService() {
        super("MarkersIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logDebug(LOG_DEBUG, " onCreate ");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            logError(LOG_DEBUG, " Intent null missed operation ");
            return;
        }
        final String action = intent.getAction();

        if (TextUtils.isEmpty(action)) {
            throw new IllegalArgumentException(" Action must be don't empty. Now action: " + action);
        }
        logDebug(LOG_DEBUG, " action: " + action);
        switch (action) {
            case ACTION_GET_NEAREST_MARKERS:
                final Location currentLocation = intent.getParcelableExtra(EXTRA_CURRENT_LOCATION);
                if (currentLocation == null) {
                    logError(LOG_DEBUG, " Can't execute ACTION_GET_NEAREST_MARKERS location = null ");
                    return;
                }
                List<BicycleMarker> markers = mDataProvider.getNearestMarkers(currentLocation);
                logDebug(LOG_DEBUG, " markers found: " + markers.size());
                sendBroadcastFoundMarkers(markers, currentLocation);
                break;
            case ACTION_ADD_MARKER:
                final BicycleMarker newMarker = intent.getParcelableExtra(EXTRA_MARKER);
                logDebug(LOG_DEBUG, " lang: " + newMarker.getLatitude() + " long: " + newMarker.getLongitude());
                int result = mDataProvider.addNewMarker(newMarker);
                sendBroadcastResultAdded(result);
                break;
            default:
                throw new UnsupportedOperationException(" Unknown action: " + action);
        }
    }

    private void sendBroadcastFoundMarkers(List<BicycleMarker> markers, Location lastLocation) {
        Intent intent = new Intent(ACTION_FOUND_MARKERS);
        intent.putExtra(EXTRA_CURRENT_LOCATION, lastLocation);
        intent.putParcelableArrayListExtra(EXTRA_MARKERS, new ArrayList<Parcelable>(markers));
        getApplicationContext().sendBroadcast(intent);
    }

    private void sendBroadcastResultAdded(int result) {
        Intent intent = new Intent(ACTION_ADD_MARKER);
        intent.putExtra(EXTRA_RESULT, result);
        getApplicationContext().sendBroadcast(intent);
    }

    public static void executeActionGetNearestMarkers(@NonNull Context context, Location location) {
        Context applicationContext = context.getApplicationContext();
        Intent intent = new Intent(applicationContext, MarkersIntentService.class);
        intent.setAction(ACTION_GET_NEAREST_MARKERS);
        intent.putExtra(EXTRA_CURRENT_LOCATION, location);
        applicationContext.startService(intent);
    }

    public static void executeActionAddMarker(@NonNull Context context, BicycleMarker marker) {
        logDebug(LOG_DEBUG, "  executeActionAddMarker lang: " + marker.getLatitude() + " long: " + marker.getLongitude());
        Context applicationContext = context.getApplicationContext();
        Intent intent = new Intent(applicationContext, MarkersIntentService.class);
        intent.setAction(ACTION_ADD_MARKER);
        intent.putExtra(EXTRA_MARKER, marker);
        applicationContext.startService(intent);
    }

    public static boolean isSuccess(int checkStatus) {
        return checkStatus == 0;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        logDebug(LOG_DEBUG, " onDestroy ");
    }
}
