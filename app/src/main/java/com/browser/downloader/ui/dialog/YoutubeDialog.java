package com.browser.downloader.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.browser.core.R;

public class YoutubeDialog {

    static Dialog mDialog;

    static public Dialog getDialog(Context context, boolean isYoutube) {
        return create(context, isYoutube);
    }

    static private Dialog create(Context context, boolean isYoutube) {
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_youtube);

        TextView tvTitle = mDialog.findViewById(R.id.tv_title);
        TextView tvContent = mDialog.findViewById(R.id.tv_content);

        tvTitle.setText(String.format(context.getString(R.string.youtube_not_support_title), isYoutube ? "Youtube" : "This site"));
        tvContent.setText(String.format(context.getString(R.string.youtube_not_support), isYoutube ? "Youtube" : "this site"));

        Button button = mDialog.findViewById(R.id.btn_ok);
        button.setOnClickListener(v -> mDialog.dismiss());

        return mDialog;
    }
}