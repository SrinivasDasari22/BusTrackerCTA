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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class RoutesVolley {

    private static final String vehiclesUrl =
            "https://www.ctabustracker.com/bustime/api/v2/getroutes";
    private static final String TAG = "RoutesVolley";
    private static final ArrayList<Route> routeList = new ArrayList<>();
    private static SharedPreferences.Editor ctaCachedDataEditor;
    private static boolean usedCache = false;


    public static void downloadRoutes(MainActivity mainActivityIn) {

        SharedPreferences ctaCachedData = mainActivityIn.getApplicationContext().getSharedPreferences("CTA_PREFS", 0);
        ctaCachedDataEditor = ctaCachedData.edit();

        String cachedTime = ctaCachedData.getString("ROUTE_TIME", null);
        if (cachedTime != null) {
            try {
                Date dataTime = MainActivity.timeFormat.parse(cachedTime);
                long delta = 0;
                if (dataTime != null) {
                    delta = new Date().getTime() - dataTime.getTime();
                }
                long lifetime = mainActivityIn.getResources().getInteger(R.integer.cache_lifetime2);
                if (delta < lifetime) {
                    String cachedData = ctaCachedData.getString("ROUTE_DATA", null);
                    if (cachedData != null) {
                        try {
                            usedCache = true;
                            handleSuccess(cachedData, mainActivityIn);
                            Toast.makeText(mainActivityIn, "Used ROUTE cache", Toast.LENGTH_SHORT).show();
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

        Uri.Builder buildURL = Uri.parse(vehiclesUrl).buildUpon();
        buildURL.appendQueryParameter("key", mainActivityIn.getString(R.string.cta_bus_key));
        buildURL.appendQueryParameter("format", "json");
        String urlToUse = buildURL.build().toString();

        Response.Listener<JSONObject> listener = response -> {
            try {
                handleSuccess(response.toString(), mainActivityIn);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        Response.ErrorListener error = RoutesVolley::handleFail;

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private static void handleSuccess(String responseText,
                                      MainActivity mainActivity) throws JSONException {
        JSONObject response = new JSONObject(responseText);

        JSONObject jsonObject = response.getJSONObject("bustime-response");
        JSONArray routes = jsonObject.getJSONArray("routes");
        routeList.clear();
        for (int i = 0; i < routes.length(); i++) {
            JSONObject route = routes.getJSONObject(i);
            String rNum = route.getString("rt");
            String rName = route.getString("rtnm");
            String rColor = route.getString("rtclr");

            Route routeObj = new Route(rNum, rName, rColor);
            routeList.add(routeObj);
        }
        if (!usedCache) {
            String formattedDate = MainActivity.timeFormat.format(new Date());

            ctaCachedDataEditor.putString("ROUTE_TIME", formattedDate);
            ctaCachedDataEditor.putString("ROUTE_DATA", response.toString());
            ctaCachedDataEditor.apply();
        }

        mainActivity.runOnUiThread(() -> mainActivity.acceptRoutes(routeList));
        System.out.println("ds1:"+routeList.size());

    }

    private static void handleFail(VolleyError ve) {
        Log.d(TAG, "handleFail: " + ve.getMessage());
    }

//    @Override
//    public void run() {
//
//    }
}
