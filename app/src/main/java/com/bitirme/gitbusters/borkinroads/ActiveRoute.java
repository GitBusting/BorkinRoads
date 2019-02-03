package com.bitirme.gitbusters.borkinroads;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

class ActiveRoute {

  private final ArrayList<LatLng> waypoints;
  private final ArrayList<Integer> colors;
  private final LatLng startCoords;
  private final LatLng endCoords;

  /**
   *
   * @param col leg polyline colors for this route
   */
  public ActiveRoute(LatLng start, LatLng end, ArrayList<LatLng> wps, ArrayList<Integer> col){
    colors = new ArrayList<>(col);
    startCoords = start;
    endCoords = end;
    waypoints = new ArrayList<>(wps);
  }

  public LatLng getEndLocation() {
    return endCoords;
  }

  public LatLng getStartLocation(){
    return startCoords;
  }

  public ArrayList<LatLng> getWaypoints() {
    return waypoints;
  }

  public ArrayList<Integer> getColors() {
    return colors;
  }
}
