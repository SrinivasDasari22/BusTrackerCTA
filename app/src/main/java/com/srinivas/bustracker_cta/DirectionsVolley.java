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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class DirectionsVolley implements Serializable{

    private static final String directionsUrl =
            "https://www.ctabustracker.com/bustime/api/v2/getdirections";
    private static final String TAG = "RoutesVolley";
    private static final ArrayList<String> directionsList = new ArrayList<>();
    private static SharedPreferences.Editor ctaCachedDataEditor;
    private static boolean usedCache = false;


    public static void downloadDirections(MainActivity mainActivityIn,String routeNumber,Route route) {

        SharedPreferences ctaCachedData = mainActivityIn.getApplicationContext().getSharedPreferences("CTA_PREFS", 0);
        ctaCachedDataEditor = ctaCachedData.edit();

        String cachedTime = ctaCachedData.getString("DIRECTION_TIME", null);
        if (cachedTime != null) {
            try {
                Date dataTime = MainActivity.timeFormat.parse(cachedTime);
                long delta = 0;
                if (dataTime != null) {
                    delta = new Date().getTime() - dataTime.getTime();
                }
                long lifetime = mainActivityIn.getResources().getInteger(R.integer.cache_lifetime2);
                if (delta < lifetime) {
//                    String cachedData = ctaCachedData.getString("DIRECTION_DATA", null);
                      String cachedData = ctaCachedData.getString(route.getRouteNumber(), null);

                    if (cachedData != null) {
                        try {
                            usedCache = true;
                            handleSuccess(cachedData, mainActivityIn,route);
                            Toast.makeText(mainActivityIn, "Used DIRECTION cache", Toast.LENGTH_SHORT).show();
                            return;
                        } catch (Exception e) {
                            Log.d(TAG, "downloadRoutes: " + e.getMessage());
                        }
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        RequestQueue queue = Volley.newRequestQueue(mainActivityIn);

        Uri.Builder buildURL = Uri.parse(directionsUrl).buildUpon();
        buildURL.appendQueryParameter("key", mainActivityIn.getString(R.string.cta_bus_key));
        buildURL.appendQueryParameter("format", "json");
        buildURL.appendQueryParameter("rt",routeNumber);
        String urlToUse = buildURL.build().toString();

        Response.Listener<JSONObject> listener = response -> {
            try {
                handleSuccess(response.toString(), mainActivityIn, route);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        Response.ErrorListener error = DirectionsVolley::handleFail;

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }


    private static void handleSuccess(String responseText,
                                      MainActivity mainActivity,Route route1) throws JSONException {
        JSONObject response = new JSONObject(responseText);

        JSONObject jsonObject = response.getJSONObject("bustime-response");
        JSONArray routes = jsonObject.getJSONArray("directions");
        directionsList.clear();
        for (int i = 0; i < routes.length(); i++) {
            JSONObject route = routes.getJSONObject(i);
            String dir = route.getString("dir");
//            Route routeObj = new Route(rNum, rName, rColor);
            directionsList.add(dir);

        }
        if (!usedCache) {
            String formattedDate = MainActivity.timeFormat.format(new Date());

            ctaCachedDataEditor.putString("DIRECTION_TIME", formattedDate);
            ctaCachedDataEditor.putString(route1.getRouteNumber(), response.toString());
            ctaCachedDataEditor.apply();
        }

        mainActivity.runOnUiThread(() -> {
            mainActivity.acceptDirections(directionsList,route1);
        });

    }

    private static void handleFail(VolleyError ve) {
        Log.d(TAG, "handleFail: " + ve.getMessage());
    }



}
