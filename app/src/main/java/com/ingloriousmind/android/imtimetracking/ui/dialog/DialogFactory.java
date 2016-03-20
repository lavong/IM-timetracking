package com.ingloriousmind.android.imtimetracking.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;

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
     * @param ctx                    a context
     * @param titleResId             dialog title resource id
     * @param message                dialog message
     * @param okButtonLabelResId     positive button label resource id
     * @param okListener             positive button click listener. null, for default dismiss action.
     * @param cancelButtonLabelResId negative button label resource id
     * @param cancelListener         negative button click listener. null, for default dismiss action.
     * @return the alert dialog
     */
    public static Dialog newTwoButtonDialog(Context ctx, @StringRes int titleResId, String message, @StringRes int okButtonLabelResId,
                                            DialogInterface.OnClickListener okListener, @StringRes int cancelButtonLabelResId,
                                            DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(titleResId);
        builder.setMessage(message);
        builder.setPositiveButton(okButtonLabelResId, okListener != null ? okListener : new DismissListener());
        builder.setNegativeButton(cancelButtonLabelResId, cancelListener != null ? cancelListener : new DismissListener());
        return builder.create();
    }

}
