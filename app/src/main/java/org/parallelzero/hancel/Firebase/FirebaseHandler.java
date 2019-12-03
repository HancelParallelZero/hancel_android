package org.parallelzero.hancel.Firebase;

import android.util.Log;
import org.parallelzero.hancel.Config;
import org.parallelzero.hancel.services.TrackLocationService;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/2/15.
 */
public class FirebaseHandler {

    public static final String TAG = TrackLocationService.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;

    private void subscribeTransaction(String trackpath) {
        if (DEBUG) Log.d(TAG, "subscribeTransaction to transaction: ");
    }

}
