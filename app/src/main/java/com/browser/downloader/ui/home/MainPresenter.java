package com.browser.downloader.ui.home;

import com.browser.core.mvp.BaseTiPresenter;
import com.browser.core.util.AdUtil;
import com.browser.downloader.AppApplication;
import com.browser.downloader.data.local.PreferencesManager;
import com.browser.downloader.data.model.ConfigData;
import com.browser.downloader.data.model.ProgressInfo;
import com.browser.downloader.data.model.Video;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

import javax.inject.Inject;

public class MainPresenter extends BaseTiPresenter<MainView> {

    @Inject
    PreferencesManager mPreferencesManager;

    public MainPresenter() {
        AppApplication.getInstance().getComponent().inject(this);
    }

    public ArrayList<Video> getSavedVideos() {
        return mPreferencesManager.getSavedVideos();
    }

    public ArrayList<ProgressInfo> getProgress() {
        return mPreferencesManager.getProgress();
    }

    public ConfigData getConfigData() {
        return mPreferencesManager.getConfigData();
    }

    public int getTabVideoBadge() {
        return mPreferencesManager.getTabVideoBadge();
    }

    public void setTabVideoBadge(int badge) {
        mPreferencesManager.setTabVideoBadge(badge);
    }

    public int getTabOnlineBadge() {
        return mPreferencesManager.getTabOnlineBadge();
    }

    public void setTabOnlineBadge(int badge) {
        mPreferencesManager.setTabOnlineBadge(badge);
    }

    public boolean isRateApp() {
        return mPreferencesManager.isRateApp();
    }

    public void setRateApp(boolean isRateApp) {
        mPreferencesManager.setRateApp(isRateApp);
    }

    public boolean isFirstTime() {
        return mPreferencesManager.isFirstTime();
    }

    public void setFirstTime(boolean isFirstTime) {
        mPreferencesManager.setFirstTime(isFirstTime);
    }

    public void loadInterstitialAd(InterstitialAd interstitialAd) {
        // Get config
        ConfigData configData = mPreferencesManager.getConfigData();

        // Check show ad
        boolean isShowAd = configData == null ? true : configData.isShowAdApp();

        // Show ad
        if (isShowAd) {
            AdUtil.loadInterstitialAd(interstitialAd, null);
        }
    }

}
