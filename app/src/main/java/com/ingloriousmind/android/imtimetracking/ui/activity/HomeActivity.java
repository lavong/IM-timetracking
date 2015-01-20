package com.ingloriousmind.android.imtimetracking.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Outline;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewOutlineProvider;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ingloriousmind.android.imtimetracking.R;
import com.ingloriousmind.android.imtimetracking.controller.TimeTrackingController;
import com.ingloriousmind.android.imtimetracking.controller.task.TimeTrackerTask;
import com.ingloriousmind.android.imtimetracking.model.Tracking;
import com.ingloriousmind.android.imtimetracking.ui.adapter.TrackingAdapter;
import com.ingloriousmind.android.imtimetracking.ui.dialog.DialogFactory;
import com.ingloriousmind.android.imtimetracking.ui.dialog.EditTrackingDialog;
import com.ingloriousmind.android.imtimetracking.util.L;
import com.ingloriousmind.android.imtimetracking.util.RedirectFacade;
import com.ingloriousmind.android.imtimetracking.util.TimeUtil;

import java.io.File;
import java.util.List;

/**
 * home activity
 *
 * @author lavong.soysavanh
 */
public class HomeActivity extends Activity {

    /**
     * log tag
     */
    private static final String TAG = HomeActivity.class.getSimpleName();

    /**
     * recycler adapter to display
     */
    private TrackingAdapter adapter;

    // views
    private ImageButton actionButtonAdd;
    private ImageButton actionButtonPause;
    private RelativeLayout overlay;
    private TextView overlayTitle;
    private TextView overlayTime;
    private ProgressDialog progressDialog;
    private TextView footerTotal;
    private RecyclerView recycler;

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
     * tracking controller callback listener
     */
    private class TimeTrackingListener implements TimeTrackerTask.TimeTrackingAware {

        @Override
        public void onTick(final String elapsedTime, long duration) {
            L.d(TAG, "tick: " + elapsedTime);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    overlayTime.setText(elapsedTime);
                }
            });
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
        public void onDelete(final int pos, final Tracking t) {
            String title = getString(R.string.dialog_title_delete);
            String msg = getString(R.string.dialog_msg_delete, t.getTitle());
            String delete = getString(R.string.dialog_btn_delete);
            String cancel = getString(R.string.dialog_btn_cancel);
            DialogFactory.newTwoButtonDialog(HomeActivity.this, title, msg, delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    L.d(TAG, "delete: " + t.toString());
                    TimeTrackingController.removeTracking(t);
                    adapter.removeTracking(pos);
                }
            }, cancel, null).show();
        }

        @Override
        public void onResume(int pos, Tracking t) {
            L.d(TAG, "resume: " + t.toString());
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
            trackings = TimeTrackingController.fetchTrackings();

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
            for (Tracking t : TimeTrackingController.fetchTrackings())
                TimeTrackingController.removeTracking(t);
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
     * async task fetching and checking most recent tracking for resuming
     */
    private class ResumeTask extends AsyncTask<Void, Void, Void> {

        Tracking mostRecent;

        @Override
        protected Void doInBackground(Void... params) {
            mostRecent = TimeTrackingController.fetchMostRecentTracking();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mostRecent != null && mostRecent.isTracking())
                startTracking(mostRecent);
        }
    }

    /**
     * async task exporting pdf file and launching share intent
     */
    private class ExportAndSharePdfTask extends AsyncTask<Void, Void, Void> {

        private File pdfFile;

        @Override
        protected Void doInBackground(Void... params) {
            pdfFile = TimeTrackingController.exportPdf(HomeActivity.this);
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
                DialogFactory.newTwoButtonDialog(HomeActivity.this, getString(R.string.dialog_share_pdf_error_title), getString(R.string.dialog_share_pdf_error_msg), getString(R.string.dialog_share_pdf_error_btn_retry), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        new ExportAndSharePdfTask().execute();
                    }
                }, getString(R.string.dialog_share_pdf_error_btn_cancel), new DialogInterface.OnClickListener() {
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

        recycler = (RecyclerView) findViewById(R.id.activity_home_recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());
        adapter = new TrackingAdapter(this, null, new TrackingListItemListener());
        recycler.setAdapter(adapter);

        footerTotal = (TextView) findViewById(R.id.activity_home_total);

        // progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.home_activity_progress_indicator_msg));

        // overlay
        overlay = (RelativeLayout) findViewById(R.id.activity_home_overlay);
        overlayTitle = (TextView) findViewById(R.id.activity_home_overlay_title);
        overlayTime = (TextView) findViewById(R.id.activity_home_overlay_time);
        overlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // consume them all
                return true;
            }
        });

        // action buttons
        actionButtonAdd = (ImageButton) findViewById(R.id.activity_home_action_add);
        actionButtonPause = (ImageButton) findViewById(R.id.activity_home_action_pause);
        ActionButtonClickListener actionBtnListener = new ActionButtonClickListener();
        actionButtonAdd.setOnClickListener(actionBtnListener);
        actionButtonPause.setOnClickListener(actionBtnListener);

        // action button elevation
        ViewOutlineProvider actionButtonOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                int size = getResources().getDimensionPixelSize(R.dimen.floating_action_button_outline_size);
                outline.setOval(0, 0, size, size);
            }
        };
        actionButtonAdd.setOutlineProvider(actionButtonOutlineProvider);
        actionButtonAdd.setClipToOutline(true);
        actionButtonAdd.setElevation(10);
        actionButtonPause.setOutlineProvider(actionButtonOutlineProvider);
        actionButtonPause.setClipToOutline(true);
        actionButtonPause.setElevation(10);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();

        // check if there is a tracking to resume
        new ResumeTask().execute();

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                TimeTrackingController.stop(true);
            }
        }).start();
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
                String title = getString(R.string.dialog_title_delete_all);
                String msg = getString(R.string.dialog_msg_delete_all);
                String delete = getString(R.string.dialog_btn_delete);
                String cancel = getString(R.string.dialog_btn_cancel);
                DialogFactory.newTwoButtonDialog(HomeActivity.this, title, msg, delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteTrackingsTask().execute();
                    }
                }, cancel, null).show();
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
        Tracking tracking = TimeTrackingController.start(trackingToResume, new TimeTrackingListener());
        if (trackingToResume == null) {
            adapter.addTracking(tracking);
        }
        overlayTitle.setText(TextUtils.isEmpty(tracking.getTitle())
                        ? getString(R.string.activity_home_overlay_unnamed_tracking_title)
                        : tracking.getTitle()
        );
        revealOverlay();
    }

    /**
     * stops time tracking and hides overlay
     */
    public void stopTracking() {
        Tracking t = TimeTrackingController.stop();
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
        int cx = (overlay.getLeft() + overlay.getRight()) / 2;
        int cy = (overlay.getTop() + overlay.getBottom()) / 2;
        int clippingCircleRadius = Math.max(overlay.getWidth(), overlay.getHeight());
        Animator anim = ViewAnimationUtils.createCircularReveal(overlay, cx, cy, 0, clippingCircleRadius);
        overlay.setVisibility(View.VISIBLE);
        anim.start();
        actionButtonAdd.setVisibility(View.GONE);
        actionButtonPause.setVisibility(View.VISIBLE);
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
                //recycler.smoothScrollToPosition(0);
            }
        });
        anim.start();
    }

}
