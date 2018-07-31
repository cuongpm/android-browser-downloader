package com.browser.core.mvp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import com.browser.core.R;
import com.browser.core.ui.dialog.DialogAction;
import com.browser.core.util.RestApiUtil;
import com.browser.downloader.data.local.Constant;
import com.browser.downloader.ui.home.MainActivity;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import net.grandcentrix.thirtyinch.TiFragment;

import github.nisrulz.easydeviceinfo.base.EasyNetworkMod;
import timber.log.Timber;

public abstract class BaseTiFragment<P extends BaseTiPresenter<V>, V extends BaseTiView>
        extends TiFragment<P, V> implements BaseTiView {

    private ProgressDialog mLoading;

    private Tracker mTracker;

    protected MainActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i(getClass().getName() + ".onCreate");
        initBase();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                onBackPressed();
                return true;
            }
            return false;
        });
    }

    private void handleBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getActivity().onBackPressed();
        } else {
            onBackPressed();
        }
    }

    protected void onBackPressed() {
        getActivity().onBackPressed();
    }

    private void initBase() {
        mActivity = (MainActivity) getActivity();
        mLoading = new ProgressDialog(getActivity(), R.style.ProgressDialogDim);

        // Init GA
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(getContext());
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
        new DialogAction.Builder(getActivity()).message(message).positive().build().show();
    }

    @Override
    public void showAlert(String message, View.OnClickListener callback) {
        Timber.i(getClass().getName() + ".showAlert(s,c)");
        new DialogAction.Builder(getActivity()).message(message).positive(callback).build().show();
    }

    @Override
    public void showAlert(String title, String message, View.OnClickListener callback) {
        Timber.i(getClass().getName() + ".showAlert(s,s,c)");
        new DialogAction.Builder(getActivity()).title(title).message(message).positive(callback).build().show();
    }

    @Override
    public void showAlertRetry(String message, View.OnClickListener onClickListener) {
        Timber.i(getClass().getName() + ".showAlertRetry");
        new DialogAction.Builder(getActivity()).retry(onClickListener).build().show();
    }

    @Override
    public void showError(Throwable throwable, Class clazz) {
        Timber.i(getClass().getName() + ".showError");
        Timber.e(throwable);
        hideLoading();
        showAlert(RestApiUtil.getError(throwable, clazz));
    }

    @Override
    public boolean isNetworkAvailable() {
        EasyNetworkMod networkMod = new EasyNetworkMod(getContext());
        boolean isNetworkAvailable = networkMod.isNetworkAvailable();
        Timber.i("isNetworkAvailable " + isNetworkAvailable);
        return isNetworkAvailable;
    }

    public void setSupportActionBar(Toolbar toolbar) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    public void setDisplayHomeAsUpEnabled(boolean enabled) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(enabled);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoading != null && mLoading.isShowing()) {
            mLoading.dismiss();
            mLoading = null;
        }
        Timber.i(getClass().getName() + ".onDestroy");
    }

    /**
     * Return FragmentTransaction with animation
     *
     * @return
     */
    public FragmentTransaction getFragmentTransaction() {
        return getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
    }
}
