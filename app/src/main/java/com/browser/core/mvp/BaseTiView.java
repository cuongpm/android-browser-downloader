package com.browser.core.mvp;

import android.view.View;

import net.grandcentrix.thirtyinch.TiView;
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;

public interface BaseTiView extends TiView {

    @CallOnMainThread
    void showLoading();

    @CallOnMainThread
    void showLoading(boolean cancelable);

    @CallOnMainThread
    void hideLoading();

    @CallOnMainThread
    void showAlert(String message);

    @CallOnMainThread
    void showAlert(String message, View.OnClickListener callback);

    @CallOnMainThread
    void showAlert(String title, String message, View.OnClickListener callback);

    @CallOnMainThread
    void showAlertRetry(String message, View.OnClickListener callback);

    @CallOnMainThread
    void showError(Throwable throwable, Class clazz);

    boolean isNetworkAvailable();
}
