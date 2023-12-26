package com.srinivas.bustracker_cta;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Route implements Serializable, Comparable<Route>{

    private final String routeNumber;
    private final String routeName;
    private final String routeColor;

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    private String dir;

    public Route(String routeNumber, String routeName, String routeColor) {
        this.routeNumber = routeNumber;
        this.routeName = routeName;
        this.routeColor = routeColor;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getRouteColor() {
        return routeColor;
    }

    @Override
    public int compareTo(Route route) {
        return routeNumber.compareTo(route.routeNumber);
    }

    @NonNull
    @Override
    public String toString() {
        return "Route{" +
                "routeNumber='" + routeNumber + '\'' +
                ", routeName='" + routeName + '\'' +
                ", routeColor='" + routeColor + '\'' +
                '}';
    }
}
