/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.Supplier;

/**
 * Level-gated logger. The {@link Supplier} overloads avoid building expensive
 * strings when the level is disabled.
 */
final class JLogger {

    private static final int MAX_TAG_LENGTH = 23; // limit on API < 26
    private static final String TAG = buildTag();

    private static volatile JLogLevel level = JLogLevel.WARN;

    private JLogger() {
    }

    private static String buildTag() {
        String tag = BuildConfig.SDK_NAME + "_V" + BuildConfig.VERSION;
        return tag.length() > MAX_TAG_LENGTH ? tag.substring(0, MAX_TAG_LENGTH) : tag;
    }

    static void setLevel(@Nullable JLogLevel newLevel) {
        level = newLevel != null ? newLevel : JLogLevel.WARN;
    }

    static boolean isEnabled(@NonNull JLogLevel messageLevel) {
        return messageLevel != JLogLevel.NONE && level.priority >= messageLevel.priority;
    }

    static void e(@Nullable Object msg) {
        if (isEnabled(JLogLevel.ERROR)) Log.e(TAG, String.valueOf(msg));
    }

    static void e(@Nullable Object msg, @Nullable Throwable t) {
        if (isEnabled(JLogLevel.ERROR)) Log.e(TAG, String.valueOf(msg), t);
    }

    static void w(@Nullable Object msg) {
        if (isEnabled(JLogLevel.WARN)) Log.w(TAG, String.valueOf(msg));
    }

    static void i(@Nullable Object msg) {
        if (isEnabled(JLogLevel.INFO)) Log.i(TAG, String.valueOf(msg));
    }

    static void d(@Nullable Object msg) {
        if (isEnabled(JLogLevel.DEBUG)) Log.d(TAG, String.valueOf(msg));
    }

    static void d(@NonNull Supplier<String> msg) {
        if (isEnabled(JLogLevel.DEBUG)) Log.d(TAG, msg.get());
    }

    static void v(@NonNull Supplier<String> msg) {
        if (isEnabled(JLogLevel.VERBOSE)) Log.v(TAG, msg.get());
    }
}
