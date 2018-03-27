package com.browser.downloader.videodownloader.data.model;

public class Video {

    public Video(String fileName, String url) {
        this.fileName = fileName;
        this.url = url;
    }

    private String fileName;

    private String url;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
