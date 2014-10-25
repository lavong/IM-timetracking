package com.ingloriousmind.android.imtimetracking.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * dialog factory
 *
 * @author lavong.soysavanh
 */
public class DialogFactory {

    /**
     * default dialog click listener
     */
    private static class DismissListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    /**
     * instantiates a two-button alert dialog with given title, content view, button labels and button listeners.
     *
     * @param ctx               a context
     * @param title             dialog title
     * @param text              dialog message
     * @param okButtonLabel     positive button label
     * @param okListener        positive button click listener. null, for default dismiss action.
     * @param cancelButtonLabel negative button label
     * @param cancelListener    negative button click listener. null, for default dismiss action.
     * @return the alert dialog
     */
    public static Dialog newTwoButtonDialog(Context ctx, String title, String text, String okButtonLabel,
                                            DialogInterface.OnClickListener okListener, String cancelButtonLabel,
                                            DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setPositiveButton(okButtonLabel, okListener != null ? okListener : new DismissListener());
        builder.setNegativeButton(cancelButtonLabel, cancelListener != null ? cancelListener : new DismissListener());
        return builder.create();
    }

}
