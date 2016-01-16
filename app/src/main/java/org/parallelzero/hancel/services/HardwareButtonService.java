package org.parallelzero.hancel.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.parallelzero.hancel.Config;
import org.parallelzero.hancel.R;
import org.parallelzero.hancel.System.Storage;
import org.parallelzero.hancel.System.Tools;
import org.parallelzero.hancel.models.Contact;
import org.parallelzero.hancel.models.Ring;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;

/**
 * Created by izel on 3/11/15.
 */
public class HardwareButtonService extends Service implements GoogleApiClient.ConnectionCallbacks,
                                    GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = HardwareButtonService.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;

    private String result;
    private boolean isFirstTime, isSendMesagge, locationActivted;
    public static boolean serviceRunning, countTimer;
    private static int countStart;
    private Timer timer;
    private HardwareButtonReceiver hwButtonReceiver;
    private Handler handlerTime;
    private ResultReceiver resultReceiver;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location lastLocation;
    private SendSMSMessage smsTask;
    private BroadcastReceiver mReceiver;
    private final IBinder mBinder = new HardwareButtonServiceBinder();


    public class HardwareButtonServiceBinder extends Binder {
        public HardwareButtonService getService() {
            return HardwareButtonService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        serviceRunning = locationActivted = false;
        countTimer = true;
        countStart = -1;
        handlerTime = new Handler();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        hwButtonReceiver = new HardwareButtonReceiver();
        registerReceiver(mReceiver, filter);
        lastLocation = null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isFirstTime) {
            isFirstTime = false;
            serviceRunning = true;
            locationActivted = false;
        }
        try {
            resultReceiver = intent.getParcelableExtra("receiver");
            //Check for the number of times the button has been pressed
            if (countStart >= 5) {
                if(DEBUG)Log.i(TAG, "=== 5 Intents");
                countStart = -1;
                countTimer = true;
                startLocationService();
            }
            else {
                //restarting counters after 5 seconds
                countStart += 1;
                if (countTimer) {
                    countTimer = false;
                    handlerTime.postDelayed(runnable, Config.RESTART_HARDWARE_BUTTON_TIME);
                }
            }
        } catch (Exception e) {
            if(DEBUG)Log.i(TAG, "=== No results available." + e);
        }

        return super.onStartCommand(intent, flags, startId);
    }


    public void sendAlertSMS() {
        startLocationService();
    }


    /*
    * Starts the API for location service if its not activated
    */
    private void startLocationService() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            if(DEBUG) Log.i(TAG, "=== Starting geolocalization service: UNCONNECTED -> CONECTED");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }
        else
            if(DEBUG)Log.i(TAG, "=== Starting geolocalization service: ALREADY CONNECTED");

        locationActivted = true;
    }

    /**
     * Stops the location service if its activated
     */
    private void stopLocationService() {
        if (mGoogleApiClient != null || mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            if(DEBUG)Log.i(TAG, "=== Stopping geolocalization service: CONNECTED -> UNCONNECTED");
        }
        else
            if(DEBUG)Log.i(TAG, "=== Stopping geolocalization service: ALREADY CONNECTED");

        locationActivted = false;
    }

    /*
     * Setting the service quality
     */
    private void setupLocationForMap() {
        long fastUpdate = Config.DEFAULT_INTERVAL_FASTER;
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(Config.DEFAULT_INTERVAL);
        mLocationRequest.setFastestInterval(fastUpdate);
    }

    /*
     * Starts the asyncronous task to send the sms messages
     */
    private void startSMSTask() {

        if (smsTask == null) {
            smsTask = new SendSMSMessage();
            smsTask.execute();
        }
    }

    /**
     * Phone vibration
     *
     * @param time time for vibration
     */
    public void vibrate(long time) {
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(time);
    }

    /**
     * Gets the battery level
     *
     * @return battery level
     */
    public int getBatteryLevel() {
        Intent i = new ContextWrapper(this).registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        serviceRunning = false;
        unregisterReceiver(hwButtonReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationActivted = true;
        mLocationRequest = LocationRequest.create();
        setupLocationForMap();
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            this.lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        locationActivted = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.lastLocation = location;
        if (countTimer) {
            startSMSTask();
            vibrate(Config.VIBRATION_TIME_SMS);
            stopLocationService();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        locationActivted = false;
    }

    /*
     * thread for restarting values
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //restart counters
            countStart = -1;
            countTimer = true;
        }
    };

    /*
     * Inner class to handle SMS mesaages for alert sms messages.
     * The task is started when AlertButton (software) is pressed
     * or the power button is pressed 4 or more times.
     */
    public class SendSMSMessage extends AsyncTask<Void, Void, Void> {
        //Contacts con = new Contacts(getApplicationContext());

        @Override
        protected void onCancelled() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<String> numbers = new ArrayList<String>();
            String location = "";

            if (lastLocation != null) {
                location = getString(R.string.map_provider) + lastLocation.getLatitude() + ","
                        + lastLocation.getLongitude() + "\n";
            }

            if(DEBUG) Log.i(TAG, "=== Localization : " + location);

            numbers.addAll(contactsRingNumbers());

            if(DEBUG) Log.i(TAG, "=== Contacts to notify : " + numbers.size());

            if (numbers.size() == 0) {
                result = getString(R.string.no_configured_rings);
                if(DEBUG)Log.i(TAG,"=== No rings found");
            }
            else {
                isSendMesagge = true;
                int fails = 0;
                String message = getString(R.string.tracking_SMS_message);
                message = message.replace("%map", location).replace("%battery",
                        getBatteryLevel() + "%");

                for (int i = 0; i < numbers.size(); i++) {
                    try {
                        String number = numbers.get(i).replaceAll("\\D+", "");
                        if (number != null && number.length() > 0)
                            sendSMS(number, message);
                    } catch (Exception ex) {
                        if(DEBUG) Log.i(TAG, "=== Error sending SMS to: " + numbers.get(i) + ex.getMessage());
                        fails += 1;
                    }
                }

                if (fails == numbers.size()) {
                    result = getString(R.string.tracking_invalid_contac_numbers);
                    if(DEBUG)Log.i(TAG,"=== Error sending SMS to : " + numbers.toString());
                }
                else
                    result = "OK";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void r) {
            super.onPostExecute(r);
            isSendMesagge = false;

            /*if (!Util.isTrackLocationServiceRunning(getApplicationContext())) {
                Util.inicarServicio(getApplicationContext());
            }*/

            if (result.equalsIgnoreCase("OK")) {
                String currentDateandTime = Tools.getDateFormatTrack(Calendar.getInstance());
                Storage.setLastPanicAlertDate(getApplicationContext(),
                        currentDateandTime);
                Tools.showToast(getApplicationContext(), getString(
                        R.string.panic_alert_sent));
            }
            else {
                Tools.showToast(getApplicationContext(), result);
            }

        }

        private void sendSMS(String mobileNumber, String message) {
            SmsManager sms = SmsManager.getDefault();
            try {
                ArrayList<String> parts = sms.divideMessage(message);
                sms.sendMultipartTextMessage(mobileNumber, null, parts, null, null);
                if(DEBUG)Log.i(TAG,"=== Message  " + message + " sent to " + mobileNumber);
            }
            catch (Exception e) {
                if(DEBUG)Log.i(TAG,"=== Error sending message to " + mobileNumber + " " + e.getMessage());
            }
        }

        private ArrayList contactsRingNumbers() {
            List<Contact> contacts;
            ArrayList<Ring> enableRings = Storage.getRingsEnable(getApplicationContext());
            ArrayList<String> numbers = new ArrayList<String>();

            if(enableRings == null)
                return null;

            for (Ring ring: enableRings) {
                contacts = ring.getContacts();
                for(Contact c : contacts){
                    if(DEBUG)Log.i(TAG, "Contact Number: " + c.getPhone());
                    numbers.add(c.getPhone().trim());
                }
            }
            if(DEBUG)Log.i(TAG, "=== Number of contacts to notify: " + numbers.size());

            return numbers;
        }
    }


}
