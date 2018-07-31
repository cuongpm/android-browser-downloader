package com.browser.downloader.ui.videoplayer;

import com.browser.core.mvp.BaseTiPresenter;
import com.browser.downloader.AppApplication;
import com.browser.downloader.data.local.PreferencesManager;

import javax.inject.Inject;

public class VideoPlayerPresenter extends BaseTiPresenter<VideoPlayerView> {

    @Inject
    PreferencesManager mPreferencesManager;

    public VideoPlayerPresenter() {
        AppApplication.getInstance().getComponent().inject(this);
    }

    public void setRateApp(boolean isRateApp) {
        mPreferencesManager.setRateApp(isRateApp);
    }
}
