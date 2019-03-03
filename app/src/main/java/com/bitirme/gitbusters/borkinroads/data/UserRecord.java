package com.bitirme.gitbusters.borkinroads.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserRecord extends RestRecordImpl {

  private String URL = "Still Empty";

  private int userID;
  private ArrayList<Integer> petIDs;

  public UserRecord(int userID, ArrayList<Integer> petIDs) {
    this.userID = userID;
    this.petIDs = petIDs;
  }

  public UserRecord(JSONObject jso) {
    if(URL.equals("Still Empty"))
      throw new AssertionError("PublicRouteRecord class does not" +
              "have a valid URL yet!");

    try {
      this.userID    = jso.getInt("userID");

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
}
