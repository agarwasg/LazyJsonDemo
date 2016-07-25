package me.doubledutch.lazyjsondemo;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

/**
 * A simple perf timer class that supports lap-time-style measurements. Once a
 * timer is started, any number of laps can be marked, but they are all relative
 * to the original start time. Based on simple timer in Android OS
 */
public class SimpleTimer {

    private static final String DEFAULT_LOG_TAG = "SimpleTimer";
    private static final int NO_THRESHOLD = -1;
    private static final int DEFAULT_LOG_LEVEL = Log.INFO;

    private String mStartMessage;
    private String mLogTag;
    private int mLogLevel;
    private long mStartTime;
    private long mLastMarkTime;

    private SimpleTimer(String startMessage, String logTag,
            int logLevel) {
        this.mStartMessage = startMessage;
        this.mLogTag = logTag;
        this.mLogLevel = logLevel;
        start();
    }

    private SimpleTimer() {
        // no empty constructor
    }

    public long mark(String msg) {
        long now = System.nanoTime();

        // show just time or including since last mark
        String fullMessage;
        long timeElapsed = now - mStartTime;
        if (mStartTime == mLastMarkTime) {
            fullMessage = String.format("%sms for %s", timeElapsed, msg);
        } else {
            fullMessage = String.format("%sms (%sms since last mark) for %s",
                    timeElapsed, now - mLastMarkTime, msg);
        }

        // log with error, or default

        log(mLogLevel, fullMessage);

        mLastMarkTime = now;
        return timeElapsed;
    }

    private void start() {
        mStartTime = mLastMarkTime = System.nanoTime();
        if (!TextUtils.isEmpty(mStartMessage)) {
            log(mLogLevel, mStartMessage);
        }
    }


    private void log(int level, String message) {
        switch (level) {
            case Log.VERBOSE:
                Log.v(getTag(), message);
                break;
            case Log.INFO:
                Log.i(getTag(), message);
                break;
            case Log.DEBUG:
                Log.d(getTag(), message);
                break;
            case Log.ERROR:
                Log.e(getTag(), message);
                break;
            default:
                Log.d(getTag(), message);
        }
    }

    private String getTag() {
        return TextUtils.isEmpty(mLogTag) ? DEFAULT_LOG_TAG : mLogTag;
    }

    public static class Builder {
        private String mStartMessage = null;
        private String mLogTag = SimpleTimer.DEFAULT_LOG_TAG;
        private long mErrorTimeThreshold = SimpleTimer.NO_THRESHOLD;
        private int mLogLevel = SimpleTimer.DEFAULT_LOG_LEVEL;

        /**
         * Log message to be displayed when the timer starts
         */
        public Builder startMessage(String startMessage) {
            mStartMessage = startMessage;
            return this;
        }

        /**
         * The log tag for any start and mark messages
         */
        public Builder logTag(String logTag) {
            mLogTag = logTag;
            return this;
        }

        /**
         * Logs at the {@link Log#VERBOSE} level
         */
        public Builder logVerbose() {
            mLogLevel = Log.VERBOSE;
            return this;
        }

        /**
         * Logs at the {@link Log#INFO} level
         */
        public Builder logInfo() {
            mLogLevel = Log.INFO;
            return this;
        }

        /**
         * Logs at the {@link Log#DEBUG} level
         */
        public Builder logDebug() {
            mLogLevel = Log.DEBUG;
            return this;
        }


        /**
         * Build and start timer
         */
        public SimpleTimer start() {
            return new SimpleTimer(mStartMessage, mLogTag,
                    mLogLevel);
        }
    }

}