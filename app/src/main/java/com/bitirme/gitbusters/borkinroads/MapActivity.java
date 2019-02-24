package com.bitirme.gitbusters.borkinroads;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.bitirme.gitbusters.borkinroads.dbinterface.RestPusher;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bitirme.gitbusters.borkinroads.data.RouteRecord;

public class MapActivity extends FragmentActivity
        implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        DirectionCallback,
        LocationListener {

  private final String apikey = "AIzaSyA3nOUd0mIm1mCoUIx1DRa-qsCT3Kz1a_k";

  private GoogleMap mMap;

  private boolean displayDirection;

  private ArrayList<Marker> markers;
  private ArrayList<LatLng> coordinates;
  private ArrayList<Polyline> routes;
  private ArrayList<Integer> legColors;

  private boolean routeActive;
  private String currTitle;
  private RouteRecord currRoute, copyRoute;
  private int estimatedMinutes;
  private CountDownTimer cdt; // try to update route on finish

  private LatLng cur_location;

  private double speed = 80.0; // meters / minute
  private int curEstTime;
  private TextView estimated;

  private boolean limitedTime;
  private int timeLimit = 0;
  private HashMap<LatLng,Double> backupMarkers;

  private CheckBox parksOnly;
  private boolean weightParks;

  private Button resetButton, genPathButton, startRouteButton, limited;

  //statistics of the active route
  private long timePassed; // in seconds
  private float metersPassed; // in meters
  private double averageSpeed; // in meters/seconds

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);
    cur_location=null;
    limitedTime = false;
    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return;
    }

    // Register both GPS provider's and Network provider's updates to our listener
    // TODO need to make sure we get an update every time the app starts
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

    //to show estimated time required for the selected route
    estimated = findViewById(R.id.estimated_time);
    estimated.setMovementMethod(new ScrollingMovementMethod());
    estimated.setVisibility(View.INVISIBLE);

    // A route is currently being displayed on the map
    // if this is true
    displayDirection = false;

    // For limited time option you can choose routes going to/through parks
    // if this is checked

    weightParks = false;
    parksOnly = findViewById(R.id.parksonly);
    parksOnly.setVisibility(View.VISIBLE);
    parksOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            weightParks = isChecked;
        }
    });


    // "Reset Button" will remove all markers from
    // the screen and clear the coordinates list
    routeActive = false;
    markers = new ArrayList<>();
    coordinates = new ArrayList<>();
    routes = new ArrayList<>();
    legColors = new ArrayList<>();
    resetButton = findViewById(R.id.resetButton);
    resetButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        clearMap();
        estimated.setText("");
        estimated.setVisibility(View.INVISIBLE);
        displayDirection = false;
        limitedTime = false;
        backupMarkers = null;
        timeLimit = 0;
      }
    });

    genPathButton = findViewById(R.id.generatePathButton);
    genPathButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(displayDirection)
          Toast.makeText(view.getContext(),"Path already present!", Toast.LENGTH_SHORT).show();
        else
          requestDirection();
      }
    });

    startRouteButton = findViewById(R.id.startRouteButton);
    startRouteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startEndRoute();
      }
    });

    SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    assert mapFragment != null;
    mapFragment.getMapAsync(this);

    // TODO this doesn't belong here
    // RestPuller rp = new RestPuller();
    // rp.start();
    // try {
    // rp.join();
    // } catch (InterruptedException e) {
    // e.printStackTrace();
    // }
    // for(RouteRecord rr : rp.getFetchedRoutes())
    // rr.prettyPrint();

    limited = findViewById(R.id.limitedtime);
    limited.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AlertDialog.Builder  alertDialogBuilder = new AlertDialog.Builder(MapActivity.this);
        alertDialogBuilder.setMessage("How much time do you have: (in minutes)");
        final EditText timeInput= new EditText(MapActivity.this);
        timeInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        alertDialogBuilder.setView(timeInput);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface arg0, int arg1) {
            String t_input = timeInput.getText().toString();
            int mins = Integer.parseInt(t_input)/2; // round-trip
            int calculated_distance = (int)Math.ceil(mins * speed);
            final DirectionsHandler requester = new DirectionsHandler(apikey,calculated_distance,"",cur_location);
            if (weightParks)
              requester.setKeyword("park");
            requester.start();
              try {
                  requester.join();
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
            Toast.makeText(MapActivity.this,"Generating a beautiful route for you and your dog.",Toast.LENGTH_LONG).show();
            coordinates.clear();
            backupMarkers = new HashMap<>(requester.getMarkerMap());
            limitedTime = true;
            timeLimit = mins * 2; //not round trip
            coordinates.add(requester.getResult());
            requestDirection();
          }
        });

        alertDialogBuilder.setNegativeButton("No, I have time.",new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface arg0, int arg1) {
              Toast.makeText(MapActivity.this,"You clicked \"No, I have time.\"",Toast.LENGTH_LONG).show();
          }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        /*
          requester.setCurrentLocation(cur_location);
          requester.setRadius(500); //this could change
          requester.start();
          */
      }
    });
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

  private void startEndRoute() {
    if(!routeActive) {
      currRoute = new RouteRecord(cur_location, cur_location,
              coordinates, legColors, estimatedMinutes);
      currRoute.setTitle(currTitle);
      copyRoute = new RouteRecord(currRoute); // Checkpoint the current state of the route
      //reset statistics for the active route
      timePassed = 0;
      metersPassed = 0;
      averageSpeed = 0.0;
      cdt = new CountDownTimer(20000, 10000) {
        public void onTick(long millisUntilFinished) {
          System.out.println("Timer heartbeat per 10 seconds.");
        }
        public void onFinish() {
          updateRoute(20000);
        }
      }.start();
    }else{
      estimated.setVisibility(View.INVISIBLE);
      cdt.cancel();
      cdt = null;
      clearMap();
    }
    if(routeActive) {
      resetButton.setVisibility(View.VISIBLE);
      genPathButton.setVisibility(View.VISIBLE);
      startRouteButton.setText(R.string.start_route);
      // TODO ask users to review their newly traversed path here

      // TODO: save the statistics
      // For now we just push the newly created route
      RestPusher rp = new RestPusher(copyRoute);
      rp.start();
    }
    else {
      genPathButton.setVisibility(View.INVISIBLE);
      resetButton.setVisibility(View.INVISIBLE);
      startRouteButton.setText(R.string.stop);
    }
    routeActive = !routeActive;
  }

  @SuppressWarnings("ConstantConditions")
  private void requestDirection() {
    ArrayList<LatLng> coords = new ArrayList<>(coordinates);
    LatLng start = cur_location;
    LatLng end   = cur_location;
    if(cur_location == null)
      throw new AssertionError("Location was null when a direction request was made");
    if (apikey.isEmpty())
      throw new AssertionError("API key not found");
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
    System.out.println(rawBody);
    if(routeActive)
      handleRouteUpdate(direction);
    else
      handleInitialRouting(direction);
  }

  @SuppressWarnings("ConstantConditions")
  private void updateRoute(long timeSpent)
  {
    // Few tasks to implement here,
    // 1 - Update the statistics
    // 2 - Did the user pass through any waypoints
    // if so we need to remove them
    // 3 - Do we have an updated location of the user
    // if we do not, we might need to set a smaller countdown
    // to pick up the next update earlier
    LatLng oldStartLoc = currRoute.getStartLocation();
    boolean userMoved = !oldStartLoc.equals(cur_location);
    final long countdownMillis = userMoved ? 20000 : 5000;
    if(userMoved)
    {
      // Updating the distance statistic
      float[] passed = new float[3];
      Location.distanceBetween(oldStartLoc.latitude, oldStartLoc.longitude,
                cur_location.latitude, cur_location.longitude, passed);
      metersPassed += passed[0];

      // Other tasks
      ArrayList<LatLng> coords = currRoute.getWaypoints();
      LatLng firstWaypoint;
      if(coords.size() > 0)
        firstWaypoint = coords.get(0);
      else
        firstWaypoint = currRoute.getEndLocation();

      float[] distanceVec = new float[3];
      Location.distanceBetween(firstWaypoint.latitude, firstWaypoint.longitude,
              cur_location.latitude, cur_location.longitude, distanceVec);
      if(distanceVec[0] < 20.0) // if the user is within 20 metres of the waypoint
      {
        // All waypoints traversed only end point left
        // so we can conclude that we have arrived
        // at the final destination
        if(coords.size() < 0) {
          startEndRoute();
          return;
        }
        markers.get(0).remove();
        markers.remove(0);
        routes.get(0).remove();
        routes.remove(0);
        coords.remove(0);
        currRoute.getColors().remove(0);
      }
      LatLng start = cur_location;
      GoogleDirection.withServerKey(apikey)
              .from(start)
              .and(coords)
              .to(currRoute.getEndLocation())
              .transportMode(TransportMode.WALKING)
              .execute(this);
    }
    //update the statistics
    timePassed += timeSpent / 1000; // adding to total seconds on this active route
    averageSpeed = metersPassed / timePassed;
    
      // Restart counter with each update call
    cdt = new CountDownTimer(countdownMillis, countdownMillis/2) {
      public void onTick(long millisUntilFinished) {
        System.out.println("Timer heartbeat");
      }
      public void onFinish() {
        updateRoute(countdownMillis);
      }
    }.start();
  }

  private void clearMap()
  {
    for (Marker m : markers)
      m.remove();
    markers.clear();
    coordinates.clear();
    for (Polyline p : routes)
      p.remove();
    routes.clear();
    legColors.clear();
  }

  private void handleRouteUpdate(Direction direction)
  {
    for(Polyline p : routes)
      p.remove();
    routes.clear();
    legColors.clear();

    if (direction.isOK()) {
      Route route = direction.getRouteList().get(0);
      int legCount = route.getLegList().size();
      curEstTime=0;
      ArrayList<Integer> lineColors = currRoute.getColors();
      for (int index = 0; index < legCount; index++) {
        Leg leg = route.getLegList().get(index);
        List<Step> stepList = leg.getStepList();
        curEstTime+=Integer.parseInt(leg.getDuration().getValue());
        // Form & display polylines according to our route on the map
        ArrayList<PolylineOptions> polylineOptionList = new ArrayList<>();
        int lineColor = lineColors.get(index);
        legColors.add(lineColor);
        for (Step s : stepList) {
          polylineOptionList.add(DirectionConverter.createPolyline(this,
                  (ArrayList<LatLng>) s.getPolyline().getPointList(), 3, lineColor));
        }
        for (PolylineOptions polylineOption : polylineOptionList) {
          routes.add(mMap.addPolyline(polylineOption));
        }
      }
      estimated.setText(getRouteOutline(route));
      estimated.setVisibility(View.VISIBLE);
      estimated.setAlpha((float) 0.75);
    } else { System.out.println("Could not find a valid route"); }
  }
  private LatLng getMax(HashMap<LatLng, Double> markerMap) {
      LatLng maxLatLng = null;
      double maxDist = Double.MIN_VALUE;
      for(Map.Entry<LatLng,Double> e : markerMap.entrySet()) {
          if(e.getValue() > maxDist ) {
              maxDist = e.getValue();
              maxLatLng = e.getKey();
          }
      }
      return maxLatLng;
  }
  private void handleInitialRouting(Direction direction)
  {
    if (direction.isOK()) {
      displayDirection = true;
      Route route = direction.getRouteList().get(0);
      int legCount = route.getLegList().size();
      curEstTime=0;
      if(limitedTime) {
          for (int index = 0; index < legCount ; index++){
              Leg leg = route.getLegList().get(index);
              curEstTime+=Integer.parseInt(leg.getDuration().getValue());
          }
          if(curEstTime > ((timeLimit + 5 /*its okay to have 5 extra minutes, also may change*/ ) * 60)) { //we are comparing seconds
              System.out.println("requesting direction again.\nlimit: " + timeLimit + " route estimate: " + curEstTime/60);
              backupMarkers.remove(coordinates.get(0));
              System.out.println("deleted: " +coordinates.get(0));
              LatLng max = getMax(backupMarkers);
              coordinates.clear();
              coordinates.add(max);
              System.out.println("added: " +coordinates.get(0));
              requestDirection();
              return;
          }
          curEstTime = 0;
      }
      for (int index = 0; index < legCount; index++) {
        Leg leg = route.getLegList().get(index);
        System.out.println("index: " + index + " duration: " + leg.getDuration().getText() + " value: " + leg.getDuration().getValue());
        curEstTime+=Integer.parseInt(leg.getDuration().getValue());
        List<Step> stepList = leg.getStepList();
        // Form & display polylines according to our route on the map
        ArrayList<PolylineOptions> polylineOptionList = new ArrayList<>();
        int r = (int) (Math.random()*256);
        int g = (int) (Math.random()*256);
        int b = (int) (Math.random()*256);
        int lineColor = Color.argb(127,r,g,b);
        legColors.add(lineColor);
        for (Step s : stepList) {
          polylineOptionList.add(DirectionConverter.createPolyline(this,
                  (ArrayList<LatLng>) s.getPolyline().getPointList(), 3, lineColor));
        }
        for (PolylineOptions polylineOption : polylineOptionList) {
          routes.add(mMap.addPolyline(polylineOption));
        }
      }
      currTitle = route.getLegList().get(0).getStartAddress();
      estimated.setText(getRouteOutline(route));
      estimated.setVisibility(View.VISIBLE);
      updateEstimatedMinutesUntilEnd(route);
        estimated.setAlpha((float) 0.75);
    } else { System.out.println("Could not find a valid route"); }
  }

  private void updateEstimatedMinutesUntilEnd(Route route)
  {
    int estTime = 0;
    int legCount = route.getLegList().size();
    for (int index = 0; index < legCount; index++) {
      Leg leg = route.getLegList().get(index);
      estTime += Integer.parseInt(leg.getDuration().getValue());
    }
    estimatedMinutes = estTime / 60;
  }

  private String getRouteOutline(Route route) {
    String outline = "";
    int estTime = 0;
    int legCount = route.getLegList().size();
    outline += "Your average speed: " + averageSpeed + "\n";
    outline += "time passed: " + timePassed + "\n";
    outline += "Next Stops:\n\n";
    for (int index = 0; index < legCount; index++) {
      Leg leg = route.getLegList().get(index);
      estTime += Integer.parseInt(leg.getDuration().getValue());
      outline = outline.concat(leg.getEndAddress() + "\n");
      outline += "distance: " + leg.getDistance().getText() + "\n";
      outline += "duration: " + leg.getDuration().getText() + "\n\n";
    }
    int sec = estTime % 60;
    int hour = estTime / 3600;
    int min = (estTime%3600) / 60;
    String overall_time="";
    if(hour>0)
      overall_time += hour + " h ";
    overall_time += min + " min " + sec + " s";

    outline = "Estimated time: " + overall_time + "\n" + outline;
    return outline;
  }
  @Override
  public void onDirectionFailure(Throwable t) {t.printStackTrace();}

  // Location related callbacks, we only need to know
  // the updated location info
  @Override
  public void onLocationChanged(Location location) {
    cur_location = new LatLng(location.getLatitude(), location.getLongitude());
  }
  @Override
  public void onStatusChanged(String s, int i, Bundle bundle) {}
  @Override
  public void onProviderEnabled(String s) {}
  @Override
  public void onProviderDisabled(String s) {}
}
