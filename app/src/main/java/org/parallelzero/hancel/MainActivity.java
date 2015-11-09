package org.parallelzero.hancel;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;

import io.fabric.sdk.android.Fabric;

import org.parallelzero.hancel.Fragments.AboutFragment;
import org.parallelzero.hancel.Fragments.ConfirmDialogFragment;
import org.parallelzero.hancel.Fragments.MainFragment;
import org.parallelzero.hancel.Fragments.MapTasksFragment;
import org.parallelzero.hancel.Fragments.RingEditFragment;
import org.parallelzero.hancel.Fragments.RingsFragment;
import org.parallelzero.hancel.Fragments.TestDialogFragment;
import org.parallelzero.hancel.System.Storage;
import org.parallelzero.hancel.System.Tools;
import org.parallelzero.hancel.models.Contact;
import org.parallelzero.hancel.services.TrackLocationService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MainActivity extends BaseActivity implements BaseActivity.OnPickerContact, OnMapReadyCallback {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;

    private Firebase fbRef;
    private OnTrackServiceConnected fbConnectReceiver;

    public MapTasksFragment tasksMap;
    private RingsFragment mRingsFragment;
    private RingEditFragment mRingEditFragment;
    private MainFragment mMainFragment;
    private AboutFragment mAboutFragment;
    private int mStackLevel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startIntro();
        initDrawer();
        showMain();
        fabHide();

        loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSIONS_REQUEST_FINE_LOCATION);

        new initStartAsync().execute();

    }

    private class initStartAsync extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

            Fabric.with(MainActivity.this, new Crashlytics());
            Firebase.setAndroidContext(MainActivity.this);
            fbRef = new Firebase(Config.FIREBASE_MAIN);
            String trackId = Tools.getAndroidDeviceId(MainActivity.this);
            Storage.setTrackId(MainActivity.this, trackId);

            setContactListener(MainActivity.this);

            loadDataFromIntent();

            if (Storage.isShareLocationEnable(MainActivity.this)) startTrackLocationService();

            return null;
        }
    }

    private void startIntro() {
        if (Storage.isFirstIntro(this)) {
            startActivity(new Intent(this, IntroActivity.class));
            Storage.setFirstIntro(this, false);
        }
    }

    private void showMapFragment() {

        if (tasksMap == null) tasksMap = new MapTasksFragment();
        if (tasksMap != null && !tasksMap.isVisible()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_default, tasksMap, MapTasksFragment.TAG);
            ft.addToBackStack(MapTasksFragment.TAG);
            ft.commitAllowingStateLoss();
            tasksMap.getMapAsync(this);
        }

    }


    public void shareLocation() {
        showSnackLong(R.string.msg_home_generate_link);
        String share_text = Config.FIREBASE_MAIN + "/" + Storage.getTrackId(this);
        Tools.shareText(MainActivity.this, share_text);
        startTrackLocationService();
    }

    public void sendSMS() {
        startSMSService();
    }


    private void loadDataFromIntent() {

        Intent intent = getIntent();
        String action = intent.getAction();

        if (DEBUG) Log.d(TAG, "[HOME] EXTERNAL INTENT: ACTION:" + action);

        if (Intent.ACTION_VIEW.equals(action)) { // TODO: maybe OR with BROWSER and others filters

            showMapFragment();

            Uri uri = intent.getData();
            String url = uri.toString();

            if (DEBUG) Log.d(TAG, "[HOME] EXTERNAL INTENT: URI: " + url);
            if (DEBUG) Log.d(TAG, "[HOME] EXTERNAL INTENT: HOST: " + uri.getHost());
            if (DEBUG) Log.d(TAG, "[HOME] EXTERNAL INTENT: PATH: " + uri.getPath());
            if (DEBUG) Log.d(TAG, "[HOME] EXTERNAL INTENT: QUERY: " + uri.getQuery());
            if (DEBUG) Log.d(TAG, "[HOME] EXTERNAL INTENT: SCHEME: " + uri.getScheme());
            if (DEBUG) Log.d(TAG, "[HOME] EXTERNAL INTENT: PORT: " + uri.getPort());
            if (DEBUG) Log.d(TAG, "[HOME] EXTERNAL INTENT: AUTHORITY: " + uri.getAuthority());

            Storage.setTargetTracking(this, uri.getPath());

        }

    }

    public void newTrackId(String trackId, String alias) {

    }

    private void subscribeLastTrack(Firebase fb, String trackId) {

        fb.child(trackId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (DEBUG) Log.d(TAG, "onDataChange: " + child.getValue());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void subscribeAllTrack(Firebase fb, String trackId) {
        if (DEBUG) Log.d(TAG, "subscribeAllTrack");
        fb.child(trackId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (DEBUG) Log.d(TAG, "onDataChange:");
                Map<String, Object> tracks = (Map<String, Object>) dataSnapshot.getValue();
                if (tracks != null) {
                    List<Location> data = new ArrayList<>();
                    if (DEBUG) Log.d(TAG, "data: " + tracks.toString());
                    Iterator<Object> it = tracks.values().iterator();
                    while (it.hasNext()) {
                        Map<String, Object> track = (Map<String, Object>) it.next();
                        Location loc = new Location("");
                        loc.setLatitude(Double.parseDouble(track.get("latitude").toString()));
                        loc.setLongitude(Double.parseDouble(track.get("longitude").toString()));
                        loc.setAccuracy(Float.parseFloat(track.get("accuracy").toString()));
//                        loc.setBearing(Float.parseFloat(track.get("bearing").toString()));
                        loc.setTime(Long.parseLong(track.get("time").toString()));
                        data.add(loc);
//                        if (DEBUG) Log.d(TAG, "geo: " +track.get("latitude")+","+track.get("longitude"));
                    }
                    tasksMap.addPoints(data);
                } else if (DEBUG) Log.w(TAG, "no data");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if (DEBUG) Log.d(TAG, "firebaseError:" + firebaseError.getMessage());
            }
        });

    }

    @Override
    public void onPickerContact(String name, String phone, Bitmap photo) {

        if (DEBUG) Log.d(TAG, "Contact Name: " + name);
        if (DEBUG) Log.d(TAG, "Contact Phone Number: " + phone);

        if (mRingEditFragment != null)
            mRingEditFragment.addConctact(new Contact(name, phone, photo));

    }

    @Override
    void showMap() {
        popBackLastFragment();
        showMapFragment();
    }

    @Override
    void showRings() {
        popBackLastFragment();
        if (mRingsFragment == null) mRingsFragment = new RingsFragment();
        if (!mRingsFragment.isVisible()) showFragment(mRingsFragment, RingsFragment.TAG, true);
    }

    @Override
    void showMain() {
        popBackLastFragment();
        if (mMainFragment == null) mMainFragment = new MainFragment();
        if (!mMainFragment.isVisible()) showFragment(mMainFragment, MainFragment.TAG, false);
    }

    @Override
    void showHelp() {
        startActivity(new Intent(this, IntroActivity.class));
    }

    @Override
    void showAbout() {
        popBackLastFragment();
        if (mAboutFragment == null) mAboutFragment = new AboutFragment();
        if (!mAboutFragment.isVisible()) showFragment(mAboutFragment, AboutFragment.TAG, true);
    }

    public void showConfirmAlertFragment() {
        ConfirmDialogFragment mConfirmDialogFragment = new ConfirmDialogFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(mConfirmDialogFragment, ConfirmDialogFragment.TAG);
        ft.show(mConfirmDialogFragment);
        ft.commitAllowingStateLoss();
    }

    public void showRingEditFragment() {
        mRingEditFragment = new RingEditFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(mRingEditFragment, RingEditFragment.TAG);
        ft.show(mRingEditFragment);
        ft.commitAllowingStateLoss();
    }

    public void showTestDialog() {
        mStackLevel++;
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(TestDialogFragment.TAG);
        if (prev != null) ft.remove(prev);
        ft.addToBackStack(null);
        // Create and show the dialog.
        TestDialogFragment newFragment = TestDialogFragment.newInstance(mStackLevel);
        newFragment.show(ft, "dialog");
    }


    public RingsFragment getRingsFragment() {
        return mRingsFragment;
    }


    private class OnTrackServiceConnected extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TrackLocationService.TRACK_SERVICE_CONNECT)) {
                if (DEBUG) Log.i(TAG, "[MainActivity] service Connected");
                if (mMainFragment != null) mMainFragment.setServiceButtonEnable(true);
            }
        }
    }

    @Override
    protected void onResume() {
        if (fbConnectReceiver == null) fbConnectReceiver = new OnTrackServiceConnected();
        IntentFilter intentFilter = new IntentFilter(TrackLocationService.TRACK_SERVICE_CONNECT);
        registerReceiver(fbConnectReceiver, intentFilter);
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        tasksMap.initMap(googleMap);
    }

    public Firebase getFbRef() {
        return fbRef;
    }

}
