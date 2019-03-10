package com.bitirme.gitbusters.borkinroads;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.auth0.android.jwt.JWT;
import com.bitirme.gitbusters.borkinroads.data.RestRecordImpl;
import com.bitirme.gitbusters.borkinroads.data.RouteDetailsRecord;
import com.bitirme.gitbusters.borkinroads.data.RouteRecord;
import com.bitirme.gitbusters.borkinroads.dbinterface.RestPuller;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class RouteDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RouteRecord routeRow;
    private List<RouteDetailsRecord> routeDetails;
    private RecyclerView mRecyclerView;
    private MapView mapView;
    private TextView mTitle;
    private TextView mRouteDate;
    private RatingBar mRatingBar;
    GoogleMap map;
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

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        System.out.println("id = " + id);


        String token = getSharedPreferences("auth", Context.MODE_PRIVATE).getString("token", "invalid");
        JWT jwt = new JWT(token);
        if (jwt.isExpired(10)) {
            Intent intent2 = new Intent(this, LoginActivity.class);
            startActivity(intent2);
        }

        RestPuller rp = new RestPuller(new RouteRecord(), getApplicationContext());
        rp.start();
        try {
            rp.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (RestRecordImpl rec : rp.getFetchedRecords()) {
            RouteRecord rr = (RouteRecord) rec;
            if (rr.getEntryID() == id) {
                routeRow = rr;
                System.out.println("gelen id ye ait route bulundu");
                rr.prettyPrint();
            }
        }

        routeDetails = new ArrayList<>();
        rp = new RestPuller(new RouteDetailsRecord(), getApplicationContext());
        rp.start();
        try {
            rp.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(RestRecordImpl rec : rp.getFetchedRecords()) {
            RouteDetailsRecord rdr = (RouteDetailsRecord) rec;
            if(rdr.getRoute_id() == id)
            routeDetails.add(rdr);
        }

//        routeDetails = testDetailsList();


        mTitle = findViewById(R.id.display_row_title);
        mTitle.setText(routeRow.getTitle());
        mRouteDate = findViewById(R.id.display_row_date);
//        if (routeRow.getDate() != null)
//            mRouteDate.setText(routeRow.getDate());
        mRatingBar = findViewById(R.id.display_row_rating);
        mRatingBar.setRating(routeRow.getRating().floatValue());
        mFavourite = findViewById(R.id.favourite);
        mFavourite.setSelected(routeRow.isFavorite());
        mNearWater = findViewById(R.id.nearWater);
        mNearWater.setChecked(routeRow.isNearLake());
        mNearForest = findViewById(R.id.nearForest);
        mNearForest.setChecked(routeRow.isNearPark());
        mNoUsed = findViewById(R.id.numberOfTimes);
//        mNoUsed.setText(routeRow.getNoUsed());
        mapView = findViewById(R.id.route_detail_map);


        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(new RouteDetailsAdapter(routeDetails));

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
        if (map == null) return;

        map.getUiSettings().setAllGesturesEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);



        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.add(routeRow.getStartCoords());
        for(LatLng point : routeRow.getWaypoints())
            polylineOptions.add(point);
        polylineOptions.add(routeRow.getEndCoords());

        polylineOptions.width(12);
        polylineOptions.clickable(false);
        polylineOptions.color(Color.BLUE);
        map.addPolyline(polylineOptions);
        LatLng firstPoint = routeRow.getStartCoords();
        LatLng lastPoint = routeRow.getEndCoords();
        LatLng middlePoint = new LatLng((firstPoint.latitude + lastPoint.latitude)/2 ,(firstPoint.longitude + lastPoint.longitude)/2);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(middlePoint, 11f));
        map.addMarker((new MarkerOptions().position(firstPoint)));
        map.addMarker(new MarkerOptions().position(lastPoint));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);


    }

//    public static List<RouteDetailsRecord> testDetailsList() {
//        ArrayList<RouteDetailsRecord> list = new ArrayList<>();
//        list.add(new RouteDetailsRecord(1,5,5,5,5,5,5,5,5,5,"14.01.1997","00:12:31"));
//        list.add(new RouteDetailsRecord(2,6,6,6,6,6,6,6,6,6,"14.01.1997","00:12:31"));
//        list.add(new RouteDetailsRecord(3,6,6,6,6,6,6,6,6,6,"14.01.1997","00:12:31"));
//        list.add(new RouteDetailsRecord(4,6,6,6,6,6,6,6,6,6,"14.01.1997","00:12:31"));
//        list.add(new RouteDetailsRecord(5,6,6,6,6,6,6,6,6,6,"14.01.1997","00:12:31"));
//
//        return list;
//    }
}
