package org.parallelzero.hancel.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.parallelzero.hancel.Config;
import org.parallelzero.hancel.R;


/**
 * Created by izel on 8/11/15.
 */
public class SlideItemFragment extends Fragment {

    public static String TAG = SlideItemFragment.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;
    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private static final String ARG_LAYOUT_BG_ID = "layoutBackgroundId";
    private int layoutResId;
    private int layoutBgId;

    public static SlideItemFragment newInstance(int layoutResId) {
        SlideItemFragment sampleSlide = new SlideItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    public static SlideItemFragment newInstance(int layoutResId, int background){
        SlideItemFragment sampleSlide = new SlideItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);
        args.putInt(ARG_LAYOUT_BG_ID, background);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }


    public SlideItemFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, getArguments() != null?"NULL":"NOT NULL");

        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID))
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);

        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_BG_ID))
            layoutBgId = getArguments().getInt(ARG_LAYOUT_BG_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(DEBUG) Log.i(TAG, "onCreateView " + " " + layoutBgId + " " + layoutResId);

        if(layoutResId !=0 && layoutBgId != 0) {
            View rootView = inflater.inflate(R.layout.welcome, container, false);
            RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.rl_welcome_bigimage);
            layout.setBackgroundResource(layoutBgId);
            return rootView;
        }
        else if(layoutResId != 0)
            return inflater.inflate(layoutResId, container, false);

        return null;
    }
}
