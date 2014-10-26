package com.ingloriousmind.android.imtimetracking.util;

/**
 * time util
 *
 * @author lavong.soysavanh
 */
public class TimeUtil {

    /**
     * temp buffer
     */
    private static StringBuffer sb = new StringBuffer();

    /**
     * pretty prints given duration to string of hours and minutes
     *
     * @param duration duration to pretty print
     * @return pretty printed string
     */
    public static synchronized String getTimeString(long duration) {
        sb.setLength(0);
        int minutes = (int) duration / 60 / 1000;
        int h = minutes / 60;
        if (h < 10)
            sb.append(" ");
        sb.append(h).append("h ");
        int m = minutes % 60;
        if (m < 10)
            sb.append("0");
        sb.append(m).append("m");
        return sb.toString();
    }

}
