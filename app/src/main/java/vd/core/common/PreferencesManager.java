package vd.core.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.browser.downloader.videodownloader.data.ConfigData;
import com.google.gson.Gson;

public class PreferencesManager {

    private static final String PRE_KEY = "PRE_KEY";
    private static final String PRE_CONFIG_DATA = "PRE_CONFIG_DATA";
    private static final String PRE_RATE_APP = "PRE_RATE_APP";

    private static PreferencesManager instance = null;

    private final SharedPreferences mSharePreferences;

    private PreferencesManager(Context context) {
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

}