package com.browser.downloader.ui.splash;

import com.browser.core.mvp.BaseTiView;
import com.browser.downloader.data.model.ConfigData;

public interface SplashView extends BaseTiView {

    void loadConfigDone(ConfigData configData);

    void loadConfigFailed();

    void openHomeScreen();

    void showAd();

}
