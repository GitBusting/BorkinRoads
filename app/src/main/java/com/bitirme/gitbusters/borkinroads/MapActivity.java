package com.bitirme.gitbusters.borkinroads;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity
        implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        DirectionCallback,
        LocationListener {

  private GoogleMap mMap;
  private ArrayList<Marker> markers;
  private ArrayList<LatLng> coordinates;
  private ArrayList<Polyline> routes;

  protected LocationManager locationManager;
  private LatLng cur_location;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);
    cur_location=null;
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    // "Reset Button" will remove all markers from
    // the screen and clear the coordinates list
    markers = new ArrayList<>();
    coordinates = new ArrayList<>();
    routes = new ArrayList<>();
    Button rb = findViewById(R.id.resetButton);
    rb.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        for (Marker m : markers)
          m.remove();
        markers.clear();
        coordinates.clear();
        for (Polyline p : routes)
          p.remove();
        routes.clear();
      }
    });

    Button genPathButton = findViewById(R.id.generatePathButton);
    genPathButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        requestDirection();
      }
    });

    SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
  }

  @Override
  public void onMapReady(GoogleMap map) {
    mMap = map;
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
      Toast.makeText(this, "Please give location permissions to this App",
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
        coordinates.add(latLng);
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

  public final void requestDirection() {
    ArrayList<LatLng> coords = new ArrayList<>(coordinates);
    LatLng start = cur_location;
    LatLng end = cur_location;
    String apikey = "";
    assert(!apikey.equals("")); //insert apikey
    GoogleDirection.withServerKey(apikey)
            .from(start)
            .and(coords)
            .to(end)
            .transportMode(TransportMode.WALKING)
            .execute(this);
  }

  // This will be called when a DirectionsAPI request returns data
  @Override
  public void onDirectionSuccess(Direction direction, String rawBody) {

    if (direction.isOK()) {
      Route route = direction.getRouteList().get(0);
      int legCount = route.getLegList().size();
      for (int index = 0; index < legCount; index++) {
        Leg leg = route.getLegList().get(index);
        List<Step> stepList = leg.getStepList();
        // Form & display polylines according to our route on the map
        ArrayList<PolylineOptions> polylineOptionList = new ArrayList<>();
        for (Step s : stepList) {
          polylineOptionList.add(DirectionConverter.createPolyline(this,
                  (ArrayList<LatLng>) s.getPolyline().getPointList(), 3, Color.BLACK));
        }
        for (PolylineOptions polylineOption : polylineOptionList) {
          routes.add(mMap.addPolyline(polylineOption));
        }
      }
    } else {
      System.out.println("Could not find a valid route");
    }
  }

  @Override
  public void onDirectionFailure(Throwable t) {
    t.printStackTrace();
  }

  @Override
  public void onLocationChanged(Location location) {
    cur_location = new LatLng(location.getLatitude(), location.getLongitude());
  }

  @Override
  public void onStatusChanged(String s, int i, Bundle bundle) {

  }

  @Override
  public void onProviderEnabled(String s) {

  }

  @Override
  public void onProviderDisabled(String s) {

  }
}
