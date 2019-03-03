package com.bitirme.gitbusters.borkinroads.data;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PublicRouteRecord extends RestRecordImpl implements Comparable<PublicRouteRecord>{

  private static String URL = "Still Empty";

  private int userID;
  private int petID;
  private ArrayList<LatLng> waypoints;
  private LatLng location;
  private int entryID;

  public int getPetID() {
    return petID;
  }

  public ArrayList<LatLng> getWaypoints() {
    return waypoints;
  }

  public LatLng getLocation() {
    return location;
  }

  public PublicRouteRecord() {
    super();
  }

  public PublicRouteRecord(JSONObject jso) {
    if(URL.equals("Still Empty"))
      throw new AssertionError("PublicRouteRecord class does not" +
            "have a valid URL yet!");

    try {
      this.userID    = jso.getInt("userID");
      this.petID     = jso.getInt("petID");
      this.waypoints = stringToWaypoints(jso.getString("route"));
      this.location  = stringToWaypoints(jso.getString("location")).get(0);
      this.entryID   = jso.getInt("id");
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getURL() {
    return URL;
  }

  @Override
  public JSONObject getJSON() {
    JSONObject jso = new JSONObject();
    try {
      jso.put("userID", this.userID);
      jso.put("petID", this.petID);
      jso.put("route", waypointsToString());
      jso.put("location", locationToString());
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return jso;
  }

  @Override
  public int getEntryID() {
    return this.entryID;
  }

  private String waypointsToString()
  {
    String ret = "";
    for(LatLng wp : waypoints)
      ret += wp.latitude + "_" + wp.longitude + ";";
    System.out.println("Route string: " + ret);
    return ret;
  }

  private String locationToString()
  {
    return location.latitude + "_" + location.longitude;
  }

  private ArrayList<LatLng> stringToWaypoints(String s)
  {
    String[] coords = s.split(";");
    ArrayList<LatLng> ret = new ArrayList<>();
    for(int i = 0 ; i < coords.length ; i++)
    {
      String[] ll = coords[i].split("_");
      double latitude = Double.parseDouble(ll[0]);
      double longtitude = Double.parseDouble(ll[1]);
      ret.add(new LatLng(latitude,longtitude));
    }
    return ret;
  }

  /*
   * This piece of code is copied from
   * https://www.geeksforgeeks.org/overriding-equals-method-in-java/
   */
  /**
   * @param o Object to compare against
   * @return true if entries' locations differ 20 meters from each other.
   */
  @Override
  public boolean equals(Object o) {

    // If the object is compared with itself then return true
    if (o == this) {
      return true;
    }

    /* Check if o is an instance of Complex or not
    "null instanceof [type]" also returns false */
    if (!(o instanceof PublicRouteRecord)) {
      return false;
    }

    // typecast o to Complex so that we can compare data members
    PublicRouteRecord prc = (PublicRouteRecord) o;

    if(prc.entryID == this.entryID)
    {
      float[] distanceVec = new float[3];
      Location.distanceBetween(prc.location.latitude, prc.location.longitude,
              this.location.latitude, this.location.longitude, distanceVec);

      if(distanceVec[0] < 20.0)
        return false;
      else
        return true;
    }

    return false;
  }

  @Override
  public int compareTo(PublicRouteRecord other)
  {
    return other.entryID < this.entryID ? 1 :
            other.entryID == this.entryID ? 0 : -1;
  }
}
