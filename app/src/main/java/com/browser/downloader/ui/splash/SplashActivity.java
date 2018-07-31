package com.browser.downloader.ui.splash;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;

import com.browser.core.R;
import com.browser.core.databinding.ActivitySplashBinding;
import com.browser.core.mvp.BaseTiActivity;
import com.browser.downloader.data.local.PreferencesManager;
import com.browser.downloader.data.model.ConfigData;
import com.browser.downloader.ui.home.MainActivity;
import com.google.android.gms.ads.InterstitialAd;

import butterknife.ButterKnife;

public class SplashActivity extends BaseTiActivity<SplashPresenter, SplashView> implements SplashView {

    ActivitySplashBinding mBinding;

    private InterstitialAd mInterstitialAd;

    @NonNull
    @Override
    public SplashPresenter providePresenter() {
        return new SplashPresenter();
    }

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
        mInterstitialAd = new InterstitialAd(this);

        // Load static data
        getPresenter().loadConfigData();
    }

    @Override
    public void loadConfigDone(ConfigData configData) {
        PreferencesManager.getInstance(this).setConfigData(configData);
        // Load interstitial ad
        getPresenter().loadInterstitialAd(mInterstitialAd);
    }

    @Override
    public void loadConfigFailed() {
        // Load interstitial ad
        getPresenter().loadInterstitialAd(mInterstitialAd);
    }

    @Override
    public void openHomeScreen() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void showAd() {
        mInterstitialAd.show();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_show_ad_splash), "Admob");
    }

}
