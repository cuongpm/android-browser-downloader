package com.browser.downloader.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.browser.core.R;

public class UpdateDialog {

    static Dialog mDialog;

    static public Dialog getDialog(Context context, String version, View.OnClickListener onClickListener) {
        return create(context, version, onClickListener);
    }

    static private Dialog create(Context context, String version, View.OnClickListener onClickListener) {
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_update);

        Button button = mDialog.findViewById(R.id.btn_ok);
        button.setOnClickListener(onClickListener);

        TextView textView = mDialog.findViewById(R.id.tv_content);
        textView.setText(String.format(context.getString(R.string.message_update), version));

        return mDialog;
    }
}