package com.browser.core.mvp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import com.browser.core.R;
import com.browser.core.ui.dialog.DialogAction;
import com.browser.core.util.RestApiUtil;
import com.browser.downloader.data.local.Constant;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import net.grandcentrix.thirtyinch.TiActivity;

import github.nisrulz.easydeviceinfo.base.EasyNetworkMod;
import timber.log.Timber;

public abstract class BaseTiActivity<P extends BaseTiPresenter<V>, V extends BaseTiView>
        extends TiActivity<P, V> implements BaseTiView {

    private ProgressDialog mLoading;

    private Tracker mTracker;

    DialogAction mDialogAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i(getClass().getName() + ".onCreate");
        initBase();
    }

    private void initBase() {
        mLoading = new ProgressDialog(this, R.style.ProgressDialogDim);

        // Init GA
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        mTracker = analytics.newTracker(Constant.UA_ID);
    }

    public void trackView(String screenName) {
        mTracker.setScreenName(screenName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void trackEvent(String category, String action, String label) {
        mTracker.send(new HitBuilders.EventBuilder().setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    @Override
    public void showLoading() {
        Timber.i(getClass().getName() + ".showLoading(");
        if (mLoading != null)
            mLoading.show();
    }

    @Override
    public void showLoading(boolean cancelable) {
        Timber.i(getClass().getName() + ".showLoading(cancelable=" + cancelable + ")");
        if (mLoading != null) {
            mLoading.setCancelable(cancelable);
            mLoading.show();
        }
    }

    @Override
    public void hideLoading() {
        Timber.i(getClass().getName() + ".hideLoading");
        if (mLoading != null)
            mLoading.dismiss();
    }

    @Override
    public void showAlert(String message) {
        Timber.i(getClass().getName() + ".showAlert(s)");
        new DialogAction.Builder(this).message(message).positive().build().show();
    }

    @Override
    public void showAlert(String message, View.OnClickListener callback) {
        Timber.i(getClass().getName() + ".showAlert(s,c)");
        new DialogAction.Builder(this).message(message).positive(callback).build().show();
    }

    @Override
    public void showAlert(String title, String message, View.OnClickListener callback) {
        Timber.i(getClass().getName() + ".showAlert(s,s,c)");
        if (mDialogAction != null) {
            mDialogAction.dismiss();
        }
        mDialogAction = new DialogAction.Builder(this).title(title).message(message).positive(callback).build();
        mDialogAction.show();
    }

    @Override
    public void showAlertRetry(String message, View.OnClickListener callback) {
        Timber.i(getClass().getName() + ".showAlertRetry");
        new DialogAction.Builder(this).message(message).retry(callback).build().show();
    }

    @Override
    public void showError(Throwable throwable, Class clazz) {
        Timber.i(getClass().getName() + ".showError");
        Timber.e(throwable);
        hideLoading();
        showAlert(RestApiUtil.getError(throwable, clazz));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.i(getClass().getName() + ".onDestroy");
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.i(getClass().getName() + ".onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.i(getClass().getName() + ".onPause");
    }

    @Override
    public boolean isNetworkAvailable() {
        EasyNetworkMod networkMod = new EasyNetworkMod(getApplicationContext());
        boolean isNetworkAvailable = networkMod.isNetworkAvailable();
        Timber.i("isNetworkAvailable " + isNetworkAvailable);
        return isNetworkAvailable;
    }
}
