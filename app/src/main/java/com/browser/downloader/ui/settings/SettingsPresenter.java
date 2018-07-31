package com.browser.downloader.ui.settings;

import com.browser.core.mvp.BaseTiPresenter;
import com.browser.downloader.AppApplication;
import com.browser.downloader.data.local.PreferencesManager;
import com.browser.downloader.data.model.WebViewData;

import java.util.ArrayList;

import javax.inject.Inject;

public class SettingsPresenter extends BaseTiPresenter<SettingsView> {

    @Inject
    PreferencesManager mPreferencesManager;

    public SettingsPresenter() {
        AppApplication.getInstance().getComponent().inject(this);
    }

    public void setHistory(ArrayList<WebViewData> history) {
        mPreferencesManager.setHistory(history);
    }

    public void setBookmark(ArrayList<WebViewData> bookmark) {
        mPreferencesManager.setBookmark(bookmark);
    }
}
