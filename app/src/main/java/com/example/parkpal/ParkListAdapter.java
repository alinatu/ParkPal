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

public class ParkListAdapter extends ArrayAdapter<JSONObject> {
    Context _context;
    private static final String TAG = ParkListAdapter.class.getSimpleName();
    public ParkListAdapter(Context context, ArrayList<JSONObject> parks) {
        super(context, 0, parks);
        _context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Activity activity = (Activity) _context;
        // Get the data item for this position
        JSONObject jsonObject = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.park_row_layout, parent, false);
        }

        // Populate the data into the template view using the data object
        try {
            String name = jsonObject.getJSONObject("properties").get("Name").toString();
            if (name != "null" || name != "" || name != null || !name.isEmpty()) {
                // Lookup view for data population
                System.out.println(name);
                TextView parkName = convertView.findViewById(R.id.parkName);
                parkName.setText(name);
            }

        } catch (JSONException e){
            Log.e(TAG, "no name found");
        }

        // Return the completed view to render on screen
        return convertView;
    }
}

