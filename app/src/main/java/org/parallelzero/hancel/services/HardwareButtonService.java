package org.parallelzero.hancel.services;

import android.app.Activity;
import android.app.PendingIntent;
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
import com.hpsaturn.tools.Logger;
import com.hpsaturn.tools.UITools;

import org.parallelzero.hancel.Config;
import org.parallelzero.hancel.R;
import org.parallelzero.hancel.System.Storage;
import org.parallelzero.hancel.models.Contact;
import org.parallelzero.hancel.models.Ring;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by izel on 3/11/15.
 */
public class HardwareButtonService extends Service implements GoogleApiClient.ConnectionCallbacks,
                                    GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = HardwareButtonService.class.getSimpleName();

    private String result;
    private boolean isFirstTime, isSendMesagge, locationActivated;
    public static boolean serviceRunning, countTimer;
    private static int countStart;
    private HardwareButtonReceiver hwButtonReceiver;
    private Handler handlerTime;
    private ResultReceiver resultReceiver;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location lastLocation;
    private SendSMSMessage smsTask;
    private BroadcastReceiver mReceiver;
    private final IBinder mBinder = new HardwareButtonServiceBinder();


    @Override
    public void onCreate() {
        super.onCreate();
        serviceRunning = locationActivated = false;
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
            locationActivated = false;
        }
        try {
            resultReceiver = intent.getParcelableExtra("receiver");
            //Check for the number of times the button has been pressed
            if (countStart >= 5) {
                Logger.i(TAG, "=== 5 Intents");
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
            Logger.i(TAG, "=== No results available." + e);
        }

        return super.onStartCommand(intent, flags, startId);
    }


    public void sendAlertSMS() {
        startLocationService();
        startSMSTask();
    }


    /*
    * Starts the API for location service if its not activated
    */
    private void startLocationService() {
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            Logger.i(TAG, "=== Starting geolocalization service: UNCONNECTED -> CONECTED");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }
        else
            Logger.i(TAG, "=== Starting geolocalization service: ALREADY CONNECTED");

        locationActivated = true;
    }

    /**
     * Stops the location service if its activated
     */
    private void stopLocationService() {
        if (mGoogleApiClient != null || mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            Logger.i(TAG, "=== Stopping geolocalization service: CONNECTED -> UNCONNECTED");
        }
        else
            Logger.i(TAG, "=== Stopping geolocalization service: ALREADY CONNECTED");

        locationActivated = false;
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
        if (smsTask == null || smsTask.getStatus() == AsyncTask.Status.FINISHED) {
            Logger.i(TAG, "=== smsTask is null or finished. Starting task ");
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
        try {
            serviceRunning = false;
            if(hwButtonReceiver!=null){
                unregisterReceiver(hwButtonReceiver);
            }
        } catch (Exception e) {
            Log.e(TAG,"unregisterReceiver Exception: "+e.getMessage());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationActivated = true;
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
        locationActivated = false;
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
        locationActivated = false;
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
        @Override
        protected void onCancelled() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<String> numbers = new ArrayList<String>();
            String location = "";
            result = "OK";

            if (lastLocation != null) {
                location = getString(R.string.map_provider)
                        + lastLocation.getLatitude() + ","
                        + lastLocation.getLongitude() + "\n";
            }

            Logger.i(TAG, "=== Localization : " + location);

            numbers.addAll(contactsRingNumbers());

            Logger.i(TAG, "=== Contacts to notify : " + numbers.size());

            if (numbers.size() == 0) {
                result = getString(R.string.no_configured_rings);
                Logger.i(TAG,"=== No rings found");
            }
            else {
                isSendMesagge = true;
                int fails = 0, nonValid = 0;
                String message = getString(R.string.tracking_SMS_message);
                message = message.replace("%map", location).replace("%battery",
                        getBatteryLevel() + "%");

                for (int i = 0; i < numbers.size(); i++) {
                    try {
                        String number = numbers.get(i).replaceAll("\\D+", "");
                        if (number != null && number.length() > 0) {
                            sendSMS(number, message);
                            if(!result.equalsIgnoreCase("OK")) {
                                fails ++;
                                Logger.i(TAG, "=== Error sending SMS to: " + numbers.get(i));
                            }
                        }
                        else{
                            nonValid ++;
                            fails ++;
                        }
                    } catch (Exception ex) {
                        Logger.i(TAG, "=== Error sending SMS to: " + numbers.get(i) + ex.getMessage());
                        fails ++;
                    }
                }
                if (fails > 0) {
                    result += getString(R.string.any_sms_sent);
                    result = result.replace("%count1", String.valueOf(fails));
                    result += String.valueOf(numbers.size());
                    Logger.i(TAG,"=== Error sending SMS to : " + numbers.toString());
                }
                if (nonValid > 0){
                    result += getString(R.string.tracking_invalid_contac_numbers);
                    result.replace("%count", String.valueOf(nonValid));
                }
                Logger.i(TAG, "=== " + result);
            }
            try {
                Logger.i(TAG, "=== Finalizing smsTask ");
                this.finalize();
            } catch (Throwable throwable) {
                Logger.i(TAG, "=== Error finalizing the smsTask ");
                throwable.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void r) {
            super.onPostExecute(r);
            isSendMesagge = false;

            Logger.i(TAG,"=== PostExecute " );

            if (result.equalsIgnoreCase("OK")) {
                //TODO: Check if is necesary to save the datetime for the last alarm sent
                String currentDateandTime = UITools.getDateFormatTrack(Calendar.getInstance());
                Storage.setLastPanicAlertDate(getApplicationContext(),
                        currentDateandTime);
                /*Tools.showToast(getApplicationContext(), getString(
                        R.string.panic_alert_sent));*/
                UITools.showToast(getBaseContext(), getString(R.string.sms_sent));
            }
            else {
                UITools.showToast(getApplicationContext(), result);
            }
        }

        private void sendSMS(String mobileNumber, String message) {
            SmsManager sms = SmsManager.getDefault();
            try {
                ArrayList<String> parts = sms.divideMessage(message);
                int numParts = parts.size();
                Context context = getApplicationContext();
                ArrayList<PendingIntent> sent = new ArrayList<PendingIntent>(numParts);
                ArrayList<PendingIntent> delivered = new ArrayList<PendingIntent>(numParts);

                for (int i = 0; i < numParts; i++) {
                    PendingIntent sentIntent = PendingIntent.getBroadcast(context,
                            0, new Intent("SENT"), 0);
                    Logger.i(TAG,"=== building intents for SMS " );

                    context.registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context arg0, Intent arg1) {
                            result = "FAIL";
                            Logger.i(TAG,"=== Intent OnReceive Sent " + getResultCode() );
                            switch (getResultCode()) {
                                case Activity.RESULT_OK:
                                    result = "OK";
                                    Logger.i(TAG,"=== " +  getString(R.string.sms_sent));
                                    break;
                                case SmsManager.RESULT_ERROR_NO_SERVICE:
                                    result = getString(R.string.sms_not_sent);
                                    Logger.i(TAG,"=== " +  getString(R.string.sms_not_sent));
                                    break;
                                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                    result = getString(R.string.sms_generic_fail);
                                    Logger.i(TAG,"=== " +  getString(R.string.sms_generic_fail));
                                    break;
                                case SmsManager.RESULT_ERROR_NULL_PDU:
                                    result = getString(R.string.sms_null_pdu);
                                    Logger.i(TAG,"=== NULL PDU ");
                                    break;
                                case SmsManager.RESULT_ERROR_RADIO_OFF:
                                    result = getString(R.string.sms_radio_off);
                                    Logger.i(TAG,"=== Error. Airplane Mode ");
                                    break;
                            }
                        }
                    }, new IntentFilter("SENT"));

                    sent.add(sentIntent);
                    PendingIntent deliveredIntent = PendingIntent.getBroadcast(
                            context, 0, new Intent("DELIVERED"), 0);
                    context.registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context arg0, Intent arg1) {
                            Logger.i(TAG,"=== Intent OnReceive Delivered "  + getResultCode());
                            switch (getResultCode()) {
                                case Activity.RESULT_OK:
                                    UITools.showToast(getBaseContext(), getString(R.string.sms_delivered));
                                    Logger.i(TAG,"=== SMS OK  ");
                                    break;
                                case Activity.RESULT_CANCELED:
                                    UITools.showToast(getBaseContext(), getString(R.string.sms_canceled));
                                    Logger.i(TAG,"=== SMS Canceled  " );
                                    break;
                            }
                        }
                    }, new IntentFilter("DELIVERED"));
                    delivered.add(deliveredIntent);
                }

                sms.sendMultipartTextMessage(mobileNumber, null, parts, sent, delivered);
                Logger.i(TAG,"=== Message  " + message + " sent to " + mobileNumber);
            }
            catch (Exception e) {
                Logger.i(TAG,"=== Error sending message to " + mobileNumber + " " + e.getMessage());
            }
        }

        private ArrayList contactsRingNumbers() {
            List<Contact> contacts;
            ArrayList<Ring> enableRings = Storage.getRingsEnable(getApplicationContext());
            ArrayList<String> numbers = new ArrayList<>();

            if(enableRings == null)
                return null;

            for (Ring ring: enableRings) {
                contacts = ring.getContacts();
                for(Contact c : contacts){
                    Logger.i(TAG, "Contact Number: " + c.getPhone());
                    numbers.add(c.getPhone().trim());
                }
            }
            Logger.i(TAG, "=== Number of contacts to notify: " + numbers.size());
            return numbers;
        }
    }

    public class HardwareButtonServiceBinder extends Binder {
        public HardwareButtonService getService() {
            return HardwareButtonService.this;
        }
    }

}
