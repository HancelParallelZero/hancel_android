package org.parallelzero.hancel.Fragments;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parallelzero.hancel.Config;
import org.parallelzero.hancel.R;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/8/15.
 */
public class TestDialogFragment extends DialogFragment {

    private static final boolean DEBUG = Config.DEBUG;
    public static final String TAG = TestDialogFragment.class.getSimpleName();
    int mNum;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static TestDialogFragment newInstance(int num) {
        TestDialogFragment f = new TestDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getInt("num");

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        int switchnum = (mNum - 1) % 8;
        switch (switchnum) {
            case 1: style = DialogFragment.STYLE_NO_TITLE; break;
            case 2: style = DialogFragment.STYLE_NO_FRAME; break;
            case 3: style = DialogFragment.STYLE_NO_INPUT; break;
            case 4: style = DialogFragment.STYLE_NORMAL; break;
            case 5: style = DialogFragment.STYLE_NORMAL; break;
            case 6: style = DialogFragment.STYLE_NO_TITLE; break;
            case 7: style = DialogFragment.STYLE_NO_FRAME; break;
            case 8: style = DialogFragment.STYLE_NORMAL; break;
        }
        switch (switchnum) {
            case 4: theme = android.R.style.Theme_Holo; break;
            case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
            case 6: theme = android.R.style.Theme_Holo_Light; break;
            case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
            case 8: theme = android.R.style.Theme_Holo_Light; break;
        }
        if(DEBUG) Log.d(TAG, "num:" + switchnum);
        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_confirm, container, false);
//        View tv = v.findViewById(R.id.text);
//        ((TextView)tv).setText("Dialog #" + mNum + ": using style " + getNameForNum(mNum));


        return v;
    }


}
