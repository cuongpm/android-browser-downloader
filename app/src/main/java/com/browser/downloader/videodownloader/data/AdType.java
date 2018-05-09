package com.browser.downloader.videodownloader.data;

public enum AdType {
    ADMOB(0),
    APPLOVIN(1);

    int value;

    AdType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    static AdType fromValue(int value) {
        if (value == ADMOB.getValue()) return ADMOB;
        else return APPLOVIN;
    }

}