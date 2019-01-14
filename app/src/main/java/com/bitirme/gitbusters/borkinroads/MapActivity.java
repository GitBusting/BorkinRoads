package com.bitirme.gitbusters.borkinroads;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MapActivity extends FragmentActivity
        implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

  private GoogleMap mMap;
  private ArrayList<Marker> markers;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);

    // "Reset Button" will remove all markers from
    // the screen and clear the coordinates list
    markers = new ArrayList<>();
    Button rb = findViewById(R.id.resetButton);
    rb.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        for (int i = 0; i < markers.size(); i++)
          markers.get(i).remove();
        markers.clear();
      }
    });

    Button genPathButton = findViewById(R.id.generatePathButton);
    genPathButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        getPath(markers,markers.get(0));
      }
    });

    SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
  }

  public void getPath(ArrayList<Marker> markers,Marker start){
    Toast.makeText(this, "getPath clicked.", Toast.LENGTH_SHORT).show();
    HttpsURLConnection conn = null;
    try {
        String parameters;
        parameters = "origin=" + start.getPosition().latitude + "," + start.getPosition().longitude;
        parameters += "\n&destination=" + start.getPosition().latitude + "," + start.getPosition().longitude; //start and end points are the same for the route
        parameters += "\n&key=AIzaSyA3nOUd0mIm1mCoUIx1DRa-qsCT3Kz1a_k"; // this should change i guess
        parameters += "\n&mode=walking";
        if(markers.size()>0){
            parameters += "\n&waypoints=";
        }
        for(int i =0;i<markers.size();i++) {
            if(i>=10) break;
            if(i!=0) parameters += "|";
            parameters += "via:" + markers.get(i).getPosition().latitude + "," + markers.get(i).getPosition().longitude;
        }
        URL webServerUrl = new URL("https://maps.googleapis.com/maps/api/directions/json?"+parameters);
        conn =(HttpsURLConnection) webServerUrl.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);

        conn.setRequestMethod("GET");
        conn.connect();
    }
    catch (IOException e) {
        e.printStackTrace();
    }

  }

  @Override
  public void onMapReady(GoogleMap map) {
    mMap = map;
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED)
    {
      Toast.makeText(this,"Please give location permissions to this App",
              Toast.LENGTH_LONG).show();
      return;
    }
    // Show users their location
    mMap.setMyLocationEnabled(true);
    mMap.setOnMyLocationButtonClickListener(this);
    mMap.setOnMyLocationClickListener(this);

    // Add markers to the map as user clicks on it
    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
      @Override
      public void onMapClick(LatLng latLng) {
        markers.add(mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Marker added by user")));
      }
    });
  }

  @Override
  public void onMyLocationClick(@NonNull Location location) {
     Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
  }

  @Override
  public boolean onMyLocationButtonClick() {
    // Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
    // Return false so that we don't consume the event and the default behavior still occurs
    // (the camera animates to the user's current position).
    return false;
  }
}
