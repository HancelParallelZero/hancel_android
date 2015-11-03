package org.parallelzero.hancel;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.parallelzero.hancel.System.Tools;
import org.parallelzero.hancel.services.StatusScheduleReceiver;
import org.parallelzero.hancel.services.TrackLocationService;

import java.util.Iterator;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    private static final int PERMISSIONS_REQUEST_COARSE_LOCATION = 1;

    private Firebase fbRef;
    private OnFireBaseConnect fbConnectReceiver;
    private boolean toggle;
    private TextView _tv_share_url;
    private FloatingActionButton _fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDrawer();
        loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSIONS_REQUEST_FINE_LOCATION);

        Firebase.setAndroidContext(this);
        fbRef = new Firebase(Config.FIREBASE_MAIN);

        loadDataFromIntent();

    }

    private void loadPermissions(String perm,int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm},requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(DEBUG)Log.d(TAG,"PERMISSIONS_REQUEST_FINE_LOCATION PERMISSION_GRANTED");
                    loadPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,PERMISSIONS_REQUEST_COARSE_LOCATION);
                }
                return;
            }
            case PERMISSIONS_REQUEST_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
            }

        }

    }

    private void initDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _fab = (FloatingActionButton) findViewById(R.id.fab);
        _fab.setOnClickListener(onButtonActionListener);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        
        _tv_share_url=(TextView)findViewById(R.id.tv_share_url);
    }


    private void startTrackLocationService(String trackId){
        if(DEBUG) Log.d(TAG,"[MainActivity] startMainService");
        Intent service = new Intent(this, TrackLocationService.class);
        service.putExtra(TrackLocationService.KEY_TRACKID,trackId);
        startService(service);
        StatusScheduleReceiver.startScheduleService(this, Config.DEFAULT_INTERVAL);
    }

    private void stopTrackLocationService() {
        if(DEBUG)Log.d(TAG,"[MainActivity] stopTrackLocationService");
        StatusScheduleReceiver.stopSheduleService(this);
        stopService(new Intent(this, TrackLocationService.class));
    }

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

            subscribeLastTrack(getFbRef(), uri.getPath());

        }

    }


    private void subscribeLastTrack(Firebase fb,String trackId){

        fb.child(trackId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (DEBUG) Log.d(TAG, "onDataChange: "+child.getValue());

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
                if(tracks!=null) {
                    Iterator<Object> it = tracks.values().iterator();
                    while (it.hasNext()) {
                        Map<String, Object> track = (Map<String, Object>) it.next();
                        if (DEBUG) Log.d(TAG, "geo: " +track.get("latitude")+","+track.get("longitude"));
                    }
                }
                else
                    if(DEBUG)Log.w(TAG,"no data");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                if (DEBUG) Log.d(TAG, "firebaseError:" + firebaseError.getMessage());
            }
        });

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

    private View.OnClickListener onButtonActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!toggle){
                toggle=true;
                Snackbar.make(view, "StartLocationService", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                String trackId = Tools.getAndroidDeviceId(MainActivity.this);
                String share_text=Config.FIREBASE_MAIN+"/"+trackId;
                _tv_share_url.setText(share_text);
                Tools.shareText(MainActivity.this,share_text);
                startTrackLocationService(trackId);

            }else{
                toggle=false;
                Snackbar.make(view, "StopLocationService", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                _tv_share_url.setText("");
                stopTrackLocationService();
            }
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Firebase getFbRef() {
        return fbRef;
    }

}
