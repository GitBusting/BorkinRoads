package com.bitirme.gitbusters.borkinroads;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DirectionsHandler extends Thread {
  /**
   * This segment is highly inspired by the snippets provided in the link below
   * https://code.tutsplus.com/tutorials/android-from-scratch-using-rest-apis--cms-27117
   */
  @Override
  public void run() {
    HttpsURLConnection conn = null, connPut = null;
    try {
      URL webServerUrl = new URL("https://safe-sea-33768.herokuapp.com/appointments.json");

      conn =
              (HttpsURLConnection) webServerUrl.openConnection();
      conn.setReadTimeout(10000 /* milliseconds */);
      conn.setConnectTimeout(15000 /* milliseconds */);

      conn.setRequestMethod("GET");
      conn.connect();

      if (conn.getResponseCode() == 200) {
        InputStream responseBody = conn.getInputStream();
        InputStreamReader responseBodyReader =
                new InputStreamReader(responseBody, "UTF-8");
        JsonReader jsonReader = new JsonReader(responseBodyReader);

        jsonReader.beginArray(); // Start processing the JSON array
        while (jsonReader.hasNext()) { // Loop through all objects
          // First element is status, which indicates
          // whether a route was plotted or not
          jsonReader.nextName();
          if(!jsonReader.nextString().equals("OK"))
          {
            System.out.println("Google could not plot a route for us...");
            break;
          }
          // Next we get a list of waypoint IDs
          // TODO design guide advices us to cache these
          // just skip the object for now
          jsonReader.beginObject();
          jsonReader.endObject();
          // Now we parse the route info
          jsonReader.beginObject();
          while(jsonReader.hasNext())
          {
            // Skip summary
            jsonReader.nextName();
            jsonReader.skipValue();

            // Begin parsing a leg of the route
            jsonReader.beginObject();
            while(jsonReader.hasNext())
            {
              // Parse the route step by step
              jsonReader.beginObject();
              while(jsonReader.hasNext())
              {
                // Skip travel mode info
                jsonReader.nextName();
                jsonReader.skipValue();

                // Skip
                jsonReader.nextName();
                jsonReader.skipValue();
              }
              jsonReader.endObject();
            }
            jsonReader.endObject();

          }
          jsonReader.endObject();
        }
        jsonReader.endArray();

      } else {
        // Unable to connect
        System.out.println("Cannot connect to google services?");
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (conn != null)
        conn.disconnect();
      if (connPut != null)
        connPut.disconnect();
    }
  }
}
