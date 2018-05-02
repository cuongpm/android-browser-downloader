package vd.core.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.browser.downloader.videodownloader.data.ConfigData;

import vd.core.common.Constant;
import vd.core.common.PreferencesManager;

public class AppUtil {

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

}
