package com.bitirme.gitbusters.borkinroads.dbinterface;

import android.util.JsonReader;

import com.bitirme.gitbusters.borkinroads.routeutilities.RouteRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class RoutePuller extends Thread {

  private ArrayList<RouteRecord> fetchedRoutes;

  public RoutePuller()
  {
    fetchedRoutes = new ArrayList<>();
  }

  public ArrayList<RouteRecord> getFetchedRoutes() {
    return fetchedRoutes;
  }

  @Override
  public void run()
  {
    HttpsURLConnection conn = null;
    try {

      URL webServerUrl = new URL("https://shielded-cliffs-47552.herokuapp.com/routes.json");
      conn =
              (HttpsURLConnection) webServerUrl.openConnection();
      conn.setReadTimeout(10000 /* milliseconds */);
      conn.setConnectTimeout(15000 /* milliseconds */);

      conn.setRequestMethod("GET");
      conn.setRequestProperty("Content-type", "application/json");
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
          fetchedRoutes.add(new RouteRecord(jsa.getJSONObject(i)));
      } else {
        // Unable to connect
        System.out.println("a very bad thing happened.");
      }

    } catch (IOException | JSONException e) {
      e.printStackTrace();
    } finally {
      if(conn!=null)
        conn.disconnect();
    }
  }

}
