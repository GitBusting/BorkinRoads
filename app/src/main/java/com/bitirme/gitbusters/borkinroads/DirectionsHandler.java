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
    private String keyword="";
    private boolean isLimitedTime = true;
    private int radius = 500; //default value
    public void setApikey(String api){
        apikey = api;
    }
    public void setCurrentLocation(LatLng cl) {
        currentLocation = new LatLng(cl.latitude,cl.longitude);
    }
    public void setKeyword(String key){
        keyword = key;
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
            String url = buildRequest(keyword);
            URL webServerUrl = new URL(url);
            conn = (HttpsURLConnection) webServerUrl.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String result;
            double marker_lat= currentLocation.latitude;
            double marker_lng= currentLocation.longitude;
            boolean loc = false;
            while ((result = br.readLine()) != null) {
                double lat=marker_lat, lng=marker_lng;
                sb.append(result);
                if(result.contains("location")) {
                    loc = true;
                }
                if(result.contains("},") && loc ) { //leaving the location part, lat and lng are the coordinates of the possible marker point
                    loc = false;
                }
                if(result.contains("\"lat\"") && loc){
                    lat = cleanText(result);

                }
                if(result.contains("\"lng\"") && loc) {
                    lng = cleanText(result);
                    //System.out.println(distance(currentLocation.latitude,currentLocation.longitude, lat,lng)
                     //       + " - " + distance(currentLocation.latitude,currentLocation.longitude,marker_lat,marker_lng));
                    if(distance(currentLocation.latitude,currentLocation.longitude, lat,lng) >
                            distance(currentLocation.latitude,currentLocation.longitude,marker_lat,marker_lng)) {
                        marker_lat = lat;
                        marker_lng = lng;
                    }
                }
            }
            setMarker(marker_lat,marker_lng);
            System.out.println(marker_lat + "," + marker_lng);
            System.out.println(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public double distance(double first_lat, double first_lng, double second_lat, double second_lng) {
        return Math.sqrt(Math.pow(first_lat-second_lat,2) + Math.pow(first_lng-second_lng,2));
    }
    public String buildRequest(String keyword){
        String parameters = "key=";
        parameters += apikey;
        parameters += "&" + "location=" + currentLocation.latitude +","+ currentLocation.longitude;
        if(!keyword.equals(""))
            parameters += "&keyword=park"; //"park" is selected for type for now, according to the user story.
        parameters += "&radius=" + radius; //radius is in meters. this can be changed in the future.
        System.out.println("https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + parameters);
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + parameters;
    }
    public static Double cleanText(String res){
        res = res.replaceAll(",","");
        res = res.replaceAll(" ","");
        res = res.substring(res.indexOf(":")+1);
        return Double.parseDouble(res);
    }
}