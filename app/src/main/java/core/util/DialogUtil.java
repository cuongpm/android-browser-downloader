package core.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.browser.downloader.videodownloader.R;

public class DialogUtil {

    private static Dialog simpleProgressDialog = null;

    public static void showSimpleProgressDialog(Context context) {
        if (simpleProgressDialog != null) {
            closeProgressDialog();
        }

        if (context != null) {
            simpleProgressDialog = new Dialog(context);
            simpleProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            simpleProgressDialog.setContentView(R.layout.dialog_progress_simple);
            setDialogOpacity(simpleProgressDialog, Color.WHITE, 0);
            simpleProgressDialog.setCancelable(false);
            simpleProgressDialog.show();
        }
    }

    public static void closeProgressDialog() {

        if (simpleProgressDialog != null) {
            try {
                simpleProgressDialog.cancel();
                simpleProgressDialog = null;
            } catch (Exception e) {
                // Handle exception: do nothing here
            }
        }
    }

    public static boolean isProgressShowing() {
        return (simpleProgressDialog != null && simpleProgressDialog.isShowing());
    }

    public static void setDialogOpacity(Dialog dialog, int bgColor, int alpha) {
        ColorDrawable bgDrawable = new ColorDrawable(bgColor);
        bgDrawable.setAlpha(alpha);
        dialog.getWindow().setBackgroundDrawable(bgDrawable);
    }

    public static void showAlertDialog(Context context, String title, String message,
                                       OnClickListener onClickListener) {
        AlertDialog.Builder arAlertDialog = new AlertDialog.Builder(context);
        arAlertDialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, onClickListener)
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
