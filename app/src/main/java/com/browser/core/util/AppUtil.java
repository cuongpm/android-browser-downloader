package com.browser.core.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.browser.downloader.data.local.Constant;
import com.browser.downloader.data.local.PreferencesManager;
import com.browser.downloader.data.model.ConfigData;

import timber.log.Timber;

public class AppUtil {

    public final static int DAY_MILISECONDS = 86400000;

    public static String buildUrl(Context context, String data) {
        ConfigData configData = PreferencesManager.getInstance(context).getConfigData();
        String server = configData != null && !TextUtils.isEmpty(configData.getParserServer()) ? configData.getParserServer() : Constant.PARSER_SERVER;
        return String.format(server, data);
    }

    public static void copyClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied link", text);
        clipboard.setPrimaryClip(clip);
    }

    public static void clearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    public static String getRetentionTime(Context context) {

        PreferencesManager preferencesManager = PreferencesManager.getInstance(context);
        long startTime = preferencesManager.getRetentionTime();
        if (startTime == 0) {
            preferencesManager.setRetentionTime(System.currentTimeMillis());
            return "new";
        } else {
            int numberOfDays = (int) ((System.currentTimeMillis() - startTime) / DAY_MILISECONDS);
            if (numberOfDays <= 1) {
                return "1 day";
            } else if (numberOfDays < 7) {
                return numberOfDays + " days";
            } else if (numberOfDays < 30) {
                int numberOfWeeks = numberOfDays / 7;
                return numberOfWeeks + (numberOfWeeks == 1 ? " week" : " weeks");
            } else {
                int numberOfMonths = numberOfDays / 30;
                return numberOfMonths + (numberOfMonths == 1 ? " month" : " months");
            }
        }
    }

    public static String getString(int id) {
        Context context = GlobalContext.getContext();
        if (context != null) {
            return context.getResources().getString(id);
        } else {
            Timber.e("Global context is null");
            return "";
        }
    }

}
