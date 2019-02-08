package com.bitirme.gitbusters.borkinroads;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.List;

public class RouteDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private DisplayRouteRow routeRow;
    private List<RouteDetails> routeDetails;
    private RecyclerView mRecyclerView;
    private MapView mapView;
    private TextView mTitle;
    private TextView mRouteDate;
    private RatingBar mRatingBar;
    GoogleMap mMap;
    private View mLayout;
    private ImageView mFavourite;
    private CheckBox mNearWater;
    private CheckBox mNearForest;
    private TextView mNoUsed;
    private RecyclerView.LayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_details);
        routeDetails = testDetailsList();


        mTitle = findViewById(R.id.display_row_title);
        mRouteDate = findViewById(R.id.display_row_date);
        mRatingBar = findViewById(R.id.display_row_rating);
        mFavourite = findViewById(R.id.favourite);
        mNearWater = findViewById(R.id.nearWater);
        mNearForest = findViewById(R.id.nearForest);
        mNoUsed = findViewById(R.id.numberOfTimes);
        mapView = findViewById(R.id.route_detail_map);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(new RouteDetailsAdapter(routeDetails));

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.getMapAsync(this);
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getApplicationContext());
        mMap = googleMap;
    }

    public static List<RouteDetails> testDetailsList() {
        ArrayList<RouteDetails> list = new ArrayList<>();
        list.add(new RouteDetails(5,5,5,5,5,5,5,5,5,"14.01.1997","00:12:31"));
        list.add(new RouteDetails(6,6,6,6,6,6,6,6,6,"14.01.1997","00:12:31"));
        list.add(new RouteDetails(6,6,6,6,6,6,6,6,6,"14.01.1997","00:12:31"));
        list.add(new RouteDetails(6,6,6,6,6,6,6,6,6,"14.01.1997","00:12:31"));
        list.add(new RouteDetails(6,6,6,6,6,6,6,6,6,"14.01.1997","00:12:31"));

        return list;
    }
}
