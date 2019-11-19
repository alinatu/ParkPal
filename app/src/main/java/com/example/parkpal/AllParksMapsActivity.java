package com.example.parkpal;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import android.os.Bundle;

import com.google.android.gms.common.Feature;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.lang.reflect.Array;
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
    ArrayList<Marker> dogareaMarkers = new ArrayList<>();

    private int position;
    Park park;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_parks_maps);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("position")) {
            position = bundle.getInt("position");
            park = Park_Search_Activity.parkObjectList.get(position);
        }

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

        if (park != null) {
            // Zoom to the map
            final int POLYGON_PADDING_PREFERENCE = 200;
            List<LatLng> polygon = park.getPolygons().get(0).getCoordinates().get(0);
            final LatLngBounds latLngBounds = getPolygonLatLngBounds(polygon);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, POLYGON_PADDING_PREFERENCE));
        } else {
            // Add a marker in New West and move the camera
            LatLng queensPark = new LatLng(49.216230, -122.906558);
//        mMap.addMarker(new MarkerOptions().position(queensPark).title("Marker in Queen's Park"));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(14.0f));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(queensPark));
        }



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
                GeoJsonPolygonStyle polygonStyle = layer.getDefaultPolygonStyle();
                polygonStyle.setFillColor(getColor(R.color.fillPark));
                polygonStyle.setStrokeWidth(3);
                layer.addLayerToMap();
            }

            for (int i = 1; i < jsonString.length; i++) {
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
            parks_Toggle_Button.setCompoundDrawableTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
            parks_Toggle_Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (int i = 0; i < parkLayers.size(); i++) {
//                            parkLayers.get(i).addLayerToMap();
                            GeoJsonPolygonStyle polygonStyle = parkLayers.get(i).getDefaultPolygonStyle();
                            polygonStyle.setVisible(true);
                            parks_Toggle_Button.setCompoundDrawableTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                        }
                    } else {
                        for (int i = 0; i < parkLayers.size(); i++) {
//                            parkLayers.get(i).removeLayerFromMap();
                            GeoJsonPolygonStyle polygonStyle = parkLayers.get(i).getDefaultPolygonStyle();
                            polygonStyle.setVisible(false);
                            parks_Toggle_Button.setCompoundDrawableTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.black));
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
                            GeoJsonPointStyle style = benchLayers.get(i).getDefaultPointStyle();
                            style.setVisible(true);
                            benches_Button.setCompoundDrawableTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                        }
                    } else {
                        for (int i = 0; i < benchLayers.size(); i++) {
                            GeoJsonPointStyle style = benchLayers.get(i).getDefaultPointStyle();
                            style.setVisible(false);
                            benches_Button.setCompoundDrawableTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.black));
                        }
                    }
                }
            });
            toggle_dog_areas_Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (int i = 0; i < dogareaLayers.size(); i++) {
                            GeoJsonPolygonStyle polygonStyle = dogareaLayers.get(i).getDefaultPolygonStyle();
                            polygonStyle.setVisible(true);
                            toggle_dog_areas_Button.setCompoundDrawableTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                        }
                        for (Marker marker: dogareaMarkers) {
                            marker.setVisible(true);
                        }
                    } else {
                        for (int i = 0; i < dogareaLayers.size(); i++) {
                            GeoJsonPolygonStyle polygonStyle = dogareaLayers.get(i).getDefaultPolygonStyle();
                            polygonStyle.setVisible(false);
                            toggle_dog_areas_Button.setCompoundDrawableTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.black));
                        }
                        for (Marker marker: dogareaMarkers) {
                            marker.setVisible(false);
                        }
                    }
                }
            });
            toggle_fountains_Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (int i = 0; i < fountainLayers.size(); i++) {
                            GeoJsonPointStyle style = fountainLayers.get(i).getDefaultPointStyle();
                            style.setVisible(true);
                            toggle_fountains_Button.setCompoundDrawableTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                        }
                    } else {
                        for (int i = 0; i < fountainLayers.size(); i++) {
                            GeoJsonPointStyle style = fountainLayers.get(i).getDefaultPointStyle();
                            style.setVisible(false);
                            toggle_fountains_Button.setCompoundDrawableTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.black));
                        }
                    }
                }
            });
            toggle_playgrounds_Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (int i = 0; i < playgroundLayers.size(); i++) {
                            GeoJsonPointStyle style = playgroundLayers.get(i).getDefaultPointStyle();
                            style.setVisible(true);
                            toggle_playgrounds_Button.setCompoundDrawableTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                        }
                    } else {
                        for (int i = 0; i < playgroundLayers.size(); i++) {
                            GeoJsonPointStyle style = playgroundLayers.get(i).getDefaultPointStyle();
                            style.setVisible(false);
                            toggle_playgrounds_Button.setCompoundDrawableTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.black));
                        }
                    }
                }
            });
            toggle_sports_fields_Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (int i = 0; i < sportsfieldLayers.size(); i++) {
                            GeoJsonPointStyle style = sportsfieldLayers.get(i).getDefaultPointStyle();
                            style.setVisible(true);
                            toggle_sports_fields_Button.setCompoundDrawableTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                        }
                    } else {
                        for (int i = 0; i < sportsfieldLayers.size(); i++) {
                            GeoJsonPointStyle style = sportsfieldLayers.get(i).getDefaultPointStyle();
                            style.setVisible(false);
                            toggle_sports_fields_Button.setCompoundDrawableTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.black));
                        }
                    }
                }
            });
            toggle_washrooms_Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (int i = 0; i < washroomLayers.size(); i++) {
                            GeoJsonPointStyle style = washroomLayers.get(i).getDefaultPointStyle();
                            style.setVisible(true);
                            toggle_washrooms_Button.setCompoundDrawableTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
                        }
                    } else {
                        for (int i = 0; i < washroomLayers.size(); i++) {
                            GeoJsonPointStyle style = washroomLayers.get(i).getDefaultPointStyle();
                            style.setVisible(false);
                            toggle_washrooms_Button.setCompoundDrawableTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.black));
                        }
                    }
                }
            });
        } catch (JSONException e) {
            Log.e("Reading JSON to Array", "File not found: " + e.toString());
        }
    }

    private static LatLngBounds getPolygonLatLngBounds(final List<LatLng> polygon) {
        final LatLngBounds.Builder centerBuilder = LatLngBounds.builder();
        for (LatLng point : polygon) {
            centerBuilder.include(point);
        }
        return centerBuilder.build();
    }

    public void getGeoJsonPointFromJsonFile(String JsonString) {
        try {
            JSONObject JObj = new JSONObject(JsonString);
            JSONArray JArray = JObj.getJSONArray("features");
            final String type = JObj.getString("name");
            Bitmap Marker = findMarkerForPoint(type);
            for (int i = 0; i < JArray.length(); i++) {
                JSONObject Obj = JArray.getJSONObject(i);
                final GeoJsonLayer layer = new GeoJsonLayer(mMap, Obj);
                GeoJsonPointStyle pointStyle = layer.getDefaultPointStyle();
                pointStyle.setIcon(BitmapDescriptorFactory.fromBitmap(Marker));
                pointStyle.setTitle(type);
                pointStyle.setVisible(false);
                switch (type) {
                    case "WASHROOMS":
                        washroomLayers.add(layer);
                        break;
                    case "BENCHES":
                        benchLayers.add(layer);
                        break;
                    case "OFFLEASH_DOG_AREAS":
                        GeoJsonMultiPolygon multiPolygon = null;
                        Iterable<GeoJsonFeature> features = layer.getFeatures();
                        for (GeoJsonFeature feature : features) {
                            if (feature.getGeometry() != null && feature.getGeometry().getType().equals("MultiPolygon")) {
                                multiPolygon = (GeoJsonMultiPolygon) feature.getGeometry();
                                List<GeoJsonPolygon> polygons = multiPolygon.getPolygons();
                                for (GeoJsonPolygon polygon : polygons) {
                                    dogareaMarkers.add(AddMarkerToCenterOfPolygon(polygon, Marker, type));
                                }
                            } else if (feature.getGeometry() != null && feature.getGeometry().getType().equals("Polygon")){
                                GeoJsonPolygon polygon = (GeoJsonPolygon)feature.getGeometry();
                                dogareaMarkers.add(AddMarkerToCenterOfPolygon(polygon, Marker, type));
                            }
                        }

                        GeoJsonPolygonStyle polygonStyle = layer.getDefaultPolygonStyle();
                        polygonStyle.setVisible(false);
                        polygonStyle.setFillColor(getColor(R.color.fillDogArea));
                        polygonStyle.setStrokeWidth(2);
                        polygonStyle.setZIndex(99);
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
                layer.addLayerToMap();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public LatLng findCenterOfPolygon(GeoJsonPolygon polygon) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        List<? extends List<LatLng>> polygonPoints = polygon.getCoordinates();
        for (List<LatLng> latLngs : polygonPoints) {
            for (LatLng latLng : latLngs) {
                builder.include(latLng);
            }
        }
        return builder.build().getCenter();
    }

    public Marker AddMarkerToCenterOfPolygon(GeoJsonPolygon polygon, Bitmap Marker, String title) {

        MarkerOptions markerOptions = new MarkerOptions().position(findCenterOfPolygon(polygon))
                .draggable(false)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromBitmap(Marker))
                .visible(false)
                .title(title);
        Marker marker = mMap.addMarker(markerOptions);
        return marker;
    }

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
