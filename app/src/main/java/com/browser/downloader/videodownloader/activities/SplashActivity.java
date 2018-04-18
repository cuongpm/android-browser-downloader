package com.browser.downloader.videodownloader.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.data.ConfigData;
import com.browser.downloader.videodownloader.service.DataService;
import com.browser.downloader.videodownloader.databinding.ActivitySplashBinding;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vd.core.common.Constant;
import vd.core.common.PreferencesManager;
import vd.core.util.AdUtil;
import vd.core.util.DialogUtil;

public class SplashActivity extends BaseActivity {

    ActivitySplashBinding mBinding;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        ButterKnife.bind(this);
        initUI();

        // Load static data
//        loadconfigData();

        // Init Admob
        MobileAds.initialize(this, Constant.AD_APP_ID);
    }

    private void initUI() {
    }

    private void loadInterstitialAd() {
        // Check show ad
        ConfigData configData = PreferencesManager.getInstance(this).getConfigData();
        boolean isShowAd = configData == null ? true : configData.isShowAdHome();
        if (isShowAd) {
            mInterstitialAd = new InterstitialAd(this);
            AdUtil.showInterstitialAd(mInterstitialAd, new AdListener() {
                @Override
                public void onAdLoaded() {
                    DialogUtil.closeProgressDialog();
                    super.onAdLoaded();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    DialogUtil.closeProgressDialog();
                    super.onAdFailedToLoad(i);
                }

            });
        } else {
            DialogUtil.closeProgressDialog();
        }
    }

    private void showInterstitlaAd() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            // google analytics
            trackEvent(getResources().getString(R.string.app_name), getString(R.string.action_show_ad_home), "");
        }
    }

    @OnClick(R.id.btn_done)
    public void clickDone() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        showInterstitlaAd();
        finish();
    }

    private void loadconfigData() {
        DataService.Factory.getInstance().getconfigData()
                .doOnSubscribe(() -> runOnUiThread(() -> DialogUtil.showSimpleProgressDialog(this)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(configData -> runOnUiThread(() -> {
                    PreferencesManager.getInstance(this).setConfigData(configData);
                    // Load ad interstitial
                    loadInterstitialAd();
                }), throwable -> runOnUiThread(() -> {
                    throwable.printStackTrace();
                    // Load ad interstitial
                    loadInterstitialAd();
                }));
    }

}
