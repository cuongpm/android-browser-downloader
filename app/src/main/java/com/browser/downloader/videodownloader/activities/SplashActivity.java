package com.browser.downloader.videodownloader.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.databinding.ActivitySplashBinding;
import com.browser.downloader.videodownloader.service.DataService;

import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vd.core.common.PreferencesManager;

public class SplashActivity extends BaseActivity {

    ActivitySplashBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        ButterKnife.bind(this);
        initUI();
    }

    private void initUI() {
        // Load static data
        loadconfigData();
    }

    private void loadconfigData() {
        DataService.Factory.getInstance().getconfigData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(configData -> runOnUiThread(() -> {
                    PreferencesManager.getInstance(this).setConfigData(configData);
                    startMainActivity();
                }), throwable -> runOnUiThread(() -> {
                    throwable.printStackTrace();
                    startMainActivity();
                }));
    }

    private void startMainActivity() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            overridePendingTransition(R.anim.enter_from_right, 0);
            finish();
        }, 2000);
    }

}
