package com.bitirme.gitbusters.borkinroads;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

public class RouteDetailsActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_details);

        mRecyclerView = findViewById(R.id.recycler_view);
        mTitle = findViewById(R.id.display_row_title);
        mRouteDate = findViewById(R.id.display_row_date);
        mRatingBar = findViewById(R.id.display_row_rating);
        mFavourite = findViewById(R.id.favourite);

        if (mapView != null) {

        }

    }
}
