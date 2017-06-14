package com.example.locationservices.home;

import android.util.Log;

import com.cocoahero.android.geojson.Feature;
import com.cocoahero.android.geojson.FeatureCollection;
import com.cocoahero.android.geojson.GeoJSON;
import com.cocoahero.android.geojson.GeoJSONObject;
import com.cocoahero.android.geojson.Point;
import com.cocoahero.android.geojson.Position;
import com.example.locationservices.API.APIService;
import com.example.locationservices.API.RestClientPublic;
import com.example.locationservices.API.classes.EndLocation_;
import com.example.locationservices.API.classes.Leg;
import com.example.locationservices.API.classes.Route;
import com.example.locationservices.API.classes.RouteList;
import com.example.locationservices.API.classes.StartLocation_;
import com.example.locationservices.API.classes.Step;
import com.example.locationservices.Constants;
import com.example.locationservices.FileManager;
import com.example.locationservices.R;
import com.example.locationservices.model.Coords;
import com.example.locationservices.model.Place;
import com.example.locationservices.model.dummy.DummyContent;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.media.CamcorderProfile.get;

/**
 * Created by danielsierraf on 4/19/17.
 */

public class MenuPresenterImpl implements MenuPresenter{

    private static final String TAG = "Home";
    MenuView menuView;

    private static final APIService publicService = RestClientPublic.getClient().getApiService();

    public MenuPresenterImpl(MenuView menuView){
        this.menuView = menuView;
    }

    @Override
    public void getPlaces() {
        List<Place> places;
//        for (Place place: DummyContent.createPlaces()){
//            menuView.addMarker(place);
//            places.add(place);
//        }
        places = getDatabase();
        //Geofence
        menuView.populateGeofences(places);
    }

    public List<Place> getDatabase(){
        File dir = new File(menuView.getActivityView().getFilesDir(),
                menuView.getActivityView().getString(R.string.app_name));
        FileManager.copyAssets(menuView.getActivityView(), dir);

        File geoPackageFile = new File(dir, Constants.geoJsonFile);

        String jsonString = FileManager.getStringFromFile(geoPackageFile.getPath());

        Log.d(TAG, jsonString);
        ArrayList<Place> places = new ArrayList<>();
        try {
            GeoJSONObject geoJSON = GeoJSON.parse(jsonString);
            if (geoJSON instanceof FeatureCollection){
                for (Feature feature: ((FeatureCollection) geoJSON).getFeatures()){
                    if (feature.getGeometry() instanceof Point){
                        Position pos = ((Point) feature.getGeometry()).getPosition();
                        Place place = new Place(feature.getProperties().get("nombre").toString());

                        place.setCoords(new Coords(pos.getLatitude(), pos.getLongitude()));
                        place.setDesc(feature.getProperties().get("descripcion") != null?
                                feature.getProperties().get("descripcion").toString(): "");
                        menuView.addMarker(place);
                        places.add(place);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return places;
    }

    public void getRoutes(String origin, String destination, boolean sensor){
        publicService.getRoutes(origin, destination, sensor).enqueue(new Callback<RouteList>() {
            @Override
            public void onResponse(Call<RouteList> call, Response<RouteList> response) {
                Log.d(TAG, "Got routes");
                RouteList routeList = response.body();
                List<Route> routes = null;
                if (routeList != null)
                    routes = routeList.getRoutes();
                    if (routes != null){
                        List<Leg> legs = routes.get(0).getLegs();
                        List<Step> steps = null;
                        if (legs != null)
                            steps = legs.get(0).getSteps();
                            if (steps != null){
                                PolylineOptions rectOptions = new PolylineOptions();
                                for (int i = 0; i < steps.size(); i++){
                                    StartLocation_ start_location = steps.get(i).getStartLocation();
                                    EndLocation_ end_location = steps.get(i).getEndLocation();
                                    rectOptions.add(new LatLng(start_location.getLat(),
                                                    start_location.getLng()));
                                }
                                menuView.drawRoutes(rectOptions);
                            }
                    }
            }

            @Override
            public void onFailure(Call<RouteList> call, Throwable t) {
                Log.d(TAG, "Failure");
            }
        });
    }

//    public void getPlaces(final List<Place> places) {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Challenge");
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> parsePlaces, ParseException e) {
//                if (e == null) {
//                    Log.d(TAG, "Got places");
//                    for (int i = 0; i < parsePlaces.size(); i++) {
//
//                        ParseObject placeObject = parsePlaces.get(i);
//
//                        Place place = new Place("Lugar "+i);
//
//                        //Get place coords
//                        JSONObject coordsObject = placeObject.getJSONObject("coords");
//                        try {
//                            Coords coords = new Coords(Double.valueOf(coordsObject.get("latitude").toString()),
//                                    Double.valueOf(coordsObject.get("longitude").toString()));
//                            place.setCoords(coords);
//                        } catch (JSONException e1) {
//                            e1.printStackTrace();
//                        }
//
//                        places.add(place);
//
//                        //Add marker in map
//                        menuView.addMarker(place);
//                    }
//                } else {
//                    Log.d(TAG, "Problem getting places");
//                }
//
//                //Geofence
//                menuView.populateGeofences(places);
//            }
//        });
//
//    }


}
