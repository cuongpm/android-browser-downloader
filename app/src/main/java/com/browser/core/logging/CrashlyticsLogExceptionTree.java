package com.browser.core.logging;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

public class CrashlyticsLogExceptionTree extends Timber.Tree {
    private final int mLogPriority;

    /**
     * Create instance with default log priority of ERROR.
     */
    public CrashlyticsLogExceptionTree() {
        this(Log.ERROR);
    }

    /**
     * @param logPriority Minimum log priority to send exception. Expects one of constants defined in {@link Log}.
     */
    public CrashlyticsLogExceptionTree(int logPriority) {
        this.mLogPriority = logPriority;
    }


    @Override
    protected boolean isLoggable(String tag, int priority) {
        return priority >= mLogPriority;
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (t != null) {
            Crashlytics.logException(t);
        } else {
            String formattedMessage = LogMessageHelper.format(priority, tag, message);
            Crashlytics.logException(new StackTraceRecorder(formattedMessage));
        }
    }
}