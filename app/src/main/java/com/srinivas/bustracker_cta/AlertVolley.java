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
import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

public class AlertVolley {
    private static final String alertUrl =
            "https://www.transitchicago.com/api/1.0/alerts.aspx";
    private static final String TAG = "AlertVolley";
    private static final ArrayList<Alert> alertList = new ArrayList<>();
//    private static SharedPreferences.Editor ctaCachedDataEditor;
//    private static boolean usedCache = false;


    public static void downloadAlerts(MainActivity2 mainActivityIn,String routeid) {


        RequestQueue queue = Volley.newRequestQueue(mainActivityIn);

        Uri.Builder buildURL = Uri.parse(alertUrl).buildUpon();
        buildURL.appendQueryParameter("routeid",routeid);
        buildURL.appendQueryParameter("activeonly","true");

        buildURL.appendQueryParameter("outputType", "JSON");
        String urlToUse = buildURL.build().toString();
        System.out.println("url::"+urlToUse);

        Response.Listener<JSONObject> listener = response -> {
            try {
                handleSuccess(response.toString(), mainActivityIn);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        Response.ErrorListener error = AlertVolley::handleFail;

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }


    private static void handleSuccess(String responseText,
                                      MainActivity2 mainActivity) throws JSONException {
        JSONObject response = new JSONObject(responseText);

        JSONObject jsonObject = response.getJSONObject("CTAAlerts");
        JSONArray routes = jsonObject.getJSONArray("Alert");
        alertList.clear();
        for (int i = 0; i < routes.length(); i++) {
            JSONObject alert = routes.getJSONObject(i);
            String alertId = alert.getString("AlertId");
            String headline = alert.getString("Headline");



            JSONObject fullDes = alert.getJSONObject("FullDescription");


            String cdata = fullDes.getString("#cdata-section");


//            JSONObject cdata = fullDes.getJSONObject("#cdata-section");
//            String dir = alert.getString("dir");
//            Route routeObj = new Route(rNum, rName, rColor);
            alertList.add(new Alert(alertId,headline,cdata));

        }


        mainActivity.runOnUiThread(() -> {
            try {
                mainActivity.acceptAlerts(alertList);
            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SAXException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private static void handleFail(VolleyError ve) {
        Log.d(TAG, "handleFail: " + ve.getMessage());
    }



}
