package core.util;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.browser.downloader.videodownloader.data.model.StaticData;
import com.browser.downloader.videodownloader.data.model.Video;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import core.common.Constant;
import core.common.PreferencesManager;

public class AppUtil {

    public static void downloadVideo(Context context, Video video) {
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
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        StaticData staticData = PreferencesManager.getInstance(context).getStaticData();
        String server1 = staticData != null && !TextUtils.isEmpty(staticData.getServer1()) ? staticData.getServer1() : Constant.URL_SERVER_1;
        String server2 = staticData != null && !TextUtils.isEmpty(staticData.getServer2()) ? staticData.getServer2() : Constant.URL_SERVER_2;
        String url = String.format(hour >= 0 && hour <= 12 ? server1 : server2, data);

        return url;
    }

}
