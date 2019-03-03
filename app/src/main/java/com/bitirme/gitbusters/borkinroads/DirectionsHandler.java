package com.bitirme.gitbusters.borkinroads;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

public class DirectionsHandler extends Thread {
    private LatLng currentLocation;
    private String apikey;
    private LatLng marker;
    private String keyword="";
    private boolean isLimitedTime = true;
    private int radius = 500; //default value
    private HashMap<LatLng,Double> markerMap;
    public DirectionsHandler(String api, int rad, String key, LatLng cl){
        apikey = api;
        if(rad>50000) radius = 50000;
        else radius = rad;
        keyword = key;
        currentLocation = new LatLng(cl.latitude,cl.longitude);
        markerMap = new HashMap<>();
    }
    public DirectionsHandler(){
        markerMap = new HashMap<>();
    }
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
    private void setMarker(double lat, double lng){
        marker = new LatLng(lat,lng);
    }
    public HashMap<LatLng,Double> getMarkerMap(){
        return markerMap;
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
            System.out.println(marker_lat + "," + marker_lng);
            boolean loc = false;
            double lat=currentLocation.latitude, lng=currentLocation.longitude;
            while ((result = br.readLine()) != null) {
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
                    insertMarkerMap(lat,lng);
                }
            }
            LatLng furthest = getFurthestMarker();
            setMarker(furthest.latitude, furthest.longitude);
            System.out.println(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void insertMarkerMap(double lat,double lng){
        double dist = distance(currentLocation.latitude,currentLocation.longitude,lat,lng);
        markerMap.put(new LatLng(lat,lng),dist);
    }
    private LatLng getFurthestMarker(){
        LatLng maxLatLng = null;
        double maxDist = Double.MIN_VALUE;
        for(Map.Entry<LatLng,Double> e : markerMap.entrySet()) {
            if(e.getValue() > maxDist ) {
                maxDist = e.getValue();
                maxLatLng = e.getKey();
            }
        }
        return maxLatLng;
    }
    private double distance(double first_lat, double first_lng, double second_lat, double second_lng) {
        return Math.sqrt(Math.pow(first_lat-second_lat,2) + Math.pow(first_lng-second_lng,2));
    }
    private String buildRequest(String keyword){
        String parameters = "key=";
        parameters += apikey;
        parameters += "&" + "location=" + currentLocation.latitude +","+ currentLocation.longitude;
        if(!keyword.equals(""))
            parameters += "&keyword=park"; //"park" is selected for type for now, according to the user story.
        parameters += "&radius=" + radius; //radius is in meters. this can be changed in the future.
        System.out.println("https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + parameters);
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + parameters;
    }
    private static Double cleanText(String res){
        res = res.replaceAll(",","");
        res = res.replaceAll(" ","");
        res = res.substring(res.indexOf(":")+1);
        return Double.parseDouble(res);
    }
}