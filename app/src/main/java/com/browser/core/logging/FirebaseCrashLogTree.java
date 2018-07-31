package com.browser.core.logging;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import timber.log.Timber;

public class FirebaseCrashLogTree extends Timber.Tree {
    private final int mLogPriority;

    /**
     * Create instance with default log priority of WARN.
     */
    public FirebaseCrashLogTree() {
        this(Log.WARN);
    }

    /**
     * @param logPriority Minimum log priority to send log. Expects one of constants defined in {@link Log}.
     */
    public FirebaseCrashLogTree(int logPriority) {
        this.mLogPriority = logPriority;
    }


    @Override
    protected boolean isLoggable(String tag, int priority) {
        return priority >= mLogPriority;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        String formattedMessage = LogMessageHelper.format(priority, tag, message);
        FirebaseCrash.log(formattedMessage);
    }
}

