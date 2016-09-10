package com.ingloriousmind.android.imtimetracking;

import com.ingloriousmind.android.imtimetracking.ui.activity.HomeActivity;
import com.ingloriousmind.android.imtimetracking.ui.dialog.EditTrackingDialog;

import javax.inject.Singleton;

import dagger.Component;

/**
 * tracking component
 *
 * @author lavong.soysavanh
 */
@Singleton
@Component(modules = {TrackingModule.class})
public interface TrackingComponent {

    void inject(HomeActivity homeActivity);

    void inject(EditTrackingDialog editTrackingDialog);

}
