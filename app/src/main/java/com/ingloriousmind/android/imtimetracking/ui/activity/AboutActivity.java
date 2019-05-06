package com.ingloriousmind.android.imtimetracking.ui.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.ingloriousmind.android.imtimetracking.BuildConfig;
import com.ingloriousmind.android.imtimetracking.R;

/**
 * about activity
 *
 * @author lavong.soysavanh
 */
public class AboutActivity extends AppCompatActivity {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // set version info
        StringBuilder sb = new StringBuilder();
        sb.append("commit: ").append(BuildConfig.GIT_COMMIT_HASH).append("\n");
        sb.append("versionName: ").append(BuildConfig.VERSION_NAME).append("\n");
        sb.append("versionCode: ").append(BuildConfig.VERSION_CODE).append("\n");
        sb.append("debug: ").append(BuildConfig.DEBUG).append("\n");
        ((TextView) findViewById(R.id.activity_about_versions)).setText(sb.toString());
    }
}
