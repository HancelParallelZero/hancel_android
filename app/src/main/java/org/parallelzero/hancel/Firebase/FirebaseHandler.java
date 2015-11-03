package org.parallelzero.hancel.Firebase;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.parallelzero.hancel.Config;
import org.parallelzero.hancel.services.TrackLocationService;

import java.util.Map;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/2/15.
 */
public class FirebaseHandler {

    public static final String TAG = TrackLocationService.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;

    private void subscribeTransaction(Firebase fb,String trackpath) {
        if (DEBUG) Log.d(TAG, "subscribeTransaction to transaction: ");
        fb.child(Config.FIREBASE_MAIN + "/" + trackpath).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (DEBUG) Log.d(TAG, "onDataChange:" + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if (DEBUG) Log.d(TAG, "firebaseError:" + firebaseError.getMessage());
            }
        });

    }

}
