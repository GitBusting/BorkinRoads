package com.bitirme.gitbusters.borkinroads;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DisplayRoutesActivity extends Activity {

    private RecyclerView mRecyclerView;
    private CheckBox mFavouriteCheckBox;
    private CheckBox mNearWater;
    private CheckBox mNearForest;
    private EditText mMinDurationEditText;
    private EditText mMaxDurationEditText;
    private Spinner mSpinnerSortingCondition;
    private ToggleButton mToggleButtonSortingDirection;
    private Button mButtonApply;
    private ExpandableRelativeLayout expandableRelativeLayout;

    /**
     * A list of locations to show in this ListView.
     */
    private static final List<DisplayRouteRow> LIST_LOCATIONS = new ArrayList<>(Arrays.asList(new DisplayRouteRow("Home to School", new LatLng[]{new LatLng(39.941734, 32.63447), new LatLng(39.920665, 32.801853)}, 3.5f, "11/11/11", 1, 25, false, true, true),
            new DisplayRouteRow("School to Somewhere", new LatLng[]{new LatLng(39.920665, 32.801853), new LatLng(39.90, 32.514)}, 3.5f, "11/11/11", 2, 20, false, false, false),
            new DisplayRouteRow("Beijing2", new LatLng[]{new LatLng(50.854509, 4.376678), new LatLng(55.679423, 12.577114), new LatLng(52.372026, 9.735672)}, 3.5f, "11/11/11", 1, 13, false, false, false),
            new DisplayRouteRow("Home to School2", new LatLng[]{new LatLng(39.941734, 32.63447), new LatLng(39.920665, 32.801853)}, 3.5f, "11/11/11", 1, 38, true, true, true)));


    //Adapter View Holder pattern for displaying lite mode Google maps
    private final RecyclerView.RecyclerListener mRecycleListener = new RecyclerView.RecyclerListener() {
        @Override
        public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
            DisplayRouteAdapter.ViewHolder mapHolder = (DisplayRouteAdapter.ViewHolder) holder;
            if (mapHolder.map != null) {
                mapHolder.map.clear();
                mapHolder.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_routes);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 2);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(new DisplayRouteAdapter());
        mRecyclerView.setRecyclerListener(mRecycleListener);

        mFavouriteCheckBox = findViewById(R.id.checkbox_favourite);
        mNearWater = findViewById(R.id.checkbox_lake_river_sea);
        mNearForest = findViewById(R.id.checkbox_park_forest);
        mMinDurationEditText = findViewById(R.id.text_min_route_duration);
        mMaxDurationEditText = findViewById(R.id.text_max_route_direction);
        mSpinnerSortingCondition = findViewById(R.id.spinner_sorting_condition);
        mToggleButtonSortingDirection = findViewById(R.id.toggle_sorting_direction);


        ImageButton expand_button = findViewById(R.id.expanded_button);
        expand_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableRelativeLayout = findViewById(R.id.expandableLayout);
                expandableRelativeLayout.toggle();
            }
        });

        setFilterMenuListeners();


    }

    /**
     * Sets listener for each filter menu item( for more dynamic filtering)
     */
    private void setFilterMenuListeners() {


        TextWatcher mEditTextListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                updateFilters();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateFilters();
            }
        };

        ToggleButton.OnCheckedChangeListener mToggleButtonListener = new ToggleButton.OnCheckedChangeListener() {
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
        mMaxDurationEditText.addTextChangedListener(mEditTextListener);
        mMinDurationEditText.addTextChangedListener(mEditTextListener);
        mToggleButtonSortingDirection.setOnCheckedChangeListener(mToggleButtonListener);
    }

    /**
     * Creates FilterPreference object from filter menu values and runs filter
     */
    private void updateFilters() {
        DisplayRouteAdapter mAdapter = (DisplayRouteAdapter) mRecyclerView.getAdapter();
        assert mAdapter != null;
        mAdapter.getFilter().filter(new FilterPreferences(mFavouriteCheckBox.isChecked(), mNearWater.isChecked(), mNearForest.isChecked(), getFloatValueMax(mMaxDurationEditText.getText().toString()), getFloatValueMin(mMinDurationEditText.getText().toString()),
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

    private class DisplayRouteAdapter extends RecyclerView.Adapter<DisplayRouteAdapter.ViewHolder> implements Filterable {
        final List<DisplayRouteRow> mRouteList;
        List<DisplayRouteRow> mFilteredRouteList;

        private DisplayRouteAdapter() {
            super();
            this.mRouteList = DisplayRoutesActivity.LIST_LOCATIONS;
            this.mFilteredRouteList = mRouteList;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bindView(position);
        }

        @Override
        public int getItemCount() {
            if (mFilteredRouteList == null) return 0;
                return mFilteredRouteList.size();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DisplayRouteAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.route_display_row, parent, false));
        }

        /**
        *Method for filtering date in RecyclerView
         */
        @Override
        public Filter getFilter() {
            return new Filter() {

                FilterResults performFiltering(FilterPreferences preferences) {
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

                    switch (preferences.getSortingCondition()) {
                        case R.string.rating + "":
                            Collections.sort(mFilteredRouteList, new Comparator<DisplayRouteRow>() {
                                @Override
                                public int compare(DisplayRouteRow o1, DisplayRouteRow o2) {
                                    return o1.getRating().compareTo(o2.getRating());
                                }
                            });
                            break;
                        case R.string.route_used + "":
                            Collections.sort(mFilteredRouteList, new Comparator<DisplayRouteRow>() {
                                @Override
                                public int compare(DisplayRouteRow o1, DisplayRouteRow o2) {
                                    return o1.getNumberOfTimesRouteTaken() - o2.getNumberOfTimesRouteTaken();
                                }
                            });
                            break;
                        case R.string.using_time + "":
                            Collections.sort(mFilteredRouteList, new Comparator<DisplayRouteRow>() {
                                @Override
                                public int compare(DisplayRouteRow o1, DisplayRouteRow o2) {
                                    return o1.getRouteDate().compareTo(o2.getRouteDate());
                                }
                            });
                            break;
                        case R.string.estimated_time + "":
                            Collections.sort(mFilteredRouteList, new Comparator<DisplayRouteRow>() {
                                @Override
                                public int compare(DisplayRouteRow o1, DisplayRouteRow o2) {
                                    return o1.getEstimatedRouteDuration().compareTo(o2.getEstimatedRouteDuration());
                                }
                            });
                            break;
                    }

                    if (preferences.getSortingDirection())
                        Collections.reverse(mFilteredRouteList);

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mFilteredRouteList;
                    return filterResults;
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    if (constraint == null) return null;

                    return performFiltering(new FilterPreferences(constraint.toString().split(",")));
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results == null || !(results.values instanceof  ArrayList<?>)) return;

                    mFilteredRouteList = (ArrayList<DisplayRouteRow>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

            final MapView mapView;
            final TextView title;
            final TextView routeDate;
            final RatingBar ratingBar;
            GoogleMap map;
            final View layout;
            final ImageView favourite;

            ViewHolder(View itemView) {
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

                        //TODO: Add db logic

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



}
