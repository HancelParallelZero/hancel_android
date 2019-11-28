package org.parallelzero.hancel.Fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import org.parallelzero.hancel.Config;
import org.parallelzero.hancel.MainActivity;
import org.parallelzero.hancel.R;
import org.parallelzero.hancel.models.Partner;
import org.parallelzero.hancel.view.ItemTouchHelperAdapter;
import org.parallelzero.hancel.view.ListPartnersAdapter;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/5/15.
 */
public class MapPartnersFragment extends Fragment {

    public static final String TAG = MapPartnersFragment.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;

    private RecyclerView mPartnersRecycler;
    private ListPartnersAdapter mPartnersAdapter;
    private TextView mEmptyMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_partners,container,false);

        mEmptyMessage = (TextView)view.findViewById(R.id.tv_partners_empty_list);
        mPartnersRecycler = (RecyclerView) view.findViewById(R.id.rv_partners);



        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mPartnersRecycler.setLayoutManager(layoutManager);

        mPartnersAdapter = new ListPartnersAdapter();
        mPartnersAdapter.setOnItemClickListener(onItemClickListener);
//        mPartnersAdapter.addItem(0,new Partner("testing","Tuesday 12, 20:35"));
//        mPartnersAdapter.addItem(0,new Partner("testing","Tuesday 13, 20:35"));
//        mPartnersAdapter.addItem(0,new Partner("testing","Tuesday 14, 20:35"));
//        mPartnersAdapter.addItem(0,new Partner("testing","Tuesday 15, 20:35"));
        mPartnersRecycler.setAdapter(mPartnersAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mPartnersAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mPartnersRecycler);

        refreshUI();

        return view;
    }

    public void addPartner(Partner partner) {

        if(DEBUG)Log.d(TAG, "addPartner: " + partner.toString());
        mPartnersAdapter.addItem(0, partner);
//        Storage.saveRing(getActivity(),partner);
        refreshUI();

    }

    private void refreshUI(){
        if(mPartnersAdapter.getItemCount()>0){
            mEmptyMessage.setVisibility(View.GONE);
            mPartnersRecycler.setVisibility(View.VISIBLE);
        }else{
            mPartnersRecycler.setVisibility(View.GONE);
            mEmptyMessage.setVisibility(View.VISIBLE);
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
//            int position = viewHolder.getAdapterPosition();
//            if(direction==16) {
//                Storage.removeRing(getActivity(), mPartnersAdapter.getItem(position));
//                mAdapter.onItemDismiss(position);
//            }else if(direction==32){
//                Ring ring = mPartnersAdapter.getItem(position);
//                boolean enable = !ring.isEnable();
//                Storage.enableRing(getActivity(), ring, enable);
//                mAdapter.onItemDisable(position,enable);
//            }
//            refreshUI();
        }

    }

    private MainActivity getMain() {
        return ((MainActivity)getActivity());
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            if(DEBUG) Log.d(TAG, "OnItemClickListener => Clicked: " + position + ", index " + mPartnersRecycler.indexOfChild(view));
        }
    };

    private View.OnClickListener onRingAddClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(DEBUG) Log.d(TAG, "onRingAddClickListener");
            getMain().showAddContactsRingFragment();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
