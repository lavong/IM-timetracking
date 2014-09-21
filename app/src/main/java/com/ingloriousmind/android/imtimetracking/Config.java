package com.ingloriousmind.android.imtimetracking;

import android.util.Log;

import java.lang.reflect.Field;

/**
 * configuration
 *
 * @author lavong.soysavanh
 */
public class Config {

    // debug flag
    public static boolean debug;

    // version info
    public static int versionCode;
    public static String versionName;


    /**
     * utility method dumping current configuration
     */
    public static void dump() {
        String tag = Config.class.getSimpleName();
        Field[] fields = Config.class.getDeclaredFields();
        Log.v(tag, "config dump (" + fields.length + " fields):");
        try {
            for (Field f : fields) {
                Log.v(tag, "  " + f.getName() + " = " + f.get(f.getName()));
            }
        } catch (IllegalAccessException e) {
            Log.w(tag, "unable to dump config field", e);
        }
    }

}
