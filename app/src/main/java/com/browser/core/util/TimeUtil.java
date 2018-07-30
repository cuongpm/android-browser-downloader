package com.browser.core.util;

public class TimeUtil {

    public static String convertMilliSecondsToTimer(long milliSeconds) {
        String timerString = "";
        String secondsString = "";
        String minuteString = "";

        int hours = (int) (milliSeconds / (1000 * 60 * 60));
        int minutes = (int) (milliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        timerString = hours > 0 ? (hours + ":") : "";
        minuteString = minutes < 10 ? ("0" + minutes) : ("" + minutes);
        secondsString = seconds < 10 ? ("0" + seconds) : ("" + seconds);
        timerString = timerString + minuteString + ":" + secondsString;

        return timerString;
    }

}
