package com.bitirme.gitbusters.borkinroads.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserRecord extends RestRecordImpl {

  private String URL = "https://shielded-cliffs-47552.herokuapp.com/users";
  public static UserRecord activeUser;
  private int userID;
  private String name;
  private ArrayList<DoggoRecord> pets;
  private ArrayList<RouteRecord> routes;

    public UserRecord() {
    }

    public UserRecord(String name, int userID, ArrayList<DoggoRecord> pets, ArrayList<RouteRecord> routes) {
    this.name   = name;
    this.userID = userID;
    this.pets = pets;
    this.routes = routes;
  }

  public UserRecord(UserRecord copy) {
    this.userID = copy.userID;
    this.name = copy.name;
    this.pets = new ArrayList<>();
    for (DoggoRecord dr : copy.pets)
      this.pets.add(new DoggoRecord(dr));
    this.routes = new ArrayList<>();
    for (RouteRecord rr: copy.routes)
      this.routes.add(new RouteRecord(rr));
  }

  public UserRecord(JSONObject jso) {
    if(URL.equals("Still Empty"))
      throw new AssertionError("UserRecord class does not" +
              "have a valid URL yet!");

    try {
      this.name      = jso.getString("name");
      this.userID    = jso.getInt("id");
      JSONArray routeJSONs = jso.getJSONArray("routes");
      this.routes = new ArrayList<>();
      for (int i = 0; i < routeJSONs.length(); i++)
        this.routes.add(new RouteRecord(routeJSONs.getJSONObject(i)));

      this.pets = new ArrayList<>();
      JSONArray petJSONs = jso.getJSONArray("pets");
      for (int i = 0; i < petJSONs.length(); i++)
        this.pets.add(new DoggoRecord(petJSONs.getJSONObject(i)));

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
      jso.put("name", name);
      JSONArray routeJSONs = new JSONArray();
      for (RouteRecord rr : routes)
        routeJSONs.put(rr.getJSON());
      jso.put("routes", routeJSONs);
      JSONArray petJSONs = new JSONArray();
      for (DoggoRecord dr : pets)
        petJSONs.put(dr.getJSON());
      jso.put("pets", pets);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return jso;
  }

  @Override
  public int getEntryID() {
    return userID;
  }

  public String getName() { return this.name; }

  public static void setActiveUser(int id) {
    activeUser = new UserRecord();
    activeUser.userID = id;
  }

    public ArrayList<DoggoRecord> getPets() {
        return pets;
    }

    public ArrayList<RouteRecord> getRoutes() {
        return routes;
    }
}
