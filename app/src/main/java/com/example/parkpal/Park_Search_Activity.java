package com.example.parkpal;

import android.app.ProgressDialog;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.geojson.*;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.PolyUtil;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Park_Search_Activity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;
    ParkListAdapter adapter;
    // URL to get contacts JSON
    private static String SERVICE_URL = "http://opendata.newwestcity.ca/downloads/parks/PARKS.json";
    private ArrayList<JSONObject> parkList;
    private ArrayList<Park> parkObjectList;
    protected GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_search_);

        final Button back_Button = findViewById(R.id.back_Button);
        back_Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        parkList = new ArrayList<JSONObject>();
        parkObjectList = new ArrayList<Park>();
        lv = findViewById(R.id.park_list);
        new GetContacts().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Park_Search_Activity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String[] jsonStr = sh.loadJSONFromAsset(getApplicationContext());
            //jsonStr = sh.makeServiceCall(SERVICE_URL);
            ArrayList<String> uniqueParks = new ArrayList<String>();

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr[0]);

                    // Getting JSON Array node
                    JSONArray jsonParks = jsonObj.getJSONArray("features");
                    Park park = null;

//                    PolygonOptions polygonOptions = new PolygonOptions();
//                    polygonOptions.strokeColor(Color.RED);
//                    polygonOptions.fillColor(Color.BLUE);
                    GeoJsonPolygon polygon;

                    // looping through All Parks
                    for (int i = 0; i < jsonParks.length(); i++) {
                        JSONObject parksObj = jsonParks.getJSONObject(i);
                        String name = parksObj.getJSONObject("properties").get("Name").toString();
                        System.out.println(name);
                        GeoJsonLayer layer = new GeoJsonLayer(map, parksObj);
                        Iterable<GeoJsonFeature> features = layer.getFeatures();
                        if (!name.equals("null") && !name.isEmpty()) {
                            if (!uniqueParks.contains(name)) {
                                uniqueParks.add(name);
                                park = new Park(name, parksObj);
                                for (GeoJsonFeature feature : features) {
                                    polygon = null;
                                    if (feature.getGeometry() != null && feature.getGeometry().getType().equals("Polygon")) {
                                        polygon = (GeoJsonPolygon) feature.getGeometry();
                                        park.addPolygon(polygon);
                                    }
                                }
                                parkList.add(parksObj);
                                parkObjectList.add(park);
                            } else {
                                // add sub-park elements to parkList element
                                for (int j = 0; j < parkObjectList.size(); j++)
                                    if (parkObjectList.get(j).getName().equals(name)) {
                                        for (GeoJsonFeature feature : features) {
                                            polygon = null;
                                            if (feature.getGeometry() != null && feature.getGeometry().getType().equals("Polygon")) {
                                                polygon = (GeoJsonPolygon) feature.getGeometry();
                                                parkObjectList.get(j).addPolygon(polygon);
                                            }
                                        }
                                        break;
                                    }
                            }
                        }
                    }
                    for (int i = 1; i < jsonStr.length; i++) {
                        loadParkData(jsonStr[i]);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            adapter = new ParkListAdapter(Park_Search_Activity.this, parkObjectList);

            // Attach the adapter to a ListView
            lv.setAdapter(adapter);

            final EditText keywordSearchEditText = findViewById(R.id.keywordSearch);
            keywordSearchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    System.out.println("Text [" + s + "]");

                    adapter.getFilter().filter(s.toString());
                    //lv.setAdapter(adapter);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

    }

    private String readFile() {
        String myData = "";
        File myExternalFile = new File(".", "log.txt");
        try {
            FileInputStream fis = new FileInputStream(myExternalFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String strLine;
            while ((strLine = br.readLine()) != null) {
                myData = myData + strLine + "\n";
            }
            br.close();
            in.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myData;
    }

    public void loadParkData(String JsonString) {
        try {
            JSONObject jsonObject = new JSONObject(JsonString);
            JSONArray featuresArray = jsonObject.getJSONArray("features");
            GeoJsonPoint point;
            String type = jsonObject.getString("name");
            for (int i = 0; i < featuresArray.length(); i++) {
                JSONObject parksObj = featuresArray.getJSONObject(i);
                GeoJsonLayer layer = new GeoJsonLayer(map, parksObj);
                Iterable<GeoJsonFeature> features = layer.getFeatures();
                LatLng latlng = null;
                for (GeoJsonFeature feature : features) {
                    point = null;
                    System.out.println(feature.getGeometry().getType());
                    if (feature.getGeometry() != null && feature.getGeometry().getType().equals("Point")) {
                        point = (GeoJsonPoint) feature.getGeometry();
                        latlng = point.getCoordinates();
                    } else if (feature.getGeometry() != null && feature.getGeometry().getType().equals("MultiPolygon")) {
                        GeoJsonMultiPolygon multiPolygon = (GeoJsonMultiPolygon) feature.getGeometry();
                        List<GeoJsonPolygon> polygons = multiPolygon.getPolygons();
                        for (GeoJsonPolygon polygon : polygons) {
                            latlng = findCenterOfPolygon(polygon);
                        }
                    } else if (feature.getGeometry() != null && feature.getGeometry().getType().equals("Polygon")) {
                        GeoJsonPolygon polygon = (GeoJsonPolygon)feature.getGeometry();
                        latlng = findCenterOfPolygon(polygon);
                    }
                        for (Park park : parkObjectList) {
                            for (GeoJsonPolygon parkPoly : park.getPolygons()) {
//                                for (int k = 0; k < parkPoly.getCoordinates().size(); k++) {
//                                    latLngPoly = parkPoly.getCoordinates().get(k);
//                                }
                                java.util.List<LatLng> latLngPoly = parkPoly.getCoordinates().get(0);
                                if (PolyUtil.containsLocation(latlng, latLngPoly, true)) {
                                    switch (type) {
                                        case "WASHROOMS":
                                            park.addWashroom(point);
                                            break;
                                        case "BENCHES":
                                            park.addBench(point);
                                            break;
                                        case "OFFLEASH_DOG_AREAS":
                                            park.addDogArea(point);
                                            break;
                                        case "DRINKING_FOUNTAINS":
                                            park.addFountain(point);
                                            break;
                                        case "PLAYGROUNDS":
                                            park.addPlayground(point);
                                            break;
                                        case "SPORTS_FIELDS":
                                            park.addSportsField(point);
                                            break;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch(JSONException e){
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
    }