package com.bitirme.gitbusters.borkinroads;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

class DisplayRouteRow {

    private final String title;
    private final LatLng[] points;
    private MapView mapView;
    private final Float rating;
    private final String routeDate;
    private final Integer numberOfTimesRouteTaken;
    private final Double estimatedRouteDuration;
    private final boolean nearWater;
    private final boolean nearPark;
    private boolean favourite;

    public DisplayRouteRow(String title, LatLng[] points, float rating, String routeDate, int numberOfTimesRouteTaken, double estimatedRouteDuration, boolean nearWater, boolean nearPark, boolean favourite) {
        this.title = title;
        this.points = points;
        this.rating = rating;
        this.routeDate = routeDate;
        this.numberOfTimesRouteTaken = numberOfTimesRouteTaken;
        this.estimatedRouteDuration = estimatedRouteDuration;
        this.nearWater = nearWater;
        this.nearPark = nearPark;
        this.favourite = favourite;
    }

    public String getTitle() {
        return title;
    }

    public LatLng[] getPoints() {
        return points;
    }


    public Float getRating() {
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


    public int getNumberOfTimesRouteTaken() {
        return numberOfTimesRouteTaken;
    }

    public Double getEstimatedRouteDuration() {
        return estimatedRouteDuration;
    }

    public boolean isNearWater() {
        return nearWater;
    }

    public boolean isNearPark() {
        return nearPark;
    }

}
