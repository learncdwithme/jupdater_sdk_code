
/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

final class Constants {


    public static final String PREFS = "j_updater_pref";
    public static final String INSTALLATION_ID = "installation_id";
    static final String KEY_SERVER_VERSION = "j_updater_version";
    static final String KEY_UPDATE_URL = "j_updater_url";
    static final String KEY_FORCE_GAP = "j_updater_gap";
    static final String KEY_BLOCKED = "j_updater_blocked";
    static final String KEY_LAST_CHECK = "j_updater_last_check";
    static final String KEY_TITLE = "j_updater_title";
    static final String KEY_MESSAGE = "j_updater_message";
    static final String KEY_ISFORCEUPDATE = "j_updater_force_update";

    public static final String EXTRA_UPDATE_URL = "update_url";
    public static final String EXTRA_TITLE = "updater_title";
    public static final String EXTRA_MESSAGE = "updater_message";
    private Constants() {
    }
}
