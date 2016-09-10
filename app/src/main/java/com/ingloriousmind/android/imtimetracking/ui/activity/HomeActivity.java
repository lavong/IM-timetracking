package com.ingloriousmind.android.imtimetracking.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ingloriousmind.android.imtimetracking.R;
import com.ingloriousmind.android.imtimetracking.TrackingApplication;
import com.ingloriousmind.android.imtimetracking.export.Exporter;
import com.ingloriousmind.android.imtimetracking.model.Tracking;
import com.ingloriousmind.android.imtimetracking.time.Tracker;
import com.ingloriousmind.android.imtimetracking.ui.adapter.TrackingAdapter;
import com.ingloriousmind.android.imtimetracking.ui.dialog.DialogFactory;
import com.ingloriousmind.android.imtimetracking.ui.dialog.EditTrackingDialog;
import com.ingloriousmind.android.imtimetracking.util.ImeUtil;
import com.ingloriousmind.android.imtimetracking.util.RedirectFacade;
import com.ingloriousmind.android.imtimetracking.util.TimeUtil;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * home activity
 *
 * @author lavong.soysavanh
 */
public class HomeActivity extends AppCompatActivity {

    @Bind(R.id.activity_home_action_add)
    FloatingActionButton actionButtonAdd;
    @Bind(R.id.activity_home_action_pause)
    FloatingActionButton actionButtonPause;
    @Bind(R.id.activity_home_overlay)
    RelativeLayout overlay;
    @Bind(R.id.activity_home_overlay_title)
    EditText overlayTitle;
    @Bind(R.id.activity_home_overlay_time)
    TextView overlayTime;
    @Bind(R.id.activity_home_total)
    TextView footerTotal;
    @Bind(R.id.activity_home_recycler)
    RecyclerView recycler;

    private ProgressDialog progressDialog;
    private TrackingAdapter adapter;

    @Inject
    Tracker tracker;

    @Inject
    Exporter exporter;

    private Subscription trackerSubscription;

    /**
     * action button click listener
     */
    private class ActionButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.activity_home_action_add:
                    startTracking();
                    break;
                case R.id.activity_home_action_pause:
                    stopTracking();
                    break;
            }
        }
    }

    /**
     * list item action listener
     */
    private class TrackingListItemListener implements TrackingAdapter.TrackingItemActionListener {

        @Override
        public void onEdit(int pos, Tracking t) {
            editTracking(t);
        }

        @Override
        public void onResume(int pos, Tracking t) {
            Timber.d("resume: %s", t);
            startTracking(t);
            adapter.notifyItemChanged(pos);
        }
    }

    /**
     * async task fetching all trackings and feeding list adapter
     */
    private class LoadTrackingsTask extends AsyncTask<Void, Void, Void> {

        private long total;
        private List<Tracking> trackings;
        private boolean postScrollTop;

        public LoadTrackingsTask(boolean postScrollTop) {
            this.postScrollTop = postScrollTop;
        }

        @Override
        protected Void doInBackground(Void... params) {
            trackings = tracker.getTrackings();

            for (Tracking t : trackings)
                total += t.getDuration();

            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.setTrackings(trackings);
            footerTotal.setText(TimeUtil.getTimeString(total));
            progressDialog.dismiss();
            if (postScrollTop) {
                recycler.smoothScrollToPosition(0);
            }
        }
    }

    /**
     * async task deleting all tasks
     */
    private class DeleteTrackingsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            for (Tracking t : tracker.getTrackings())
                tracker.removeTracking(t);
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.setTrackings(null);
            footerTotal.setText(TimeUtil.getTimeString(0));
            progressDialog.dismiss();
        }
    }

    /**
     * async task exporting pdf file and launching share intent
     */
    private class ExportAndSharePdfTask extends AsyncTask<Void, Void, Void> {

        private File pdfFile;

        @Override
        protected Void doInBackground(Void... params) {
            pdfFile = exporter.export();
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            if (pdfFile != null && pdfFile.exists()) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_SUBJECT, pdfFile.getName());
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pdfFile));
                startActivity(Intent.createChooser(intent, getString(R.string.home_activity_share_pdf_intent_chooser_title)));
            } else {
                DialogFactory.newTwoButtonDialog(HomeActivity.this, R.string.dialog_share_pdf_error_title, getString(R.string.dialog_share_pdf_error_msg), R.string.dialog_share_pdf_error_btn_retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        new ExportAndSharePdfTask().execute();
                    }
                }, R.string.dialog_share_pdf_error_btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        ((TrackingApplication) getApplication()).getComponent().inject(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());
        adapter = new TrackingAdapter(this, null, new TrackingListItemListener());
        recycler.setAdapter(adapter);

        // progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.home_activity_progress_indicator_msg));

        // overlay
        overlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // consume them all
                return true;
            }
        });
        overlayTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    updateTrackingTitle(v.getText());
                    v.clearFocus();
                    ImeUtil.hideIme(v);
                }
                return false;
            }
        });

        // action buttons
        ActionButtonClickListener actionBtnListener = new ActionButtonClickListener();
        actionButtonAdd.setOnClickListener(actionBtnListener);
        actionButtonPause.setOnClickListener(actionBtnListener);
    }

    private void updateTrackingTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            Timber.d("updateTrackingTitle: %s", title);
            Tracking tracking = tracker.getCurrentTracking();
            if (tracking != null) {
                tracking.setTitle(title.toString());
                tracker.persistTracking(tracking);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();

        trackerSubscription = tracker.observe()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Tracking>() {
                    @Override
                    public void call(Tracking tracking) {
                        final String elapsedTime = TimeUtil.getTimeString(tracking.getDuration());
                        Timber.v("observe tracking: %s (%d ms)", elapsedTime, tracking.getDuration());
                        overlayTime.setText(elapsedTime);
                    }
                });

        // check if there is a tracking to resume
        Tracking trackingResumed = tracker.resumeIfNecessary();
        if (trackingResumed != null) {
            onTrackingStarted(trackingResumed);
        }

        // load items
        reloadTrackingList(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        super.onPause();

        // pause tracking
        tracker.pause();

        trackerSubscription.unsubscribe();
    }

    /**
     * triggers async tracking list reloading
     *
     * @param postScrollTop true, to have have the recycler scrolls to top of the list after reloading. false, otherwise.
     */
    private void reloadTrackingList(boolean postScrollTop) {
        new LoadTrackingsTask(postScrollTop).execute();
    }

    /**
     * displays edit tracking dialog
     *
     * @param t tracking to edit
     */
    private void editTracking(final Tracking t) {
        EditTrackingDialog d = new EditTrackingDialog(HomeActivity.this, t);
        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                reloadTrackingList(false);
            }
        });
        d.show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                RedirectFacade.goAbout(this);
                break;
            case R.id.action_export_pdf:
                new ExportAndSharePdfTask().execute();
                break;
            case R.id.action_pdf_archive:
                RedirectFacade.goPdfArchive(this);
                break;
            case R.id.action_clear:
                DialogFactory.newTwoButtonDialog(HomeActivity.this, R.string.dialog_title_delete_all, getString(R.string.dialog_msg_delete_all), R.string.dialog_btn_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteTrackingsTask().execute();
                    }
                }, R.string.dialog_btn_cancel, null).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @see #startTracking(com.ingloriousmind.android.imtimetracking.model.Tracking)
     */
    public void startTracking() {
        startTracking(null);
    }

    /**
     * starts new time tracking and updates UI for {@link com.ingloriousmind.android.imtimetracking.model.Tracking} to resume
     *
     * @param trackingToResume tracking to resume. null for new tracking.
     */
    public void startTracking(final Tracking trackingToResume) {
        Tracking trackingStarted = tracker.start(trackingToResume != null ? trackingToResume : new Tracking());
        onTrackingStarted(trackingStarted);
    }

    private void onTrackingStarted(Tracking trackingStarted) {
        adapter.addTracking(trackingStarted);
        overlayTitle.setText(TextUtils.isEmpty(trackingStarted.getTitle())
                ? getString(R.string.activity_home_overlay_unnamed_tracking_title)
                : trackingStarted.getTitle()
        );
        revealOverlay();
    }

    /**
     * stops time tracking and hides overlay
     */
    public void stopTracking() {
        Tracking t = tracker.stop();

        hideOverlay();
        int pos = adapter.indexOf(t);
        if (pos > 0) {
            adapter.notifyItemMoved(pos, 0);
        }
        recycler.postDelayed(new Runnable() {
            @Override
            public void run() {
                reloadTrackingList(true);
            }
        }, 300);
    }

    /**
     * reveals {@link #overlay}
     */
    public void revealOverlay() {
        overlay.post(new Runnable() {
            @Override
            public void run() {
                int cx = (overlay.getLeft() + overlay.getRight()) / 2;
                int cy = (overlay.getTop() + overlay.getBottom()) / 2;
                int clippingCircleRadius = Math.max(overlay.getWidth(), overlay.getHeight());
                Animator anim = ViewAnimationUtils.createCircularReveal(overlay, cx, cy, 0, clippingCircleRadius);
                overlay.setVisibility(View.VISIBLE);
                anim.start();
                actionButtonAdd.setVisibility(View.GONE);
                actionButtonPause.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * hides {@link #overlay}
     */
    public void hideOverlay() {
        int cx = (overlay.getLeft() + overlay.getRight()) / 2;
        int cy = (overlay.getTop() + overlay.getBottom()) / 2;
        int initialRadius = overlay.getWidth();
        Animator anim = ViewAnimationUtils.createCircularReveal(overlay, cx, cy, initialRadius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                overlay.setVisibility(View.INVISIBLE);
                actionButtonAdd.setVisibility(View.VISIBLE);
                actionButtonPause.setVisibility(View.GONE);
                ImeUtil.hideIme(overlayTitle);
            }
        });
        anim.start();
    }

}
