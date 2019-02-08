package com.bitirme.gitbusters.borkinroads;

public class RouteDetails {
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

    public RouteDetails(double maxPace, double avgPace, double movingPace, double maxSpeed, double avgSpeed, double movingSpeed, double routeLength, double totalTime, double movingTime, String date, String time) {
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
}
