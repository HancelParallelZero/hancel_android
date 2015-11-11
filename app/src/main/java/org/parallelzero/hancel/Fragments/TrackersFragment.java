package org.parallelzero.hancel.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parallelzero.hancel.R;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/10/15.
 */
public class TrackersFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trackers,container,false);

        return v;
    }
}
