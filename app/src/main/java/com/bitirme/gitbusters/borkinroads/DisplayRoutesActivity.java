package com.bitirme.gitbusters.borkinroads;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class DisplayRoutesActivity extends Activity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_routes);
        mGridLayoutManager = new GridLayoutManager(this, 2);
        mLinearLayoutManager = new LinearLayoutManager(this);

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(new DisplayRouteAdapter(LIST_LOCATIONS));
        mRecyclerView.setRecyclerListener(mRecycleListener);
    }



    private class DisplayRouteAdapter extends RecyclerView.Adapter<DisplayRouteAdapter.ViewHolder> {
        DisplayRouteRow[] mRouteList;
        LayoutInflater inflater;

        private DisplayRouteAdapter(DisplayRouteRow[] routes) {
            super();
            this.mRouteList = routes;
        }



        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder == null) return;

            holder.bindView(position);
        }

        @Override
        public int getItemCount() {
            if (mRouteList == null) return 0;
            return mRouteList.length;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DisplayRouteAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.route_display_row, parent, false));
        }


        class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

            MapView mapView;
            TextView title;
            TextView routeDate;
            RatingBar ratingBar;
            GoogleMap map;
            View layout;

            public ViewHolder(View itemView) {
                super(itemView);
                layout = itemView;
                mapView = layout.findViewById(R.id.display_row_map);
                title = layout.findViewById(R.id.display_row_title);
                routeDate = layout.findViewById(R.id.display_row_date);
                ratingBar = layout.findViewById(R.id.display_row_rating);

                if (mapView != null) {
                    mapView.onCreate(null);
                    mapView.getMapAsync(this);
                }
            }

            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapsInitializer.initialize(getApplicationContext());
                map = googleMap;
                setMapLocation();

            }

            private void setMapLocation() {
                if (map == null) return;

                DisplayRouteRow data = (DisplayRouteRow) mapView.getTag();
                if (data == null) return;

                //map.moveCamera(CameraUpdateFactory.newLatLngZoom(data.getLocation()));
                map.addMarker(new MarkerOptions().position(data.getLocation()));

                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }

            private void bindView(int pos) {
                DisplayRouteRow item = mRouteList[pos];
                layout.setTag(this);
                mapView.setTag(item);
                setMapLocation();
                title.setText(item.getTitle());
                ratingBar.setRating(item.getRating());
                routeDate.setText(item.getRouteDate());
            }


        }
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
     * A list of locations to show in this ListView.
     */
    private static final DisplayRouteRow[] LIST_LOCATIONS = new DisplayRouteRow[]{
            new DisplayRouteRow("Cape Town", new LatLng(-33, 18), 3.5f, "11/11/11"),
            new DisplayRouteRow("Beijing", new LatLng(39, 116), 3.5f, "11/11/11")
    };

}
