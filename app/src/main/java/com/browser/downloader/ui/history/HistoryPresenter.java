package com.browser.downloader.ui.history;

import com.browser.core.mvp.BaseTiPresenter;
import com.browser.downloader.AppApplication;
import com.browser.downloader.data.local.PreferencesManager;
import com.browser.downloader.data.model.WebViewData;

import java.util.ArrayList;

import javax.inject.Inject;

public class HistoryPresenter extends BaseTiPresenter<HistoryView> {

    @Inject
    PreferencesManager mPreferencesManager;

    public HistoryPresenter() {
        AppApplication.getInstance().getComponent().inject(this);
    }

    public ArrayList<WebViewData> getHistory() {
        return mPreferencesManager.getHistory();
    }
}
