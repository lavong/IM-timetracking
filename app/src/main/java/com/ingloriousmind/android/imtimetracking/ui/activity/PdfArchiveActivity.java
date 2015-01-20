package com.ingloriousmind.android.imtimetracking.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ingloriousmind.android.imtimetracking.R;
import com.ingloriousmind.android.imtimetracking.ui.adapter.PdfArchiveAdapter;
import com.ingloriousmind.android.imtimetracking.ui.dialog.DialogFactory;
import com.ingloriousmind.android.imtimetracking.util.FileUtil;
import com.ingloriousmind.android.imtimetracking.util.L;

import java.io.File;
import java.util.List;

/**
 * pdf archive activity
 *
 * @author lavong.soysavavnh
 */
public class PdfArchiveActivity extends Activity implements PdfArchiveAdapter.FileItemClickListener {

    /**
     * log tag
     */
    private static final String TAG = PdfArchiveActivity.class.getSimpleName();

    // views
    private RecyclerView recycler;
    private PdfArchiveAdapter recyclerAdapter;
    private TextView empty;

    /**
     * task feeding recycler adapter
     */
    private class LoadArchivedFilesTask extends AsyncTask<Void, Void, Void> {

        private List<File> files;

        @Override
        protected Void doInBackground(Void... params) {
            files = FileUtil.getArchivedPdfFiles();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            recyclerAdapter.setFiles(files);
            updateEmptyViewVisibility();
        }
    }

    /**
     * task removing all archived pdf files under {@link com.ingloriousmind.android.imtimetracking.util.FileUtil#appDir}
     */
    private class DeleteArchivedPdfFilesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            List<File> pdfFiles = FileUtil.getArchivedPdfFiles();
            for (File f : pdfFiles) {
                if (f.delete()) {
                    L.v(TAG, "deleted " + f.getAbsolutePath());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new LoadArchivedFilesTask().execute();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_pdf);

        empty = (TextView) findViewById(R.id.activity_archive_pdf_empty);
        recycler = (RecyclerView) findViewById(R.id.activity_archive_pdf_recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recyclerAdapter = new PdfArchiveAdapter(this, this);
        recycler.setAdapter(recyclerAdapter);

        recyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                L.d(TAG, "onChanged");
                updateEmptyViewVisibility();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();
        new LoadArchivedFilesTask().execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pdf_archive, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                String title = getString(R.string.dialog_title_delete_all_pdfs);
                String msg = getString(R.string.dialog_msg_delete_all_pdfs);
                String delete = getString(R.string.dialog_btn_delete);
                String cancel = getString(R.string.dialog_btn_cancel);
                DialogFactory.newTwoButtonDialog(PdfArchiveActivity.this, title, msg, delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteArchivedPdfFilesTask().execute();
                    }
                }, cancel, null).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * updates list vs. emptyview visibility
     */
    private void updateEmptyViewVisibility() {
        boolean adapterEmpty = recyclerAdapter.getItemCount() == 0;
        empty.setVisibility(adapterEmpty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(adapterEmpty ? View.GONE : View.VISIBLE);
    }

    /**
     * recycler adapter onClick callback
     *
     * @param file the file being clicked
     */
    @Override
    public void onClick(File file) {
        L.d(TAG, "onclick: " + file.getAbsolutePath());
        if (file != null && file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }
    }

}
