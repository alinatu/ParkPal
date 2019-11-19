package com.example.parkpal;

import com.google.maps.android.geojson.GeoJsonPoint;
import com.google.maps.android.geojson.GeoJsonPolygon;

import org.json.JSONObject;

import java.util.ArrayList;

public class Park {
    private String name;
    private JSONObject shapeJSON;
    private ArrayList<GeoJsonPolygon> polygons;
    private ArrayList<GeoJsonPoint> benches;
    private ArrayList<GeoJsonPoint> fountains;
    private ArrayList<GeoJsonPoint> dogAreas;
    private ArrayList<GeoJsonPoint> playgrounds;
    private ArrayList<GeoJsonPoint> sportsFields;
    private ArrayList<GeoJsonPoint> washrooms;

    Park(String name, JSONObject shapeJSON) {
        this.name = name;
        this.shapeJSON = shapeJSON;
        this.polygons = new ArrayList<GeoJsonPolygon>();
        this.benches = new ArrayList<GeoJsonPoint>();
        this.dogAreas = new ArrayList<GeoJsonPoint>();
        this.fountains = new ArrayList<GeoJsonPoint>();
        this.playgrounds = new ArrayList<GeoJsonPoint>();
        this.sportsFields = new ArrayList<GeoJsonPoint>();
        this.washrooms = new ArrayList<GeoJsonPoint>();
    }
    public String getName() {return name;}
    public ArrayList<GeoJsonPolygon> getPolygons() {return polygons;}
    public ArrayList<GeoJsonPoint> getBenches() {return benches;}
    public ArrayList<GeoJsonPoint> getDogAreas() {return dogAreas;}
    public ArrayList<GeoJsonPoint> getFountains() {return fountains;}
    public ArrayList<GeoJsonPoint> getPlaygrounds() {return playgrounds;}
    public ArrayList<GeoJsonPoint> getSportsFields() {return sportsFields;}
    public ArrayList<GeoJsonPoint> getWashrooms() {return washrooms;}
    public void addPolygon(GeoJsonPolygon polygon) {polygons.add(polygon);}
    public void addBench(GeoJsonPoint bench) {benches.add(bench);}
    public void addDogArea(GeoJsonPoint dogArea) {dogAreas.add(dogArea);}
    public void addFountain(GeoJsonPoint fountain) {fountains.add(fountain);}
    public void addPlayground(GeoJsonPoint playground) {playgrounds.add(playground);}
    public void addSportsField(GeoJsonPoint sportsField) {sportsFields.add(sportsField);}
    public void addWashroom(GeoJsonPoint washroom) {washrooms.add(washroom);}
    public int getNumBenches() {
        int count = 0;
        for (GeoJsonPoint point: benches) {
            ++count;
        }
        return count;
    };
    public int getNumDogAreas() {
        int count = 0;
        for (GeoJsonPoint point: dogAreas) {
            ++count;
        }
        return count;
    };
    public int getNumFountains() {
        int count = 0;
        for (GeoJsonPoint point: fountains) {
            ++count;
        }
        return count;
    };
    public int getNumPlaygrounds() {
        int count = 0;
        for (GeoJsonPoint point: playgrounds) {
            ++count;
        }
        return count;
    };
    public int getNumSportsFields() {
        int count = 0;
        for (GeoJsonPoint point: sportsFields) {
            ++count;
        }
        return count;
    };
    public int getNumWashrooms() {
        int count = 0;
        for (GeoJsonPoint point: washrooms) {
            ++count;
        }
        return count;
    };
}
