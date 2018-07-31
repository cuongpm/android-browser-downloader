package com.browser.downloader.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConfigData {

    @SerializedName("is_show_ad_splash")
    @Expose
    private boolean isShowAdSplash = true;

    @SerializedName("is_show_ad_app")
    @Expose
    private boolean isShowAdApp = true;

    @SerializedName("is_show_ad_app_full")
    @Expose
    private boolean isShowAdAppFull = true;

    @SerializedName("is_update_app")
    @Expose
    private boolean isUpdateApp;

    @SerializedName("is_show_all_pages")
    @Expose
    private boolean isShowAllPages;

    @SerializedName("show_ad_splash_type")
    @Expose
    private int showAdSplashType;

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

    public boolean isShowAdApp() {
        return isShowAdApp;
    }

    public void setShowAdApp(boolean showAdApp) {
        isShowAdApp = showAdApp;
    }

    public boolean isShowAdAppFull() {
        return isShowAdAppFull;
    }

    public void setShowAdAppFull(boolean showAdAppFull) {
        isShowAdAppFull = showAdAppFull;
    }

    public boolean isUpdateApp() {
        return isUpdateApp;
    }

    public void setUpdateApp(boolean updateApp) {
        isUpdateApp = updateApp;
    }

    public boolean isShowAllPages() {
        return isShowAllPages;
    }

    public void setShowAllPages(boolean showAllPages) {
        isShowAllPages = showAllPages;
    }

    public int getShowAdSplashType() {
        return showAdSplashType;
    }

    public void setShowAdSplashType(int showAdSplashType) {
        this.showAdSplashType = showAdSplashType;
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
