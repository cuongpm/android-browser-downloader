package com.browser.downloader.videodownloader.data;

public enum SuggestionType {
    SUGGESTION(0),
    WEB(1);

    int value;

    SuggestionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    static SuggestionType fromValue(int value) {
        if (value == SUGGESTION.getValue()) return SUGGESTION;
        else return WEB;
    }

}