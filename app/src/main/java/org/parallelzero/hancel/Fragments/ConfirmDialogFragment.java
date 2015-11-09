package org.parallelzero.hancel.Fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
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

        int style = DialogFragment.STYLE_NO_TITLE;
        setStyle(style, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_confirm, container, false);

        mButtonSMS = (Button) v.findViewById(R.id.bt_alert_dialog_sms);
        mButtonShare = (Button) v.findViewById(R.id.bt_alert_dialog_share);

        mButtonSMS.setOnClickListener(onSMSClickListener);
        mButtonShare.setOnClickListener(onShareClickListener);

        if (Storage.getRings(getActivity()).size() == 0) setSMSButtonEnable(false);
        if (Storage.getRingsEnable(getActivity()).size() == 0) setSMSButtonEnable(false);  // TODO: fix not work!

        return v;
    }

    private void setSMSButtonEnable(boolean enable) {
        if (enable) {
            mButtonSMS.getBackground().setColorFilter(ContextCompat.getColor(getActivity(), R.color.red), PorterDuff.Mode.MULTIPLY);
            this.sms_enable = enable;
        } else {
            mButtonSMS.getBackground().setColorFilter(ContextCompat.getColor(getActivity(), R.color.grey), PorterDuff.Mode.MULTIPLY);
            this.sms_enable = false;
        }
    }

    private View.OnClickListener onSMSClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (DEBUG) Log.d(TAG, "onSMSClickListener");
            if (!sms_enable) Tools.showToast(getActivity(), R.string.msg_alert_not_rings);
            else {

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
        if (Storage.getRings(getActivity()).size() == 0) setSMSButtonEnable(false);
        else setSMSButtonEnable(true);
    }

    private MainActivity getMain() {
        return ((MainActivity) getActivity());
    }

}
