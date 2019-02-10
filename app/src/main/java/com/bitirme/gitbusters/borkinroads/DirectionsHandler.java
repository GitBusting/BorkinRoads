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
            String url = buildRequest("");
            URL webServerUrl = new URL(url);
            conn = (HttpsURLConnection) webServerUrl.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String result;
            double lat= currentLocation.latitude;
            double lng= currentLocation.longitude;
            while ((result = br.readLine()) != null) {
                sb.append(result);
                if(result.contains("\"lat\""))
                    lat = cleanText(result);
                    //lat = (Math.abs(cleanText(result)-currentLocation.latitude) > Math.abs(currentLocation.latitude-lat)) ? cleanText(result) : lat;
                if(result.contains("\"lng\""))
                    lng = cleanText(result);
                    //lng = (Math.abs(cleanText(result)-currentLocation.longitude) > Math.abs(currentLocation.longitude - lng)) ? cleanText(result) : lng;
            }
            setMarker(lat,lng);
            System.out.println(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String buildRequest(String keyword){
        String parameters = "key=";
        parameters += apikey;
        parameters += "&" + "location=" + currentLocation.latitude +","+ currentLocation.longitude;
        parameters += "&radius=" + radius; //radius is in meters. this can be changed in the future.
        if(!keyword.equals(""))
            parameters += "&keyword=" + keyword; //"park" is selected for type for now, according to the user story.
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + parameters;
    }
    public static Double cleanText(String res){
        res = res.replaceAll(",","");
        res = res.replaceAll(" ","");
        res = res.substring(res.indexOf(":")+1);
        return Double.parseDouble(res);
    }
}