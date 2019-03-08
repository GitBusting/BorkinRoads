package com.bitirme.gitbusters.borkinroads.data;

import com.google.android.gms.maps.model.LatLng;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UserStatusRecord extends RestRecordImpl {
    private int entryId;
    private int userId;
    private boolean isActive;
    private LatLng currentPosition;
    private ArrayList<LatLng> waypoints;
    private LatLng startPoint, endPoint;
    String date,time;

    //Probably will not be used
    public UserStatusRecord(){super();}

    public UserStatusRecord(int userId, boolean isActive, LatLng currentPosition, ArrayList<LatLng> waypoints,LatLng startPoint, LatLng endPoint) {
        this.entryId = -1;
        this.userId = userId;
        this.isActive = isActive;
        this.currentPosition = new LatLng(currentPosition.latitude, currentPosition.longitude);
        this.waypoints = new ArrayList<>(waypoints);
        this.startPoint = new LatLng(startPoint.latitude,startPoint.longitude);
        this.endPoint = new LatLng(endPoint.latitude,endPoint.longitude);
        Date currentTime = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(currentTime);
        date = strDate.split(" ")[0] + "T";
        time = strDate.split(" ")[1] + ".000Z";
    }

    //Copy the existing record
    public UserStatusRecord(UserStatusRecord cp) {
        this.entryId = cp.entryId;
        this.userId = cp.userId;
        this.isActive = cp.isActive;
        this.currentPosition = new LatLng(cp.currentPosition.latitude, cp.currentPosition.longitude);
        this.waypoints = new ArrayList<>(cp.waypoints);
        this.startPoint = new LatLng(cp.startPoint.latitude,cp.startPoint.longitude);
        this.endPoint = new LatLng(cp.endPoint.latitude,cp.endPoint.longitude);
        Date currentTime = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(currentTime);
        date = strDate.split(" ")[0] + "T";
        time = strDate.split(" ")[1] + ".000Z";
    }

    @Override
    public int getEntryID() { return entryId; }

    public int getUserId() { return userId; }

    public boolean isActive() { return isActive; }

    public LatLng getCurrentPosition() { return currentPosition; }

    public ArrayList<LatLng> getWaypoints() { return waypoints; }

    public LatLng getStartPoint() { return startPoint; }

    public LatLng getEndPoint() { return endPoint; }

    public String get_date() { return date; }

    public String get_time() { return time; }

    public void setActive(boolean active) { isActive = active; }

    public void setCurrentPosition(LatLng cur) { currentPosition = new LatLng(cur.latitude,cur.longitude); }

    private void parseRecordFromJSON(JSONObject jso)
    {
        try {
            this.entryId = jso.getInt("id");
            this.waypoints = this.stringToWaypoints(jso.getString("path"));
            this.startPoint = waypoints.remove(0);
            this.endPoint = waypoints.remove(waypoints.size()-1);
            String dateTime = jso.getString("date");
            String[] tokens = dateTime.split("T");
            this.date = tokens[0] + "T";
            this.time = tokens[1].substring(0, tokens[1].indexOf('.'));
            this.isActive = jso.getBoolean("isActive");
            String pos = jso.getString("currentPosition");
            double lat = Double.parseDouble(pos.substring(0,pos.indexOf(",")));
            double lng = Double.parseDouble(pos.substring(pos.indexOf(",")));
            this.currentPosition = new LatLng(lat,lng);
        } catch(JSONException jse){
            jse.printStackTrace();
        }
    }

    @Override
    public String getURL() {
        return "https://shielded-cliffs-47552.herokuapp.com/CHANGEDIS"; // TODO: CHANGE THIS.
    }

    @Override
    public JSONObject getJSON() {
        JSONObject jso = new JSONObject();
        try {
            //jso.put("entryID",this.entryId);
            jso.put("userID",this.userId);
            jso.put("isActive", this.isActive);
            jso.put("currentPosition", this.currentPosition.latitude +","+this.currentPosition.longitude);
            jso.put("path",this.waypointsToString());
            jso.put("date", this.date + this.time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jso;
    }

    /*Copied from RouteRecord
     *Adding start and end points to the string too.
     */
    private String waypointsToString()
    {
        String ret = "";
        ret += startPoint.latitude + "_" + startPoint.longitude + ";";
        for(LatLng wp : waypoints)
            ret += wp.latitude + "_" + wp.longitude + ";";
        ret += endPoint.latitude + "_" + endPoint.longitude;
        System.out.println("Route string: " + ret);
        return ret;
    }

    //Copied from RouteRecord
    private ArrayList<LatLng> stringToWaypoints(String s)
    {
        String[] coords = s.split(";");
        ArrayList<LatLng> ret = new ArrayList<>();
        for(int i = 0 ; i < coords.length ; i++)
        {
            String[] ll = coords[i].split("_");
            double latitude = Double.parseDouble(ll[0]);
            double longtitude = Double.parseDouble(ll[1]);
            ret.add(new LatLng(latitude,longtitude));
        }
        return ret;
    }

}
