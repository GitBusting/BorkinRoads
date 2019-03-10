package com.bitirme.gitbusters.borkinroads.dbinterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.auth0.android.jwt.JWT;
import com.bitirme.gitbusters.borkinroads.LoginActivity;
import com.bitirme.gitbusters.borkinroads.data.RestRecord;
import com.bitirme.gitbusters.borkinroads.data.RestRecordImpl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

public class RestPuller extends Thread {

  private ArrayList<RestRecordImpl> fetchedRecords;
  private RestRecord templateReference;
  private String DB_URL;
  private Context context;

  public RestPuller(RestRecordImpl template, Context context)
  {
    this.fetchedRecords = new ArrayList<>();
    this.templateReference = template;
    DB_URL = template.getURL() + ".json";
    this.context = context;

  }

  public ArrayList<RestRecordImpl> getFetchedRecords() {
    return fetchedRecords;
  }

  @Override
  public void run()
  {
    // Obtain the record's constructor
    Class actualRecordClass = templateReference.getClass();
    Constructor recordConstructor = null;
    try {
      recordConstructor = actualRecordClass.getConstructor
              (new Class[]{JSONObject.class});
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }

    HttpURLConnection conn = null;
    try {
      Logger.getGlobal().log(Level.INFO,"Requesting routes from " + DB_URL);

      URL webServerUrl = new URL(DB_URL);
      conn = (HttpURLConnection) webServerUrl.openConnection();
      conn.setReadTimeout(10000 /* milliseconds */);
      conn.setConnectTimeout(15000 /* milliseconds */);

      conn.setRequestMethod("GET");
      conn.setRequestProperty("Content-type", "application/json");

      AuthenticationValidator av = new AuthenticationValidator(context);
      String token = av.getAuthenticationToken();

      conn.addRequestProperty("Authorization", "Bearer " + token);
      conn.connect();

      if (conn.getResponseCode() == 200) {
        InputStream responseBody = conn.getInputStream();
        BufferedReader r = new BufferedReader(new InputStreamReader(responseBody));
        StringBuilder total = new StringBuilder();
        for (String line; (line = r.readLine()) != null; ) {
          total.append(line).append('\n');
        }
        JSONArray jsa = new JSONArray(total.toString());
        for(int i = 0 ; i < jsa.length() ; i++)
          fetchedRecords.add((RestRecordImpl) recordConstructor.newInstance(jsa.getJSONObject(i)));
      } else {
        // Unable to connect
        System.out.println("a very bad thing happened.");
      }

    } catch (IOException | JSONException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } finally {
      if(conn!=null)
        conn.disconnect();
    }
  }

}
