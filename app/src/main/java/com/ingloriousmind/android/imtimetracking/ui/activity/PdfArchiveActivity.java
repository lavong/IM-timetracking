package com.ingloriousmind.android.imtimetracking.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * pdf archive activity
 *
 * @author lavong.soysavavnh
 */
public class PdfArchiveActivity extends AppCompatActivity implements PdfArchiveAdapter.FileItemClickListener {

    @Bind(R.id.activity_archive_pdf_recycler)
    RecyclerView recycler;
    @Bind(R.id.activity_archive_pdf_empty)
    TextView empty;

    private PdfArchiveAdapter recyclerAdapter;

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
                    Timber.v("deleted: %s", f.getAbsolutePath());
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
        ButterKnife.bind(this);

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
                DialogFactory.newTwoButtonDialog(PdfArchiveActivity.this, R.string.dialog_title_delete_all_pdfs, getString(R.string.dialog_msg_delete_all_pdfs), R.string.dialog_btn_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DeleteArchivedPdfFilesTask().execute();
                    }
                }, R.string.dialog_btn_cancel, null).show();
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
    public void onClick(final File file) {
        try {
            Timber.d("onClick: %s", file.getAbsolutePath());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Timber.e(e, "no activity to open pdf file: %s", file.getAbsoluteFile());
            DialogFactory.newTwoButtonDialog(
                    this,
                    R.string.dialog_open_pdf_error_title,
                    getString(R.string.dialog_open_pdf_error_msg),
                    R.string.dialog_open_pdf_error_btn_retry,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PdfArchiveActivity.this.onClick(file);
                            dialog.dismiss();
                        }
                    },
                    R.string.dialog_open_pdf_error_btn_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

}
