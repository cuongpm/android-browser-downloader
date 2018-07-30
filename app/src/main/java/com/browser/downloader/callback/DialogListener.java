package com.browser.downloader.callback;

import android.app.Dialog;

public interface DialogListener {

    void onPositiveButton(Dialog dialog);

    void onNegativeButton(Dialog dialog);
}
