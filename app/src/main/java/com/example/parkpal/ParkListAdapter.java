package com.example.parkpal;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ParkListAdapter extends ArrayAdapter<Park> implements Filterable {
    private Activity context;
    private ArrayList<Park> parkList = null;
    private static final String TAG = ParkListAdapter.class.getSimpleName();

    public ParkListAdapter(Activity context, ArrayList<Park> parkList) {
        super(context, R.layout.park_row_layout, parkList);
        this.context = context;
        this.parkList = parkList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.park_row_layout, null, true);

        // Get the data item for this position

        // Populate the data into the template view using the data object
        TextView parkName = listViewItem.findViewById(R.id.parkName);
        TextView numBenches = listViewItem.findViewById(R.id.numBenches);
        TextView numDogAreas = listViewItem.findViewById(R.id.numDogAreas);
        TextView numFountains = listViewItem.findViewById(R.id.numFountains);
        TextView numPlaygrounds = listViewItem.findViewById(R.id.numPlaygrounds);
        TextView numSportsFields = listViewItem.findViewById(R.id.numSportsFields);
        TextView numWashrooms = listViewItem.findViewById(R.id.numWashrooms);

        Park park = parkList.get(position);
        parkName.setText(park.getName());
        numBenches.setText("Benches: " + park.getNumBenches());
        numWashrooms.setText("Washrooms: " + park.getNumWashrooms());
        numDogAreas.setText("Dog Areas: " + park.getNumDogAreas());
        numFountains.setText("Fountains: " + park.getNumFountains());
        numPlaygrounds.setText("Playgrounds: " + park.getNumPlaygrounds());
        numSportsFields.setText("Sports Fields: " + park.getNumSportsFields());

        // Return the completed view to render on screen
        return listViewItem;
    }
}

