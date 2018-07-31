package com.browser.downloader.ui.videoplayer;

import com.browser.core.mvp.BaseTiPresenter;
import com.browser.downloader.AppApplication;
import com.browser.downloader.data.local.PreferencesManager;

import javax.inject.Inject;

public class OfflinePresenter extends BaseTiPresenter<OfflineView> {

    @Inject
    PreferencesManager mPreferencesManager;

    public OfflinePresenter() {
        AppApplication.getInstance().getComponent().inject(this);
    }

}
