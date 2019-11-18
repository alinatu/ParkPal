package com.example.parkpal;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ParkListAdapter extends ArrayAdapter<Park> {
    Context _context;
    private static final String TAG = ParkListAdapter.class.getSimpleName();
    public ParkListAdapter(Context context, ArrayList<Park> parks) {
        super(context, 0, parks);
        _context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Activity activity = (Activity) _context;
        // Get the data item for this position
        Park park = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.park_row_layout, parent, false);
        }

        // Populate the data into the template view using the data object
        String name = park.getName();
        System.out.println(name);
        TextView parkName = convertView.findViewById(R.id.parkName);
        parkName.setText(name);

        TextView numBenches = convertView.findViewById(R.id.numBenches);
        numBenches.setText("Benches: " + park.getNumBenches());

        TextView numDogAreas = convertView.findViewById(R.id.numDogAreas);
        numDogAreas.setText("Dog Areas: " + park.getNumDogAreas());

        TextView numFountains = convertView.findViewById(R.id.numFountains);
        numFountains.setText("Fountains: " + park.getNumFountains());

        TextView numPlaygrounds = convertView.findViewById(R.id.numPlaygrounds);
        numPlaygrounds.setText("Playgrounds: " + park.getNumPlaygrounds());

        TextView numSportsFields = convertView.findViewById(R.id.numSportsFields);
        numSportsFields.setText("Sports Fields: " + park.getNumSportsFields());

        TextView numWashrooms = convertView.findViewById(R.id.numWashrooms);
        numWashrooms.setText("Washrooms: " + park.getNumWashrooms());


//            if (name != "null" || name != "" || name != null || !name.isEmpty()) {
//                if (!parkNames.contains(name)) {
//                    parkNames.add(name);
//                }
//                // Lookup view for data population
//            }


        // Return the completed view to render on screen
        return convertView;
    }
}

