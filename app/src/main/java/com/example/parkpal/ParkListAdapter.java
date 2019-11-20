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

import java.util.ArrayList;

public class ParkListAdapter extends ArrayAdapter<Park> implements Filterable {
    Context _context;
//    private ArrayList<Park> parkList = null;
//    private ArrayList<Park> filteredParkList = null;
    private ItemFilter mFilter = new ItemFilter();
    private static final String TAG = ParkListAdapter.class.getSimpleName();
    public ParkListAdapter(Context context, ArrayList<Park> parkList) {
        super(context, 0, parkList);
//        this.parkList = parkList;
//        this.filteredParkList = parkList;
        _context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Activity activity = (Activity) _context;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.park_row_layout, parent, false);
        }
        // Get the data item for this position
        //if (position < Park_Search_Activity.parkObjectList.size()) {
            Park park = Park_Search_Activity.parkObjectList.get(position);

            // Populate the data into the template view using the data object
            String name = park.getName();
            //System.out.println(name);
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

        //}
        // Return the completed view to render on screen
        return convertView;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            //ArrayList<Park> list = new ArrayList<Park>();

            //int count = list.size();
            final ArrayList<Park> nlist = new ArrayList<Park>(Park_Search_Activity.parkObjectList.size());

            Park park;
            //System.out.println("Size of count: " + count);
            for (int i = 0; i < Park_Search_Activity.parkObjectList.size(); i++) {
                park = Park_Search_Activity.parkObjectList.get(i);
                System.out.println("Park name: " + park.getName().toLowerCase());
                if (park.getName().toLowerCase().contains(filterString)) {
                    nlist.add(park);
                    System.out.println(park.getName().toLowerCase() + " matches");
                }
            }

            results.values = nlist;
            results.count = nlist.size();

//            if (filterString.isEmpty()) {
//                results.values = list;
//                results.count = list.size();
//                return results;
//            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Park_Search_Activity.parkObjectList = (ArrayList<Park>) results.values;
        }

    }
}

