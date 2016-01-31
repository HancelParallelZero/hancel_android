package org.parallelzero.hancel.Fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.parallelzero.hancel.Config;
import org.parallelzero.hancel.MainActivity;
import org.parallelzero.hancel.R;
import org.parallelzero.hancel.System.Storage;
import org.parallelzero.hancel.System.Tools;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/8/15.
 */
public class ConfirmDialogFragment extends DialogFragment {

    private static final boolean DEBUG = Config.DEBUG;
    public static final String TAG = ConfirmDialogFragment.class.getSimpleName();
    private Button mButtonSMS;
    private Button mButtonShare;
    private boolean sms_enable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int theme=R.style.BaseTheme_Dialog;
        int style = DialogFragment.STYLE_NORMAL;
        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_confirm, container, false);

        mButtonSMS = (Button) v.findViewById(R.id.bt_alert_dialog_sms);
        mButtonShare = (Button) v.findViewById(R.id.bt_alert_dialog_share);

        mButtonSMS.setOnClickListener(onSMSClickListener);
        mButtonShare.setOnClickListener(onShareClickListener);

        validateSMS();

        return v;
    }

    private void validateSMS() {

        if (Storage.getRingsEnable(getActivity()).size() == 0 || Storage.getRings(getActivity()).size() == 0) {
            Tools.setButtonTintBackground(getActivity(), mButtonSMS, R.color.grey);
            sms_enable = false;
        }
        else {
            Tools.setButtonTintBackground(getActivity(),mButtonSMS,R.color.red);
            sms_enable = true;
        }

    }

    private View.OnClickListener onSMSClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (DEBUG) Log.d(TAG, "onSMSClickListener");
            if (!sms_enable) Tools.showToast(getActivity(), R.string.msg_alert_not_rings);
            else {
                getMain().sendSMS();
                getDialog().dismiss();
            }
        }
    };


    private View.OnClickListener onShareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (DEBUG) Log.d(TAG, "onShareClickListener");
            getMain().shareLocation();
            getDialog().dismiss();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        validateSMS();
    }

    private MainActivity getMain() {
        return ((MainActivity) getActivity());
    }

}
