package com.srinivas.bustracker_cta;

import java.io.Serializable;

public class Stops implements Comparable<Stops>, Serializable {



    private  String stpid;
    private  String stpName;
    private  String lat;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    private double distance;

    public String getStpid() {
        return stpid;
    }

    public void setStpid(String stpid) {
        this.stpid = stpid;
    }

    public String getStpName() {
        return stpName;
    }

    public void setStpName(String stpName) {
        this.stpName = stpName;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    private  String lon;


    public Stops(String stpid, String stpName,double distance) {
        this.stpid = stpid;
        this.stpName = stpName;
//        this.lat = lat;
//        this.lon = lon;
        this.distance = distance;
    }


    @Override
    public int compareTo(Stops o) {

        return (int) (  distance - o.distance);
//        return 0;
    }
}
