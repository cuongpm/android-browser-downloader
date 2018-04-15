package com.browser.downloader.videodownloader.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StaticData {

    @SerializedName("is_show_ad_home")
    @Expose
    private boolean isShowAdHome = true;

    @SerializedName("is_show_ad_browser")
    @Expose
    private boolean isShowAdBrowser = true;

    @SerializedName("is_show_ad_video")
    @Expose
    private boolean isShowAdVideo = true;

    @SerializedName("is_show_all_pages")
    @Expose
    private boolean isShowAllPages;

    @SerializedName("is_show_rate_app")
    @Expose
    private boolean isShowRateApp;

    @SerializedName("parser_server")
    @Expose
    private String parserServer;

    @SerializedName("pages_general")
    @Expose
    private List<String> pagesGeneral;

    @SerializedName("pages_general_1")
    @Expose
    private List<String> pagesGeneral1;

    @SerializedName("pages_unsupported")
    @Expose
    private List<String> pagesUnsupported;

    public boolean isShowAdHome() {
        return isShowAdHome;
    }

    public void setShowAdHome(boolean showAdHome) {
        isShowAdHome = showAdHome;
    }

    public boolean isShowAdBrowser() {
        return isShowAdBrowser;
    }

    public void setShowAdBrowser(boolean showAdBrowser) {
        isShowAdBrowser = showAdBrowser;
    }

    public boolean isShowAdVideo() {
        return isShowAdVideo;
    }

    public void setShowAdVideo(boolean showAdVideo) {
        isShowAdVideo = showAdVideo;
    }

    public boolean isShowAllPages() {
        return isShowAllPages;
    }

    public void setShowAllPages(boolean showAllPages) {
        isShowAllPages = showAllPages;
    }

    public boolean isShowRateApp() {
        return isShowRateApp;
    }

    public void setShowRateApp(boolean showRateApp) {
        isShowRateApp = showRateApp;
    }

    public String getParserServer() {
        return parserServer;
    }

    public void setParserServer(String parserServer) {
        this.parserServer = parserServer;
    }

    public List<String> getPagesGeneral() {
        return pagesGeneral;
    }

    public void setPagesGeneral(List<String> pagesGeneral) {
        this.pagesGeneral = pagesGeneral;
    }

    public List<String> getPagesGeneral1() {
        return pagesGeneral1;
    }

    public void setPagesGeneral1(List<String> pagesGeneral1) {
        this.pagesGeneral1 = pagesGeneral1;
    }

    public List<String> getPagesUnsupported() {
        return pagesUnsupported;
    }

    public void setPagesUnsupported(List<String> pagesUnsupported) {
        this.pagesUnsupported = pagesUnsupported;
    }
}
