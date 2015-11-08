package org.parallelzero.hancel.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import org.parallelzero.hancel.R;
import org.parallelzero.hancel.models.Contact;
import org.parallelzero.hancel.view.ListContactsAdapter.ContactViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Antonio Vanegas @hpsaturn on 10/20/15.
 */

public class ListContactsAdapter extends RecyclerView.Adapter<ContactViewHolder> implements ItemTouchHelperAdapter {

    private AdapterView.OnItemClickListener mOnItemClickListener;
    private Context ctx;
    private ArrayList<Contact> contacts = new ArrayList<>();

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rowView = inflater.inflate(R.layout.fragment_ring_contact_item, parent, false);
        this.ctx = parent.getContext();
        return new ContactViewHolder(rowView, this);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        final Contact contact = contacts.get(position);
        holder.contact_name.setText(contact.getName());
        holder.contact_phone.setText(contact.getPhone());
//        holder.contact_photo.setImageBitmap(contact.getPhoto());
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void updateData(ArrayList<Contact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    public void addItem(int position, Contact contact) {
        if (position > contacts.size()) return;
        contacts.add(position, contact);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        if (position >= contacts.size()) return;
        contacts.remove(position);
        notifyItemRemoved(position);
    }

    public Contact getItem(int position) {
        return contacts.get(position);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void onItemHolderClick(ContactViewHolder itemHolder) {

        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(
                    null, itemHolder.itemView, itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }

    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(contacts, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(contacts, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        contacts.remove(position);
        notifyItemRemoved(position);
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected final TextView contact_name;
        protected final TextView contact_phone;
//        protected final ImageView contact_photo;

        private final ListContactsAdapter mAdapter;


        public ContactViewHolder(View itemView, ListContactsAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.mAdapter = adapter;
            contact_name = (TextView) itemView.findViewById(R.id.tv_contact_name);
            contact_phone = (TextView) itemView.findViewById(R.id.tv_contact_phone);
//            contact_photo = (ImageView) itemView.findViewById(R.id.iv_contact_icon);
        }

        @Override
        public void onClick(View view) {
            mAdapter.onItemHolderClick(this);
        }
    }


}
