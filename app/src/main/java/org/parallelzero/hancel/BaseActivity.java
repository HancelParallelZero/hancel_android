package org.parallelzero.hancel;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
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
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import org.parallelzero.hancel.Fragments.MapTasksFragment;
import org.parallelzero.hancel.System.Tools;
import org.parallelzero.hancel.services.StatusScheduleReceiver;
import org.parallelzero.hancel.services.TrackLocationService;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/5/15.
 */
public abstract class BaseActivity extends AppCompatActivity implements OnMapReadyCallback, OnNavigationItemSelectedListener {

    public static final String TAG = BaseActivity.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;

    public static final int PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    public static final int PERMISSIONS_REQUEST_COARSE_LOCATION = 1;
    private static final int PICK_CONTACT = 0;

    private Uri uriContact;
    private String contactID;     // contacts unique ID

    private FloatingActionButton _fab;
    public MapTasksFragment tasksMap;
    private boolean toggle;

    private OnPickerContact contactListener;


    public void initDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _fab = (FloatingActionButton) findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    public void fabHide(){
        _fab.setVisibility(View.GONE);
    }

    public void fabShow(){
        _fab.setVisibility(View.VISIBLE);
    }

    public void fabSetIcon(){
        _fab.setVisibility(View.VISIBLE);
    }

    public void fabSetOnClickListener(OnClickListener onButtonActionListener){
        _fab.setOnClickListener(onButtonActionListener);
    }

    public void startTrackLocationService(String trackId){
        if(DEBUG) Log.d(TAG, "[MainActivity] startMainService");
        Intent service = new Intent(this, TrackLocationService.class);
        service.putExtra(TrackLocationService.KEY_TRACKID,trackId);
        startService(service);
        StatusScheduleReceiver.startScheduleService(this, Config.DEFAULT_INTERVAL);
    }

    public void stopTrackLocationService() {
        if(DEBUG)Log.d(TAG,"[MainActivity] stopTrackLocationService");
        StatusScheduleReceiver.stopSheduleService(this);
        stopService(new Intent(this, TrackLocationService.class));
    }


    public void loadPermissions(String perm, int requestCode) {
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

            getContact();

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        tasksMap.initMap(googleMap);
    }

    public void getContact(){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == PICK_CONTACT && resultCode == RESULT_OK) {
            if(DEBUG)Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();

            String name = retrieveContactName();
            String number = retrieveContactNumber();
            Bitmap photo = retrieveContactPhoto();

            if(contactListener !=null) contactListener.onPickerContact(name,number,photo);

        }

    }
    private String retrieveContactNumber() {

        String number = "";
        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact, new String[]{ContactsContract.Contacts._ID}, null, null, null);

        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        if(DEBUG)Log.d(TAG, "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            number = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();
        return number;

    }

    private String retrieveContactName() {

        String name = "";
        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();


        return name;

    }

    private Bitmap retrieveContactPhoto() {

        Bitmap photo = null;

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return photo;

    }

    public void setContactListener(OnPickerContact contactListener) {
        this.contactListener = contactListener;
    }


    public interface OnPickerContact{
        void onPickerContact(String name, String number, Bitmap photo);
    }

}
