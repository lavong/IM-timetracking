package com.ingloriousmind.android.imtimetracking.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ingloriousmind.android.imtimetracking.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * pdf archive recycler adapter
 *
 * @author lavong.soysavanh
 */
public class PdfArchiveAdapter extends RecyclerView.Adapter<PdfArchiveAdapter.ViewHolder> {

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
        TextView filename;

        public ViewHolder(View itemView) {
            super(itemView);
            filename = itemView.findViewById(R.id.list_item_archive_pdf_filename);
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
