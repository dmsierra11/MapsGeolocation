package com.example.locationservices.home;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.locationservices.model.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.locationservices.R;
import com.example.locationservices.model.Coords;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends MapsActivity implements OnMapReadyCallback, MenuView {

    private static final String TAG = "MainActivity";

    private MenuPresenter presenter;

    /**
     * Activity methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_menu);

        //Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        presenter = new MenuPresenterImpl(this);
    }

    /**
     * Map
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setPadding(0, 200, 0, 0);

        if (checkPermission()) {
            mMap.setMyLocationEnabled(true);
            if (mCurrentLocation != null)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation
                        .getLatitude(), mCurrentLocation.getLongitude()), zoomLevel));
        }

        //TODO: Zoom to user's locations
        mMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));

        Log.d(TAG, "My location enabled " + mMap.isMyLocationEnabled());

        //TODO: esconder lista hasta que lleguen los places
        presenter.getPlaces();

        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "Map clicked");
            }
        });
    }

    private String getDistanceFromplace(Location placeLoc) {
        float distance = mCurrentLocation.distanceTo(placeLoc);
        String distanceStr = "";
        if (distance > 1000){
            distance = distance / 1000;
            distanceStr = String.format("%.1f", distance)+" km";
        } else {
            distanceStr = String.format("%.1f", distance)+" m";
        }
        return distanceStr;
    }

    @Override
    public void addMarker(Place place) {
        LatLng newPlace = new LatLng(place.getCoords().getLatitude(),
                place.getCoords().getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(newPlace)
                .title(place.getName());
        Marker marker = mMap.addMarker(markerOptions);
        marker.setTag(place);
    }

    @Override
    public void populateGeofences(List<Place> placesList) {
        places = placesList;
        if (!places.isEmpty()) {
            populateGeofenceList();
//                    drawGeofence();
            if (!mGeofencesAdded)
                addGeofences();
        }
    }

    /**
     * Marker window Adapter override
     */

    class MyInfoWindowAdapter extends MapsActivity.MyInfoWindowAdapter{
        @Override
        public View getInfoContents(Marker marker) {
            Place place = (Place) marker.getTag();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.getCoords()
                    .getLatitude(), place.getCoords().getLongitude()), zoomLevel));

            //update proximity
            Location placeLoc = new Location("");
            placeLoc.setLongitude(place.getCoords().getLongitude());
            placeLoc.setLatitude(place.getCoords().getLatitude());
            String distanceStr = "";
            if (mCurrentLocation != null)
                distanceStr = getDistanceFromplace(placeLoc);
            place.setProximity(distanceStr);
            marker.setTag(place);
            tv_proximity.setText(place.getProximity());
            return super.getInfoContents(marker);
        }
    }
}
