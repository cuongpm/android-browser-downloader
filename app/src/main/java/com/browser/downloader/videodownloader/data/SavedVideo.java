package com.browser.downloader.videodownloader.data;

public class SavedVideo {

    public SavedVideo(Video video) {
        this.video = video;
    }

    private Video video;

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }
}
