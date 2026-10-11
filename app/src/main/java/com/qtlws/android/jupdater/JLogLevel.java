/*
 * Copyright (c) 2026 JUpdater
 *
 * Licensed under the MIT License.
 *
 */

package com.qtlws.android.jupdater;

/**
 * Verbosity of the SDK's logcat output. Each level includes all levels above it
 * (e.g. {@link #INFO} also prints warnings and errors).
 *
 * <ul>
 *   <li>{@link #NONE}    - nothing is logged</li>
 *   <li>{@link #ERROR}   - failures only</li>
 *   <li>{@link #WARN}    - failures and recoverable problems (default)</li>
 *   <li>{@link #INFO}    - lifecycle / high level flow</li>
 *   <li>{@link #DEBUG}   - decisions, versions, URLs (no personal data)</li>
 *   <li>{@link #VERBOSE} - everything, including the request payload sent in managed mode</li>
 * </ul>
 */
public enum JLogLevel {
    NONE(0),
    ERROR(1),
    WARN(2),
    INFO(3),
    DEBUG(4),
    VERBOSE(5);

    final int priority;

    JLogLevel(int priority) {
        this.priority = priority;
    }
}
