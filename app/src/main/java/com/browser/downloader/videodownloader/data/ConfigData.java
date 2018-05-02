package com.browser.downloader.videodownloader.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConfigData {

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
