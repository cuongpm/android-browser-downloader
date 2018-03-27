package core.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

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

    public static void shareLink(Context context, String link) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + link));
        context.startActivity(Intent.createChooser(intent, "Share via:"));
    }
}
