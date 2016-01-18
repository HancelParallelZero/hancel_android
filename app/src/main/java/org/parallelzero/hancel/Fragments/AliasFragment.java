package org.parallelzero.hancel.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;

import org.parallelzero.hancel.MainActivity;
import org.parallelzero.hancel.R;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/7/15.
 */
public class AliasFragment extends Fragment {

    public static final String TAG = AliasFragment.class.getSimpleName();
    private Button mAcceptButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alias,container,false);
        mAcceptButton = (Button)view.findViewById(R.id.bt_alias_accept);
        mAcceptButton.setOnClickListener(onAliasAcceptListener);

        return view;

    }

    private View.OnClickListener onAliasAcceptListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    private MainActivity getMain() {
        return ((MainActivity)getActivity());
    }


}
