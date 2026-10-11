/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Detects app foreground/background by counting started activities. All callbacks arrive on the
 * main thread, so no timers, executors or locks are needed.
 */
final class JLifecycleListener implements Application.ActivityLifecycleCallbacks {

    interface Callback {
        void onAppForeground();

        void onAppBackground();
    }

    private static final AtomicBoolean registered = new AtomicBoolean(false);
    private static volatile WeakReference<Activity> resumedActivity;

    private final Callback callback;
    private int startedCount = 0;

    private JLifecycleListener(Callback callback) {
        this.callback = callback;
    }

    /** Registers once per process. */
    static void register(@NonNull Application app, @NonNull Callback callback) {
        if (registered.compareAndSet(false, true)) {
            app.registerActivityLifecycleCallbacks(new JLifecycleListener(callback));
        }
    }

    @Nullable
    static Activity getCurrentActivity() {
        WeakReference<Activity> ref = resumedActivity;
        return ref != null ? ref.get() : null;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (startedCount++ == 0) {
            callback.onAppForeground();
        }
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        if (startedCount > 0 && --startedCount == 0) {
            callback.onAppBackground();
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        resumedActivity = new WeakReference<>(activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        WeakReference<Activity> ref = resumedActivity;
        if (ref != null && ref.get() == activity) {
            resumedActivity = null;
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
    }
}