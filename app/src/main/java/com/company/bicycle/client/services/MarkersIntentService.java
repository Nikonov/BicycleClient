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
    public static final String ACTION_FOUND_MARKERS = "com.company.bicycle.client.action.FOUND_MARKERS";
    public static final String EXTRA_MARKERS = "extra_markers";
    public static final String EXTRA_LOCATION = "extra_location";
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
                final Location location = intent.getParcelableExtra(EXTRA_LOCATION);
                if (location == null) {
                    logError(LOG_DEBUG, " Can't execute ACTION_GET_NEAREST_MARKERS location = null ");
                    return;
                }
                location.setLatitude(55.752220);
                location.setLongitude(37.615555);
                List<BicycleMarker> markers = mDataProvider.getNearestMarkers(location);
                logDebug(LOG_DEBUG, " markers found: " + markers.size());
                sendBroadcastFoundMarkers(markers);
                break;
            default:
                throw new UnsupportedOperationException(" Unknown action: " + action);
        }
    }

    private void sendBroadcastFoundMarkers(List<BicycleMarker> markers) {
        Intent intent = new Intent(ACTION_FOUND_MARKERS);
        intent.putParcelableArrayListExtra(EXTRA_MARKERS, new ArrayList<Parcelable>(markers));
        getApplicationContext().sendBroadcast(intent);
    }

    public static void executeActionGetNearestMarkers(@NonNull Context context, Location location) {
        Context applicationContext = context.getApplicationContext();
        Intent intent = new Intent(applicationContext, MarkersIntentService.class);
        intent.setAction(ACTION_GET_NEAREST_MARKERS);
        intent.putExtra(EXTRA_LOCATION, location);
        applicationContext.startService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logDebug(LOG_DEBUG, " onDestroy ");
    }
}
