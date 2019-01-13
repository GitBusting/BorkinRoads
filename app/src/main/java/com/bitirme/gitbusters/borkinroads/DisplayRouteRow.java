package com.bitirme.gitbusters.borkinroads;

import android.widget.RatingBar;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

public class DisplayRouteRow {

    private String title;
    private LatLng location;
    private MapView mapView;
    private float rating;
    private String routeDate;

    public DisplayRouteRow(String title, LatLng location, float rating, String routeDate) {
        this.title = title;
        this.rating = rating;
        this.routeDate = routeDate;
        this.location = location;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MapView getMapView() {
        return mapView;
    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getRouteDate() {
        return routeDate;
    }

    public void setRouteDate(String routeDate) {
        this.routeDate = routeDate;
    }


}
