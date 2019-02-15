package com.bitirme.gitbusters.borkinroads.data;

import org.json.JSONException;
import org.json.JSONObject;

public class RouteDetailsRecord extends RestRecordImpl {
    private int entryId;
    private int route_id;
    private double maxPace;
    private double avgPace;
    private double movingPace;
    private double maxSpeed;
    private double avgSpeed;
    private double movingSpeed;
    private double routeLength;
    private double totalTime;
    private double movingTime;
    private String date, time;

    public RouteDetailsRecord(int id, int route_id, double maxPace, double avgPace, double movingPace, double maxSpeed, double avgSpeed, double movingSpeed, double routeLength, double totalTime, double movingTime, String date, String time) {
        this.entryId = id;
        this.route_id = route_id;
        this.maxPace = maxPace;
        this.avgPace = avgPace;
        this.movingPace = movingPace;
        this.maxSpeed = maxSpeed;
        this.avgSpeed = avgSpeed;
        this.movingSpeed = movingSpeed;
        this.routeLength = routeLength;
        this.totalTime = totalTime;
        this.movingTime = movingTime;
        this.date = date;
        this.time = time;
    }

    public RouteDetailsRecord(JSONObject json) {
        try {
            this.entryId = json.getInt("id");
            this.route_id = json.getInt("route_id");
            this.maxPace = json.getDouble("maxPace");
            this.avgPace = json.getDouble("avgPace");
            this.movingPace = json.getDouble("movingPace");
            this.maxSpeed = json.getDouble("maxSpeed");
            this.avgSpeed = json.getDouble("avgSpeed");
            this.movingSpeed = json.getDouble("movingSpeed");
            this.routeLength = json.getDouble("routeLength");
            this.totalTime = json.getDouble("totalTime");
            this.movingTime = json.getDouble("movingTime");
            this.date = json.getString("date");
            this.time = json.getString("time");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public RouteDetailsRecord() {
        super();
    }

    public double getMaxPace() {
        return maxPace;
    }

    public double getAvgPace() {
        return avgPace;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public double getRouteLength() {
        return routeLength;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public double getMovingTime() {
        return movingTime;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public double getMovingSpeed() {
        return movingSpeed;
    }

    public double getMovingPace() {
        return movingPace;
    }

    public int getRoute_id() {
        return route_id;
    }

    @Override
    public String getURL() {
        return "https://shielded-cliffs-47552.herokuapp.com/route_details";
    }

    @Override
    public JSONObject getJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("entryId", entryId);
            json.put("route_id", route_id);
            json.put("maxPace", maxPace);
            json.put("avgPace", avgPace);
            json.put("movingPace", movingPace);
            json.put("maxSpeed", maxSpeed);
            json.put("avgSpeed", avgSpeed);
            json.put("movingSpeed", movingSpeed);
            json.put("routeLength", routeLength);
            json.put("totalTime", totalTime);
            json.put("movingTime", movingTime);
            json.put("date", date);
            json.put("time", time);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
