package com.bitirme.gitbusters.borkinroads.data;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RouteRecord extends RestRecordImpl{

  private String title;
  private ArrayList<LatLng> waypoints;
  private ArrayList<Integer> colors;
  private LatLng startCoords;
  private LatLng endCoords;
  private String date, time;
  private double rating;
  private int estimatedMinutes;
  private boolean isFavorite;
  private boolean nearLake;
  private boolean nearPark;
  private int noUsed;
  private int entryID;
  private int userID;
  /**
   * Practically useless, can be used to generate
   * template routerecords.
   */
  public RouteRecord()
  {
    super();
  }

  /**
   *
   * @param col leg polyline colors for this route
   */
  public RouteRecord(LatLng start, LatLng end, ArrayList<LatLng> wps, ArrayList<Integer> col, int est){
    colors = new ArrayList<>(col);
    startCoords = start;
    endCoords = end;
    waypoints = new ArrayList<>(wps);
    estimatedMinutes = est;
    title = "N/A";
    rating = 0;
    isFavorite = false;
    nearLake = false;
    nearPark = false;
    noUsed = 1;
    Date currentTime = Calendar.getInstance().getTime();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    String strDate = dateFormat.format(currentTime);
    date = strDate.split(" ")[0] + "T";
    time = strDate.split(" ")[1] + ".000Z";
    entryID = -1;
    userID = UserRecord.activeUser.getEntryID(); //may want to change this
  }

  public RouteRecord(RouteRecord copy)
  {
    if (copy.colors == null)
      this.colors = null;
    else
      this.colors = new ArrayList<>(copy.colors);
    this.waypoints = new ArrayList<>(copy.waypoints);
    this.startCoords = copy.startCoords;
    this.endCoords = copy.endCoords;
    this.estimatedMinutes = copy.estimatedMinutes;
    this.title = copy.title;
    this.rating = copy.rating;
    this.isFavorite = copy.isFavorite;
    this.nearLake = copy.nearLake;
    this.nearPark = copy.nearPark;
    this.noUsed = copy.noUsed;
    this.date = copy.date;
    this.time = copy.time;
    this.entryID = copy.entryID;
    this.userID = copy.userID;
  }

  public RouteRecord(JSONObject jso)
  {
    super(jso);
    parseRecordFromJSON(jso);
  }

  public void toggleFavorite()
  {
    isFavorite = !isFavorite;
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

  public void setTitle(String title) { this.title = title; }

  private void parseRecordFromJSON(JSONObject jso)
  {
    try {
      this.entryID = jso.getInt("id");
      this.title = jso.getString("title");
      this.waypoints = this.stringToWaypoints(jso.getString("path"));
      this.startCoords = waypoints.remove(0);
      this.endCoords = waypoints.remove(waypoints.size()-1);
      this.rating = jso.getDouble("rating");
      String dateTime = jso.getString("date");
      String[] tokens = dateTime.split("T");
      this.date = tokens[0] + "T";
      this.time = tokens[1].substring(0, tokens[1].indexOf('.'));
      this.estimatedMinutes = jso.getInt("estimatedDuration");
      this.nearLake = jso.getBoolean("nearWater");
      this.nearPark = jso.getBoolean("nearPark");
      this.isFavorite = jso.getBoolean("favourite");
      this.noUsed = jso.getInt("numberOfTimesUsed");
      this.userID = jso.getInt("user_id");
    } catch(JSONException jse){
      jse.printStackTrace();
    }
  }

  public void prettyPrint()
  {
    Logger.getGlobal().log(Level.INFO,"Route Record\n" +
            "Title: " + title + "\n" +
            "Rating: " + rating + "\n" +
            "Date&Time: " + date + time + "\n" +
            "Estimated Time to Finish: " + estimatedMinutes + "\n" +
            "Start&End: " + startCoords.toString() + " & "+ endCoords.toString() +
            "First Waypoint: " + waypoints.get(0)
    );
  }

  @Override
  public String getURL() {
    return "https://shielded-cliffs-47552.herokuapp.com/routes";
  }

  @Override
  public JSONObject getJSON() {
    JSONObject jso = new JSONObject();
    try {
      jso.put("title", this.title);
      jso.put("path",this.waypointsToString());
      jso.put("rating", this.rating);
      jso.put("date", this.date + this.time);
      jso.put("estimatedDuration", this.estimatedMinutes);
      jso.put("nearWater", this.nearLake);
      jso.put("nearPark", this.nearPark);
      jso.put("favourite", this.isFavorite);
      jso.put("numberOfTimesUsed", this.noUsed);
      jso.put("user_id", userID);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return jso;
  }

  @Override
  public int getEntryID() {
    return entryID;
  }

  private String waypointsToString()
  {
    String ret = "";
    ret += startCoords.latitude + "_" + startCoords.longitude + ";";
    for(LatLng wp : waypoints)
      ret += wp.latitude + "_" + wp.longitude + ";";
    ret += endCoords.latitude + "_" + endCoords.longitude;
    System.out.println("Route string: " + ret);
    return ret;
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


  public String getTitle() {
    return title;
  }

  public LatLng getStartCoords() {
    return startCoords;
  }

  public LatLng getEndCoords() {
    return endCoords;
  }

  public String getDate() {
    return date;
  }

  public String getTime() {
    return time;
  }

  public Double getRating() {
    return rating;
  }

  public int getEstimatedMinutes() {
    return estimatedMinutes;
  }

  public boolean isFavorite() {
    return isFavorite;
  }

  public boolean isNearLake() {
    return nearLake;
  }

  public boolean isNearPark() {
    return nearPark;
  }

  public int getNoUsed() {
    return noUsed;
  }

  public void setRating(double rating) {
      this.rating = rating;
  }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
