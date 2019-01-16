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
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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

    /*
    * Adapter View Holder pattern for displaying lite mode Gmap
     */

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
                displayRouteOnMap();

            }

//            private void setMapLocation() {
//                if (map == null) return;
//
//                DisplayRouteRow data = (DisplayRouteRow) mapView.getTag();
//                if (data == null) return;
//
//                //map.moveCamera(CameraUpdateFactory.newLatLngZoom(data.getPoints()));
//                map.addMarker(new MarkerOptions().position(data.getPoints()));
//
//                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//
//
//
//            }

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
                DisplayRouteRow item = mRouteList[pos];
                layout.setTag(this);
                mapView.setTag(item);
                displayRouteOnMap();
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
            new DisplayRouteRow("Home to School", new LatLng[]{new LatLng(39.941734, 32.63447), new LatLng(39.920665, 32.801853)}, 3.5f, "11/11/11"),
            new DisplayRouteRow("Beijing", new LatLng[]{new LatLng(50.854509, 4.376678), new LatLng(55.679423, 12.577114), new LatLng(52.372026, 9.735672)}, 3.5f, "11/11/11")
    };

}
