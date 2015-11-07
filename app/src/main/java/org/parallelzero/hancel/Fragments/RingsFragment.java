package org.parallelzero.hancel.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parallelzero.hancel.Config;
import org.parallelzero.hancel.MainActivity;
import org.parallelzero.hancel.R;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/5/15.
 */
public class RingsFragment extends Fragment {

    public static final String TAG = RingsFragment.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rings,container,false);
        getMain().fabSetOnClickListener(onRingAddClickListener);
        getMain().fabShow();

        return view;
    }

    private MainActivity getMain() {
        return ((MainActivity)getActivity());
    }


    private View.OnClickListener onRingAddClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(DEBUG) Log.d(TAG, "onRingAddClickListener");
        }
    };

    @Override
    public void onDestroy() {
        getMain().fabHide();
        super.onDestroy();
    }
}
