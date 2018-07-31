package com.browser.downloader.ui.bookmark;

import com.browser.core.mvp.BaseTiPresenter;
import com.browser.downloader.AppApplication;
import com.browser.downloader.data.local.PreferencesManager;
import com.browser.downloader.data.model.WebViewData;

import java.util.ArrayList;

import javax.inject.Inject;

public class BookmarkPresenter extends BaseTiPresenter<BookmarkView> {

    @Inject
    PreferencesManager mPreferencesManager;

    public BookmarkPresenter() {
        AppApplication.getInstance().getComponent().inject(this);
    }

    public ArrayList<WebViewData> getBookmark() {
        return mPreferencesManager.getBookmark();
    }

}
