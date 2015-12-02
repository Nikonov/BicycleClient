package com.company.bicycle.client.utils;

import android.util.Log;

import com.company.bicycle.client.BuildConfig;

/**
 * Created by andrey on 03.12.15.
 */
public class Logger {
    private static final String PREFIX = "BicycleClient_";
    private static final String DEFAULT_LOG_DEBUG_TAG = "BicycleClient_" + makeLogTag(Logger.class);

    public static boolean isDebugLevel() {
        return BuildConfig.DEBUG;
    }

    public static void logError(String tag, String message) {
        if (isDebugLevel()) {
            Log.e(tag, message);
        }
    }

    public static void logDebug(String tag, String message) {
        if (isDebugLevel()) {
            Log.d(tag, message);
        }
    }

    public static String makeLogTag(Class<?> cls) {
        if (cls == null) return DEFAULT_LOG_DEBUG_TAG;
        final String simpleName = cls.getSimpleName();
        return PREFIX.concat(simpleName);
    }
}
