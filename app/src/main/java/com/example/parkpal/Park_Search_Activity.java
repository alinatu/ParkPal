package com.example.parkpal;

import android.app.ProgressDialog;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class Park_Search_Activity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;
    ParkListAdapter adapter;
    // URL to get contacts JSON
    private static String SERVICE_URL = "http://opendata.newwestcity.ca/downloads/parks/PARKS.json";
    private ArrayList<JSONObject> parkList;
    static ArrayList<Park> parkObjectList;
    static ArrayList<Park> fullParkObjectList;
    static ArrayList<Park> oldParkObjectList;
    private List<CheckBox> checkBoxes;
    private String userEntry;

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

        userEntry = "";
        parkList = new ArrayList<JSONObject>();
        parkObjectList = new ArrayList<Park>();
        fullParkObjectList = new ArrayList<Park>();
        checkBoxes = new ArrayList<>();
        lv = findViewById(R.id.park_list);
        new GetContacts().execute();
    }


    @Override
    public void onResume() {
//        parkObjectList = fullParkObjectList;
        super.onResume();
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

                    Collections.sort(parkObjectList, new Comparator<Park>() {
                        @Override
                        public int compare(Park lhs, Park rhs) {
                            return (lhs.getName().compareTo(rhs.getName()));
                        }
                    });

                    fullParkObjectList = new ArrayList<>(parkObjectList);

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

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    //Intent intent = new Intent(Park_Search_Activity.this.getApplicationContext(), SearchMapsActivity.class);
                    Intent intent = new Intent(Park_Search_Activity.this.getApplicationContext(), AllParksMapsActivity.class);
                    //intent.putExtra("ParkList", parkObjectList);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            });

            //filter function
            final CheckBox bench_search_checkbox = findViewById(R.id.bench_search_checkbox);
            final CheckBox dog_area_search_checkbox = findViewById(R.id.dog_area_search_checkbox);
            final CheckBox fountain_search_checkbox = findViewById(R.id.fountain_search_checkbox);
            final CheckBox playground_search_checkbox = findViewById(R.id.playground_search_checkbox);
            final CheckBox sports_field_search_checkbox = findViewById(R.id.sports_field_search_checkbox);
            final CheckBox washrooms_search_checkbox = findViewById(R.id.washrooms_search_checkbox);

            checkBoxes.add(bench_search_checkbox);
            checkBoxes.add(dog_area_search_checkbox);
            checkBoxes.add(fountain_search_checkbox);
            checkBoxes.add(playground_search_checkbox);
            checkBoxes.add(sports_field_search_checkbox);
            checkBoxes.add(washrooms_search_checkbox);
            System.out.println(checkBoxes.size());
            for(CheckBox checkBox : checkBoxes) {
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        parkObjectList.clear();
                        System.out.println("Before: " + parkObjectList.size());
                        getParksBaseOnFilter();
                        System.out.println("After: " + parkObjectList.size());

                        adapter = new ParkListAdapter(Park_Search_Activity.this, parkObjectList);
                        lv.setAdapter(adapter);
                    }
                });
            }

            //search function
            final EditText keywordSearch = findViewById(R.id.keywordSearch);

            keywordSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    parkObjectList.clear();
                    if(!charSequence.equals(null)) {
                        userEntry = charSequence.toString();
                    }
                    getParksBaseOnFilter();
                    System.out.println(parkObjectList.size());
                    adapter = new ParkListAdapter(Park_Search_Activity.this, parkObjectList);
                    lv.setAdapter(adapter);
                }

                @Override
                public void afterTextChanged(Editable editable) { }
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
                    //System.out.println(feature.getGeometry().getType());
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

    public void getParksBaseOnFilter() {
        parkObjectList = new ArrayList<>(fullParkObjectList);
        if(!userEntry.isEmpty()) {
            for (int i = parkObjectList.size() - 1; i >= 0; i--) {
                if (!parkObjectList.get(i).getName().toLowerCase().contains(userEntry.toLowerCase())) {
                    parkObjectList.remove(i);
                }
            }
        }
        for(int i = 0; i < checkBoxes.size(); i++) {
            if(checkBoxes.get(i).isChecked()) {
                System.out.println(i);
                switch(i) {
                    case 0:
                        System.out.println("remove Benches");
                        for(int j = parkObjectList.size() - 1; j >= 0; j--) {
                            if(!parkObjectList.get(j).hasBenches()) {
                                System.out.println("remove Benches" + j);
                                parkObjectList.remove(parkObjectList.get(j));
                            }
                        }
                        break;
                    case 1:
                        for(int j = parkObjectList.size() - 1; j >= 0; j--) {
                            if(!parkObjectList.get(j).hasDogAreas()) {
                                parkObjectList.remove(parkObjectList.get(j));
                            }
                        }
                        break;
                    case 2:
                        for(int j = parkObjectList.size() - 1; j >= 0; j--) {
                            if(!parkObjectList.get(j).hasFountains()) {
                                parkObjectList.remove(parkObjectList.get(j));
                            }
                        }
                        break;
                    case 3:
                        for(int j = parkObjectList.size() - 1; j >= 0; j--) {
                            if(!parkObjectList.get(j).hasPlaygrounds()) {
                                parkObjectList.remove(parkObjectList.get(j));
                            }
                        }
                        break;
                    case 4:
                        for(int j = parkObjectList.size() - 1; j >= 0; j--) {
                            if(!parkObjectList.get(j).hasSportsFields()) {
                                parkObjectList.remove(parkObjectList.get(j));
                            }
                        }
                        break;
                    case 5:
                        for(int j = parkObjectList.size() - 1; j >= 0; j--) {
                            if(!parkObjectList.get(j).hasWashRoom()) {
                                parkObjectList.remove(parkObjectList.get(j));
                            }
                        }
                        break;
                }
            }
        }
    }
}