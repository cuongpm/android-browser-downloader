package com.browser.downloader.videodownloader.data;

public class ProgressInfo {

    private long downloadId;

    private Video video;

    private int progress;

    private String progressSize;

    private boolean isDownloaded;

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

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }
}
