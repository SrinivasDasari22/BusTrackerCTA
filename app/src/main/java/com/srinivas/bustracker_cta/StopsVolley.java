package com.srinivas.bustracker_cta;

import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class StopsVolley {

    private static final String stopsUrl =
            "https://www.ctabustracker.com/bustime/api/v2/getstops";
    private static final String TAG = "StopsVolley";
    private static final ArrayList<Stops> stopsList = new ArrayList<>();
    private static SharedPreferences.Editor ctaCachedDataEditor;
    private static boolean usedCache = false;
    private static LatLng myLocation;


    public static void downloadStops(MainActivity2 mainActivityIn, String dir, Route route, LatLng myLocation) {

//        myLocation = myLocation;

        SharedPreferences ctaCachedData = mainActivityIn.getApplicationContext().getSharedPreferences("CTA_PREFS", 0);
        ctaCachedDataEditor = ctaCachedData.edit();

        String cachedTime = ctaCachedData.getString("STOP_TIME", null);
        if (cachedTime != null) {
            try {
                Date dataTime = MainActivity.timeFormat.parse(cachedTime);
                long delta = 0;
                if (dataTime != null) {
                    delta = new Date().getTime() - dataTime.getTime();
                }
                long lifetime = mainActivityIn.getResources().getInteger(R.integer.cache_lifetime2);
                if (delta < lifetime) {
                    String cachedData = ctaCachedData.getString("STOPS_DATA", null);
                    if (cachedData != null) {
                        try {
                            usedCache = true;
                            handleSuccess(cachedData, mainActivityIn,route,myLocation);
                            Toast.makeText(mainActivityIn, "Used STOPS cache", Toast.LENGTH_SHORT).show();
                            return;
                        } catch (Exception e) {
                            Log.d(TAG, "downloadStops: " + e.getMessage());
                        }
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        RequestQueue queue = Volley.newRequestQueue(mainActivityIn);

        Uri.Builder buildURL = Uri.parse(stopsUrl).buildUpon();
        buildURL.appendQueryParameter("key", mainActivityIn.getString(R.string.cta_bus_key));
        buildURL.appendQueryParameter("format", "json");
        buildURL.appendQueryParameter("rt",route.getRouteNumber());
        buildURL.appendQueryParameter("dir",dir);

        String urlToUse = buildURL.build().toString();
        System.out.println(urlToUse);

        Response.Listener<JSONObject> listener = response -> {
            try {
                handleSuccess(response.toString(), mainActivityIn, route,myLocation);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        Response.ErrorListener error = StopsVolley::handleFail;

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private static double getDistance(LatLng currentLL,LatLng newLL)
    {
//        LatLng currentLL = myLocation;
        if (currentLL == null || newLL == null)
            return 0;
        return SphericalUtil.computeDistanceBetween(newLL,currentLL);
    }


    private static void handleSuccess(String responseText,
                                      MainActivity2 mainActivity,Route route1,LatLng myLocation) throws JSONException {
        JSONObject response = new JSONObject(responseText);

        JSONObject jsonObject = response.getJSONObject("bustime-response");
        JSONArray routes = jsonObject.getJSONArray("stops");
        stopsList.clear();
        String stpid="";
        String stpName="";
        String lat="";
        String lon="";
        for (int i = 0; i < routes.length(); i++) {
            JSONObject route = routes.getJSONObject(i);
            stpid = route.getString("stpid");

            stpName = route.getString("stpnm");

            lat = route.getString("lat");

            lon = route.getString("lon");

            LatLng location_this = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
            double distance = getDistance(myLocation,location_this);
            if(distance<1000){

//            Route routeObj = new Route(rNum, rName, rColor);
            stopsList.add(new Stops(stpid,stpName,distance));}

        }
        System.out.println("dsdsdsd: "+stopsList.size());
        if (!usedCache) {
            String formattedDate = MainActivity.timeFormat.format(new Date());

            ctaCachedDataEditor.putString("STOP_TIME", formattedDate);
            ctaCachedDataEditor.putString("STOP_DATA", response.toString());
            ctaCachedDataEditor.apply();
        }

        mainActivity.runOnUiThread(() -> {
            mainActivity.acceptStops(stopsList,route1);
        });

    }

    private static void handleFail(VolleyError ve) {
        Log.d(TAG, "handleFail: " + ve.getMessage());
    }




}
