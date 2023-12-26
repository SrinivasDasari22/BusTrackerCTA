package com.srinivas.bustracker_cta;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {


    private RecyclerView recyclerView;

    public static  int nightFlag;

    private final ArrayList<Alert> alertList = new ArrayList<>();

    private ActivityResultLauncher<Intent> activityResultLauncher;

    private StopsAdapter stopsAdapter;

    private View selectedView;

    private AdView adView2;

    private TextView stopTitle;

    private static final String TAG = "StopActivity TAG";



    private final String adUnitId = "ca-app-pub-3940256099942544/6300978111";


    private Route route;

    private double lat;
    private double lon;


    //mine = ca-app-pub-9837773904596179/5474064201
    //ca-app-pub-3877624148995309/2572537268
    //ca-app-pub-3940256099942544/6300978111

    private final ArrayList<Stops> stopList = new ArrayList<>();

    private String t;
    private static SharedPreferences.Editor ctaCachedDataEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.bus_icon);


        MobileAds.initialize(this, initializationStatus ->
                Log.d(TAG, "onInitializationComplete:"));


        FrameLayout adViewContainer = findViewById(R.id.adView_container2);
        adView2 = new AdView(this);
        adView2.setAdUnitId(adUnitId);
        adViewContainer.addView(adView2);

        route = (Route) getIntent().getSerializableExtra("DIRECTION");
        t = getIntent().getStringExtra("OPEN_STOPS");
        lat = getIntent().getDoubleExtra("LAT",0.00);
        lon = getIntent().getDoubleExtra("LON",0.00);



        AlertVolley.downloadAlerts(this,route.getRouteNumber());

        LatLng myLocation = new LatLng(lat,lon);


        StopsVolley.downloadStops(this,t,route,myLocation);


        stopTitle = findViewById(R.id.stop_title);
        stopTitle.setText(t+ " Stops");
        int color = Color.parseColor(route.getRouteColor());
        stopTitle.setBackgroundColor(color);
        if (Color.luminance(color) < 0.25) {

            stopTitle.setTextColor(Color.WHITE);

        } else {
            stopTitle.setTextColor(Color.BLACK);
        }


        recyclerView = findViewById(R.id.recycler2);


        stopsAdapter = new StopsAdapter(this,stopList,route,lat,lon);
        recyclerView.setAdapter(stopsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setTitle("Route "+route.getRouteNumber()+" - "+route.getRouteName());

        adViewContainer.post(this::loadAdaptiveBanner);
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::doAction);


    }

    private  void doAction(ActivityResult result) {
    }

    private void loadAdaptiveBanner() {

        AdRequest adRequest = new AdRequest.Builder().build();

        AdSize adSize = getAdSize();
        // Set the adaptive ad size on the ad view.
        adView2.setAdSize(adSize);

        adView2.setAdListener(new MainActivity2.BannerAdListener());

        // Start loading the ad in the background.
        adView2.loadAd(adRequest);
    }




    class BannerAdListener extends AdListener {
        @Override
        public void onAdClosed() {
            super.onAdClosed();
            Log.d(TAG, "onAdClosed: ");
        }

        @Override
        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            super.onAdFailedToLoad(loadAdError);
            Log.d(TAG, "onAdFailedToLoad: " + loadAdError);
        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
            Log.d(TAG, "onAdOpened: ");
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            Log.d(TAG, "onAdLoaded: ");
        }

        @Override
        public void onAdClicked() {
            super.onAdClicked();
            Log.d(TAG, "onAdClicked: ");
        }

        @Override
        public void onAdImpression() {
            super.onAdImpression();
            Log.d(TAG, "onAdImpression: ");
        }
    }

    private AdSize getAdSize() {

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float adWidthPixels = adView2.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels;
        }

        float density = getResources().getDisplayMetrics().density;
        int adWidth = (int) (adWidthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);

    }


    public void acceptStops(ArrayList<Stops> stopsListIn,Route route){


        stopList.addAll(stopsListIn);


        int size = stopList.size();
        this.stopList.clear();
        stopsAdapter.notifyItemRangeRemoved(0, size);
        this.stopList.addAll(stopsListIn);
        stopsAdapter.notifyItemRangeChanged(0, stopList.size());



        stopsAdapter.notifyItemRangeChanged(0, stopList.size());

//        Toast.makeText(this,
//                route.getRouteName() + ": " + stopsListIn.size() + ": ",
//                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        selectedView = view;
        int pos = recyclerView.getChildLayoutPosition(view);
        Stops selectedStops = stopList.get(pos);

        thirdActivity(selectedStops);

        //PredictionsVolley.downloadPredictions(this,route,selectedStops,t);
        //DirectionsVolley.downloadDirections(this,selectedRoute.getRouteNumber(),selectedRoute);

    }



    public void thirdActivity(Stops stop) {

        Intent intent = new Intent(this,PredictionsActivity.class);

//        int position = binding.recycler.getChildLayoutPosition(v);

//        currentOfficial = officialArrayList.get(position);
        intent.putExtra("OPEN_STOPS",t);
        intent.putExtra("DIRECTION",route);
        intent.putExtra("STOPS",stop);
        activityResultLauncher.launch(intent);
    }



//    public String HTMLParser(String html) throws ParserConfigurationException, IOException, SAXException {

////        String url = "https://example.com"; // replace with your URL
//
//        // fetch HTML content from URL
////        String html = Jsoup.connect(url).get().html();
//
//        // parse HTML content into a Jsoup document
//        Document doc = Jsoup.parse(html);
//
////        StringBuilder result = new StringBuilder();
//        ArrayList<String> temp = new ArrayList<>();
//        // extract all text content from paragraphs
//        Elements paragraphs = doc.select("p");
//        doc.
//        for (Element paragraph : paragraphs) {
//            System.out.println(paragraph.text());
//            temp.add(paragraph.text());
//        }
//
//
//
//            return result.toString();
//    }

    public void createAlertDialog(Alert alert){


        AlertDialog.Builder builderS1 = new AlertDialog.Builder(this);
        builderS1.setTitle(alert.getHeadline());

        nightFlag =
                getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (nightFlag != Configuration.UI_MODE_NIGHT_YES) {
            builderS1.setIcon(R.drawable.bus_icon_black);

        }
        else{
            builderS1.setIcon(R.drawable.bus_icon);

        }


        WebView webView = new WebView(this);

        webView.loadDataWithBaseURL(null, alert.getData(), "text/html", "UTF-8", null);


//        webView.loadData(alert.getData(),"text/html", "UTF-8");
        webView.computeScroll();
        builderS1.setView(webView);



        builderS1.setPositiveButton("OK", (dialog1, p) -> {
            dialog1.dismiss();
        });
        AlertDialog dialog = builderS1.create();
        dialog.show();
    }

    public void acceptAlerts(ArrayList<Alert> alertLisIn) throws ParserConfigurationException, IOException, SAXException {

//        this.alertList.clear();
        SharedPreferences ctaCachedData = this.getApplicationContext().getSharedPreferences("CTA_PREFS", 0);
        ctaCachedDataEditor = ctaCachedData.edit();
        String cachedTime = ctaCachedData.getString("ALERTS", null);


//        ctaCachedDataEditor.putString("DIRECTION_TIME", formattedDate);

        if (cachedTime != null) {

            String[] existingAlerts = cachedTime.split(" ");
//            String str = "Hello, World!";
            StringBuilder sb = new StringBuilder(cachedTime);



//            ArrayList<String> alertIdList = new ArrayList<>();



            for (Alert alert : alertLisIn) {
                if(!cachedTime.contains(alert.getAlertId())) {

                    createAlertDialog(alert);
                    sb.append(alert.getAlertId()+" ");
                }
            }
            ctaCachedDataEditor.putString("ALERTS", sb.toString());

        } else{
            StringBuilder alertString= new StringBuilder();

            for (Alert alert : alertLisIn) {

                createAlertDialog(alert);
                alertString.append(alert.getAlertId()).append(" ");
            }
            ctaCachedDataEditor.putString("ALERTS", alertString.toString());
            ctaCachedDataEditor.apply();
        }


    }

}