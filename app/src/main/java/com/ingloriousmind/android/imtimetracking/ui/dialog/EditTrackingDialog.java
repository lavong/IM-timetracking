package com.ingloriousmind.android.imtimetracking.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.ingloriousmind.android.imtimetracking.R;
import com.ingloriousmind.android.imtimetracking.controller.TimeTrackingController;
import com.ingloriousmind.android.imtimetracking.model.Tracking;
import com.ingloriousmind.android.imtimetracking.util.L;

/**
 * edit tracking dialog
 *
 * @author lavong.soysavah
 */
public class EditTrackingDialog extends Dialog implements View.OnClickListener {

    /**
     * log tag
     */
    private static final String TAG = EditTrackingDialog.class.getSimpleName();

    // views
    private Tracking trackingToEdit;
    private EditText title;
    private EditText description;
    private TimePicker timePicker;
    private Button save;
    private Button cancel;

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

        title = (EditText) findViewById(R.id.dialog_tracking_edit_title);
        description = (EditText) findViewById(R.id.dialog_tracking_edit_description);
        timePicker = (TimePicker) findViewById(R.id.dialog_tracking_edit_time_picker);
        timePicker.setIs24HourView(true);
        save = (Button) findViewById(R.id.dialog_tracking_edit_btn_save);
        cancel = (Button) findViewById(R.id.dialog_tracking_edit_btn_cancel);
        save.setOnClickListener(this);
        cancel.setOnClickListener(this);

        title.setText(trackingToEdit.getTitle());
        description.setText(trackingToEdit.getDescription());
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
                trackingToEdit.setDescription(description.getText().toString());
                Integer h = timePicker.getCurrentHour();
                Integer m = timePicker.getCurrentMinute();
                trackingToEdit.setDuration((h * 60 + m) * 60 * 1000);
                TimeTrackingController.storeTracking(trackingToEdit);
                dismiss();
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
        L.i(TAG, "onStart");

        if (trackingToEdit == null) {
            L.w(TAG, "tracking expected");
            dismiss();
        }

        title.selectAll();
    }
}
