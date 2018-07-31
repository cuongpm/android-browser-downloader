package com.browser.downloader.ui.home;

import android.text.TextUtils;
import android.webkit.WebView;

import com.browser.core.mvp.BaseTiPresenter;
import com.browser.downloader.AppApplication;
import com.browser.downloader.data.local.PreferencesManager;
import com.browser.downloader.data.model.ConfigData;
import com.browser.downloader.data.model.PagesSupported;
import com.browser.downloader.data.model.Suggestion;
import com.browser.downloader.data.model.SuggestionType;
import com.browser.downloader.data.model.Video;
import com.browser.downloader.data.model.WebViewData;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class BrowserPresenter extends BaseTiPresenter<BrowserView> {

    private ArrayList<WebViewData> mHistoryData;

    private ArrayList<WebViewData> mBookmarData;

    @Inject
    PreferencesManager mPreferencesManager;

    public BrowserPresenter() {
        AppApplication.getInstance().getComponent().inject(this);
    }

    public ArrayList<Video> getSavedVideos() {
        return mPreferencesManager.getSavedVideos();
    }

    public void setSavedVideos(ArrayList<Video> videos) {
        mPreferencesManager.setSavedVideos(videos);
    }

    public ConfigData getConfigData() {
        return mPreferencesManager.getConfigData();
    }

    public void saveWebViewHistory(WebView webView) {
        WebViewData webViewData = new WebViewData();
        String url = webView.getUrl();
        if (!TextUtils.isEmpty(url)) {
            webViewData.setUrl(url);
            String title = webView.getTitle();
            if (TextUtils.isEmpty(title)) {
                try {
                    title = url.split("/")[2];
                } catch (Exception e) {
                    e.printStackTrace();
                    title = url;
                }
            }
            webViewData.setTitle(title);
            getWebViewHistory().add(0, webViewData);
            mPreferencesManager.setHistory(getWebViewHistory());
        }
    }

    public ArrayList<WebViewData> getWebViewHistory() {
        if (mHistoryData == null) {
            mHistoryData = mPreferencesManager.getHistory();
        }
        return mHistoryData;
    }

    public void saveWebViewBookmark(WebView webView) {
        WebViewData webViewData = new WebViewData();
        String url = webView.getUrl();
        if (!TextUtils.isEmpty(url)) {
            webViewData.setUrl(url);
            String title = webView.getTitle();
            if (TextUtils.isEmpty(title)) {
                try {
                    title = url.split("/")[2];
                } catch (Exception e) {
                    e.printStackTrace();
                    title = url;
                }
            }
            webViewData.setTitle(title);
            getWebViewBookmark().add(0, webViewData);
            mPreferencesManager.setBookmark(getWebViewBookmark());
        }
    }

    public ArrayList<WebViewData> getWebViewBookmark() {
        if (mBookmarData == null) {
            mBookmarData = mPreferencesManager.getBookmark();
        }
        return mBookmarData;
    }

    public boolean isBookmarkLink(WebView webView) {
        for (WebViewData webViewData : getWebViewBookmark()) {
            if (webViewData.getUrl().equals(webView.getUrl())) {
                return true;
            }
        }

        return false;
    }

    public void removeBookmark(WebView webView) {
        for (WebViewData webViewData : getWebViewBookmark()) {
            if (webViewData.getUrl().equals(webView.getUrl())) {
                getWebViewBookmark().remove(webViewData);
                mPreferencesManager.setBookmark(getWebViewBookmark());
                return;
            }
        }
    }

    public List<Suggestion> addAllSupportedPages(String searchValue) {
        List<Suggestion> suggestionList = new ArrayList<>();

        // Add all supported pages
        ConfigData configData = mPreferencesManager.getConfigData();
        if (configData != null && configData.getPagesSupported() != null) {
            for (PagesSupported pagesSupported : configData.getPagesSupported()) {
                if (pagesSupported.getName().contains(searchValue.toLowerCase())) {
                    Suggestion suggestionWeb = new Suggestion();
                    suggestionWeb.setSuggestion(pagesSupported.getName());
                    suggestionWeb.setSuggestionType(SuggestionType.WEB.getValue());
                    suggestionList.add(suggestionWeb);
                }
            }
        }

        return suggestionList;
    }

    public List<Suggestion> addAllSuggestions(List<Suggestion> suggestionList, List<String> suggestions) {
        if (suggestions == null || suggestions.size() == 0) {
            return suggestionList;
        }

        // Add all suggestions
        for (String suggestion : suggestions) {
            Suggestion suggestionString = new Suggestion();
            suggestionString.setSuggestion(suggestion);
            suggestionString.setSuggestionType(SuggestionType.SUGGESTION.getValue());
            suggestionList.add(suggestionString);
        }

        return suggestionList;
    }

}
