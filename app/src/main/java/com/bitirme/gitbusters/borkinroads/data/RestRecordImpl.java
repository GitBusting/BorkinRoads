package com.bitirme.gitbusters.borkinroads.data;

import org.json.JSONObject;

public abstract class RestRecordImpl implements RestRecord {

  public RestRecordImpl(){}
  public RestRecordImpl(JSONObject jso){}

  @Override
  public String getURL() {
    return null;
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
