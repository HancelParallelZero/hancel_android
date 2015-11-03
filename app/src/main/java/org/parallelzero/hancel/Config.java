package org.parallelzero.hancel;

/**
 * Created by Antonio Vanegas @hpsaturn on 8/12/15.
 */
public class Config {

	public static final boolean DEBUG=true;    // ==> R E V I S A R  P A R A  T I E N D A S //
	public static final boolean DEBUG_SERVICE = true;
	public static final boolean DEBUG_LOCATION = false;
	public static final boolean DEBUG_TASKS = true;
	public static final boolean DEBUG_MAP = true;

    // location
 	public static final int     TIME_AFTER_START   = 15;  // Start on x seconds after init Scheduler
	public static final long    DEFAULT_INTERVAL   = 1000 * 60 * 3;  // Default interval for background service: 3 minutes
	public static final long 	DEFAULT_INTERVAL_FASTER = 1000 * 60 * 1;
	public static final float   ACCURACY 	= 200;
	public static final long    LOCATION_ROUTE_INTERVAL = 1000 * 60;
	public static final long    LOCATION_MAP_INTERVAL = 1000 * 120;

	public static final int		VIBRATION_TIME_SMS = 2000;

	public static final String FIREBASE_MAIN = "https://hancel.firebaseio.com";
	public static final String FIREBASE_TRANSACTIONS = "tracks";
	public static float map_zoom_init = 14;
}
