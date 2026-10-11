
/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;


import android.net.Uri;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class UpdateChecker {
    private static final int CONNECT_TIMEOUT_MS = 10_000;
    private static final int READ_TIMEOUT_MS = 10_000;
    private static final int MAX_RESPONSE_BYTES = 64 * 1024;

    private UpdateChecker() {
    }

    /**
     * Non-200 answer from the server.
     */
    static final class HttpException extends IOException {
        final int code;

        HttpException(int code) {
            super("HTTP " + code);
            this.code = code;
        }
    }

    static final class Response {
        final String pid;
        final String title;
        final String message;
        final int versionCode;
        final boolean blocked;
        final boolean isForceUpdate;
        @Nullable
        final String updateUrl;
        final int forceGap;      // already >= 1

        Response(String pid, int versionCode, boolean blocked, @Nullable String updateUrl, int forceGap, String title, String message, boolean isForceUpdate) {
            this.pid = pid;
            this.versionCode = versionCode;
            this.blocked = blocked;
            this.updateUrl = updateUrl;
            this.forceGap = forceGap;
            this.isForceUpdate = isForceUpdate;
            this.title = title;
            this.message = message;
        }
    }

    /**
     * @param payload JSON body for managed mode (POST), or {@code null} for custom mode (GET with
     *                only pid + version_code as query parameters, nothing else is sent).
     */
    @NonNull
    static Response check(@NonNull JUpdaterConfig config, @NonNull AppInfo app, @Nullable JSONObject payload)
            throws IOException, JSONException {

        String url = Uri.parse(config.getCustomServerUrl()).buildUpon()
                .appendQueryParameter("pid", app.packageName)
                .appendQueryParameter("version_code", Integer.toString(app.versionCode))
                .build().toString();

        if (url.regionMatches(true, 0, "http://", 0, 7)) {
            JLogger.w("Update server is not using HTTPS; the response can be tampered with.");
        }
        JLogger.d(() -> "Checking " + url);

        HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
        try {
            c.setConnectTimeout(CONNECT_TIMEOUT_MS);
            c.setReadTimeout(READ_TIMEOUT_MS);
            c.setUseCaches(false);
            c.setRequestProperty("Accept", "application/json");

            if (payload != null) {
                byte[] body = payload.toString().getBytes(StandardCharsets.UTF_8);
                c.setRequestMethod("POST");
                c.setDoOutput(true);
                c.setRequestProperty("x-api-key", config.getApiKey());
                c.setRequestProperty("Content-Type", "application/json");
                c.setFixedLengthStreamingMode(body.length);
                try (OutputStream os = c.getOutputStream()) {
                    os.write(body);
                }
            } else {
                c.setRequestMethod("GET");
            }

            int code = c.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                throw new HttpException(code);
            }

            String json = readLimited(c.getInputStream());
            JLogger.v(() -> "Response: " + json);
            return parse(json, config);
        } finally {
            c.disconnect();
        }
    }

    @NonNull
    private static Response parse(@NonNull String json, @NonNull JUpdaterConfig config) throws JSONException {
        JSONObject o = new JSONObject(json);
        int gap = o.has("force_update_gap")
                ? Math.max(1, o.optInt("force_update_gap", 1))   // 0 or negative => always force
                : config.getForceUpdateThreshold();
        String title = o.optString("title", "").trim();
        if (title.isEmpty()) {
            title = "An update is available!";
        }

        String message = o.optString("message", "").trim();
        if (message.isEmpty()) {
            message = "Please update the app to the latest\nversion to continue using it. This\n"
                    + "update includes new features and improvements.";
        }
        return new Response(
                o.getString("pid"),
                o.getInt("version_code"),
                o.optBoolean("is_blocked", false),
                o.isNull("update_url") ? null : o.optString("update_url", null),
                gap,
                title,
                message,
                o.optBoolean("force_update", false)
        );
    }

    @NonNull
    private static String readLimited(@NonNull InputStream in) throws IOException {
        try (InputStream is = in) {
            ByteArrayOutputStream out = new ByteArrayOutputStream(512);
            byte[] buf = new byte[1024];
            int n;
            while ((n = is.read(buf)) != -1) {
                out.write(buf, 0, n);
                if (out.size() > MAX_RESPONSE_BYTES) {
                    throw new IOException("Response too large");
                }
            }
            return out.toString("UTF-8");
        }
    }
}
