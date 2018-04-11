package vd.core.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.browser.downloader.videodownloader.R;

public class IntentUtil {

    public static void openGooglePlay(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + packageName));
        context.startActivity(intent);
    }

    public static void openGooglePlayDeveloper(Context context, String developer) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://search?q=pub:" + developer));
        context.startActivity(intent);
    }

    public static void shareVideo(Context context, String link) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + link));

        if (intent.resolveActivityInfo(context.getPackageManager(), 0) != null) {
            context.startActivity(Intent.createChooser(intent, "Share via:"));
        } else {
            Toast.makeText(context, context.getString(R.string.message_open_video), Toast.LENGTH_SHORT).show();
        }
    }

    public static void shareLink(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.setType("text/plain");
        context.startActivity(Intent.createChooser(intent, "Share with:"));
    }

    public static void openFolder(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "resource/*");

        if (intent.resolveActivityInfo(context.getPackageManager(), 0) != null) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, context.getString(R.string.message_open_folder), Toast.LENGTH_SHORT).show();
        }
    }
}
