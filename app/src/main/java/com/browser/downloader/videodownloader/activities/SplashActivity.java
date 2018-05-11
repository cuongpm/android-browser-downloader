package com.browser.downloader.videodownloader.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.adview.AppLovinInterstitialAdDialog;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinSdk;
import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.data.AdType;
import com.browser.downloader.videodownloader.data.ConfigData;
import com.browser.downloader.videodownloader.databinding.ActivitySplashBinding;
import com.browser.downloader.videodownloader.service.DataService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vd.core.common.Constant;
import vd.core.common.PreferencesManager;
import vd.core.util.AdUtil;

public class SplashActivity extends BaseActivity {

    ActivitySplashBinding mBinding;

    private boolean isLoadAdFailed;

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
        // Init AppLovin
        AppLovinSdk.initializeSdk(getApplicationContext());
//        AppLovinSdk.getInstance(getApplicationContext()).getSettings().setTestAdsEnabled(true);

        // Init Admob
        MobileAds.initialize(this, Constant.AD_APP_ID);

        // Load static data
        loadconfigData();
    }

    private void startMainActivity() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    private void loadconfigData() {
        DataService.Factory.getInstance().getconfigData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(configData -> runOnUiThread(() -> {
                    PreferencesManager.getInstance(this).setConfigData(configData);
                    // Load interstitial ad
                    loadInterstitialAd();
                }), throwable -> runOnUiThread(() -> {
                    throwable.printStackTrace();
                    // Load interstitial ad
                    loadInterstitialAd();
                }));
    }

    private void loadInterstitialAd() {

        // Get config
        ConfigData configData = mPreferenceManager.getConfigData();

        // Check show ad
        boolean isShowAd = configData == null ? true : configData.isShowAdSplash();

        // Check ad type
        int adType = configData == null ? AdType.ADMOB.getValue() : configData.getShowAdSplashType();

        // Show ad
        if (isShowAd) {
            if (adType == AdType.ADMOB.getValue()) {
                // Admob type
                showInterstitialAdmob();
            } else if (adType == AdType.APPLOVIN.getValue()) {
                // AppLovin type
                showInterstitialAppLovin();
            } else {
                // Open home screen
                startMainActivity();
            }
        } else {
            // Open home screen
            startMainActivity();
        }
    }

    private void showInterstitialAdmob() {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        AdUtil.loadInterstitialAd(interstitialAd, new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // Show ad
                interstitialAd.show();
                // google analytics
                trackEvent(getString(R.string.app_name), getString(R.string.action_show_ad_splash), "Admob");
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                // Load admob failed -> load Applovin
                if (isLoadAdFailed) {
                    // Ad loaded failed second time -> Open home screen
                    startMainActivity();
                } else {
                    // Ad loaded failed first time -> load other ad
                    showInterstitialAppLovin();
                    isLoadAdFailed = true;
                }

            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                // Open home screen
                startMainActivity();
            }
        });
    }

    private void showInterstitialAppLovin() {
        AppLovinInterstitialAdDialog interstitialAd = AppLovinInterstitialAd.
                create(AppLovinSdk.getInstance(SplashActivity.this), SplashActivity.this);
        interstitialAd.setAdDisplayListener(new AppLovinAdDisplayListener() {
            @Override
            public void adDisplayed(AppLovinAd appLovinAd) {
            }

            @Override
            public void adHidden(AppLovinAd appLovinAd) {
                // Open home screen
                startMainActivity();
            }
        });
        AppLovinSdk.getInstance(this).getAdService().loadNextAd(AppLovinAdSize.INTERSTITIAL, new AppLovinAdLoadListener() {
            @Override
            public void adReceived(AppLovinAd ad) {
                // Show ad
                runOnUiThread(() -> {
                    interstitialAd.showAndRender(ad);
                    // google analytics
                    trackEvent(getString(R.string.app_name), getString(R.string.action_show_ad_splash), "AppLovin");
                });
            }

            @Override
            public void failedToReceiveAd(int errorCode) {
                // Load applovin failed -> load admob
                runOnUiThread(() -> {
                    if (isLoadAdFailed) {
                        // Ad loaded failed second time -> Open home screen
                        startMainActivity();
                    } else {
                        // Ad loaded failed first time -> load other ad
                        showInterstitialAdmob();
                        isLoadAdFailed = true;
                    }
                });
            }
        });
    }
}
