package org.parallelzero.hancel.Fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;

import org.parallelzero.hancel.Config;
import org.parallelzero.hancel.R;

/**
 * Created by izel on 9/11/15.
 */
public class AboutFragment extends Fragment {
    public static final String TAG = AboutFragment.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;
    private ScrollView _sv_about;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.about, container, false);
        TextView aboutText = (TextView) view.findViewById(R.id.AboutText);
        try {
            aboutText.setText(String.format(getString(R.string.about_licence),
                    getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),
                            0).versionName));
        }
        catch (PackageManager.NameNotFoundException e) {
            if(DEBUG) Log.i(TAG, "cannot get version name");
        }

        _sv_about = (ScrollView)view.findViewById(R.id.sv_about);
        Animation translatebu= AnimationUtils.loadAnimation(getActivity(), R.anim.about);
        _sv_about.startAnimation(translatebu);

        return view;
    }
}
