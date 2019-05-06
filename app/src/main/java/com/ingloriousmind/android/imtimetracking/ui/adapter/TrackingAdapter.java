package com.ingloriousmind.android.imtimetracking.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ingloriousmind.android.imtimetracking.R;
import com.ingloriousmind.android.imtimetracking.model.Tracking;
import com.ingloriousmind.android.imtimetracking.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * tracking recycler adapter
 *
 * @author lavong.soysavanh
 */
public class TrackingAdapter extends RecyclerView.Adapter<TrackingAdapter.ViewHolder> {

    /**
     * layout inflater
     */
    private final LayoutInflater inflater;

    /**
     * onClick callback
     */
    private final TrackingItemActionListener listener;

    /**
     * adapter model
     */
    private List<Tracking> trackings = new ArrayList<>();

    /**
     * view holder class
     */
    public final static class ViewHolder extends RecyclerView.ViewHolder {
        TextView clock;
        TextView title;
        ImageButton resumeOrPause;

        public ViewHolder(View itemView) {
            super(itemView);
            clock = itemView.findViewById(R.id.list_item_tracking_clock);
            title = itemView.findViewById(R.id.list_item_tracking_title);
            resumeOrPause = itemView.findViewById(R.id.list_item_tracking_btn_resume_pause);
        }
    }

    /**
     * list item click listener
     */
    private class TrackingItemClickListener implements View.OnClickListener {

        private Tracking t;

        public TrackingItemClickListener(Tracking t) {
            this.t = t;
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int pos = indexOf(t);
                switch (v.getId()) {
                    case R.id.list_item_tracking_btn_resume_pause:
                        listener.onResume(pos, t);
                        break;
                    default:
                        listener.onEdit(pos, t);
                        break;
                }
            }
        }
    }

    /**
     * list item callback interface
     */
    public interface TrackingItemActionListener {
        void onEdit(int pos, Tracking t);

        void onResume(int pos, Tracking t);
    }

    /**
     * ctor
     *
     * @param ctx       a context
     * @param trackings initial set of trackings to display
     * @param listener  item click callback
     */
    public TrackingAdapter(Context ctx, List<Tracking> trackings, TrackingItemActionListener listener) {
        this.inflater = LayoutInflater.from(ctx);
        setTrackings(trackings);
        this.listener = listener;
    }

    /**
     * updates trackings list to display
     *
     * @param trackings the trackings to display
     */
    public void setTrackings(List<Tracking> trackings) {
        this.trackings.clear();
        if (trackings != null && !trackings.isEmpty()) {
            this.trackings.addAll(trackings);
        }
        notifyDataSetChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.list_item_tracking, null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Tracking t = trackings.get(position);

        // clock
        holder.clock.setText(TimeUtil.getTimeString(t.getDuration()));

        // title
        holder.title.setText(t.getTitle());

        // buttons
        TrackingItemClickListener clickListener = new TrackingItemClickListener(t);
        holder.resumeOrPause.setOnClickListener(clickListener);
        holder.resumeOrPause.setImageResource(t.isTracking() ? R.drawable.btn_pause : R.drawable.btn_play);
        holder.itemView.setOnClickListener(clickListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return trackings.size();
    }

    /**
     * remove item at given position
     *
     * @param position the items position to remove
     */
    public void removeTracking(int position) {
        if (position >= 0 && position < trackings.size()) {
            trackings.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * adds tracking
     *
     * @param t tracking to add
     */
    public void addTracking(Tracking t) {
        if (t != null && !trackings.contains(t)) {
            trackings.add(0, t);
            notifyItemInserted(0);
        }
    }

    /**
     * returns the item position for given {@link com.ingloriousmind.android.imtimetracking.model.Tracking}
     *
     * @param t tracking to look up index for
     * @return index of given tracking. -1 for not in list.
     */
    public int indexOf(Tracking t) {
        return trackings.indexOf(t);
    }

}
