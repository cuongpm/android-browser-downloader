package core.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.browser.downloader.videodownloader.data.model.StaticData;
import com.google.gson.Gson;

public class PreferencesManager {

    private static final String PRE_KEY = "PRE_KEY";
    private static final String PRE_STATIC_DATA = "PRE_STATIC_DATA";
    private static final String PRE_RATE_APP = "PRE_RATE_APP";

    private static PreferencesManager instance = null;

    private final SharedPreferences mSharePreferences;

    /**
     * Constructor
     */
    private PreferencesManager(Context context) {
        mSharePreferences = context.getSharedPreferences(PRE_KEY, Context.MODE_PRIVATE);
    }

    /**
     * Get class instance
     */
    public static PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context);
        }
        return instance;
    }

    public StaticData getStaticData() {
        try {
            String data = mSharePreferences.getString(PRE_STATIC_DATA, "");
            return TextUtils.isEmpty(data) ? null : new Gson().fromJson(data, StaticData.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setStaticData(StaticData staticData) {
        if (staticData != null)
            mSharePreferences.edit().putString(PRE_STATIC_DATA, new Gson().toJson(staticData)).apply();
    }

    public boolean isRateApp() {
        return mSharePreferences.getBoolean(PRE_RATE_APP, false);
    }

    public void setRateApp(boolean isRateApp) {
        mSharePreferences.edit().putBoolean(PRE_RATE_APP, isRateApp).apply();
    }

}