package com.example.locationservices.home;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.Manifest;
import android.app.Dialog;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.locationservices.model.Place;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.example.locationservices.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.os.Build.VERSION.SDK_INT;


public class MapsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback,
        LocationListener {

    private static final String TAG = "MapsActivity";

    /**
     * Location
     */
    protected static final int MY_LOCATION_REQUEST_CODE = 1;
    protected GoogleMap mMap;
    protected static final float zoomLevel = 14;
    protected Location mCurrentLocation;
    protected LatLng mCurrentLatLong;
    protected static final int REQUEST_CHECK_SETTINGS = 1;
    protected static final long LOCATION_INTERVAL_MILISECONDS = 10 * 1000; //10 seconds
    protected static final long LOCATION_INTERVAL_MILISECONDS_SLOW = 3600000; //60 minutes
    protected static final long LOCATION_FASTEST_INTERVAL_MILISECONDS = 60000; //1 minute
    protected LocationRequest mLocationRequestAccuracy;
    protected GoogleApiClient mGoogleApiClient;
    protected boolean mRequestingLocationUpdates = false;

    /**
     * Geofence
     */
    public static final String sharedPrefsKey = "preferences";
    public static final String geofencesAddedKey = "geofencesAdded";
    public static final float GEOFENCE_RADIUS_IN_METERS = 1000;
    public static final int TIME_IN_MILLIS_DWELL = 5 * 60 * 1000;
    protected PendingIntent mGeofencePendingIntent;
    protected boolean mGeofencesAdded;
    protected SharedPreferences mSharedPreferencesChallengesGeo;
    protected List<Geofence> mGeofenceList = new ArrayList<Geofence>();

    protected List<Place> places = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGoogleApi();
        //Geofences
        mGeofencePendingIntent = null;
        mSharedPreferencesChallengesGeo = getSharedPreferences(sharedPrefsKey, MODE_PRIVATE);
        mGeofencesAdded = mSharedPreferencesChallengesGeo.getBoolean(geofencesAddedKey, false);

        //Last location
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(checkPermission())
            mCurrentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO: Uncomment for marshmallow
        grantPermissions();
        mLocationRequestAccuracy = LocationRequest.create().
                setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).
                setInterval(LOCATION_INTERVAL_MILISECONDS);
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //TODO: Reducir consumo de recursos cuando está en background
//        mLocationRequestAccuracy = LocationRequest.create().
//                setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY).
//                setFastestInterval(LOCATION_FASTEST_INTERVAL_MILISECONDS).
//                setInterval(LOCATION_INTERVAL_MILISECONDS_SLOW);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        if (mGeofencesAdded)
            removeGeofences();
    }

    /**
     * Permissions
     */

    //TODO: Uncomment this for marshmallow
    public void grantPermissions() {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission())
                requestPermission();
//            else {
//                Toast.makeText(this,"Permission already granted.",Toast.LENGTH_LONG).show();
//                if (mMap != null)
//                    mMap.setMyLocationEnabled(true);
//            }
        }
    }
//
    protected boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;

        } else {
            return false;
        }
    }

    //TODO: Uncomment this for marshmallow
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//            Toast.makeText(this, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mMap != null) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
//                    Toast.makeText(this, getString(R.string.location_permission), Toast.LENGTH_LONG)
//                            .show();
                }
                break;
        }
    }

    public void initGoogleApi(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //TODO: You can do a validation that if app is on foreground then it should be HIGH_ACCURACY and if not the BALANCED_POWER_ACCURACY
//        Toast.makeText(this, "Location services connected", Toast.LENGTH_SHORT).show();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequestAccuracy);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(this);

        if (checkPermission()){
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mMap != null && mCurrentLocation != null)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation
                        .getLatitude(), mCurrentLocation.getLongitude()), zoomLevel));

            if (!mRequestingLocationUpdates)
                startLocationUpdates();
        }

        if (!mGeofencesAdded)
            addGeofences();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Location failed");
        Integer resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SUCCESS) {
            //Do what you want
            mGoogleApiClient.connect();
        } else {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, 0);
            if (dialog != null) {
                //This dialog will help the user update to the latest GooglePlayServices
                dialog.show();
            }
        }
    }

    protected void startLocationUpdates() {
        Log.d(TAG, "Starting location updates");
        if (checkPermission()) {
            mRequestingLocationUpdates = true;
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequestAccuracy, this);
        }
    }

    protected void stopLocationUpdates() {
        mRequestingLocationUpdates = false;
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
//        Log.d(TAG, "Location changed");
        mCurrentLocation = location;
        mCurrentLatLong = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//        if (mMap != null)
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentLatLong));
//        drawGeofence();
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    protected void populateGeofenceList() {
        for (Place place : places) {
            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(place.getId())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            place.getCoords().getLatitude(),
                            place.getCoords().getLongitude(),
                            GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                            | Geofence.GEOFENCE_TRANSITION_EXIT)
//                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
//                        .setLoiteringDelay(TIME_IN_MILLIS_DWELL)
                    // Create the geofence.
                    .build());
        }
    }

    protected GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        Log.d(TAG, "Getting geofencing request");
        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
//        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);
        Log.d(TAG, "Geofence list empty: "+mGeofenceList.isEmpty());
        Log.d(TAG, "Build Geofencing Request");
        return builder.build();
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofences() {
        if (!mGoogleApiClient.isConnected()) {
//            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Google api clienet not connected");
            return;
        }

        Log.d(TAG, "Adding geodences");
        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Log.e(TAG, "Could not add geofences, security exception");
        } catch (Exception e) {
            Log.e(TAG, "Could not add geofences. "+e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void removeGeofences(){
        if (!mGoogleApiClient.isConnected()) {
//            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            securityException.printStackTrace();
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    protected PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(@NonNull Result result) {
        final Status status = result.getStatus();
        if (result instanceof LocationSettingsResult){
            LocationSettingsResult locationSettingsResult = (LocationSettingsResult) result;
            final LocationSettingsStates locationSettingsStates = locationSettingsResult
                    .getLocationSettingsStates();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied. But could be fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                        e.printStackTrace();
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no way to fix the
                    // settings so we won't show the dialog.
//                    Toast.makeText(this, "Settings change unavailable", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            Log.d(TAG, "Goefences result: "+status.isSuccess());
            if (status.isSuccess()) {
                Log.d(TAG, "Geofences added");
                // Update state and save in shared preferences.
                mGeofencesAdded = !mGeofencesAdded;
                SharedPreferences.Editor editor = mSharedPreferencesChallengesGeo.edit();
                editor.putBoolean(geofencesAddedKey, mGeofencesAdded);
                editor.apply();
            } else {
                // Get the status code for the error and log it using a user-friendly message.
                String errorMessage = GeofenceErrorMessages.getErrorString(this, status.getStatusCode());
                Log.e(TAG, errorMessage+". Status code: "+status.getStatusCode());
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    Log.d(TAG, "Failed. Retrying to add geofences");

                    //addGeofences();
                }
            }
        }
    }

    // Draw Geofence circle on GoogleMap
    protected Circle geoFenceLimits;
    protected void drawGeofence() {
        Log.d(TAG, "Drawing geofence");

        if ( geoFenceLimits != null )
            geoFenceLimits.remove();

        if (mCurrentLocation != null){
            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                    .strokeColor(Color.argb(50, 70,70,70))
                    .fillColor( Color.argb(100, 150,150,150) )
                    .radius( GEOFENCE_RADIUS_IN_METERS );
            geoFenceLimits = mMap.addCircle( circleOptions );
        }
    }

    /**
     * Custom info windows
     */
    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        @BindView(R.id.title)
        TextView tvTitle;
        @BindView(R.id.tv_proximity)
        TextView tv_proximity;

        MyInfoWindowAdapter() {
            myContentsView = getLayoutInflater().inflate(R.layout.custom_info, null);
            ButterKnife.bind(this, myContentsView);
        }

        @Override
        public View getInfoContents(Marker marker) {
//            Challenge challenge = challenge_marker.get(marker.getTitle());
            Place place = (Place) marker.getTag();
            tvTitle.setText(marker.getTitle());
            tv_proximity.setText(place.getProximity());

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

    }
}
