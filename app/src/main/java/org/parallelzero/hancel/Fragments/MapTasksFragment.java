package org.parallelzero.hancel.Fragments;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import org.parallelzero.hancel.Config;
import org.parallelzero.hancel.MainActivity;
import org.parallelzero.hancel.models.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by hasus on 4/9/15.
 */
public class MapTasksFragment extends SupportMapFragment implements OnMapClickListener, OnMarkerClickListener, OnMapLongClickListener {

    public static final String TAG = MapTasksFragment.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG && Config.DEBUG_MAP;

    private static final int ANIM_TIME = 1600;
    private GoogleMap map;

    private HashMap<Track, Marker> hmMarks = new HashMap<Track, Marker>();
    private HashMap<Long, String> hmIDs = new HashMap<Long, String>();

    //    private MGPlatformApi platformApi = new MGPlatformApi();
    private Polyline lastPoline;

    public void initMap(GoogleMap map) {

        this.map = map;

        if (DEBUG) Log.d(TAG, "initMap..");
        UiSettings settings = map.getUiSettings();

        settings.setAllGesturesEnabled(true);
        settings.setZoomControlsEnabled(false);
        settings.setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        map.setOnMarkerClickListener(this);

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setOnMapClickListener(this);

        //Setting on long click listener
        map.setOnMapLongClickListener(this);

//        animToPosition(new LatLng(4.65149, -74.05929)); // Default Bogot?

    }


    public void addPoints(List<Track> data) {
        Iterator<Track> it = data.iterator();
        while (it.hasNext()) {
            Track track = it.next();
            if (!hmIDs.containsKey(track.loc.getTime())) addMark(track);
            else if (DEBUG) Log.d(TAG, "skip add mark");
        }
    }

    public void addMark(Track track) {
        if (DEBUG)Log.d(TAG,"addMark: "+track.toString());
        Marker marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(track.loc.getLatitude(), track.loc.getLongitude()))
                        .title(track.alias)
        );
        hmIDs.put(track.loc.getTime(), marker.getId());
        hmMarks.put(track, marker);
        animToPosition(new LatLng(track.loc.getLatitude(),track.loc.getLongitude()));
        if (DEBUG) Log.d(TAG, "[MAP_FRAGMENT] addMark: " + marker.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mapView = super.onCreateView(inflater, container, savedInstanceState);

//        moveMyLocationButton(mapView);

        return mapView;

    }

    /*
    private void moveMyLocationButton(View mapView) {

        // Get the button view
        View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);

        // and next place it, for exemple, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 40, 40);

    }*/

    public void animToPosition(LatLng pos) {
        if (pos != null) {
            if (DEBUG)
                Log.d(TAG, "[MAP_FRAGMENT] animateMark at: " + pos.latitude + "," + pos.longitude);
            CameraUpdate center = CameraUpdateFactory.newLatLngZoom(pos, Config.map_zoom_init);
            map.animateCamera(center, ANIM_TIME, null);
        } else if (DEBUG) Log.d(TAG, "[MAP_FRAGMENT] animateMark SKIP! pos is NULL");

    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (DEBUG)
            Log.d(TAG, "[MAP_FRAGMENT] onMapClick: " + latLng.latitude + "," + latLng.longitude);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (DEBUG) Log.d(TAG, "[MAP_FRAGMENT] onMarkerClick: " + marker.getId());
        return false;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (DEBUG)
            Log.d(TAG, "[MAP_FRAGMENT] onMapLongClick: " + latLng.latitude + "," + latLng.longitude);

    }
//
//    public void paintRoute(RouteResponse routeResponse) {
//
//        GeometryList routeLines = routeResponse.geometry;
//        if (routeLines != null) {
//            PolylineOptions rectOptions = new PolylineOptions();
//            for (ArrayList<Double> pairLatLon : routeLines.coordinates) {
//                rectOptions.add(new LatLng(pairLatLon.get(1), pairLatLon.get(0)));
//            }
//
//            if (lastPoline != null) lastPoline.remove();
//            lastPoline = map.addPolyline(rectOptions);
//            fixZoom(lastPoline);
//
//        } else {
//
//            if (DEBUG) Log.d(TAG, "[MAP_FRAGMENT] paintRoute !FAIL!");
//            UITools.showToast(getActivity(), R.string.toast_route_traced_error);
//
//        }
//
//    }


    private void fixZoom(Polyline route) {

        List<LatLng> points = route.getPoints();
        LatLngBounds.Builder bc = new LatLngBounds.Builder();

        for (LatLng item : points) {
            bc.include(item);
        }
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));

    }


    public void addPoints(Map<String, Object> tracks, String trackId, String alias) {
        if (tracks != null) {
            List<Track> data = new ArrayList<>();
            if (DEBUG) Log.d(TAG, "data: " + tracks.toString());
            Iterator<Object> it = tracks.values().iterator();
            while (it.hasNext()) {
                Map<String, Object> fbtrack = (Map<String, Object>) it.next();
                Location loc = new Location("");
                loc.setLatitude(Double.parseDouble(fbtrack.get("latitude").toString()));
                loc.setLongitude(Double.parseDouble(fbtrack.get("longitude").toString()));
                loc.setAccuracy(Float.parseFloat(fbtrack.get("accuracy").toString()));
//                        loc.setBearing(Float.parseFloat(track.get("bearing").toString()));
                loc.setTime(Long.parseLong(fbtrack.get("time").toString()));
                Track track = new Track();
                track.trackId = trackId;
                track.loc = loc;
                track.alias=alias;
                data.add(track);
            }
            addPoints(data);
        } else if (DEBUG) Log.w(TAG, "no data");
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy");
        getMain().removePartnersFragment();
        super.onDestroy();
    }

    private MainActivity getMain() {
        return ((MainActivity)getActivity());
    }

}
