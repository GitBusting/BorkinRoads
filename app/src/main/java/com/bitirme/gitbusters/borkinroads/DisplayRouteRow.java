package com.bitirme.gitbusters.borkinroads;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

public class DisplayRouteRow {

    private String title;
    private LatLng[] points;
    private MapView mapView;
    private float rating;
    private String routeDate;


    private boolean favourite;

    public DisplayRouteRow(String title, LatLng[] points, float rating, String routeDate, boolean favourite) {
        this.title = title;
        this.points = points;
        this.rating = rating;
        this.routeDate = routeDate;
        this.favourite = favourite;
    }

    public String getTitle() {
        return title;
    }

    public LatLng[] getPoints() {
        return points;
    }


    public float getRating() {
        return rating;
    }

    public String getRouteDate() {
        return routeDate;
    }

    public boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}
