package com.browser.downloader.data.model;

import java.util.ArrayList;

public class Video {

    public Video(String fileName, String url, String thumbnail, long duration) {
        this.fileName = fileName;
        this.url = url;
        this.thumbnail = thumbnail;
        this.duration = duration;
    }

    public Video(String fileName, String url) {
        this.fileName = fileName;
        this.url = url;
    }

    private ArrayList<Format> formats;

    private String fileName;

    private String url;

    private String thumbnail;

    private long duration;

    private boolean isDownloadCompleted;

    public ArrayList<Format> getFormats() {
        return formats;
    }

    public void setFormats(ArrayList<Format> formats) {
        this.formats = formats;
    }

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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isDownloadCompleted() {
        return isDownloadCompleted;
    }

    public void setDownloadCompleted(boolean downloadCompleted) {
        isDownloadCompleted = downloadCompleted;
    }
}
