package com.example.parkpal;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.*;

import org.json.JSONException;
import org.json.JSONObject;
//import org.json.simple.parser.JSONParser;
import org.json.JSONArray;
import android.util.Log;

import java.util.ArrayList;


public class AllParksMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_parks_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a marker in Sydney and move the camera
        LatLng queensPark = new LatLng(49.216230, -122.906558);
        mMap.addMarker(new MarkerOptions().position(queensPark).title("Marker in Queen's Park"));
        mMap.moveCamera(CameraUpdateFactory.zoomTo( 14.0f ));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(queensPark));

        HttpHandler sh = new HttpHandler();
        String jsonString = sh.loadJSONFromAsset(getApplicationContext());

        try {
            JSONObject jsonObj = new JSONObject(jsonString);
            JSONArray jsonParks = jsonObj.getJSONArray("features");
            // looping through All Contacts
            for (int i = 0; i < jsonParks.length(); i++) {
                JSONObject parksObj = jsonParks.getJSONObject(i);
                 GeoJsonLayer layer = new GeoJsonLayer(mMap, parksObj);
                layer.addLayerToMap();
            }
        } catch (JSONException e) {
            Log.e("Reading JSON to Array", "File not found: " + e.toString());
        }
    }
}
