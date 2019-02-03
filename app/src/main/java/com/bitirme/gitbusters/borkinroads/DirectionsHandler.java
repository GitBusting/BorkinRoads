package com.bitirme.gitbusters.borkinroads;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DirectionsHandler extends Thread {
    private LatLng currentLocation;
    private int radius = 500; //default value
    public void setCurrentLocation(LatLng cl) {
        currentLocation = new LatLng(cl.latitude,cl.longitude);
    }
    public boolean setRadius(int rad) {
        if (rad > 50000) return false;
        radius = rad;
        return true;
    }
    @Override
    public void run() {
        HttpsURLConnection conn = null;
        try {
            String parameters = "";
            String apikey = "key=AIzaSyA3nOUd0mIm1mCoUIx1DRa-qsCT3Kz1a_k";
            parameters += apikey;
            parameters += "&" + "location=" + currentLocation.latitude +","+ currentLocation.longitude;
            parameters += "&radius=" + radius; //radius is in meters. this can be changed in the future.
            parameters += "&keyword=park"; //"park" is selected for type for now, according to the user story.
            URL webServerUrl = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + parameters);
            conn = (HttpsURLConnection) webServerUrl.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);

            conn.setRequestMethod("GET");
            conn.connect();
            System.out.println("parameters: " + parameters);
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();

            String result;
            while ((result = br.readLine()) != null)
                sb.append(result);

            System.out.println(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}