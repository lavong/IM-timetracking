package com.ingloriousmind.android.imtimetracking.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

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
     * @see #newTwoButtonDialog(android.content.Context, String, android.view.View, String, android.content.DialogInterface.OnClickListener, String, android.content.DialogInterface.OnClickListener)
     */
    public static Dialog newTwoButtonDialog(Context ctx, String title, String text, String okButtonLabel,
                                            DialogInterface.OnClickListener okButtonOnClickListener, String cancelButtonLabel,
                                            DialogInterface.OnClickListener cancelButtonOnClickListener) {
        TextView textView = new TextView(ctx);
        textView.setText(text);
        return newTwoButtonDialog(ctx, title, textView, okButtonLabel, okButtonOnClickListener, cancelButtonLabel,
                cancelButtonOnClickListener);
    }

    /**
     * instantiates a two-button alert dialog with given title, content view, button labels and button listeners.
     *
     * @param ctx               a context
     * @param title             dialog title
     * @param contentView       the dialog's content view
     * @param okButtonLabel     positive button label
     * @param okListener        positive button click listener. null, for default dismiss action.
     * @param cancelButtonLabel negative button label
     * @param cancelListener    negative button click listener. null, for default dismiss action.
     * @return the alert dialog
     */
    public static Dialog newTwoButtonDialog(Context ctx, String title, View contentView, String okButtonLabel,
                                            DialogInterface.OnClickListener okListener, String cancelButtonLabel,
                                            DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setView(contentView);
        builder.setPositiveButton(okButtonLabel, okListener != null ? okListener : new DismissListener());
        builder.setNegativeButton(cancelButtonLabel, cancelListener != null ? cancelListener : new DismissListener());
        return builder.create();
    }
}
