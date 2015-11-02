package org.parallelzero.hancel.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.parallelzero.hancel.Config;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/2/15.
 */

public class TrackLocationService extends Service implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener{

    public static final String TAG = TrackLocationService.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;


    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private LocationRequest locationRequest;
    private Firebase fbRef;

    @Override
    public void onCreate(){
//        fbRef = new Firebase(Config.FIREBASE_MAIN);
        startLocationService();
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId){
        if(DEBUG) Log.i(TAG, "=== OnStartCommand ");
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

    public void onDestroy(){
        stopLocationService();
        if(DEBUG)Log.i(TAG, "=== onDestroy");
    }

    public void stopLocationService() {
        if(DEBUG)Log.i(TAG, "=== stopLocationService");
        if (mGoogleApiClient!=null&&mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void sendDataFrame() {
        if(DEBUG)Log.i(TAG, "=== Sending Tracking to server");

    }

    private void setupLocationForMap() {
        long fastUpdate = Config.DEFAULT_INTERVAL_FASTER;
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
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
//            this.location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            if(DEBUG)Log.i(TAG, "=== Connected: Latitude: " + this.location.getLatitude() + " Longitude: " + this.location.getLongitude());
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
        sendDataFrame();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(DEBUG)Log.i(TAG, "=== Connection Fail");
    }


}

