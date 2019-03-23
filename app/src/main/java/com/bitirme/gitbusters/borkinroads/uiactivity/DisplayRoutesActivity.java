package com.bitirme.gitbusters.borkinroads.uiactivity;

import android.app.Activity;
import android.content.Intent;
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

import com.bitirme.gitbusters.borkinroads.R;
import com.bitirme.gitbusters.borkinroads.data.RestRecordImpl;
import com.bitirme.gitbusters.borkinroads.data.RouteRecord;
import com.bitirme.gitbusters.borkinroads.data.UserRecord;
import com.bitirme.gitbusters.borkinroads.dbinterface.RestPuller;
import com.bitirme.gitbusters.borkinroads.dbinterface.RestUpdater;
import com.bitirme.gitbusters.borkinroads.uihelpers.FilterPreferences;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DisplayRoutesActivity extends Activity {

    private RecyclerView mRecyclerView;
    private CheckBox mFavouriteCheckBox;
    private CheckBox mNearWater;
    private CheckBox mNearForest;
    private EditText mMinDurationEditText;
    private EditText mMaxDurationEditText;
    private Spinner mSpinnerSortingCondition;
    private ToggleButton mToggleButtonSortingDirection;
    private ExpandableRelativeLayout expandableRelativeLayout;

    private static List<RouteRecord> routeList;


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

        routeList  = UserRecord.activeUser.getRoutes();

        System.out.println("Number of previous routes: " + routeList.size());

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        final DisplayRouteAdapter displayRouteAdapter = new DisplayRouteAdapter(routeList);
        mRecyclerView.setAdapter(displayRouteAdapter);
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
        final List<RouteRecord> mRouteList;
        List<RouteRecord> mFilteredRouteList;

        private DisplayRouteAdapter(List<RouteRecord> list) {
            super();
            this.mRouteList = list;
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
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.route_display_row, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = mRecyclerView.getChildLayoutPosition(view);

                    Intent intent = new Intent(DisplayRoutesActivity.this, RouteDetailsActivity.class);
                    intent.putExtra("id", mFilteredRouteList.get(pos).getEntryID());
                    startActivity(intent);

                }
            });

            return new DisplayRouteAdapter.ViewHolder(view);
        }

        /**
        *Method for filtering date in RecyclerView
         */
        @Override
        public Filter getFilter() {
            return new Filter() {

                FilterResults performFiltering(FilterPreferences preferences) {
                    List<RouteRecord> mFilteredRouteList = new ArrayList<>();
                    for(RouteRecord route : mRouteList) {
                        if ( (!preferences.isFavourite() || (route.isFavorite() == preferences.isFavourite())) &&
                                (!preferences.isNearPark() || (route.isNearPark() == preferences.isNearPark())) &&
                                (!preferences.isNearWater() || (route.isNearLake() == preferences.isNearWater())) &&
                                (preferences.getMaxDuration() == null || (route.getEstimatedMinutes() <= preferences.getMaxDuration())) &&
                                (preferences.getMinDuration() == null || (route.getEstimatedMinutes() >= preferences.getMinDuration()))) {
                            mFilteredRouteList.add(route);
                        }
                    }

                    switch (preferences.getSortingCondition()) {
                        case R.string.rating + "":
                            Collections.sort(mFilteredRouteList, new Comparator<RouteRecord>() {
                                @Override
                                public int compare(RouteRecord o1, RouteRecord o2) {
                                    return o1.getRating().compareTo(o2.getRating());
                                }
                            });
                            break;
                        case R.string.route_used + "":
                            Collections.sort(mFilteredRouteList, new Comparator<RouteRecord>() {
                                @Override
                                public int compare(RouteRecord o1, RouteRecord o2) {
                                    return o1.getNoUsed() - o2.getNoUsed();
                                }
                            });
                            break;
                        case R.string.using_time + "":
                            Collections.sort(mFilteredRouteList, new Comparator<RouteRecord>() {
                                @Override
                                public int compare(RouteRecord o1, RouteRecord o2) {
                                    return o1.getDate().compareTo(o2.getDate());
                                }
                            });
                            break;
                        case R.string.estimated_time + "":
                            Collections.sort(mFilteredRouteList, new Comparator<RouteRecord>() {
                                @Override
                                public int compare(RouteRecord o1, RouteRecord o2) {
                                    return o1.getEstimatedMinutes() -(o2.getEstimatedMinutes());
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

                    mFilteredRouteList = (ArrayList<RouteRecord>) results.values;
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
            final Button mButtonWalk;

            ViewHolder(View itemView) {
                super(itemView);
                layout = itemView;
                mapView = layout.findViewById(R.id.display_row_map);
                title = layout.findViewById(R.id.display_row_title);
                routeDate = layout.findViewById(R.id.display_row_date);
                ratingBar = layout.findViewById(R.id.display_row_rating);
                favourite = layout.findViewById(R.id.favourite);
                mButtonWalk = (Button) layout.findViewById(R.id.use_route);
                if (mapView != null) {
                    mapView.onCreate(null);
                    mapView.setClickable(false);
                    mapView.getMapAsync(this);
                }


            }

            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapsInitializer.initialize(getApplicationContext());
                map = googleMap;
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                displayRouteOnMap();
            }


            /**
             * Sets up route from set of points and puts them on map
             * TODO: Add more dynamic focus on map for larger routes
             */
            private void displayRouteOnMap() {
                if (map == null) return;

                RouteRecord data = (RouteRecord) mapView.getTag();
                if (data == null) return;

                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.add(data.getStartCoords());
                for(LatLng point : data.getWaypoints())
                    polylineOptions.add(point);
                polylineOptions.add(data.getEndCoords());

                polylineOptions.width(12);
                polylineOptions.clickable(false);
                polylineOptions.color(Color.BLUE);
                map.addPolyline(polylineOptions);
                LatLng firstPoint = data.getStartCoords();
                LatLng lastPoint = data.getEndCoords();
                LatLng middlePoint = new LatLng((firstPoint.latitude + lastPoint.latitude)/2 ,(firstPoint.longitude + lastPoint.longitude)/2);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(middlePoint, 11f));
                map.addMarker((new MarkerOptions().position(firstPoint)));
                map.addMarker(new MarkerOptions().position(lastPoint));
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            }

            private void bindView(final int pos) {
                RouteRecord item = mFilteredRouteList.get(pos);
                layout.setTag(this);
                mapView.setTag(item);
                displayRouteOnMap();
                title.setText(item.getTitle());
                ratingBar.setRating((item.getRating().floatValue()));
                routeDate.setText(item.getDate());
                if (item.isFavorite())
                    favourite.setColorFilter(Color.YELLOW);
                else
                    favourite.setColorFilter(null);

                favourite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        /* Below code is to test the update functionality */
                        System.out.println("Favourite button pressed!");
                        RouteRecord rr = routeList.get(pos);
                        rr.toggleFavorite();
                        RestUpdater ru = new RestUpdater(rr, getApplicationContext());
                        ru.start();


                        if (favourite.getColorFilter() != null) {
                            favourite.clearColorFilter();
                        } else {
                            favourite.setColorFilter(Color.YELLOW);
                        }
                    }
                });

                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                        System.out.println("Rating  bar is pressed!");
                        RouteRecord rr  = mFilteredRouteList.get(pos);
                        rr.setRating(rating);
                        RestUpdater ru = new RestUpdater(rr, getApplicationContext());
                        ru.start();
                    }
                });


                mButtonWalk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DisplayRoutesActivity.this, MapActivity.class);
                        intent.putExtra("ROUTE", mFilteredRouteList.get(pos).getEntryID());
                        startActivity(intent);
                    }
                });
            }


        }
    }



}
