package org.parallelzero.hancel.view;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import org.parallelzero.hancel.R;
import org.parallelzero.hancel.models.Ring;
import org.parallelzero.hancel.view.ListRingsAdapter.RingViewHolder;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Antonio Vanegas @hpsaturn on 10/20/15.
 */

public class ListRingsAdapter extends RecyclerView.Adapter<RingViewHolder> implements ItemTouchHelperAdapter {

    private AdapterView.OnItemClickListener mOnItemClickListener;
    private Context ctx;
    private ArrayList<Ring> rings = new ArrayList<>();

    @Override
    public RingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rowView = inflater.inflate(R.layout.fragment_rings_item, parent, false);
        this.ctx = parent.getContext();
        return new RingViewHolder(rowView, this);
    }

    @Override
    public void onBindViewHolder(RingViewHolder holder, int position) {
        final Ring ring = rings.get(position);
        int icon;
        if(ring.isEnable())icon=R.drawable.bt_rings_orange_rings;
        else icon=R.drawable.bt_rings_grey_rings;
        holder.ringIcon.setImageResource(icon);
        holder.ring_name.setText(ring.getName());
        holder.ring_description.setText(ring.getDescription());
    }

    @Override
    public int getItemCount() {
        return rings.size();
    }

    public void loadData(ArrayList<Ring> rings) {
        this.rings = rings;
        notifyDataSetChanged();
    }

    public void addItem(int position, Ring ring) {
        if (position > rings.size()) return;
        rings.add(position, ring);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        if (position >= rings.size()) return;
        rings.remove(position);
        notifyItemRemoved(position);
    }

    public Ring getItem(int position) {
        return rings.get(position);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void onItemHolderClick(RingViewHolder itemHolder) {

        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, itemHolder.itemView, itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }

    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(rings, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(rings, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        rings.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemDisable(int position, boolean enable) {
        rings.get(position).setEnable(enable);
        notifyDataSetChanged();
    }

    public List<Ring> getRings() {
        return rings;
    }

    public class RingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected final TextView ring_name;
        protected final TextView ring_description;
        protected final ImageView ringIcon;
        private final ListRingsAdapter mAdapter;


        public RingViewHolder(View itemView, ListRingsAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.mAdapter = adapter;
            ringIcon = (ImageView) itemView.findViewById(R.id.iv_ring_icon);
            ring_name = (TextView) itemView.findViewById(R.id.tv_ring_name);
            ring_description = (TextView) itemView.findViewById(R.id.tv_ring_description);

        }

        @Override
        public void onClick(View view) {
            mAdapter.onItemHolderClick(this);
        }
    }


}
