package com.bitirme.gitbusters.borkinroads;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisplayRoutesActivity extends Activity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;
    private CheckBox mFavouriteCheckBox;
    private CheckBox mNearWater;
    private CheckBox mNearForest;
    private EditText mMinDurationEditText;
    private EditText mMaxDurationEditText;
    private Spinner mSpinnerSortingCondition;
    private ToggleButton mToggleButtonSortingDirection;
    private Button mButtonApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_routes);
        mGridLayoutManager = new GridLayoutManager(this, 2);
        mLinearLayoutManager = new LinearLayoutManager(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(new DisplayRouteAdapter(LIST_LOCATIONS));
        mRecyclerView.setRecyclerListener(mRecycleListener);

        mFavouriteCheckBox = (CheckBox) findViewById(R.id.checkbox_favourite);
        mNearWater = (CheckBox) findViewById(R.id.checkbox_lake_river_sea);
        mNearForest = (CheckBox) findViewById(R.id.checkbox_park_forest);
        mMinDurationEditText = (EditText) findViewById(R.id.text_min_route_duration);
        mMaxDurationEditText = (EditText) findViewById(R.id.text_max_route_diretion);
        mSpinnerSortingCondition = (Spinner) findViewById(R.id.spinner_sorting_condtion);
        mToggleButtonSortingDirection = (ToggleButton) findViewById(R.id.toggle_sorting_direction);

        mButtonApply = (Button) findViewById(R.id.button_apply);
        mButtonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFilters();
            }
        });
    }

    /*
    * Adapter View Holder pattern for displaying lite mode Gmap
     */

    private class DisplayRouteAdapter extends RecyclerView.Adapter<DisplayRouteAdapter.ViewHolder> implements Filterable {
        List<DisplayRouteRow> mRouteList;
        List<DisplayRouteRow> mFilteredRouteList;

        private DisplayRouteAdapter(List<DisplayRouteRow> routes) {
            super();
            this.mRouteList = routes;
            this.mFilteredRouteList = mRouteList;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder == null) return;

            holder.bindView(position);
        }

        @Override
        public int getItemCount() {
            if (mFilteredRouteList == null) return 0;
                return mFilteredRouteList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DisplayRouteAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.route_display_row, parent, false));
        }

        /**
        *Method for filtering date in RecyclerView
         */
        @Override
        public Filter getFilter() {
            return new Filter() {

                protected FilterResults performFiltering(FilterPreferences preferences) {
                    List<DisplayRouteRow> mFilteredRouteList = new ArrayList<>();
                    for(DisplayRouteRow route : mRouteList) {
                        if ( (!preferences.isFavourite() || (route.getFavourite() == preferences.isFavourite())) &&
                                (!preferences.isNearPark() || (route.isNearPark() == preferences.isNearPark())) &&
                                (!preferences.isNearWater() || (route.isNearWater() == preferences.isNearWater())) &&
                                (preferences.getMaxDuration() == null || (route.getEstimatedRouteDuration() <= preferences.getMaxDuration())) &&
                                (preferences.getMinDuration() == null || (route.getEstimatedRouteDuration() >= preferences.getMinDuration()))) {
                            mFilteredRouteList.add(route);
                        }
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mFilteredRouteList;
                    return filterResults;
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    if (constraint == null) return null;

                    return performFiltering(new FilterPreferences(constraint.toString().split(",")));
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results == null || !(results.values instanceof  ArrayList<?>)) return;

                    mFilteredRouteList = (ArrayList<DisplayRouteRow>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

            MapView mapView;
            TextView title;
            TextView routeDate;
            RatingBar ratingBar;
            GoogleMap map;
            View layout;
            ImageView favourite;

            public ViewHolder(View itemView) {
                super(itemView);
                layout = itemView;
                mapView = layout.findViewById(R.id.display_row_map);
                title = layout.findViewById(R.id.display_row_title);
                routeDate = layout.findViewById(R.id.display_row_date);
                ratingBar = layout.findViewById(R.id.display_row_rating);
                favourite = layout.findViewById(R.id.favourite);

                if (mapView != null) {
                    mapView.onCreate(null);
                    mapView.getMapAsync(this);
                }
            }

            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapsInitializer.initialize(getApplicationContext());
                map = googleMap;
                displayRouteOnMap();
            }


            /**
             * Sets up route from set of points and puts them on map
             * TODO: Add more dynamic focus on map for larger routes
             */
            private void displayRouteOnMap() {
                if (map == null) return;

                DisplayRouteRow data = (DisplayRouteRow) mapView.getTag();
                if (data == null) return;

                PolylineOptions polylineOptions = new PolylineOptions();
                for(LatLng point : data.getPoints())
                    polylineOptions.add(point);

                polylineOptions.width(12);
                polylineOptions.clickable(false);
                polylineOptions.color(Color.BLUE);
                map.addPolyline(polylineOptions);
                LatLng firstPoint = data.getPoints()[0];
                LatLng lastPoint = data.getPoints()[data.getPoints().length - 1];
                LatLng middlePoint = new LatLng((firstPoint.latitude + lastPoint.latitude)/2 ,(firstPoint.longitude + lastPoint.longitude)/2);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(middlePoint, 11f));
                map.addMarker((new MarkerOptions().position(firstPoint)));
                map.addMarker(new MarkerOptions().position(lastPoint));
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            }

            private void bindView(int pos) {
                DisplayRouteRow item = mFilteredRouteList.get(pos);
                layout.setTag(this);
                mapView.setTag(item);
                displayRouteOnMap();
                title.setText(item.getTitle());
                ratingBar.setRating(item.getRating());
                routeDate.setText(item.getRouteDate());
                if (item.getFavourite())
                    favourite.setColorFilter(Color.YELLOW);
                else
                    favourite.setColorFilter(null);

                favourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        /**
                         * TODO: Add db logic
                         */
                        if (favourite.getColorFilter() != null) {
                            favourite.clearColorFilter();
                        } else {
                            favourite.setColorFilter(Color.YELLOW);
                        }
                    }
                });
            }


        }
    }

    /**
     * Sets listener for each filter menu item( for more dynamic filtering)
     */
    private void setFilterMenuListeners() {
        EditText.OnFocusChangeListener mEditTextListener = new EditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    updateFilters();
            }
        };

        ToggleButton.OnCheckedChangeListener mToogleButtonListener = new ToggleButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateFilters();
            }
        };

        CheckBox.OnCheckedChangeListener mCheckBoxListener = new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateFilters();
            }
        };

        mFavouriteCheckBox.setOnCheckedChangeListener(mCheckBoxListener);
        mNearForest.setOnCheckedChangeListener(mCheckBoxListener);
        mNearWater.setOnCheckedChangeListener(mCheckBoxListener);
        mMaxDurationEditText.setOnFocusChangeListener(mEditTextListener);
        mMinDurationEditText.setOnFocusChangeListener(mEditTextListener);
        mToggleButtonSortingDirection.setOnCheckedChangeListener(mToogleButtonListener);
    }

    private RecyclerView.RecyclerListener mRecycleListener = new RecyclerView.RecyclerListener() {
        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            DisplayRouteAdapter.ViewHolder mapHolder = (DisplayRouteAdapter.ViewHolder) holder;
            if (mapHolder != null && mapHolder.map != null) {
                mapHolder.map.clear();
                mapHolder.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        }
    };



    /**
     * Creates FilterPreference object from filter menu values and runs filter
     */
    private void updateFilters() {
        DisplayRouteAdapter mAdapter = (DisplayRouteAdapter) mRecyclerView.getAdapter();
        mAdapter.getFilter().filter(new FilterPreferences(mFavouriteCheckBox.isChecked(), mNearWater.isChecked(), mNearForest.isChecked(), getFloatValueMax(mMaxDurationEditText.getText().toString()),getFloatValueMin(mMinDurationEditText.getText().toString()),
                mSpinnerSortingCondition.getSelectedItem().toString(), mToggleButtonSortingDirection.isChecked()).toString());

    }

    /**
    * Method for dealing with non-numerical values of EditText
     */

    private Float getFloatValueMax(String s) {
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            return Float.MAX_VALUE;
        }
    }

    private Float getFloatValueMin(String s) {
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            return Float.MIN_VALUE;
        }
    }

    /**
     * A list of locations to show in this ListView.
     */
    private static final List<DisplayRouteRow> LIST_LOCATIONS = new ArrayList<>(Arrays.asList(new DisplayRouteRow[]{
            new DisplayRouteRow("Home to School", new LatLng[]{new LatLng(39.941734, 32.63447), new LatLng(39.920665, 32.801853)}, 3.5f, "11/11/11", 1, 25, false, true, true),
            new DisplayRouteRow("School to Somewhere", new LatLng[]{new LatLng(39.920665, 32.801853), new LatLng(39.90, 32.514)}, 3.5f, "11/11/11", 2, 20, false, false, false),
            new DisplayRouteRow("Beijing2", new LatLng[]{new LatLng(50.854509, 4.376678), new LatLng(55.679423, 12.577114), new LatLng(52.372026, 9.735672)}, 3.5f, "11/11/11", 1, 13, false, false, false),
            new DisplayRouteRow("Home to School2", new LatLng[]{new LatLng(39.941734, 32.63447), new LatLng(39.920665, 32.801853)}, 3.5f, "11/11/11", 1, 38, true, true, true)
    }));



}
