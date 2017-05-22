package com.example.locationservices.model;

import com.example.locationservices.model.dummy.DummyContent;

import java.io.Serializable;

/**
 * Created by danielsierraf on 1/9/17.
 */

public class Place implements Serializable{
    private String id;
    private String name;
    private String desc;
    private Coords coords;
    private String proximity;

    public Place(){
    }

    public Place(String name) {
        this.id = DummyContent.SessionIdentifierGenerator.nextSessionId();
        this.name = name;
    }

    public String getProximity() {
        return proximity;
    }

    public void setProximity(String proximity) {
        this.proximity = proximity;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coords getCoords() {
        return coords;
    }

    public void setCoords(Coords coords) {
        this.coords = coords;
    }
}
