package com.browser.downloader.data.model;

public enum AdType {
    ADMOB(0),
    APPLOVIN(1),
    AIRPUSH(2);

    int value;

    AdType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    static AdType fromValue(int value) {
        if (value == ADMOB.getValue()) return ADMOB;
        else if (value == APPLOVIN.getValue()) return APPLOVIN;
        else return AIRPUSH;
    }

}