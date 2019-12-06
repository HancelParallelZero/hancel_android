package org.parallelzero.hancel.Fragments;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hpsaturn.tools.UITools;

import org.parallelzero.hancel.Config;
import org.parallelzero.hancel.MainActivity;
import org.parallelzero.hancel.R;
import org.parallelzero.hancel.models.Contact;
import org.parallelzero.hancel.models.Ring;
import org.parallelzero.hancel.view.ItemTouchHelperAdapter;
import org.parallelzero.hancel.view.ListContactsAdapter;

import java.util.List;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/5/15.
 */
public class ContactsRingFragment extends DialogFragment {

    public static final String TAG = ContactsRingFragment.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;

    private Button mButtonPicker;
    private Button mButtonSave;

    private ListContactsAdapter mContactsAdapter;
    private RecyclerView mContactsRecycler;
    private TextView mEmptyMessage;
    private EditText mEditRingName;
    private String name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        int theme = android.R.style.Theme_DeviceDefault_Light_Dialog_Alert;
        int theme = R.style.BaseTheme_DialogHeigh;
        int style = DialogFragment.STYLE_NORMAL;
        setStyle(style,theme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ring_edit, container, false);

        mEditRingName = (EditText)view.findViewById(R.id.et_ring_edit_name);
        mEmptyMessage = (TextView)view.findViewById(R.id.tv_ring_contacts_empty);
        mButtonPicker = (Button)view.findViewById(R.id.bt_ring_edit_pick_contact);
        mButtonSave   = (Button)view.findViewById(R.id.bt_ring_edit_save);
        mContactsRecycler= (RecyclerView) view.findViewById(R.id.rv_contacts);
        mButtonPicker.setOnClickListener(onPickerContactListener);
        mButtonSave.setOnClickListener(onSaveButtonListener);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mContactsRecycler.setLayoutManager(gridLayoutManager);

        mContactsAdapter= new ListContactsAdapter();
        mContactsAdapter.setOnItemClickListener(onItemClickListener);
        mContactsRecycler.setAdapter(mContactsAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mContactsAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mContactsRecycler);

        return view;

    }

    public void addConctact(Contact contact){
        if(DEBUG)Log.d(TAG,"addConctact: "+contact.toString());
        mEmptyMessage.setVisibility(View.GONE);
        mContactsRecycler.setVisibility(View.VISIBLE);
        mContactsAdapter.addItem(0,contact);
    }

    public List<Contact> getContacts() {
        return mContactsAdapter.getContacts();
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
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
//            getMain().getCharactersFragment().notifyPlayersChange();
        }

    }

    @Override
    public void onDestroy() {
        if(DEBUG)Log.d(TAG,"onDestroy");
        super.onDestroy();
    }


    private boolean isValidData() {

        name = mEditRingName.getText().toString();
        if(name.length()==0){
            UITools.showToast(getActivity(),R.string.error_ring_empty_name);
            return false;
        }
        else if (mContactsAdapter.getItemCount()==0){
            UITools.showToast(getActivity(),R.string.error_ring_empty_contacts);
            return false;
        }
        return true;
    }

    private Ring saveData() {
        Ring ring = new Ring();
        ring.setName(name);
        ring.setContacts(mContactsAdapter.getContacts());
        ring.setDescription(""+mContactsAdapter.getItemCount()+" "+getString(R.string.ring_desc_contacts));
        return ring;
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            if(DEBUG) Log.d(TAG, "OnItemClickListener => Clicked: " + position + ", index " + mContactsRecycler.indexOfChild(view));
        }
    };

    private View.OnClickListener onPickerContactListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getMain().getContact();
        }
    };

    private View.OnClickListener onSaveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(isValidData()){
                if(DEBUG)Log.d(TAG,"saving ring..");
                getMain().getRingsFragment().addRing(saveData());
                getDialog().dismiss();
            }
        }
    };

    private MainActivity getMain() {
        return ((MainActivity)getActivity());
    }


}
