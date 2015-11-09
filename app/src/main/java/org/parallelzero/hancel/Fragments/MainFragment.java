package org.parallelzero.hancel.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import org.parallelzero.hancel.MainActivity;
import org.parallelzero.hancel.R;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/7/15.
 */
public class MainFragment extends Fragment {

    public static final String TAG = MainFragment.class.getSimpleName();
    private Button mMainButton;
    private SwitchCompat mServiceButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main,container,false);
        mMainButton = (Button)view.findViewById(R.id.bt_home_main_alert);
        mServiceButton = (SwitchCompat)view.findViewById(R.id.tb_home_enable_tracking);
        mMainButton.setOnClickListener(onMainButtonListener);
        mServiceButton.setOnCheckedChangeListener(onServiceEnableChecked);

        return view;

    }

    public void setServiceButtonEnable(boolean enable){
        mServiceButton.setChecked(enable);
    }

    private View.OnClickListener onMainButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getMain().showConfirmAlertFragment();
        }
    };

    private CompoundButton.OnCheckedChangeListener onServiceEnableChecked = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if(isChecked)getMain().startTrackLocationService();
            else getMain().stopTrackLocationService();
        }
    };

    private MainActivity getMain() {
        return ((MainActivity)getActivity());
    }


}
