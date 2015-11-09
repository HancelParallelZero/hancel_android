package org.parallelzero.hancel;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.parallelzero.hancel.Fragments.MainFragment;
import org.parallelzero.hancel.Fragments.MapTasksFragment;
import org.parallelzero.hancel.Fragments.RingEditFragment;
import org.parallelzero.hancel.Fragments.RingsFragment;
import org.parallelzero.hancel.System.Storage;
import org.parallelzero.hancel.System.Tools;
import org.parallelzero.hancel.models.Contact;
import org.parallelzero.hancel.services.TrackLocationService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MainActivity extends BaseActivity implements BaseActivity.OnPickerContact {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;

    private Firebase fbRef;
    private OnFireBaseConnect fbConnectReceiver;
    private boolean toggle;

    private RingsFragment mRingsFragment;
    private RingEditFragment mRingEditFragment;
    private MainFragment mMainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startIntro();
        initDrawer();
        showMain();

        loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSIONS_REQUEST_FINE_LOCATION);

        Firebase.setAndroidContext(this);
        fbRef = new Firebase(Config.FIREBASE_MAIN);

        fabHide();
        setContactListener(this);

        loadDataFromIntent();

    }

    private void startIntro() {
        if(Storage.isFirstIntro(this)){
            startActivity(new Intent(this, IntroActivity.class));
            Storage.setFirstIntro(this,false);
        }
    }

    private void initMapFragment(){

        tasksMap = new MapTasksFragment();
        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.content_default, tasksMap, MapTasksFragment.TAG);
        ft.commitAllowingStateLoss();
        tasksMap.getMapAsync(this);

    }


    private View.OnClickListener onFabLocationService = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!toggle){
                toggle=true;
                showSnackLong("StartLocationService");
                String trackId = Tools.getAndroidDeviceId(MainActivity.this);
                String share_text=Config.FIREBASE_MAIN+"/"+trackId;
                Tools.shareText(MainActivity.this,share_text);
                startTrackLocationService(trackId);

            }else{
                toggle=false;
                showSnackLong("StopLocationService");
                stopTrackLocationService();
            }
        }
    };


    private void loadDataFromIntent() {

        Intent intent = getIntent();
        String action = intent.getAction();

        if(DEBUG)Log.d(TAG,"[HOME] EXTERNAL INTENT: ACTION:"+action);

        if (Intent.ACTION_VIEW.equals(action)) { // TODO: maybe OR with BROWSER and others filters

            Uri uri = intent.getData();
            String url = uri.toString();

            if(DEBUG)Log.d(TAG,"[HOME] EXTERNAL INTENT: URI: "+url);
            if(DEBUG)Log.d(TAG,"[HOME] EXTERNAL INTENT: HOST: "+uri.getHost());
            if(DEBUG)Log.d(TAG,"[HOME] EXTERNAL INTENT: PATH: "+uri.getPath());
            if(DEBUG)Log.d(TAG,"[HOME] EXTERNAL INTENT: QUERY: "+uri.getQuery());
            if(DEBUG)Log.d(TAG,"[HOME] EXTERNAL INTENT: SCHEME: "+uri.getScheme());
            if(DEBUG)Log.d(TAG,"[HOME] EXTERNAL INTENT: PORT: "+uri.getPort());
            if(DEBUG)Log.d(TAG,"[HOME] EXTERNAL INTENT: AUTHORITY: "+uri.getAuthority());

            subscribeAllTrack(getFbRef(), uri.getPath());
            initMapFragment();
        }

    }


    private void subscribeLastTrack(Firebase fb,String trackId){

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
                        loc.setBearing(Float.parseFloat(track.get("bearing").toString()));
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

        if(DEBUG)Log.d(TAG, "Contact Name: " + name);
        if(DEBUG)Log.d(TAG, "Contact Phone Number: " + phone);

       if(mRingEditFragment!=null)mRingEditFragment.addConctact(new Contact(name, phone, photo));

    }

    @Override
    void showRings() {
        if(mRingsFragment==null) mRingsFragment = new RingsFragment();
        if(!mRingsFragment.isVisible())showFragment(mRingsFragment, RingsFragment.TAG, true);
    }

    @Override
    void showMain() {
        if(mMainFragment==null) mMainFragment = new MainFragment();
        showFragment(mMainFragment, MainFragment.TAG, false);
    }

    @Override
    void showHelp() {
        startActivity(new Intent(this, IntroActivity.class));
    }

    public void showRingEditFragment() {

        mRingEditFragment = new RingEditFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(mRingEditFragment, RingEditFragment.TAG);
        ft.show(mRingEditFragment);
        ft.commitAllowingStateLoss();
    }

    public RingsFragment getRingsFragment() {
        return mRingsFragment;
    }

    private class OnFireBaseConnect extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TrackLocationService.FIREBASE_CONNECT)) {
                if (DEBUG) Log.i(TAG, "[MainActivity] onFireBaseConnect");
            }
        }
    }

    @Override
    protected void onResume() {
        if(fbConnectReceiver==null)fbConnectReceiver=new OnFireBaseConnect();
        IntentFilter intentFilter = new IntentFilter(TrackLocationService.FIREBASE_CONNECT);
        registerReceiver(fbConnectReceiver, intentFilter);
        super.onResume();
    }

    public Firebase getFbRef() {
        return fbRef;
    }

}
