package com.browser.downloader.ui.splash;

import com.browser.core.mvp.BaseTiPresenter;
import com.browser.core.util.AdUtil;
import com.browser.downloader.AppApplication;
import com.browser.downloader.data.local.PreferencesManager;
import com.browser.downloader.data.model.ConfigData;
import com.browser.downloader.data.remote.DataService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SplashPresenter extends BaseTiPresenter<SplashView> {

    @Inject
    DataService mDataService;

    @Inject
    PreferencesManager mPreferencesManager;

    public SplashPresenter() {
        AppApplication.getInstance().getComponent().inject(this);
    }

    public void loadConfigData() {
        manageSubscription(mDataService.getconfigData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(configData -> {
                    sendToView(view -> view.loadConfigDone(configData));
                }, throwable -> sendToView(view -> {
                    view.loadConfigFailed();
                })));
    }

    void loadInterstitialAd(InterstitialAd interstitialAd) {

        // Get config
        ConfigData configData = mPreferencesManager.getConfigData();

        // Check show ad
        boolean isShowAd = configData == null ? true : configData.isShowAdSplash();

        // Show ad
        if (isShowAd) {
            AdUtil.loadInterstitialAd(interstitialAd, new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    // Show ad
                    sendToView(view -> view.showAd());
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    // Ad loaded failed -> Open home screen
                    sendToView(view -> view.openHomeScreen());
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    // Open home screen
                    sendToView(view -> view.openHomeScreen());
                }
            });
        } else {
            // Open home screen
            sendToView(view -> view.openHomeScreen());
        }
    }


}
