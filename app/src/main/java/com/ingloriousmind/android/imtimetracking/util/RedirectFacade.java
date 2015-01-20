package com.ingloriousmind.android.imtimetracking.util;

import android.content.Context;
import android.content.Intent;

import com.ingloriousmind.android.imtimetracking.ui.activity.AboutActivity;
import com.ingloriousmind.android.imtimetracking.ui.activity.HomeActivity;
import com.ingloriousmind.android.imtimetracking.ui.activity.PdfArchiveActivity;

/**
 * redirect facade
 *
 * @author lavong.soysavanh
 */
public class RedirectFacade {

    /**
     * redirects to {@link com.ingloriousmind.android.imtimetracking.ui.activity.AboutActivity}
     *
     * @param ctx a context
     */
    public static void goAbout(Context ctx) {
        Intent intent = new Intent(ctx, AboutActivity.class);
        ctx.startActivity(intent);
    }

    /**
     * redirects to {@link com.ingloriousmind.android.imtimetracking.ui.activity.HomeActivity}
     *
     * @param ctx a context
     */
    public static void goHome(Context ctx) {
        Intent intent = new Intent(ctx, HomeActivity.class);
        ctx.startActivity(intent);
    }

    /**
     * redirects to {@link com.ingloriousmind.android.imtimetracking.ui.activity.PdfArchiveActivity}
     *
     * @param ctx a context
     */
    public static void goPdfArchive(Context ctx) {
        Intent intent = new Intent(ctx, PdfArchiveActivity.class);
        ctx.startActivity(intent);
    }

}
