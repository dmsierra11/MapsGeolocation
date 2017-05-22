package com.example.locationservices.home;

import com.example.locationservices.model.Place;

import java.util.List;

/**
 * Created by danielsierraf on 4/19/17.
 */

public interface MenuView {
    void addMarker(Place place);
    void populateGeofences(List<Place> places);
}
