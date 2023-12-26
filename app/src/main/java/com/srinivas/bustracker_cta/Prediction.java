package com.srinivas.bustracker_cta;

import java.io.Serializable;

public class Prediction implements Comparable<Prediction>, Serializable {


    private String destinationPt;
    private String routeDirection;
    private String destination;
    private String preTime;
    private String dueTime;

    public String getDestinationPt() {
        return destinationPt;
    }

    public void setDestinationPt(String destinationPt) {
        this.destinationPt = destinationPt;
    }

    public String getRouteDirection() {
        return routeDirection;
    }

    public void setRouteDirection(String routeDirection) {
        this.routeDirection = routeDirection;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPreTime() {
        return preTime;
    }

    public void setPreTime(String preTime) {
        this.preTime = preTime;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public Prediction(String destinationPt, String routeDirection, String destination, String preTime, String dueTime) {
        this.destinationPt = destinationPt;
        this.routeDirection = routeDirection;
        this.destination = destination;
        this.preTime = preTime;
        this.dueTime = dueTime;
    }


    @Override
    public int compareTo(Prediction o) {
        return (int) (Integer.parseInt(o.dueTime) - Integer.parseInt(dueTime));

    }
}
