package com.ingloriousmind.android.imtimetracking.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ingloriousmind.android.imtimetracking.R;
import com.ingloriousmind.android.imtimetracking.util.L;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * pdf archive recycler adapter
 *
 * @author lavong.soysavanh
 */
public class PdfArchiveAdapter extends RecyclerView.Adapter<PdfArchiveAdapter.ViewHolder> {

    /**
     * log tag
     */
    private static final String TAG = PdfArchiveAdapter.class.getSimpleName();

    /**
     * layout inflater
     */
    private final LayoutInflater inflater;

    /**
     * adapter model
     */
    private final List<File> pdfFiles = new ArrayList<>();

    /**
     * item onClick callback
     */
    private final FileItemClickListener clickListener;

    /**
     * view holder class
     */
    public final static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView filename;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.list_item_archive_pdf_icon);
            filename = (TextView) itemView.findViewById(R.id.list_item_archive_pdf_filename);
        }
    }

    /**
     * callback listener
     */
    public interface FileItemClickListener {
        void onClick(File file);
    }

    /**
     * ctor
     *
     * @param ctx           a context
     * @param clickListener click callback
     */
    public PdfArchiveAdapter(Context ctx, FileItemClickListener clickListener) {
        inflater = LayoutInflater.from(ctx);
        this.clickListener = clickListener;
    }

    /**
     * updates adapter model
     *
     * @param files the files to set
     */
    public void setFiles(List<File> files) {
        pdfFiles.clear();
        if (files != null && !files.isEmpty()) {
            pdfFiles.addAll(files);
        }
        notifyDataSetChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PdfArchiveAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.list_item_archive_pdf, parent, false));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(PdfArchiveAdapter.ViewHolder holder, final int position) {
        final File f = pdfFiles.get(position);
        holder.filename.setText(f.getName());
        if (clickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    L.v(TAG, "onclick: " + position);
                    clickListener.onClick(f);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return pdfFiles.size();
    }
}
