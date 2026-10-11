/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;

import static com.qtlws.android.jupdater.Constants.INSTALLATION_ID;
import static com.qtlws.android.jupdater.Constants.KEY_BLOCKED;
import static com.qtlws.android.jupdater.Constants.KEY_FORCE_GAP;
import static com.qtlws.android.jupdater.Constants.KEY_LAST_CHECK;
import static com.qtlws.android.jupdater.Constants.KEY_SERVER_VERSION;
import static com.qtlws.android.jupdater.Constants.KEY_UPDATE_URL;
import static com.qtlws.android.jupdater.Constants.PREFS;
import static com.qtlws.android.jupdater.Constants.KEY_TITLE;
import static com.qtlws.android.jupdater.Constants.KEY_MESSAGE;
import static com.qtlws.android.jupdater.Constants.KEY_ISFORCEUPDATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.UUID;

/** Persists the last server answer (for throttling and offline fallback) and the installation id. */
final class UpdateStore {

    /** Last known answer from the update server. */
    static final class ServerState {
        final String title;
        final String message;
        final int versionCode;
        final boolean isForceUpdate;
        @Nullable final String updateUrl;
        final int forceGap;       // <= 0 means "use the configured threshold"
        final boolean blocked;

        ServerState(int versionCode, @Nullable String updateUrl, int forceGap, boolean blocked, String title, String message, boolean isForceUpdate) {
            this.versionCode = versionCode;
            this.updateUrl = updateUrl;
            this.forceGap = forceGap;
            this.blocked = blocked;
            this.isForceUpdate = isForceUpdate;
            this.title = title;
            this.message = message;
        }
    }

    private final SharedPreferences prefs;

    UpdateStore(@NonNull Context appContext) {
        this.prefs = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    /**
     * Random, app-scoped installation id. It is not derived from any hardware identifier and is
     * reset when the app is uninstalled or its data cleared.
     */
    @NonNull
    synchronized String installationId() {
        String id = prefs.getString(INSTALLATION_ID, null);
        if (id == null) {
            id = UUID.randomUUID().toString();
            prefs.edit().putString(INSTALLATION_ID, id).apply();
        }
        return id;
    }

    @Nullable
    ServerState load() {
        int code = prefs.getInt(KEY_SERVER_VERSION, -1);
        if (code <= 0) return null;
        return new ServerState(
                code,
                prefs.getString(KEY_UPDATE_URL, null),
                prefs.getInt(KEY_FORCE_GAP, 0),
                prefs.getBoolean(KEY_BLOCKED, false),
                prefs.getString(KEY_TITLE, null),
                prefs.getString(KEY_MESSAGE, null),
                prefs.getBoolean(KEY_ISFORCEUPDATE, false));
    }

    long lastCheckTime() {
        return prefs.getLong(KEY_LAST_CHECK, 0L);
    }

    void save(@NonNull ServerState s, long checkedAt) {
        prefs.edit()
                .putInt(KEY_SERVER_VERSION, s.versionCode)
                .putString(KEY_UPDATE_URL, s.updateUrl)
                .putString(KEY_TITLE, s.title)
                .putString(KEY_MESSAGE, s.message)
                .putBoolean(KEY_ISFORCEUPDATE, s.isForceUpdate)
                .putInt(KEY_FORCE_GAP, s.forceGap)
                .putBoolean(KEY_BLOCKED, s.blocked)
                .putLong(KEY_LAST_CHECK, checkedAt)
                .apply();
    }
}