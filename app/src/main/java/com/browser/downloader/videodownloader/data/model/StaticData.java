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

    @SerializedName("server_1")
    @Expose
    private String server1;

    @SerializedName("server_2")
    @Expose
    private String server2;

    @SerializedName("apps")
    @Expose
    private List<App> apps;

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

    public String getServer1() {
        return server1;
    }

    public void setServer1(String server1) {
        this.server1 = server1;
    }

    public String getServer2() {
        return server2;
    }

    public void setServer2(String server2) {
        this.server2 = server2;
    }

    public List<App> getApps() {
        return apps;
    }

    public void setApps(List<App> apps) {
        this.apps = apps;
    }
}
