package org.parallelzero.hancel.Fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SwitchCompat;
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
public class MainFragment extends Fragment {

    public static final String TAG = MainFragment.class.getSimpleName();
    private Button mMainButton;
    private SwitchCompat mServiceButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main,container,false);
        mMainButton = view.findViewById(R.id.bt_home_main_alert);
        mServiceButton = view.findViewById(R.id.tb_home_enable_tracking);
        mMainButton.setOnClickListener(onMainButtonListener);
        mServiceButton.setOnCheckedChangeListener(onServiceEnableChecked);

        return view;

    }

    public void setServiceButtonEnable(boolean enable){
        mServiceButton.setChecked(enable);
    }

    private View.OnClickListener onMainButtonListener = view -> getMain().showConfirmAlertFragment();

    private CompoundButton.OnCheckedChangeListener onServiceEnableChecked = (compoundButton, isChecked) -> {
        if(isChecked)getMain().startTrackLocationService();
        else getMain().stopTrackLocationService();
    };

    private MainActivity getMain() {
        return ((MainActivity)getActivity());
    }


}
