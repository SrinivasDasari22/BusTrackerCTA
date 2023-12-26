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

public class PredictionsVolley {


    private static final String stopsUrl =
            "https://www.ctabustracker.com/bustime/api/v2/getpredictions";
    private static final String TAG = "PredictionsVolley";
    private static final ArrayList<Prediction> predictionList = new ArrayList<>();
    private static SharedPreferences.Editor ctaCachedDataEditor;
    private static boolean usedCache = false;




    public static void downloadPredictions(PredictionsActivity mainActivityIn,Route route,Stops stops) {

//        SharedPreferences ctaCachedData = mainActivityIn.getApplicationContext().getSharedPreferences("CTA_PREFS", 0);
//        ctaCachedDataEditor = ctaCachedData.edit();
//
//        String cachedTime = ctaCachedData.getString("PRE_TIME", null);
//        if (cachedTime != null) {
//            try {
//                Date dataTime = MainActivity.timeFormat.parse(cachedTime);
//                long delta = 0;
//                if (dataTime != null) {
//                    delta = new Date().getTime() - dataTime.getTime();
//                }
//                long lifetime = mainActivityIn.getResources().getInteger(R.integer.cache_lifetime);
//                if (delta < lifetime) {
//                    String cachedData = ctaCachedData.getString("PRE_DATA", null);
//                    if (cachedData != null) {
//                        try {
//                            usedCache = true;
//                            handleSuccess(cachedData, mainActivityIn,route,stops);
//                            Toast.makeText(mainActivityIn, "Used Prediction cache", Toast.LENGTH_SHORT).show();
//                            return;
//                        } catch (Exception e) {
//                            Log.d(TAG, "downloadPredictions: " + e.getMessage());
//                        }
//                    }
//                }
//
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
        RequestQueue queue = Volley.newRequestQueue(mainActivityIn);

        Uri.Builder buildURL = Uri.parse(stopsUrl).buildUpon();
        buildURL.appendQueryParameter("key", mainActivityIn.getString(R.string.cta_bus_key));
        buildURL.appendQueryParameter("format", "json");
        buildURL.appendQueryParameter("rt",route.getRouteNumber());
        buildURL.appendQueryParameter("stpid",stops.getStpid());



        String urlToUse = buildURL.build().toString();
        System.out.println(urlToUse);

        Response.Listener<JSONObject> listener = response -> {
            try {
                handleSuccess(response.toString(), mainActivityIn, route,stops);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        Response.ErrorListener error = PredictionsVolley::handleFail;

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }


    private static void handleSuccess(String responseText,
                                      PredictionsActivity mainActivity,Route route1,Stops stops) throws JSONException {
        JSONObject response = new JSONObject(responseText);

        JSONObject jsonObject = response.getJSONObject("bustime-response");
        JSONArray routes = jsonObject.getJSONArray("prd");
        predictionList.clear();
        String dstp="";
        String rtdir="";
        String des="";
        String prdtm="";

        String prdctdn ="";

        System.out.println("in predictions volley");
        for (int i = 0; i < routes.length(); i++) {
            JSONObject route = routes.getJSONObject(i);
            dstp = route.getString("dstp");

            rtdir = route.getString("rtdir");

            des = route.getString("des");

            prdtm = route.getString("prdtm");
            prdctdn = route.getString("prdctdn");


//            Route routeObj = new Route(rNum, rName, rColor);
            predictionList.add(new Prediction(dstp,rtdir,des,prdtm,prdctdn));

        }
        System.out.println("dsdsdsd: "+predictionList.size());

        mainActivity.runOnUiThread(() -> {
            mainActivity.acceptStops(predictionList,route1,stops);
        });

    }

    private static void handleFail(VolleyError ve) {
        Log.d(TAG, "handleFail: " + ve.getMessage());
    }






}
