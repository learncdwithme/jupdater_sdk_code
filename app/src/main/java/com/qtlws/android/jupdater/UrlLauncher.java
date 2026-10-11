/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/** Safe wrappers around opening URLs. */
final class UrlLauncher {

    private UrlLauncher() {
    }

    /** Only http(s) links are accepted from the update server. */
    static boolean isHttpUrl(@Nullable String url) {
        if (url == null) return false;
        String scheme = Uri.parse(url.trim()).getScheme();
        return "https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme);
    }

    static boolean open(@NonNull Context context, @Nullable String url) {
        if (url == null || url.trim().isEmpty()) {
            JLogger.w("No URL to open.");
            return false;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.trim()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            JLogger.d(() -> "Opened " + url);
            return true;
        } catch (ActivityNotFoundException | SecurityException e) {
            JLogger.w("Could not open URL: " + e.getLocalizedMessage());
            return false;
        }
    }
}
