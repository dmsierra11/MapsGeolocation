package com.example.locationservices.model.dummy;

import com.example.locationservices.model.Coords;
import com.example.locationservices.model.Place;

import java.util.ArrayList;
import java.util.List;

import java.security.SecureRandom;
import java.math.BigInteger;

/**
 * Created by danielsierraf on 1/9/17.
 */

public class DummyContent {
    private static final String TAG = "DummyContent";
    public static List<Place> your_places = new ArrayList<>();

    public static List<Place> createPlaces(){
        //Madrid
        Place place = new Place("ETSISI");
        //TODO: Create dummy content
        place.setCoords(new Coords(40.390398, -3.628552));

        Place place2 = new Place("Otro lugar");
        place2.setCoords(new Coords(40.444392, -3.696666));

        Place place3 = new Place("Escuela Topografía");
        place3.setCoords(new Coords(40.390688, -3.630338));

        Place place4 = new Place("Biblioteca");
        place4.setCoords(new Coords(40.390623, -3.626916));

        your_places.add(place);
        your_places.add(place2);
        your_places.add(place3);
        your_places.add(place4);
        return your_places;
    }

    public static final class SessionIdentifierGenerator {
        private static SecureRandom random = new SecureRandom();

        public static String nextSessionId() {
            return new BigInteger(130, random).toString(32);
        }
    }
}
