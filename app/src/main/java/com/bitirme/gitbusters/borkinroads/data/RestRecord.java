package com.bitirme.gitbusters.borkinroads.data;

import org.json.JSONObject;

import java.io.Serializable;

public interface RestRecord extends Serializable{
  String getURL();
  JSONObject getJSON();
  int getEntryID();
}
