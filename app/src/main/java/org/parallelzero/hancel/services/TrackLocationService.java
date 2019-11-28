package org.parallelzero.hancel.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.parallelzero.hancel.Config;
import org.parallelzero.hancel.System.Storage;
import org.parallelzero.hancel.System.Tools;
import org.parallelzero.hancel.models.Track;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/2/15.
 */

public class TrackLocationService extends Service implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener{

    public static final String TAG = TrackLocationService.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;
    public static final String TRACK_SERVICE_CONNECT = "brodcast_onfirebase_connected";
    public static final String KEY_TRACKID = "key_trackId";


    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private LocationRequest locationRequest;
    private String trackId;
    private Firebase trackerRef;
    private Firebase oldPush;

    public Firebase getFbRef() {
        return fbRef;
    }

    private Firebase fbRef;

    @Override
    public void onCreate(){
        startFireBase();
        startLocationService();
    }

    private void startFireBase() {
        Firebase.setAndroidContext(this);
        fbRef = new Firebase(Config.FIREBASE_MAIN);
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId){
        if(DEBUG) Log.i(TAG, "=== OnStartCommand ");
        this.trackId = Storage.getTrackId(this);
        sendBroadcast(new Intent(TRACK_SERVICE_CONNECT));
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

    public void onDestroy(){

        stopLocationService();
        removeDeviceId();
        if(DEBUG)Log.i(TAG, "=== onDestroy");
    }

    private void removeDeviceId(){
        if(trackerRef!=null)trackerRef.removeValue();
        Firebase fbRef = new Firebase(Config.FIREBASE_MAIN+ "/"+Tools.getAndroidDeviceId(this));
        fbRef.removeValue();
    }

    public void stopLocationService() {
        if(DEBUG)Log.i(TAG, "=== stopLocationService");
        if (mGoogleApiClient!=null&&mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void sendDataFrame(Location location) {
        if(DEBUG)Log.i(TAG, "=== Sending Tracking to server");
//        if(trackerRef!=null)trackerRef.removeValue();
        if(oldPush!=null)oldPush.removeValue();
        oldPush = trackerRef = getFbRef().child(trackId).push();
        if (DEBUG) Log.d(TAG, "== trackerRef: " + trackerRef);
        Track track = new Track();
        track.lat=location.getLatitude();
        track.lon=location.getLongitude();
        track.acu=location.getAccuracy();
        track.upd=location.getTime();

        track.alias=Storage.getCurrentAlias(this);
        track.color=Storage.getCurrentColor(this);
        trackerRef.setValue(track, new Firebase.CompletionListener() {

            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {

                if (firebaseError != null) {
                    if (DEBUG) Log.d(TAG, "=firebaseError: " + firebaseError.toString());
                    if (DEBUG) Tools.showToast(TrackLocationService.this, firebaseError.toString());
                } else {
                    if (DEBUG) Log.d(TAG, "=add track ok!");
                }

            }

        });
//        String transactionId = trackerRef.getKey();
    }

    private void setupLocationForMap() {
        long fastUpdate = Config.DEFAULT_INTERVAL_FASTER;
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(Config.DEFAULT_INTERVAL);
        locationRequest.setFastestInterval(fastUpdate);
    }

    private synchronized void startLocationService() {
        if( mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            if(DEBUG)Log.i(TAG, "=== Starting LocationServices: NON CONECTED -> CONECTED");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }
        else
            if(DEBUG)Log.i(TAG, "=== GPS service started: CONECTED");
    }

    @Override
    public void onConnected(Bundle bundle) {

        setupLocationForMap();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }

    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        if(DEBUG)Log.i(TAG, "=== Connection suspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if(DEBUG)Log.i(TAG, "=== onLocationChanged: Latitude: " + this.location.getLatitude() + " Longitude: " + this.location.getLongitude());
        sendDataFrame(location);
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(DEBUG)Log.i(TAG, "=== Connection Fail");
    }


}

