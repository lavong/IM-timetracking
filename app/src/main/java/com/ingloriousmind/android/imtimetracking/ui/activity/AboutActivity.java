package com.ingloriousmind.android.imtimetracking.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.ingloriousmind.android.imtimetracking.Config;
import com.ingloriousmind.android.imtimetracking.R;

/**
 * about activity
 *
 * @author lavong.soysavanh
 */
public class AboutActivity extends Activity {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // set version info
        TextView versions = (TextView) findViewById(R.id.activity_about_versions);
        StringBuffer sb = new StringBuffer();
        sb.append("versionName: ").append(Config.versionName).append("\n");
        sb.append("versionCode: ").append(Config.versionCode).append("\n");
        sb.append("debug: ").append(Config.debug).append("\n");
        versions.setText(sb.toString());
    }
}
