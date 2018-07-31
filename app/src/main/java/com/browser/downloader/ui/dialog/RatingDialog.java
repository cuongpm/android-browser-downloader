package com.browser.downloader.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;

import com.browser.core.R;
import com.browser.downloader.callback.DialogListener;
import com.hsalf.smilerating.SmileRating;

public class RatingDialog {

    static Dialog mDialog;

    static public Dialog getDialog(Context context, DialogListener dialogListener) {
        return create(context, dialogListener);
    }

    static private Dialog create(Context context, final DialogListener dialogListener) {
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_rating);

        TextView btnRate = mDialog.findViewById(R.id.btn_rate);
        TextView btnLater = mDialog.findViewById(R.id.btn_later);
        SmileRating smileRating = mDialog.findViewById(R.id.smile_rating);

        smileRating.setSelectedSmile(SmileRating.GREAT);

        btnRate.setOnClickListener(v -> {
            if (dialogListener != null) dialogListener.onPositiveButton(mDialog);

        });
        btnLater.setOnClickListener(v -> {
            if (dialogListener != null) dialogListener.onNegativeButton(mDialog);

        });

        return mDialog;
    }
}