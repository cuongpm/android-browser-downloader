package com.browser.downloader.videodownloader.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConfigData {

    @SerializedName("is_show_ad_splash")
    @Expose
    private boolean isShowAdSplash = true;

    @SerializedName("is_show_ad_browser")
    @Expose
    private boolean isShowAdBrowser = true;

    @SerializedName("is_show_ad_app")
    @Expose
    private boolean isShowAdApp = true;

    @SerializedName("is_show_all_pages")
    @Expose
    private boolean isShowAllPages;

    @SerializedName("total_action_show_ad")
    @Expose
    private int totalActionShowAd;

    @SerializedName("show_ad_splash_type")
    @Expose
    private int showAdSplashType;

    @SerializedName("show_ad_browser_type")
    @Expose
    private int showAdBrowserType;

    @SerializedName("show_ad_app_type")
    @Expose
    private int showAdAppType;

    @SerializedName("app_version")
    @Expose
    private String appVersion;

    @SerializedName("parser_server")
    @Expose
    private String parserServer;

    @SerializedName("pages_supported")
    @Expose
    private List<PagesSupported> pagesSupported;

    @SerializedName("pages_general")
    @Expose
    private List<String> pagesGeneral;

    public boolean isShowAdSplash() {
        return isShowAdSplash;
    }

    public void setShowAdSplash(boolean showAdSplash) {
        isShowAdSplash = showAdSplash;
    }

    public boolean isShowAdBrowser() {
        return isShowAdBrowser;
    }

    public void setShowAdBrowser(boolean showAdBrowser) {
        isShowAdBrowser = showAdBrowser;
    }

    public boolean isShowAdApp() {
        return isShowAdApp;
    }

    public void setShowAdApp(boolean showAdApp) {
        isShowAdApp = showAdApp;
    }

    public boolean isShowAllPages() {
        return isShowAllPages;
    }

    public void setShowAllPages(boolean showAllPages) {
        isShowAllPages = showAllPages;
    }

    public int getTotalActionShowAd() {
        return totalActionShowAd;
    }

    public void setTotalActionShowAd(int totalActionShowAd) {
        this.totalActionShowAd = totalActionShowAd;
    }

    public int getShowAdSplashType() {
        return showAdSplashType;
    }

    public void setShowAdSplashType(int showAdSplashType) {
        this.showAdSplashType = showAdSplashType;
    }

    public int getShowAdBrowserType() {
        return showAdBrowserType;
    }

    public void setShowAdBrowserType(int showAdBrowserType) {
        this.showAdBrowserType = showAdBrowserType;
    }

    public int getShowAdAppType() {
        return showAdAppType;
    }

    public void setShowAdAppType(int showAdAppType) {
        this.showAdAppType = showAdAppType;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getParserServer() {
        return parserServer;
    }

    public void setParserServer(String parserServer) {
        this.parserServer = parserServer;
    }

    public List<PagesSupported> getPagesSupported() {
        return pagesSupported;
    }

    public void setPagesSupported(List<PagesSupported> pagesSupported) {
        this.pagesSupported = pagesSupported;
    }

    public List<String> getPagesGeneral() {
        return pagesGeneral;
    }

    public void setPagesGeneral(List<String> pagesGeneral) {
        this.pagesGeneral = pagesGeneral;
    }

}
