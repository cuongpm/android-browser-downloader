package com.browser.downloader.videodownloader.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.databinding.ActivityMainBinding;

import butterknife.ButterKnife;
import butterknife.OnClick;
import core.common.PreferencesManager;
import core.util.AdUtil;
import core.util.IntentUtil;

public class MainActivity extends BaseActivity {

    ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ButterKnife.bind(this);
        initUI();

        // google analytics
        trackEvent(getResources().getString(R.string.app_name), getString(R.string.screen_home), "");

        // Show ad banner
        AdUtil.showBanner(this, mBinding.layoutBanner);
    }

    @Override
    public void onResume() {
        trackView(getString(R.string.screen_home));
        super.onResume();
    }

    private void initUI() {
    }

    @OnClick(R.id.tv_browser)
    public void clickBrowser() {
        startActivity(new Intent(this, BrowserActivity.class));
    }

    @OnClick(R.id.tv_video)
    public void clickVideo() {
        startActivity(new Intent(this, VideoActivity.class));
    }

    @OnClick(R.id.tv_rate_us)
    public void clickRateUs() {
        PreferencesManager.getInstance(this).setRateApp(true);
        IntentUtil.openGooglePlay(this, getPackageName());
        // google analytics
        trackEvent(getResources().getString(R.string.app_name), getString(R.string.action_rate_us), "");
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.rate_app))
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    PreferencesManager.getInstance(this).setRateApp(true);
                    IntentUtil.openGooglePlay(MainActivity.this, getPackageName());
                    // google analytics
                    trackEvent(getResources().getString(R.string.app_name), getString(R.string.action_rate_us_exit_app), "");
                })
                .setNegativeButton("EXIT", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    finish();
                })
                .show();
    }
}