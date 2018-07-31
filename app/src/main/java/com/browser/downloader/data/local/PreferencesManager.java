package com.browser.downloader.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.browser.downloader.data.model.ConfigData;
import com.browser.downloader.data.model.ProgressInfo;
import com.browser.downloader.data.model.Video;
import com.browser.downloader.data.model.WebViewData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PreferencesManager {

    private static final String PRE_KEY = "PRE_KEY";
    private static final String PRE_CONFIG_DATA = "PRE_CONFIG_DATA";
    private static final String PRE_RATE_APP = "PRE_RATE_APP";
    private static final String PRE_TAB_VIDEO_BADGE = "PRE_TAB_VIDEO_BADGE";
    private static final String PRE_TAB_ONLINE_BADGE = "PRE_TAB_ONLINE_BADGE";
    private static final String PRE_HISTORY = "PRE_HISTORY";
    private static final String PRE_BOOKMARK = "PRE_BOOKMARK";
    private static final String PRE_PROGRESS = "PRE_PROGRESS";
    private static final String PRE_VIDEO_SAVED = "PRE_VIDEO_SAVED";
    private static final String PRE_RETENTION_TIME = "PRE_RETENTION_TIME";
    private static final String PRE_FIRST_TIME = "PRE_FIRST_TIME";


    private static PreferencesManager instance = null;

    private final SharedPreferences mSharePreferences;

    @Inject
    public PreferencesManager(Context context) {
        mSharePreferences = context.getSharedPreferences(PRE_KEY, Context.MODE_PRIVATE);
    }

    public static PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context);
        }
        return instance;
    }

    public ConfigData getConfigData() {
        try {
            String data = mSharePreferences.getString(PRE_CONFIG_DATA, "");
            return TextUtils.isEmpty(data) ? null : new Gson().fromJson(data, ConfigData.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setConfigData(ConfigData configData) {
        if (configData != null)
            mSharePreferences.edit().putString(PRE_CONFIG_DATA, new Gson().toJson(configData)).apply();
    }

    public boolean isRateApp() {
        return mSharePreferences.getBoolean(PRE_RATE_APP, false);
    }

    public void setRateApp(boolean isRateApp) {
        mSharePreferences.edit().putBoolean(PRE_RATE_APP, isRateApp).apply();
    }

    public int getTabVideoBadge() {
        return mSharePreferences.getInt(PRE_TAB_VIDEO_BADGE, 0);
    }

    public void setTabVideoBadge(int badge) {
        mSharePreferences.edit().putInt(PRE_TAB_VIDEO_BADGE, badge).apply();
    }

    public int getTabOnlineBadge() {
        return mSharePreferences.getInt(PRE_TAB_ONLINE_BADGE, 0);
    }

    public void setTabOnlineBadge(int badge) {
        mSharePreferences.edit().putInt(PRE_TAB_ONLINE_BADGE, badge).apply();
    }

    public long getRetentionTime() {
        return mSharePreferences.getLong(PRE_RETENTION_TIME, 0);
    }

    public void setRetentionTime(long time) {
        mSharePreferences.edit().putLong(PRE_RETENTION_TIME, time).apply();
    }

    public void setHistory(ArrayList<WebViewData> listHistory) {
        mSharePreferences.edit().putString(PRE_HISTORY, new Gson().toJson(listHistory)).apply();
    }

    public boolean isFirstTime() {
        return mSharePreferences.getBoolean(PRE_FIRST_TIME, true);
    }

    public void setFirstTime(boolean isFirstTime) {
        mSharePreferences.edit().putBoolean(PRE_FIRST_TIME, isFirstTime).apply();
    }

    public ArrayList<WebViewData> getHistory() {
        String listHistory = mSharePreferences.getString(PRE_HISTORY, "");
        if (listHistory.length() > 0) {
            return new Gson().fromJson(listHistory, new TypeToken<ArrayList<WebViewData>>() {
            }.getType());
        } else {
            return new ArrayList<>();
        }
    }

    public void setBookmark(ArrayList<WebViewData> listBookmark) {
        mSharePreferences.edit().putString(PRE_BOOKMARK, new Gson().toJson(listBookmark)).apply();
    }

    public ArrayList<WebViewData> getBookmark() {
        String listBookmark = mSharePreferences.getString(PRE_BOOKMARK, "");
        if (listBookmark.length() > 0) {
            return new Gson().fromJson(listBookmark, new TypeToken<ArrayList<WebViewData>>() {
            }.getType());
        } else {
            return new ArrayList<>();
        }
    }

    public void setProgress(ArrayList<ProgressInfo> listProgress) {
        mSharePreferences.edit().putString(PRE_PROGRESS, new Gson().toJson(listProgress)).apply();
    }

    public ArrayList<ProgressInfo> getProgress() {
        String listProgress = mSharePreferences.getString(PRE_PROGRESS, "");
        if (listProgress.length() > 0) {
            return new Gson().fromJson(listProgress, new TypeToken<ArrayList<ProgressInfo>>() {
            }.getType());
        } else {
            return new ArrayList<>();
        }
    }

    public void setSavedVideos(ArrayList<Video> videos) {
        mSharePreferences.edit().putString(PRE_VIDEO_SAVED, new Gson().toJson(videos)).apply();
    }

    public ArrayList<Video> getSavedVideos() {
        String videos = mSharePreferences.getString(PRE_VIDEO_SAVED, "");
        if (videos.length() > 0) {
            return new Gson().fromJson(videos, new TypeToken<ArrayList<Video>>() {
            }.getType());
        } else {
            return new ArrayList<>();
        }
    }
}