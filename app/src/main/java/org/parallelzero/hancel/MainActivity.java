package org.parallelzero.hancel;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import io.fabric.sdk.android.Fabric;

import org.parallelzero.hancel.Fragments.AboutFragment;
import org.parallelzero.hancel.Fragments.ConfirmDialogFragment;
import org.parallelzero.hancel.Fragments.InputDialogFragment;
import org.parallelzero.hancel.Fragments.MainFragment;
import org.parallelzero.hancel.Fragments.MapPartnersFragment;
import org.parallelzero.hancel.Fragments.MapTasksFragment;
import org.parallelzero.hancel.Fragments.RingEditFragment;
import org.parallelzero.hancel.Fragments.RingsFragment;
import org.parallelzero.hancel.Fragments.TestDialogFragment;
import org.parallelzero.hancel.System.Storage;
import org.parallelzero.hancel.System.Tools;
import org.parallelzero.hancel.models.Contact;
import org.parallelzero.hancel.models.Partner;
import org.parallelzero.hancel.models.Track;
import org.parallelzero.hancel.services.HardwareButtonReceiver;
import org.parallelzero.hancel.services.HardwareButtonService;
import org.parallelzero.hancel.services.TrackLocationService;

import java.util.ArrayList;
import java.util.Iterator;
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
    private MapPartnersFragment mPartnersFragment;
    private int mStackLevel = 0;
    private HardwareButtonService mHardwareButtonService;
    private boolean mBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startIntro();
        initDrawer();
        showMain();
        fabHide();
        initPermissionsFlow();
        new initStartAsync().execute();
        loadDataFromIntent();
        startHardwareButtonService();
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

    public void initPermissionsFlow(){
        loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSIONS_REQUEST_FINE_LOCATION);
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
        showPartnersFragment();

    }

    private void showPartnersFragment() {
        if (mPartnersFragment == null) mPartnersFragment = new MapPartnersFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_map_partners, mPartnersFragment, MapPartnersFragment.TAG);
        ft.addToBackStack(MapPartnersFragment.TAG);
        ft.commitAllowingStateLoss();
    }

    public void shareLocation() {
        showSnackLong(R.string.msg_home_generate_link);
        String share_text = Config.FIREBASE_MAIN + "/" + Storage.getTrackId(this);
        Tools.shareText(MainActivity.this, share_text);
        startTrackLocationService();
    }

    public void sendSMS() {
        if(DEBUG) Log.d(TAG, " Before mHardwareButtonService");
        if(mHardwareButtonService != null ) {
            if(DEBUG) Log.d(TAG, " mHardwareButtonService ins not null");
            getHardwareButtonService().sendAlertSMS();
        }
        else{
            if(DEBUG) Log.d(TAG, " mHardwareButtonService is null");
        }
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

            String trackerId = uri.getPath();
            if(!Storage.isOldTracker(this,trackerId)) showInputDialogFragment(trackerId);

        }

    }

    public void newTrackId(String trackId, String alias) {
        Storage.addTracker(this,trackId,alias);
        subscribeTrack(getFbRef(), trackId, alias);
    }

    private void subscribeForSingleTrack(Firebase fb, String trackId, String alias) {

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

    private void subscribeTrack(Firebase fb, final String trackId, final String alias) {
        if (DEBUG) Log.d(TAG, "subscribeTrack");
        fb.child(trackId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (DEBUG) Log.d(TAG, "onDataChange:");
                Map<String, Object> tracks = (Map<String, Object>) dataSnapshot.getValue();
                tasksMap.addPoints(tracks, trackId, alias);
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

    public void showInputDialogFragment(String trackId) {
        InputDialogFragment dialog = InputDialogFragment.newInstance(trackId);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(dialog, InputDialogFragment.TAG);
        ft.show(dialog);
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
        super.onResume();
        try {
            if (fbConnectReceiver == null) fbConnectReceiver = new OnTrackServiceConnected();
            IntentFilter intentFilter = new IntentFilter(TrackLocationService.TRACK_SERVICE_CONNECT);
            registerReceiver(fbConnectReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        tasksMap.initMap(googleMap);
        registerTrackers();
    }

    private void registerTrackers() {
        ArrayList<Track> trackers = Storage.getTrackers(this);
        Iterator<Track> it = trackers.iterator();
        while(it.hasNext()){
            Track track = it.next();
            subscribeTrack(getFbRef(),track.trackId,track.alias);
            mPartnersFragment.addPartner(new Partner(track.alias,track.getLastUpdate()));
        }
    }


    public Firebase getFbRef() {
        return fbRef;
    }

    private void startHardwareButtonService(){
        if(DEBUG)Log.d(TAG,"startHardwareButtonService");
        startService(new Intent(this, HardwareButtonService.class));
        HardwareButtonReceiver.startScheduleService(this, Config.DEFAULT_INTERVAL);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            HardwareButtonService.HardwareButtonServiceBinder binder =
                    (HardwareButtonService.HardwareButtonServiceBinder) service;
            mHardwareButtonService = binder.getService();
            mBound = true;
            if(DEBUG)Log.d(TAG,"HardwareButtonService onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            if(DEBUG)Log.d(TAG,"HardwareButtonService onServiceDisconnected");
            mBound = false;
        }
    };

    public HardwareButtonService getHardwareButtonService() {
        return mHardwareButtonService;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, HardwareButtonService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
}
