
/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;

import android.app.Activity;

import androidx.annotation.NonNull;

final class JUpdateSheetController {

    private static boolean hasSheetShown = false;

    private JUpdateSheetController() {
    }

    static void show(@NonNull String updateUrl,@NonNull String t,@NonNull String m) {
        if (hasSheetShown) return;
        Activity activity = JLifecycleListener.getCurrentActivity();
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return; // retried on the next foreground
        }
        hasSheetShown = true;
        JUpdateBottomSheet.show(activity, updateUrl,t,m);
    }
}
