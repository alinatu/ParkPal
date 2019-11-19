package com.example.parkpal;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPoint;
import com.google.maps.android.geojson.GeoJsonPolygon;

import java.util.ArrayList;
import java.util.List;

public class SearchMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int position;
    Park park;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_maps);
        Bundle bundle = getIntent().getExtras();
        position = bundle.getInt("position");
        park = Park_Search_Activity.parkObjectList.get(position);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private static LatLngBounds getPolygonLatLngBounds(final List<LatLng> polygon) {
        final LatLngBounds.Builder centerBuilder = LatLngBounds.builder();
        for (LatLng point : polygon) {
            centerBuilder.include(point);
        }
        return centerBuilder.build();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final Button back_Button = findViewById(R.id.back_Button);
        back_Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        // Zoom to the map
        final int POLYGON_PADDING_PREFERENCE = 200;
        List<LatLng> polygon = park.getPolygons().get(0).getCoordinates().get(0);
        final LatLngBounds latLngBounds = getPolygonLatLngBounds(polygon);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, POLYGON_PADDING_PREFERENCE));
        // Draw the park polygon
        PolygonOptions opts=new PolygonOptions();
        for (LatLng location : polygon) {
            opts.add(location);
        }
        Polygon mapPoly = mMap.addPolygon(opts.strokeColor(Color.BLACK).fillColor(getColor(R.color.fillPark)));

        // Draw the points
        // Benches
        String type = "BENCHES";
        for (int i = 0; i < park.getBenches().size(); i++) {
            LatLng location = park.getBenches().get(i).getCoordinates();
            Bitmap Marker = findMarkerForPoint(type);
            MarkerOptions markerOptions = new MarkerOptions().position(location)
                    .draggable(false)
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(Marker))
                    .visible(true)
                    .title(type);
            com.google.android.gms.maps.model.Marker marker = mMap.addMarker(markerOptions);
        }
        // DRINKING_FOUNTAINS
        type = "DRINKING_FOUNTAINS";
        for (int i = 0; i < park.getFountains().size(); i++) {
            LatLng location = park.getFountains().get(i).getCoordinates();
            Bitmap Marker = findMarkerForPoint(type);
            MarkerOptions markerOptions = new MarkerOptions().position(location)
                    .draggable(false)
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(Marker))
                    .visible(true)
                    .title(type);
            com.google.android.gms.maps.model.Marker marker = mMap.addMarker(markerOptions);
        }
        // OFFLEASH_DOG_AREAS
        type = "OFFLEASH_DOG_AREAS";
//        for (GeoJsonPolygon polygon : park.getDogAreas().get(0).getCoordinates().get(0)) {
//            dogareaMarkers.add(AddMarkerToCenterOfPolygon(polygon, Marker, type));
//        }
        for (int i = 0; i < park.getDogAreas().size(); i++) {
//            opts=new PolygonOptions();
//            for (LatLng location : polygon) {
//                opts.add(location);
//            }
//            mapPoly = mMap.addPolygon(opts.strokeColor(Color.BLACK).fillColor(getColor(R.color.fillPark)));
            LatLng location = park.getDogAreas().get(i).getCoordinates();
            Bitmap Marker = findMarkerForPoint(type);
            MarkerOptions markerOptions = new MarkerOptions().position(location)
                    .draggable(false)
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(Marker))
                    .visible(true)
                    .title(type);
            com.google.android.gms.maps.model.Marker marker = mMap.addMarker(markerOptions);
        }
        // PLAYGROUNDS
        type = "PLAYGROUNDS";
        for (int i = 0; i < park.getPlaygrounds().size(); i++) {
            LatLng location = park.getPlaygrounds().get(i).getCoordinates();
            Bitmap Marker = findMarkerForPoint(type);
            MarkerOptions markerOptions = new MarkerOptions().position(location)
                    .draggable(false)
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(Marker))
                    .visible(true)
                    .title(type);
            com.google.android.gms.maps.model.Marker marker = mMap.addMarker(markerOptions);
        }
        // SPORTS_FIELDS
        type = "SPORTS_FIELDS";
        for (int i = 0; i < park.getSportsFields().size(); i++) {
            LatLng location = park.getSportsFields().get(i).getCoordinates();
            Bitmap Marker = findMarkerForPoint(type);
            MarkerOptions markerOptions = new MarkerOptions().position(location)
                    .draggable(false)
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(Marker))
                    .visible(true)
                    .title(type);
            com.google.android.gms.maps.model.Marker marker = mMap.addMarker(markerOptions);
        }
        // WASHROOMS
        type = "WASHROOMS";
        for (int i = 0; i < park.getWashrooms().size(); i++) {
            LatLng location = park.getWashrooms().get(i).getCoordinates();
            Bitmap Marker = findMarkerForPoint(type);
            MarkerOptions markerOptions = new MarkerOptions().position(location)
                    .draggable(false)
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(Marker))
                    .visible(true)
                    .title(type);
            com.google.android.gms.maps.model.Marker marker = mMap.addMarker(markerOptions);
        }
    }
//
//    public LatLng findCenterOfPolygon(GeoJsonPolygon polygon) {
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        List<? extends List<LatLng>> polygonPoints = polygon.getCoordinates();
//        for (List<LatLng> latLngs : polygonPoints) {
//            for (LatLng latLng : latLngs) {
//                builder.include(latLng);
//            }
//        }
//        return builder.build().getCenter();
//    }
//
//    public Marker AddMarkerToCenterOfPolygon(GeoJsonPolygon polygon, Bitmap Marker, String title) {
//
//        MarkerOptions markerOptions = new MarkerOptions().position(findCenterOfPolygon(polygon))
//                .draggable(false)
//                .flat(true)
//                .icon(BitmapDescriptorFactory.fromBitmap(Marker))
//                .visible(false)
//                .title(title);
//        Marker marker = mMap.addMarker(markerOptions);
//        return marker;
//    }

    public Bitmap findMarkerForPoint(String type) {
        BitmapDrawable bitmapDraw = null;
        int size = 60;
        switch (type) {
            case "WASHROOMS":
                bitmapDraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.washroom);
                break;
            case "BENCHES":
                bitmapDraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.bench);
                size = 50;
                break;
            case "OFFLEASH_DOG_AREAS":
                bitmapDraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.dog_leash);
                break;
            case "DRINKING_FOUNTAINS":
                bitmapDraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.drinking_fountains);
                break;
            case "PLAYGROUNDS":
                bitmapDraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.playground);
                break;
            case "SPORTS_FIELDS":
                bitmapDraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.sport_field);
                break;
        }

        //resize the icon
        Bitmap b = bitmapDraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, size, size, false);

        return smallMarker;
    }
}
