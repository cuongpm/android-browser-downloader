package vd.core.util;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.browser.downloader.videodownloader.data.ConfigData;
import com.browser.downloader.videodownloader.data.Video;

import java.io.File;

import vd.core.common.Constant;
import vd.core.common.PreferencesManager;

public class AppUtil {

    public static boolean isDownloadVideo = false;

    public static void downloadVideo(Context context, Video video) {
        isDownloadVideo = true;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(video.getUrl()));

        File localFile = FileUtil.getFolderDir();
        if (!localFile.exists() && !localFile.mkdirs()) return;

        request.setDestinationInExternalPublicDir(FileUtil.FOLDER_NAME, video.getFileName());
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        dm.enqueue(request);
    }

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
