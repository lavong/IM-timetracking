package com.ingloriousmind.android.imtimetracking.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.ingloriousmind.android.imtimetracking.BuildConfig;
import com.ingloriousmind.android.imtimetracking.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * about activity
 *
 * @author lavong.soysavanh
 */
public class AboutActivity extends Activity {

    @Bind(R.id.activity_about_versions)
    TextView versions;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        // set version info
        StringBuilder sb = new StringBuilder();
        sb.append("commit: ").append(BuildConfig.GIT_COMMIT_HASH).append("\n");
        sb.append("versionName: ").append(BuildConfig.VERSION_NAME).append("\n");
        sb.append("versionCode: ").append(BuildConfig.VERSION_CODE).append("\n");
        sb.append("debug: ").append(BuildConfig.DEBUG).append("\n");
        versions.setText(sb.toString());
    }
}
