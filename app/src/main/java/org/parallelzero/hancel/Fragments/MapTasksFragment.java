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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.parallelzero.hancel.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by hasus on 4/9/15.
 */
public class MapTasksFragment extends MapFragment implements OnMapClickListener,OnMarkerClickListener,OnMapLongClickListener {

    public static final String TAG = MapTasksFragment.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG&&Config.DEBUG_MAP;

    private static final int 	ANIM_TIME 	= 1600;
    private GoogleMap map;

//    private HashMap<Visit, Marker> hmMarks=new HashMap<Visit, Marker>();
//    private HashMap<String, String> hmIDs=new HashMap<String, String>();

//    private MGPlatformApi platformApi = new MGPlatformApi();
    private Polyline lastPoline;

    public void initMap(GoogleMap map){
        
        this.map=map;

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

        animToPosition(new LatLng(4.65149, -74.05929)); // Default Bogot?

    }

    public void addVisitMark(Location location){
        if(location!=null) {
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(),location.getLongitude()))
                    .rotation(location.getBearing())
                    );
        }else{
            if (DEBUG) Log.d(TAG, "[MAP_FRAGMENT] addVisitMark failed!! SKIP!");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mapView = super.onCreateView(inflater, container, savedInstanceState);

        moveMyLocationButton(mapView);

        return mapView;

    }

    private void moveMyLocationButton(View mapView){

        // Get the button view
        View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);

        // and next place it, for exemple, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 40, 40);

    }

    public void animToPosition(LatLng pos) {
        if(pos!=null) {
            if (DEBUG)
                Log.d(TAG, "[MAP_FRAGMENT] animateMark at: " + pos.latitude + "," + pos.longitude);
            CameraUpdate center = CameraUpdateFactory.newLatLngZoom(pos, Config.map_zoom_init);
            map.animateCamera(center, ANIM_TIME, null);
        }else
            if (DEBUG) Log.d(TAG, "[MAP_FRAGMENT] animateMark SKIP! pos is NULL");

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


	private void fixZoom(Polyline route){

		List<LatLng> points = route.getPoints();
	    LatLngBounds.Builder bc = new LatLngBounds.Builder();

	    for (LatLng item : points) { bc.include(item); }
	    map.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));

	}


}
