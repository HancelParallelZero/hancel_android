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
import org.parallelzero.hancel.System.Tools;

/**
 * Created by izel on 9/11/15.
 */
public class AboutFragment extends Fragment {
    public static final String TAG = AboutFragment.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;
    private ScrollView _sv_about;
    private TextView _tv_survey;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.about, container, false);
        TextView aboutText = (TextView) view.findViewById(R.id.AboutTextL1);

        aboutText.setText(String.format(getString(R.string.about_licence_l1),
                    Tools.getVersionName(getActivity()),
                    ""+Tools.getVersionCode(getActivity())));

        _tv_survey = (TextView)view.findViewById(R.id.tv_about_survey);
        _tv_survey.setOnClickListener(onSurverClickListener);

        _sv_about = (ScrollView)view.findViewById(R.id.sv_about);
        Animation translatebu= AnimationUtils.loadAnimation(getActivity(), R.anim.about);
        _sv_about.startAnimation(translatebu);

        return view;
    }

    private View.OnClickListener onSurverClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Tools.viewLink(getActivity(), _tv_survey.getText().toString());
        }
    };
}
