package org.parallelzero.hancel.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hpsaturn.tools.DeviceUtil;
import com.hpsaturn.tools.UITools;

import org.parallelzero.hancel.Config;
import org.parallelzero.hancel.R;

import java.util.Objects;

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
        TextView aboutText = view.findViewById(R.id.AboutTextL1);

        aboutText.setText(
                String.format(getString(R.string.about_licence_l1),
                        DeviceUtil.getVersionName(Objects.requireNonNull(getActivity())),
                        DeviceUtil.getVersionCode(getActivity())+""
                )
        );

        _sv_about = view.findViewById(R.id.sv_about);
        Animation translatebu= AnimationUtils.loadAnimation(getActivity(), R.anim.about);
        _sv_about.startAnimation(translatebu);

        return view;
    }

    private View.OnClickListener onSurverClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            UITools.viewLink(Objects.requireNonNull(getActivity()), _tv_survey.getText().toString());
        }
    };
}
