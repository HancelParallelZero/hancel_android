package org.parallelzero.hancel.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.parallelzero.hancel.R;
import org.parallelzero.hancel.models.Track;
import org.parallelzero.hancel.view.ListTrackersAdapter.TrackerViewHolder;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/10/15.
 */
public class ListTrackersAdapter extends RecyclerView.Adapter<TrackerViewHolder> implements ItemTouchHelperAdapter{

    private AdapterView.OnItemClickListener mOnItemClickListener;
    private Context ctx;
    private ArrayList<Track> tracks = new ArrayList<>();

    @Override
    public TrackerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rowView = inflater.inflate(R.layout.fragment_partners_item, parent, false);
        this.ctx = parent.getContext();
        return new TrackerViewHolder(rowView, this);
    }

    @Override
    public void onBindViewHolder(TrackerViewHolder holder, int position) {
        final Track track = tracks.get(position);
        holder.track_alias.setText(track.alias);
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public void loadData(ArrayList<Track> rings) {
        this.tracks = rings;
        notifyDataSetChanged();
    }

    public void addItem(int position, Track track) {
        if (position > tracks.size()) return;
        tracks.add(position, track);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        if (position >= tracks.size()) return;
        tracks.remove(position);
        notifyItemRemoved(position);
    }

    public Track getItem(int position) {
        return tracks.get(position);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void onItemHolderClick(TrackerViewHolder itemHolder) {

        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, itemHolder.itemView, itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }

    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(tracks, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(tracks, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        tracks.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemDisable(int position, boolean enable) {
        tracks.get(position).setEnable(enable);
        notifyDataSetChanged();
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public class TrackerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected final RelativeLayout track_ly;
        protected final TextView track_alias;
        private final ListTrackersAdapter mAdapter;

        public TrackerViewHolder(View itemView, ListTrackersAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.mAdapter = adapter;
            track_alias = (TextView) itemView.findViewById(R.id.tv_partner_alias);
            track_ly = (RelativeLayout) itemView.findViewById(R.id.rl_track);
        }

        @Override
        public void onClick(View view) {
            mAdapter.onItemHolderClick(this);
        }
    }

}
