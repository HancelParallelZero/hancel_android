package org.parallelzero.hancel.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import org.parallelzero.hancel.R;
import org.parallelzero.hancel.models.Partner;
import org.parallelzero.hancel.view.ListPartnersAdapter.PartnerViewHolder;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Antonio Vanegas @hpsaturn on 10/20/15.
 */

public class ListPartnersAdapter extends RecyclerView.Adapter<PartnerViewHolder> implements ItemTouchHelperAdapter {

    private AdapterView.OnItemClickListener mOnItemClickListener;
    private Context ctx;
    private ArrayList<Partner> partners = new ArrayList<>();

    @Override
    public PartnerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rowView = inflater.inflate(R.layout.fragment_partners_item, parent, false);
        this.ctx = parent.getContext();
        return new PartnerViewHolder (rowView, this);
    }

    @Override
    public void onBindViewHolder(PartnerViewHolder holder, int position) {
        final Partner partner = partners.get(position);
        holder.alias.setText(partner.getAlias());
        holder.last_update.setText(partner.getLast_update());
    }

    @Override
    public int getItemCount() {
        return partners.size();
    }

    public void loadData(ArrayList<Partner> partners) {
        this.partners = partners;
        notifyDataSetChanged();
    }

    public void addItem(int position, Partner partner) {
        if (position > partners.size()) return;
        partners.add(position, partner);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        if (position >= partners.size()) return;
        partners.remove(position);
        notifyItemRemoved(position);
    }

    public Partner getItem(int position) {
        return partners.get(position);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void onItemHolderClick(PartnerViewHolder itemHolder) {

        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, itemHolder.itemView, itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }

    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(partners, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(partners, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        partners.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemDisable(int position, boolean enable) {
//        partners.get(position).setEnable(enable);
        notifyDataSetChanged();
    }

    public ArrayList<Partner> getPartners() {
        return partners;
    }

    public class PartnerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected final TextView alias;
        protected final TextView last_update;
        private final ListPartnersAdapter mAdapter;


        public PartnerViewHolder(View itemView, ListPartnersAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.mAdapter = adapter;
            alias = (TextView) itemView.findViewById(R.id.tv_partner_alias);
            last_update = (TextView) itemView.findViewById(R.id.tv_partner_last_update);

        }

        @Override
        public void onClick(View view) {
            mAdapter.onItemHolderClick(this);
        }
    }


}
