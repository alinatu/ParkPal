package com.example.parkpal;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.*;

import org.json.JSONException;
import org.json.JSONObject;
//import org.json.simple.parser.JSONParser;
import org.json.JSONArray;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;


public class AllParksMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<GeoJsonLayer> parkLayers = new ArrayList<GeoJsonLayer>();
    ArrayList<GeoJsonLayer> benchLayers = new ArrayList<GeoJsonLayer>();
    ArrayList<GeoJsonLayer> fountainLayers = new ArrayList<GeoJsonLayer>();
    ArrayList<GeoJsonLayer> dogareaLayers = new ArrayList<GeoJsonLayer>();
    ArrayList<GeoJsonLayer> washroomLayers = new ArrayList<GeoJsonLayer>();
    ArrayList<GeoJsonLayer> playgroundLayers = new ArrayList<GeoJsonLayer>();
    ArrayList<GeoJsonLayer> sportsfieldLayers = new ArrayList<GeoJsonLayer>();

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
        String[] jsonString = sh.loadJSONFromAsset(getApplicationContext());

        try {
            JSONObject jsonObj = new JSONObject(jsonString[0]);
            JSONArray jsonParks = jsonObj.getJSONArray("features");
            // looping through All Contacts
            for (int i = 0; i < jsonParks.length(); i++) {
                JSONObject parksObj = jsonParks.getJSONObject(i);
                GeoJsonLayer layer = new GeoJsonLayer(mMap, parksObj);
                parkLayers.add(layer);
                layer.addLayerToMap();
            }

            for(int i = 1; i < jsonString.length; i++) {
                getGeoJsonPointFromJsonFile(jsonString[i]);
            }

            final Button back_Button = findViewById(R.id.back_Button);
            back_Button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });

            final ToggleButton parks_Toggle_Button = findViewById(R.id.parks_Toggle_Button);
            parks_Toggle_Button.setChecked(true);

            parks_Toggle_Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (int i = 0; i < parkLayers.size(); i++) {
                            parkLayers.get(i).addLayerToMap();
                        }
                    } else {
                        for (int i = 0; i < parkLayers.size(); i++) {
                            parkLayers.get(i).removeLayerFromMap();
                        }
                    }
                }
            });


            final ToggleButton benches_Button = findViewById(R.id.toggle_benches_Button);
            final ToggleButton toggle_dog_areas_Button = findViewById(R.id.toggle_dog_areas_Button);
            final ToggleButton toggle_fountains_Button = findViewById(R.id.toggle_fountains_Button);
            final ToggleButton toggle_playgrounds_Button = findViewById(R.id.toggle_playgrounds_Button);
            final ToggleButton toggle_sports_fields_Button = findViewById(R.id.toggle_sports_fields_Button);
            final ToggleButton toggle_washrooms_Button = findViewById(R.id.toggle_washrooms_Button);

            benches_Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (int i = 0; i < benchLayers.size(); i++) {
                            benchLayers.get(i).addLayerToMap();
                        }
                    } else {
                        for (int i = 0; i < benchLayers.size(); i++) {
                            benchLayers.get(i).removeLayerFromMap();
                        }
                    }
                }
            });
            toggle_dog_areas_Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (int i = 0; i < dogareaLayers.size(); i++) {
                            dogareaLayers.get(i).addLayerToMap();
                        }
                    } else {
                        for (int i = 0; i < dogareaLayers.size(); i++) {
                            dogareaLayers.get(i).removeLayerFromMap();
                        }
                    }
                }
            });
            toggle_fountains_Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (int i = 0; i < fountainLayers.size(); i++) {
                            fountainLayers.get(i).addLayerToMap();
                        }
                    } else {
                        for (int i = 0; i < fountainLayers.size(); i++) {
                            fountainLayers.get(i).removeLayerFromMap();
                        }
                    }
                }
            });
            toggle_playgrounds_Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (int i = 0; i < playgroundLayers.size(); i++) {
                            playgroundLayers.get(i).addLayerToMap();
                        }
                    } else {
                        for (int i = 0; i < playgroundLayers.size(); i++) {
                            playgroundLayers.get(i).removeLayerFromMap();
                        }
                    }
                }
            });
            toggle_sports_fields_Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (int i = 0; i < sportsfieldLayers.size(); i++) {
                            sportsfieldLayers.get(i).addLayerToMap();
                        }
                    } else {
                        for (int i = 0; i < sportsfieldLayers.size(); i++) {
                            sportsfieldLayers.get(i).removeLayerFromMap();
                        }
                    }
                }
            });
            toggle_washrooms_Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (int i = 0; i < washroomLayers.size(); i++) {
                            washroomLayers.get(i).addLayerToMap();
                        }
                    } else {
                        for (int i = 0; i < washroomLayers.size(); i++) {
                            washroomLayers.get(i).removeLayerFromMap();
                        }
                    }
                }
            });

//            JSONArray jsonWashrooms = Washrooms.getJSONArray("features");
//
//            for (int i = 0; i < jsonWashrooms.length(); i++) {
//                JSONObject WashroomsObj = jsonWashrooms.getJSONObject(i);
//                GeoJsonLayer layer = new GeoJsonLayer(mMap, WashroomsObj);
//                GeoJsonPointStyle WashroomStyle = layer.getDefaultPointStyle();
//                WashroomStyle.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.washroom));
//                layer.addLayerToMap();
//            }
        } catch (JSONException e) {
            Log.e("Reading JSON to Array", "File not found: " + e.toString());
        }
    }

    public void getGeoJsonPointFromJsonFile(String JsonString) {
        try {
            JSONObject JObj = new JSONObject(JsonString);
            JSONArray JArray = JObj.getJSONArray("features");
            BitmapDrawable bitmapDraw = null;
            String type = JObj.getString("name");

            switch (type) {
                case "WASHROOMS":
                    bitmapDraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.washroom);
                    break;
                case "BENCHES":
                    bitmapDraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.bench);
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
            Bitmap b = bitmapDraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 60, 60, false);

            for (int i = 0; i < JArray.length(); i++) {
                JSONObject Obj = JArray.getJSONObject(i);
                GeoJsonLayer layer = new GeoJsonLayer(mMap, Obj);
                GeoJsonPointStyle pointStyle = layer.getDefaultPointStyle();
                pointStyle.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                pointStyle.setTitle(type);
                //layer.addLayerToMap();
                switch (type) {
                    case "WASHROOMS":
                        washroomLayers.add(layer);
                        break;
                    case "BENCHES":
                        benchLayers.add(layer);
                        break;
                    case "OFFLEASH_DOG_AREAS":
                        dogareaLayers.add(layer);
                        break;
                    case "DRINKING_FOUNTAINS":
                        fountainLayers.add(layer);
                        break;
                    case "PLAYGROUNDS":
                        playgroundLayers.add(layer);
                        break;
                    case "SPORTS_FIELDS":
                        sportsfieldLayers.add(layer);
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
