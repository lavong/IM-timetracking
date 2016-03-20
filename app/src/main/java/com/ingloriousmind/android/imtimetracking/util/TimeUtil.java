package com.ingloriousmind.android.imtimetracking.util;

/**
 * time util
 *
 * @author lavong.soysavanh
 */
public class TimeUtil {

    /**
     * pretty prints given duration to string of hours and minutes
     *
     * @param duration duration to pretty print
     * @return pretty printed string
     */
    public static String getTimeString(final long duration) {
        StringBuilder sb = new StringBuilder(11);
        int minutesTotal = (int) duration / 60 / 1000;
        int h = minutesTotal / 60;
        sb.append(h).append("h ");
        int m = minutesTotal % 60;
        if (m < 10)
            sb.append("0");
        sb.append(m).append("m ");
        int s = (int) ((duration / 1000) % 60);
        if (s < 10)
            sb.append("0");
        sb.append(s).append("s");
        return sb.toString();
    }

}
