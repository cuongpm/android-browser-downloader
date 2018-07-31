package com.browser.downloader.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.Button;

import com.browser.core.R;

public class GuidelineDialog {

    static Dialog mDialog;

    static public Dialog getDialog(Context context) {
        return create(context);
    }

    static private Dialog create(Context context) {
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_guide);

        Button button = mDialog.findViewById(R.id.btn_ok);
        button.setOnClickListener(v -> mDialog.dismiss());

        return mDialog;
    }
}