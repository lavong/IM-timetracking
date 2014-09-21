package com.ingloriousmind.android.imtimetracking.util;

import android.util.Log;

import com.ingloriousmind.android.imtimetracking.Config;


/**
 * log util
 *
 * @author lavong.soysavanh
 */
public class L {

    public static void d(String tag, String msg) {
        if (Config.debug) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable t) {
        if (Config.debug) {
            Log.d(tag, msg, t);
        }
    }

    public static void e(String tag, String msg) {
        if (Config.debug) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable t) {
        if (Config.debug) {
            Log.e(tag, msg, t);
        }
    }

    public static void i(String tag, String msg) {
        if (Config.debug) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable t) {
        if (Config.debug) {
            Log.i(tag, msg, t);
        }
    }

    public static void v(String tag, String msg) {
        if (Config.debug) {
            Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable t) {
        if (Config.debug) {
            Log.v(tag, msg, t);
        }
    }

    public static void w(String tag, String msg) {
        if (Config.debug) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable t) {
        if (Config.debug) {
            Log.w(tag, msg, t);
        }
    }

}
