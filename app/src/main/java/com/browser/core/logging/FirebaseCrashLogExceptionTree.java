package com.browser.core.logging;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import timber.log.Timber;

public class FirebaseCrashLogExceptionTree extends Timber.Tree {
    private final int mLogPriority;

    /**
     * Create instance with default log priority of ERROR.
     */
    public FirebaseCrashLogExceptionTree() {
        this(Log.ERROR);
    }

    /**
     * @param logPriority Minimum log priority to send exception. Expects one of constants defined in {@link Log}.
     */
    public FirebaseCrashLogExceptionTree(int logPriority) {
        this.mLogPriority = logPriority;
    }


    @Override
    protected boolean isLoggable(String tag, int priority) {
        return priority >= mLogPriority;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (t != null) {
            FirebaseCrash.report(t);
        } else {
            String formattedMessage = LogMessageHelper.format(priority, tag, message);
            FirebaseCrash.report(new StackTraceRecorder(formattedMessage));
        }
    }
}