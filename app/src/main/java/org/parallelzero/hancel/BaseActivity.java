package org.parallelzero.hancel;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.snackbar.Snackbar;

import org.parallelzero.hancel.Fragments.TestDialogFragment;
import org.parallelzero.hancel.System.Storage;
import org.parallelzero.hancel.services.HardwareButtonService;
import org.parallelzero.hancel.services.StatusScheduleReceiver;
import org.parallelzero.hancel.services.TrackLocationService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Created by Antonio Vanegas @hpsaturn on 11/5/15.
 */
public abstract class BaseActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    public static final String TAG = BaseActivity.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;

    public static final int PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    public static final int PERMISSIONS_REQUEST_COARSE_LOCATION = 1;
    private static final int PERMISSIONS_READ_CONTACTS = 2;
    private static final int PERMISSIONS_SEND_SMS = 3;

    private static final int PICK_CONTACT = 0;

    private Uri uriContact;
    private OnPickerContactUri contactListener;
    private FloatingActionButton fabPrimary;
    private FloatingActionButton fabSecondary;
    private FloatingActionsMenu fabMenu;

    private int mStackLevel = 0;
    private DrawerLayout drawerLaoyout;

    public void initDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLaoyout = findViewById(R.id.drawer_layout);
        fabMenu = findViewById(R.id.multiple_actions_down);
        fabPrimary = findViewById(R.id.bt_rings_from_contacts);
        fabSecondary = findViewById(R.id.bt_rings_from_qrcode);
        fabPrimary.setIconDrawable(getResources().getDrawable(R.drawable.ic_contact_mail_white_36dp));
        fabSecondary.setIconDrawable(getResources().getDrawable(R.drawable.ic_qrcode_scan_white_36dp));


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    public void setFbPrimaryListener(OnClickListener listener){
        fabPrimary.setOnClickListener(listener);
    }

    public void setFbSecondaryListener(OnClickListener listener){
        fabSecondary.setOnClickListener(listener);
    }

    public void fabHide() {
        fabMenu.setVisibility(View.GONE);
    }

    public void fabShow() {
        fabMenu.setVisibility(View.VISIBLE);
    }

    public void fabColapse(){
        fabMenu.collapse();
    }

    public void showFragment(Fragment fragment, String fragmentTag, boolean toStack) {

        try {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_default, fragment, fragmentTag);
            if (toStack) ft.addToBackStack(fragmentTag);
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showFragment(Fragment fragment, String fragmentTag, boolean toStack,int content) {

        try {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(content, fragment, fragmentTag);
            if (toStack) ft.addToBackStack(fragmentTag);
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showFragmentFull(Fragment fragment, String fragmentTag, boolean toStack) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_default, fragment, fragmentTag);
        if (toStack) ft.addToBackStack(fragmentTag);
        ft.commitAllowingStateLoss();

    }

    public void showDialogFragment(DialogFragment dialog, String TAG) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(dialog, TAG);
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

    public void popBackStackSecure(String TAG) {
        try {
            if (DEBUG) Log.d(TAG, "popBackStackSecure to: " + TAG);
            getSupportFragmentManager().popBackStack(TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void popBackLastFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            if (DEBUG) Log.d(TAG, "onBackPressed popBackStack for:" + getLastFragmentName());
            getSupportFragmentManager().popBackStack();
        }
    }

    public void removeFragment(Fragment fragment) {
        try {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLastFragmentName() {
        try {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) return "";
            FragmentManager fm = getSupportFragmentManager();
            return fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void showSnackLong(String msg) {
        Snackbar.make(drawerLaoyout, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void showSnackLong(int msg) {
        Snackbar.make(drawerLaoyout, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void startTrackLocationService() {
        if (DEBUG) Log.d(TAG, "[MainActivity] startMainService");
        Intent service = new Intent(this, TrackLocationService.class);
        startService(service);
        StatusScheduleReceiver.startScheduleService(this, Config.DEFAULT_INTERVAL);
        Storage.setShareLocationEnable(this, true);
    }

    public void startSMSService() {
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

    public void printUriData(Uri uri){
        if (DEBUG) Log.d(TAG, "[HOME] EXTERNAL INTENT: URI: " + uri.toString());
        if (DEBUG) Log.d(TAG, "[HOME] EXTERNAL INTENT: HOST: " + uri.getHost());
        if (DEBUG) Log.d(TAG, "[HOME] EXTERNAL INTENT: PATH: " + uri.getPath());
        if (DEBUG) Log.d(TAG, "[HOME] EXTERNAL INTENT: QUERY: " + uri.getQuery());
        if (DEBUG) Log.d(TAG, "[HOME] EXTERNAL INTENT: SCHEME: " + uri.getScheme());
        if (DEBUG) Log.d(TAG, "[HOME] EXTERNAL INTENT: PORT: " + uri.getPort());
        if (DEBUG) Log.d(TAG, "[HOME] EXTERNAL INTENT: AUTHORITY: " + uri.getAuthority());
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            try {
//                if(getLastFragmentName().equals(MapPartnersFragment.TAG)){
//                    popBackStackSecure(MapPartnersFragment.TAG);
//                    popBackStackSecure(MapTasksFragment.TAG);
//                }else
                    super.onBackPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

        if (id == R.id.nav_home) {

            showMain();

        } else if (id == R.id.nav_map){

            showMap();

        } else if (id == R.id.nav_rings) {

            showRings();

        } else if (id == R.id.nav_help) {
            showHelp();

        } else if (id == R.id.nav_about) {
            showAbout();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void drawerUnSelectAll() {

    }

    abstract void showMap();

    abstract void showRings();

    abstract void showMain();

    abstract void showHelp();

    abstract void showAbout();

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
            Uri uri = retrieveContactPhotoUri();
//            if (DEBUG) Log.d(TAG, "Contact Uri Photo: " + uri.toString());

            if (contactListener != null) contactListener.onPickerContact(name, number, uri.toString());

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

        if (cursor != null && cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));
            cursor.close();
        }

        return name;

    }

    private Bitmap retrieveContactPhoto() {

        Bitmap photo = null;
        Cursor cursorID = getContentResolver().query(uriContact, new String[]{Contacts._ID}, null, null, null);

        String contactID = "";
        if (cursorID != null && cursorID.moveToFirst()) {
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

    private Uri retrieveContactPhotoUri() {

        Cursor cursorID = getContentResolver().query(uriContact, new String[]{Contacts._ID}, null, null, null);
        String contactID = "";
        if (cursorID != null && cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(Contacts._ID));
            cursorID.close();
        }
//
////        return Uri.withAppendedPath(Contacts.CONTENT_URI,contactID);
//        return ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID));

        Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI,new Long(contactID));
        Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);

        return photoUri;


    }

    public void setContactListener(OnPickerContactUri contactListener) {
        this.contactListener = contactListener;
    }


    public interface OnPickerContact {
        void onPickerContact(String name, String number, Bitmap photo);
    }

    public interface OnPickerContactUri {
        void onPickerContact(String name, String number, String uri);
    }



}
