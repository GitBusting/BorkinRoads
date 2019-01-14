package com.bitirme.gitbusters.borkinroads;

import android.util.JsonReader;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class DirectionsHandler extends Thread {

    private ArrayList<LatLng> markers;
    private LatLng start;

    public void setPath(ArrayList<LatLng> markers, LatLng start) {
        this.start = start;
        this.markers = markers;
    }

    /**
     * This segment is highly inspired by the snippets provided in the link below
     * https://code.tutsplus.com/tutorials/android-from-scratch-using-rest-apis--cms-27117
     */
    @Override
    public void run() {
        HttpsURLConnection conn = null;
        try {
            String parameters;
            parameters = "origin=" + start.latitude + "," + start.longitude;
            parameters += "&destination=" + start.latitude + "," + start.longitude; //start and end points are the same for the route
            String key = "";
            assert(!key.equals("")); //insert key
            parameters += "&key=" + key;
            parameters += "&mode=walking";
            if (markers.size() > 0) {
                parameters += "&waypoints=";
            }
            for (int i = 0; i < markers.size(); i++) {
                if (i >= 10) break;
                if (i != 0) parameters += "|";
                parameters += "via:" + markers.get(i).latitude + "," + markers.get(i).longitude;
            }
            URL webServerUrl = new URL("https://maps.googleapis.com/maps/api/directions/json?" + parameters);
            conn = (HttpsURLConnection) webServerUrl.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);

            conn.setRequestMethod("GET");
            conn.connect();
            System.out.println("parameters: " + parameters);
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb= new StringBuilder();

            String result;
            while((result = br.readLine()) != null)
                sb.append(result);

            System.out.println(sb.toString());

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
                    if (!jsonReader.nextString().equals("OK")) {
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
                    while (jsonReader.hasNext()) {
                        // Skip summary
                        jsonReader.nextName();
                        jsonReader.skipValue();

                        // Begin parsing a leg of the route
                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            // Parse the route step by step
                            jsonReader.beginObject();
                            while (jsonReader.hasNext()) {
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
        }
    }
}
