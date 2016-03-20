package com.ingloriousmind.android.imtimetracking.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.ingloriousmind.android.imtimetracking.R;
import com.ingloriousmind.android.imtimetracking.controller.TimeTrackingController;
import com.ingloriousmind.android.imtimetracking.model.Tracking;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * edit tracking dialog
 *
 * @author lavong.soysavah
 */
public class EditTrackingDialog extends Dialog implements View.OnClickListener {

    @Bind(R.id.dialog_tracking_edit_title)
    EditText title;
    @Bind(R.id.dialog_tracking_edit_time_picker)
    TimePicker timePicker;
    @Bind(R.id.dialog_tracking_edit_btn_save)
    Button save;
    @Bind(R.id.dialog_tracking_edit_btn_delete)
    Button delete;
    @Bind(R.id.dialog_tracking_edit_btn_cancel)
    Button cancel;

    private Tracking trackingToEdit;

    /**
     * ctor
     *
     * @param ctx            a context
     * @param trackingToEdit the tracking to edit
     */
    public EditTrackingDialog(Context ctx, Tracking trackingToEdit) {
        super(ctx, R.style.AppTheme);
        this.trackingToEdit = trackingToEdit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_tracking_edit);
        ButterKnife.bind(this);

        timePicker.setIs24HourView(true);
        save.setOnClickListener(this);
        delete.setOnClickListener(this);
        cancel.setOnClickListener(this);

        title.setText(trackingToEdit.getTitle());
        int minutes = (int) trackingToEdit.getDuration() / 60 / 1000;
        timePicker.setCurrentHour(minutes / 60);
        timePicker.setCurrentMinute(minutes % 60);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_tracking_edit_btn_save:
                trackingToEdit.setTitle(title.getText().toString());
                Integer h = timePicker.getCurrentHour();
                Integer m = timePicker.getCurrentMinute();
                trackingToEdit.setDuration((h * 60 + m) * 60 * 1000);
                TimeTrackingController.storeTracking(trackingToEdit);
                dismiss();
                break;
            case R.id.dialog_tracking_edit_btn_delete:
                String msg = getContext().getString(R.string.dialog_msg_delete, trackingToEdit.getTitle());
                DialogFactory.newTwoButtonDialog(getContext(), R.string.dialog_title_delete, msg, R.string.dialog_btn_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Timber.d("delete: %s", trackingToEdit.toString());
                        TimeTrackingController.removeTracking(trackingToEdit);
                        dismiss();
                    }
                }, R.string.dialog_btn_cancel, null).show();
                break;
            case R.id.dialog_tracking_edit_btn_cancel:
                dismiss();
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        super.onStart();

        if (trackingToEdit == null) {
            Timber.w("tracking expected");
            dismiss();
        }

        title.selectAll();
    }
}
