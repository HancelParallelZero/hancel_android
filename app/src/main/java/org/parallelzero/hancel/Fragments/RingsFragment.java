package org.parallelzero.hancel.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.parallelzero.hancel.Config;
import org.parallelzero.hancel.MainActivity;
import org.parallelzero.hancel.R;
import org.parallelzero.hancel.System.Storage;
import org.parallelzero.hancel.models.Ring;
import org.parallelzero.hancel.view.ItemTouchHelperAdapter;
import org.parallelzero.hancel.view.ListRingsAdapter;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/5/15.
 */
public class RingsFragment extends Fragment {

    public static final String TAG = RingsFragment.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;

    private RecyclerView mRingsRecycler;
    private ListRingsAdapter mRingsAdapter;
    private TextView mEmptyMessage;
    private FloatingActionButton mAddRingFromContacts;
    private FloatingActionButton mAddRingFromQRCode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rings,container,false);

        mRingsRecycler= (RecyclerView) view.findViewById(R.id.rv_rings);
        mAddRingFromContacts = (FloatingActionButton) view.findViewById(R.id.bt_rings_from_contacts);
        mAddRingFromQRCode = (FloatingActionButton) view.findViewById(R.id.bt_rings_from_qrcode);
        mAddRingFromContacts.setIconDrawable(getResources().getDrawable(R.drawable.ic_contact_mail_white_36dp));
        mAddRingFromQRCode.setIconDrawable(getResources().getDrawable(R.drawable.ic_qrcode_scan_white_36dp));
        mAddRingFromContacts.setOnClickListener(onRingAddFromContactsListener);
        mAddRingFromQRCode.setOnClickListener(onRingAddFromQRcodeListener);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRingsRecycler.setLayoutManager(gridLayoutManager);

        mRingsAdapter= new ListRingsAdapter();
        mRingsAdapter.setOnItemClickListener(onItemClickListener);
        mRingsAdapter.loadData(Storage.getRings(getActivity()));
        mRingsRecycler.setAdapter(mRingsAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mRingsAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRingsRecycler);

        refreshUI();

        return view;
    }

    public void addRing(Ring ring) {

        if(DEBUG)Log.d(TAG, "addRing: " + ring.toString());
        mRingsAdapter.addItem(0, ring);
        Storage.saveRing(getActivity(),ring);
        refreshUI();

    }

    private void refreshUI(){
        if(mRingsAdapter.getItemCount()>0){
            mRingsRecycler.setVisibility(View.VISIBLE);
        }else{
            mRingsRecycler.setVisibility(View.GONE);
        }

    }

    public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

        private final ItemTouchHelperAdapter mAdapter;

        public ItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);

        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            if (DEBUG) Log.d(TAG, "ItemTouchHelperCallback: onMove");
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            if (DEBUG) Log.d(TAG, "ItemTouchHelperCallback: onSwiped: direction="+direction);
            int position = viewHolder.getAdapterPosition();
            if(direction==16) {
                Storage.removeRing(getActivity(), mRingsAdapter.getItem(position));
                mAdapter.onItemDismiss(position);
            }else if(direction==32){
                Ring ring = mRingsAdapter.getItem(position);
                boolean enable = !ring.isEnable();
                Storage.enableRing(getActivity(), ring, enable);
                mAdapter.onItemDisable(position,enable);
            }
            refreshUI();
        }

    }

    private MainActivity getMain() {
        return ((MainActivity)getActivity());
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            if(DEBUG) Log.d(TAG, "OnItemClickListener => Clicked: " + position + ", index " + mRingsRecycler.indexOfChild(view));
        }
    };

    private View.OnClickListener onRingAddFromContactsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(DEBUG) Log.d(TAG, "onRingAddFromContactsListener");
            getMain().showRingEditFragment();
        }


    };
    private View.OnClickListener onRingAddFromQRcodeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(DEBUG) Log.d(TAG, "onRingAddFromQRcodeListener");
            getMain().showRingEditFragment();
        }
    };

    @Override
    public void onDestroy() {
        getMain().fabHide();
        super.onDestroy();
    }


}
