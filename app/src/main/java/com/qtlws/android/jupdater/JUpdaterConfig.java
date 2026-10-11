/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for {@link JUpdater}.
 *
 * <p><b>Privacy:</b> in <i>managed</i> mode the SDK sends device and app information to the
 * managed server on every update check (see PRIVACY.md for the exact fields). In <i>custom</i>
 * mode nothing but the package name and version code (as query parameters) is sent, and only to
 * your own server. The integrating developer is responsible for disclosing this in the app's
 * privacy policy and for obtaining consent where GDPR / ePrivacy require it, before calling
 * {@link JUpdater#init}.
 */
public final class JUpdaterConfig {

    public static final String MANAGED_SERVER_URL = "https://jupdater.qtlws.com/api/check-update";
    public static final int DEFAULT_FORCE_UPDATE_THRESHOLD = 5;
    public static final long DEFAULT_MIN_CHECK_INTERVAL_MINUTES = 5;

    private final String apiKey;               // managed mode only
    private final String serverUrl;
    private final String fallbackApkUrl;       // custom mode only

    private volatile JLogLevel logLevel = JLogLevel.WARN;

    private int forceUpdateThreshold = DEFAULT_FORCE_UPDATE_THRESHOLD;
    private long minCheckIntervalMs = TimeUnit.MINUTES.toMillis(DEFAULT_MIN_CHECK_INTERVAL_MINUTES);

    /**
     * Self-hosted mode. Only {@code pid} and {@code version_code} are sent, via GET, to
     * {@code customServerUrl}. No device data is collected or transmitted.
     *
     * @param updatedApkUrl   fallback download/store URL used if the server does not return one
     * @param customServerUrl your update-check endpoint (HTTPS strongly recommended)
     */
    public static JUpdaterConfig useCustomServer(@NonNull String updatedApkUrl, @NonNull String customServerUrl) {
        return new JUpdaterConfig(updatedApkUrl, customServerUrl);
    }

    /**
     * Managed mode. Device and app data (see PRIVACY.md) is sent via POST to the managed server
     * so update insights can be shown on the dashboard.
     */
    public static JUpdaterConfig useManagedServer(@NonNull String apiKey) {
        return new JUpdaterConfig(apiKey);
    }

    private JUpdaterConfig(@NonNull String updatedApkUrl, @NonNull String customServerUrl) {
        if (updatedApkUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Update APK URL cannot be null or empty.");
        }
        if (customServerUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Server URL cannot be null or empty.");
        }
        this.apiKey = null;
        this.fallbackApkUrl = updatedApkUrl.trim();
        this.serverUrl = customServerUrl.trim();
    }

    private JUpdaterConfig(@NonNull String apiKey) {
        if (apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API KEY cannot be null or empty.");
        }
        this.apiKey = apiKey.trim();
        this.fallbackApkUrl = null;
        this.serverUrl = MANAGED_SERVER_URL;
    }

    public boolean isUsingManagedServer() {
        return apiKey != null;
    }

    @Nullable
    public String getApiKey() {
        return apiKey;
    }

    public String getCustomServerUrl() {
        return serverUrl;
    }


    /**
     * Sets how much the SDK logs to logcat. Default: {@link JLogLevel#WARN}.
     */
    public JUpdaterConfig setLogLevel(@NonNull JLogLevel level) {
        this.logLevel = level;
        return this;
    }

    public JLogLevel getLogLevel() {
        return logLevel;
    }

    /**
     * @deprecated use {@link #setLogLevel(JLogLevel)}. {@code true} maps to DEBUG, {@code false} to WARN.
     */
    @Deprecated
    public JUpdaterConfig setDebug(boolean debug) {
        return setLogLevel(debug ? JLogLevel.DEBUG : JLogLevel.WARN);
    }

    /**
     * Minimum time between two network checks. Foreground events inside this window are answered
     * from the last server response, without any request. {@code 0} checks on every foreground.
     * Default: {@value #DEFAULT_MIN_CHECK_INTERVAL_MINUTES} minutes.
     */
    public JUpdaterConfig setMinCheckIntervalMinutes(long minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Interval cannot be negative.");
        }
        this.minCheckIntervalMs = TimeUnit.MINUTES.toMillis(minutes);
        return this;
    }

    long getMinCheckIntervalMs() {
        return minCheckIntervalMs;
    }

    int getForceUpdateThreshold() {
        return forceUpdateThreshold;
    }

    @Nullable
    String getFallbackApkUrl() {
        return fallbackApkUrl;
    }
}
