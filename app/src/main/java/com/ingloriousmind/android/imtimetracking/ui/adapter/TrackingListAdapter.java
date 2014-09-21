package com.ingloriousmind.android.imtimetracking.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ingloriousmind.android.imtimetracking.R;
import com.ingloriousmind.android.imtimetracking.model.Tracking;

import java.util.ArrayList;
import java.util.List;

/**
 * tracking list adapter
 *
 * @author lavong.soysavanh
 */
public class TrackingListAdapter extends BaseAdapter {

    /**
     * layout inflater
     */
    private final LayoutInflater inflater;

    /**
     * list item buttons click listener
     */
    private final TrackingItemActionListener listener;

    /**
     * trackings list
     */
    private List<Tracking> trackings = new ArrayList<Tracking>();

    /**
     * temp buffer
     */
    private StringBuilder sb = new StringBuilder();

    /**
     * default list item title
     */
    private final String unnamedTitle;

    /**
     * list item click listener
     */
    private class TrackingItemClickListener implements View.OnClickListener {
        private int pos;

        public TrackingItemClickListener(int pos) {
            this.pos = pos;
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                switch (v.getId()) {
                    case R.id.list_item_tracking_btn_delete:
                        listener.onDelete(trackings.get(pos));
                        trackings.remove(pos);
                        notifyDataSetChanged();
                        break;
                    case R.id.list_item_tracking_btn_resume_pause:
                        listener.onResume(trackings.get(pos));
                        break;
                }
            }
        }
    }

    /**
     * list item callback interface
     */
    public interface TrackingItemActionListener {
        void onDelete(Tracking t);

        void onResume(Tracking t);
    }

    /**
     * list item view holder
     */
    static class ViewHolder {
        public TextView clock;
        public TextView title;
        public ImageButton delete;
        public ImageButton resumeOrPause;
    }

    /**
     * ctor
     *
     * @param ctx       a context
     * @param trackings trackings to display
     * @param listener  list item actions callback listener
     */
    public TrackingListAdapter(Context ctx, List<Tracking> trackings, TrackingItemActionListener listener) {
        this.inflater = LayoutInflater.from(ctx);
        setTrackings(trackings);
        this.listener = listener;
        this.unnamedTitle = ctx.getString(R.string.list_item_tracking_unnamed_title);
    }

    /**
     * updates trackings list to display
     *
     * @param trackings the trackings to display
     */
    public void setTrackings(List<Tracking> trackings) {
        if (trackings == null) {
            this.trackings.clear();
        } else {
            this.trackings = trackings;
        }
        notifyDataSetChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        return trackings.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getItem(int position) {
        return trackings.get(position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_tracking, null);
            ViewHolder holder = new ViewHolder();
            holder.clock = (TextView) convertView.findViewById(R.id.list_item_tracking_clock);
            holder.title = (TextView) convertView.findViewById(R.id.list_item_tracking_title);
            holder.delete = (ImageButton) convertView.findViewById(R.id.list_item_tracking_btn_delete);
            holder.resumeOrPause = (ImageButton) convertView.findViewById(R.id.list_item_tracking_btn_resume_pause);
            convertView.setTag(holder);
        }
        Tracking t = trackings.get(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();

        // clock
        holder.clock.setText(DateUtils.formatElapsedTime(sb, t.getDuration() / 1000));

        // title
        holder.title.setText(TextUtils.isEmpty(t.getTitle()) ? unnamedTitle : t.getTitle());

        // buttons
        TrackingItemClickListener clickListener = new TrackingItemClickListener(position);
        holder.delete.setOnClickListener(clickListener);
        holder.resumeOrPause.setOnClickListener(clickListener);
        holder.resumeOrPause.setImageResource(t.isTracking() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);

        return convertView;
    }
}
