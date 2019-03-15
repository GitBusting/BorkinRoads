package com.bitirme.gitbusters.borkinroads.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserRecord extends RestRecordImpl {

  private String URL = "https://shielded-cliffs-47552.herokuapp.com/users";
  public static UserRecord activeUser;
  private int userID;
  private String name;
  private ArrayList<Integer> petIDs;

  public UserRecord(){};

  public UserRecord(String name, int userID, ArrayList<Integer> petIDs) {
    this.name   = name;
    this.userID = userID;
    this.petIDs = petIDs;
  }

  public UserRecord(JSONObject jso) {
    if(URL.equals("Still Empty"))
      throw new AssertionError("UserRecord class does not" +
              "have a valid URL yet!");

    try {
      this.name      = jso.getString("name");
      this.userID    = jso.getInt("userID");
      this.petIDs    = new ArrayList<>();
      for(String pet : jso.getString("petID").split(","))
        this.petIDs.add(Integer.parseInt(pet));
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
    return null;
  }

  @Override
  public int getEntryID() {
    return 0;
  }

  public String getName() { return this.name; }

  public static void setActiveUser(int id) {
    activeUser.userID = id;
  }
}
