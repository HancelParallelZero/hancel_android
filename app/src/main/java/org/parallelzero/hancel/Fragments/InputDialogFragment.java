package org.parallelzero.hancel.Fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hpsaturn.tools.UITools;

import org.parallelzero.hancel.Config;
import org.parallelzero.hancel.MainActivity;
import org.parallelzero.hancel.R;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/9/15.
 */
public class InputDialogFragment extends DialogFragment {

    public static final String TAG = InputDialogFragment.class.getSimpleName();
    private static final String TRACK_ID = "trackId";
    private static final boolean DEBUG = Config.DEBUG;

    private EditText mEditAlias;
    private Button mButtonContinue;
    private String trackId;
    private String alias;

    public static InputDialogFragment newInstance(String trackId){

        InputDialogFragment dialog = new InputDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(TRACK_ID, trackId);
        dialog.setArguments(args);

        if(DEBUG) Log.d(TAG, "InputDialogFragment for trackId" + trackId);

        return dialog;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        trackId=getArguments().getString(TRACK_ID);

        int style = DialogFragment.STYLE_NO_TITLE;
        setStyle(style, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_input, container, false);

        mEditAlias = (EditText)v.findViewById(R.id.et_alias_name);
        mButtonContinue = (Button)v.findViewById(R.id.bt_alias_continue);
        mButtonContinue.setOnClickListener(onClickContinueListener);

        return v;

    }

    private View.OnClickListener onClickContinueListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//            if(isValidData())getMain().newTrackId(trackId,alias);
            getDialog().dismiss();
        }
    };

    private boolean isValidData() {
        alias = mEditAlias.getText().toString();
        if(alias.length()==0){
            UITools.showToast(getActivity(),R.string.msg_track_alias);
            return false;
        }
        return true;
    }


    private MainActivity getMain() {
        return ((MainActivity) getActivity());
    }


}
