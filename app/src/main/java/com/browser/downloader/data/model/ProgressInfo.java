package com.browser.downloader.data.model;

public class ProgressInfo {

    private long downloadId;

    private Video video;

    private int progress;

    private String progressSize;

    private int bytesDownloaded;

    private int bytesTotal;

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getProgressSize() {
        return progressSize;
    }

    public void setProgressSize(String progressSize) {
        this.progressSize = progressSize;
    }

    public int getBytesDownloaded() {
        return bytesDownloaded;
    }

    public void setBytesDownloaded(int bytesDownloaded) {
        this.bytesDownloaded = bytesDownloaded;
    }

    public int getBytesTotal() {
        return bytesTotal;
    }

    public void setBytesTotal(int bytesTotal) {
        this.bytesTotal = bytesTotal;
    }
}
