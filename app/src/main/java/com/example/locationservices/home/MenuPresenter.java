package com.example.locationservices.home;

import com.example.locationservices.model.Place;

import java.util.List;

/**
 * Created by danielsierraf on 4/19/17.
 */

public interface MenuPresenter {
    void getPlaces();
    void getRoutes(String origin, String destination, boolean sensor);
}
