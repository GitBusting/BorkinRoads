package com.bitirme.gitbusters.borkinroads;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DirectionsHandler extends Thread {
    private LatLng currentLocation;
    private String apikey;
    private LatLng marker;
    private boolean isLimitedTime = true;
    private int radius = 500; //default value
    public void setApikey(String api){
        apikey = api;
    }
    public void setCurrentLocation(LatLng cl) {
        currentLocation = new LatLng(cl.latitude,cl.longitude);
    }
    public boolean setRadius(int rad) {
        if (rad > 50000) return false;
        radius = rad;
        return true;
    }
    public LatLng getResult(){
        return marker;
    }
    public void setIsLimitedTime(boolean limitedTime){
        isLimitedTime = limitedTime;
    }
    public void setMarker(double lat, double lng){
        marker = new LatLng(lat,lng);
    }
    @Override
    public void run() {
        HttpsURLConnection conn = null;
        try {
            String parameters = "key=";
            parameters += apikey;
            parameters += "&" + "location=" + currentLocation.latitude +","+ currentLocation.longitude;
            parameters += "&radius=" + radius; //radius is in meters. this can be changed in the future.
            //parameters += "&keyword=park"; //"park" is selected for type for now, according to the user story.
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
            String lat="";
            String lng="";
            while ((result = br.readLine()) != null) {
                System.out.println(result);
                sb.append(result);
                if(result.contains("\"lat\"")) {
                    result = result.replaceAll(",","");
                    result = result.replaceAll(" ","");
                    lat = result;
                }
                if(result.contains("\"lng\"")) lng = result;
            }
            lat=lat.replaceAll(",","");
            lat=lat.replaceAll(" ","");
            lat = lat.substring(lat.indexOf(":")+1);
            lng=lng.replaceAll(",","");
            lng=lng.replaceAll(" ","");
            lng = lng.substring(lng.indexOf(":")+1);
            setMarker(Double.parseDouble(lat),Double.parseDouble(lng));
            System.out.println(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}