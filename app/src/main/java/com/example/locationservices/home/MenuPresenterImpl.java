package com.example.locationservices.home;

import com.example.locationservices.model.Place;
import com.example.locationservices.model.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danielsierraf on 4/19/17.
 */

public class MenuPresenterImpl implements MenuPresenter{

    private static final String TAG = "Home";
    MenuView menuView;

    public MenuPresenterImpl(MenuView menuView){
        this.menuView = menuView;
    }

    @Override
    public void getPlaces() {
        List<Place> places = new ArrayList<>();
        for (Place place: DummyContent.createPlaces()){
            menuView.addMarker(place);
            places.add(place);
        }
        //Geofence
        menuView.populateGeofences(places);
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
