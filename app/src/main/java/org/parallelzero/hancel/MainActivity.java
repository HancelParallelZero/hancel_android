package org.parallelzero.hancel;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.hpsaturn.tools.DeviceUtil;
import com.hpsaturn.tools.Logger;
import com.hpsaturn.tools.UITools;
import com.livinglifetechway.quickpermissions.annotations.WithPermissions;

import org.parallelzero.hancel.Fragments.AboutFragment;
import org.parallelzero.hancel.Fragments.AliasFragment;
import org.parallelzero.hancel.Fragments.ConfirmDialogFragment;
import org.parallelzero.hancel.Fragments.ContactsRingFragment;
import org.parallelzero.hancel.Fragments.InputDialogFragment;
import org.parallelzero.hancel.Fragments.MainFragment;
import org.parallelzero.hancel.Fragments.MapPartnersFragment;
import org.parallelzero.hancel.Fragments.MapTasksFragment;
import org.parallelzero.hancel.Fragments.RingsFragment;
import org.parallelzero.hancel.System.Storage;
import org.parallelzero.hancel.models.Contact;
import org.parallelzero.hancel.models.Partner;
import org.parallelzero.hancel.models.Track;
import org.parallelzero.hancel.services.HardwareButtonReceiver;
import org.parallelzero.hancel.services.HardwareButtonService;
import org.parallelzero.hancel.services.TrackLocationService;

import java.util.ArrayList;
import java.util.Iterator;

import info.guardianproject.panic.PanicResponder;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends BaseActivity implements BaseActivity.OnPickerContactUri, OnMapReadyCallback, View.OnKeyListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private OnTrackServiceConnected fbConnectReceiver;

    public MapTasksFragment tasksMap;
    private RingsFragment mRingsFragment;
    private ContactsRingFragment mContactsRingFragment;
    private MainFragment mMainFragment;
    private AboutFragment mAboutFragment;
    private MapPartnersFragment mPartnersFragment;
    private AliasFragment mAliasFragment;

    private HardwareButtonService mHardwareButtonService;
    private boolean mBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fabric.with(MainActivity.this, new Crashlytics());

        startIntro();
        initDrawer();
        fabHide();
        String trackId = DeviceUtil.getAndroidDeviceId(MainActivity.this);
        Storage.setTrackId(this, trackId);
        setContactListener(this);
        if (Storage.isShareLocationEnable(this)) startTrackLocationService();
        checkAlias();
        registerPanicKit();
        loadDataFromIntent();
        startHardwareButtonService();

    }

    private void checkAlias() {
        if(Storage.getCurrentAlias(this).equals(""))  showAliasFragment();
        else showMain();
    }

    private void startIntro() {
        if (Storage.isFirstIntro(this)) {
            startActivity(new Intent(this, IntroActivity.class));
            Storage.setFirstIntro(this, false);
        }
    }

    private void registerPanicKit(){
        PanicResponder.setTriggerPackageName(this,"info.guardianproject.ripple");
        Logger.d(TAG,"TriggerPackageName: "+PanicResponder.getTriggerPackageName(this));
    }

    /**************************************************
     ************ FRAGMENT PRIMITIVES *****************
     *************************************************/

    private void showMainFragment() {
        popBackLastFragment();
        if (mMainFragment == null) mMainFragment = new MainFragment();
        if (!mMainFragment.isVisible()) showFragment(mMainFragment, MainFragment.TAG, false);
    }

    private void showMapFragment() {

        popBackLastFragment();
        if (tasksMap == null) tasksMap = new MapTasksFragment();
        if (tasksMap != null && !tasksMap.isVisible()) {
            showFragmentFull(tasksMap,MapTasksFragment.TAG,true);
            tasksMap.getMapAsync(this);
        }
        showPartnersFragment();
    }

    private void showPartnersFragment() {
        if (mPartnersFragment == null) mPartnersFragment = new MapPartnersFragment();
        if(!mPartnersFragment.isVisible()) {
            showFragment(mPartnersFragment, MapPartnersFragment.TAG, false, R.id.content_map_partners);
        }
    }

    private void showAliasFragment() {
        if (mAliasFragment == null) mAliasFragment = new AliasFragment();
        if(!mAliasFragment.isVisible()) {
            showFragment(mAliasFragment,AliasFragment.TAG,false);
        }
    }

    public void removeAliasFragment() {
        removeFragment(mAliasFragment);
        showMain();
    }

    public void removePartnersFragment() {
        removeFragment(mPartnersFragment);
    }

    public void shareLocation() {
        showSnackLong(R.string.msg_home_generate_link);
        String share_text = Config.FIREBASE_MAIN + "/" + Storage.getTrackId(this);
        UITools.shareText(MainActivity.this, share_text,"Share Location");
        startTrackLocationService();
    }

    public void sendSMS() {
        Logger.d(TAG, " Before mHardwareButtonService");
        if(mHardwareButtonService != null ) {
            Logger.d(TAG, " mHardwareButtonService ins not null");
            getHardwareButtonService().sendAlertSMS();
        }
        else{
            Logger.d(TAG, " mHardwareButtonService is null");
        }
    }

    private void loadDataFromIntent() {

        Intent intent = getIntent();
        String action = intent.getAction();

        Logger.d(TAG, "[HOME] EXTERNAL INTENT: ACTION:" + action);

        if (Intent.ACTION_VIEW.equals(action)&&Storage.getCurrentAlias(this).length()!=0) { // TODO: maybe OR with BROWSER and others filters
//            showMapFragment();
            Uri uri = intent.getData();
            printUriData(uri);
//            String trackId = uri.getPath();
        }

    }

    private void subscribeTrack(String trackId) {
        Logger.d(TAG, "subscribeTrack: " + trackId);

    }

    @Override
    public void onPickerContact(String name, String phone, String uri) {
        Logger.d(TAG, "Contact Name: " + name);
        Logger.d(TAG, "Contact Phone Number: " + phone);

        if (mContactsRingFragment != null)
            mContactsRingFragment.addConctact(new Contact(name, phone, uri));
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() != KeyEvent.ACTION_DOWN) {
            DeviceUtil.hideKeyBoard(this);
            return true;
        } else
            return false;
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
        showMainFragment();
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
        showDialogFragment(mConfirmDialogFragment,ConfirmDialogFragment.TAG);
    }

    @WithPermissions(
            permissions = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_CONTACTS
            }
    )
    public void showAddContactsRingFragment() {
        mContactsRingFragment = new ContactsRingFragment();
        showDialogFragment(mContactsRingFragment,ContactsRingFragment.TAG);
    }

    public void showInputDialogFragment(String trackId) {
        InputDialogFragment dialog = InputDialogFragment.newInstance(trackId);
        showDialogFragment(dialog,InputDialogFragment.TAG);
    }


    public RingsFragment getRingsFragment() {
        return mRingsFragment;
    }


    private class OnTrackServiceConnected extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TrackLocationService.TRACK_SERVICE_CONNECT)) {
                Logger.i(TAG, "[MainActivity] service Connected");
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
        try {
            if(fbConnectReceiver!=null)unregisterReceiver(fbConnectReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            subscribeTrack(track.trackId);
            if(mPartnersFragment!=null)mPartnersFragment.addPartner(new Partner(track.alias,track.getLastUpdate()));
        }
    }

    private void startHardwareButtonService(){
        Logger.d(TAG,"startHardwareButtonService");
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
            Logger.d(TAG,"HardwareButtonService onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Logger.d(TAG,"HardwareButtonService onServiceDisconnected");
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
