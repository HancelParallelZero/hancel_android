package org.parallelzero.hancel;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import org.parallelzero.hancel.System.Storage;
import org.parallelzero.hancel.services.HardwareButtonService;
import org.parallelzero.hancel.services.StatusScheduleReceiver;
import org.parallelzero.hancel.services.TrackLocationService;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/5/15.
 */
public abstract class BaseActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    public static final String TAG = BaseActivity.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;

    public static final int PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    public static final int PERMISSIONS_REQUEST_COARSE_LOCATION = 1;
    private static final int PERMISSIONS_READ_CONTACTS = 2;
    private static final int PICK_CONTACT = 0;

    private Uri uriContact;

    private FloatingActionButton _fab;

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


    public void showFragment(Fragment fragment, String fragmentTag, boolean toStack) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_default, fragment, fragmentTag);
        if (toStack) ft.addToBackStack(fragmentTag);
        ft.commitAllowingStateLoss();

    }

    public void showFragmentFull(Fragment fragment, String fragmentTag, boolean toStack) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_default, fragment, fragmentTag);
        if (toStack) ft.addToBackStack(fragmentTag);
        ft.commitAllowingStateLoss();

    }

    public void popBackStackSecure(String TAG) {
        try {
            if (DEBUG) Log.d(TAG, "popBackStackSecure to: " + TAG);
            getFragmentManager().popBackStack(TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void popBackLastFragment() {
        if (getFragmentManager().getBackStackEntryCount() != 0) {
            if (DEBUG) Log.d(TAG, "onBackPressed popBackStack for:" + getLastFragmentName());
            getFragmentManager().popBackStack();
        }
    }


    public void removeFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
    }

    public String getLastFragmentName() {
        if (getFragmentManager().getBackStackEntryCount() == 0) return "";
        FragmentManager fm = getSupportFragmentManager();
        return fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName();
    }

    public void fabHide() {
        _fab.setVisibility(View.GONE);
    }

    public void fabShow() {
        _fab.setVisibility(View.VISIBLE);
    }

    public void fabSetIcon(int resourse) {
        _fab.setImageResource(resourse);
    }

    public void fabSetOnClickListener(OnClickListener onButtonActionListener) {
        _fab.setOnClickListener(onButtonActionListener);
    }

    public void showSnackLong(String msg) {
        Snackbar.make(this.getCurrentFocus(), msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void showSnackLong(int msg) {
        Snackbar.make(this.getCurrentFocus(), msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void startTrackLocationService() {
        if (DEBUG) Log.d(TAG, "[MainActivity] startMainService");
        Intent service = new Intent(this, TrackLocationService.class);
        startService(service);
        StatusScheduleReceiver.startScheduleService(this, Config.DEFAULT_INTERVAL);
        Storage.setShareLocationEnable(this,true);
    }


    public void startSMSService(){
        if (DEBUG) Log.d(TAG, "[MainActivity] startSMSService");
        Intent service = new Intent(this, HardwareButtonService.class);
        startService(service);
    }

    public void stopTrackLocationService() {
        if (DEBUG) Log.d(TAG, "[MainActivity] stopTrackLocationService");
        StatusScheduleReceiver.stopSheduleService(this);
        stopService(new Intent(this, TrackLocationService.class));
        Storage.setShareLocationEnable(this, false);
    }


    public void loadPermissions(String perm, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (DEBUG) Log.d(TAG, "PERMISSIONS_REQUEST_FINE_LOCATION PERMISSION_GRANTED");
                    loadPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, PERMISSIONS_REQUEST_COARSE_LOCATION);
                    loadPermissions(Manifest.permission.READ_CONTACTS, PERMISSIONS_READ_CONTACTS);
                }
                return;
            }
            case PERMISSIONS_REQUEST_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
            }
            case PERMISSIONS_READ_CONTACTS: {
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

        if (id == R.id.nav_rings) {

            showRings();

        } else if (id == R.id.nav_help) {
            showHelp();

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_history) {
            getContact();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void drawerUnSelectAll(){

    }

    abstract void showRings();

    abstract void showMain();

    abstract void showHelp();

    public void getContact() {
//        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//        startActivityForResult(intent, PICK_CONTACT);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(CommonDataKinds.Phone.CONTENT_TYPE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, PICK_CONTACT);
        }

    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == PICK_CONTACT && resultCode == RESULT_OK) {
            if (DEBUG) Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();

            String name = retrieveContactName();
            String number = retrieveContactNumber();
            Bitmap photo = retrieveContactPhoto();

            if (contactListener != null) contactListener.onPickerContact(name, number, photo);

        }

    }

    private String retrieveContactNumber() {

        String number = "";
        String[] projection = new String[]{CommonDataKinds.Phone.NUMBER};
        Cursor cursor = getContentResolver().query(uriContact, projection, null, null, null);
        // If the cursor returned is valid, get the phone number
        if (cursor != null && cursor.moveToFirst()) {
            int numberIndex = cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER);
            number = cursor.getString(numberIndex);
            cursor.close();
        }

        return number;

    }

    private String retrieveContactName() {

        String name = "";
        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor!=null&&cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));
            cursor.close();
        }

        return name;

    }

    private Bitmap retrieveContactPhoto() {

        Bitmap photo = null;
        Cursor cursorID = getContentResolver().query(uriContact, new String[]{Contacts._ID}, null, null, null);

        String contactID="";
        if (cursorID!=null&&cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(Contacts._ID));
            cursorID.close();
        }

        try {
            InputStream inputStream = Contacts.openContactPhotoInputStream(getContentResolver(),
                    ContentUris.withAppendedId(Contacts.CONTENT_URI, new Long(contactID)));

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


    public interface OnPickerContact {
        void onPickerContact(String name, String number, Bitmap photo);
    }

}
