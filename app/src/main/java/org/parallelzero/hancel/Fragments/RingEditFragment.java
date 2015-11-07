package org.parallelzero.hancel.Fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.parallelzero.hancel.MainActivity;
import org.parallelzero.hancel.R;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/5/15.
 */
public class RingEditFragment extends DialogFragment {

    public static final String TAG = RingEditFragment.class.getSimpleName();
    private Button _bt_picker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ring_edit, container, false);

        _bt_picker = (Button)view.findViewById(R.id.bt_ring_edit_pick_contact);
        _bt_picker.setOnClickListener(onPickerContactListener);

        return view;
    }

    private View.OnClickListener onPickerContactListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getMain().getContact();
        }
    };

    private MainActivity getMain() {
        return ((MainActivity)getActivity());
    }


}
