package org.parallelzero.hancel.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hpsaturn.tools.UITools;

import org.parallelzero.hancel.MainActivity;
import org.parallelzero.hancel.R;
import org.parallelzero.hancel.System.Storage;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/7/15.
 */
public class AliasFragment extends Fragment {

    public static final String TAG = AliasFragment.class.getSimpleName();
    private Button mAcceptButton;
    private EditText mAliasEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alias,container,false);
        mAcceptButton = (Button)view.findViewById(R.id.bt_alias_accept);
        mAliasEdit = (EditText)view.findViewById(R.id.et_alias_alias);
        mAcceptButton.setOnClickListener(onAliasAcceptListener);

        return view;

    }

    private View.OnClickListener onAliasAcceptListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String alias =mAliasEdit.getText().toString();
            if(alias.length()!=0) {
                Storage.setCurrentAlias(getActivity(),alias);
                getMain().removeAliasFragment();
            }
            else UITools.showToast(getActivity(),R.string.msg_alias_help);
        }
    };

    private MainActivity getMain() {
        return ((MainActivity)getActivity());
    }


}
