package com.srinivas.bustracker_cta;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PredictionsActivity extends AppCompatActivity implements View.OnClickListener{


    private RecyclerView recyclerView;
    private SwipeRefreshLayout swiper;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    private TextView selected_stop;
    private TextView time_field;
    private PreAdapter preAdapter;
    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");

    private AdView adView3;
    private final ArrayList<Prediction> predictionList = new ArrayList<>();
    private Route route;
    private String t;
    private Stops stop;
    private static final String TAG = "Predictions TAG";


    private final String adUnitId = "ca-app-pub-3940256099942544/6300978111";
    //mine = ca-app-pub-9837773904596179/5474064201
    //ca-app-pub-3877624148995309/2572537268
    //ca-app-pub-3940256099942544/6300978111


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predictions);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.bus_icon);

        MobileAds.initialize(this, initializationStatus ->
                Log.d(TAG, "onInitializationComplete:"));

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(this::doRefresh);

        recyclerView = findViewById(R.id.pre_recycler);
        String formattedDate = dateFormat.format(new Date());
//        System.out.println("Current time: " + formattedDate);


        FrameLayout adViewContainer = findViewById(R.id.adView_container3);
        adView3 = new AdView(this);
        adView3.setAdUnitId(adUnitId);
        adViewContainer.addView(adView3);


        route = (Route) getIntent().getSerializableExtra("DIRECTION");
        t = getIntent().getStringExtra("OPEN_STOPS");

        stop = (Stops) getIntent().getSerializableExtra("STOPS");

        PredictionsVolley.downloadPredictions(this,route,stop);

        selected_stop = findViewById(R.id.selected_stop);
        time_field = findViewById(R.id.time_field);


        selected_stop.setText(stop.getStpName()+" ("+t+")");
        time_field.setText(formattedDate);
        int color = Color.parseColor(route.getRouteColor());

        if (Color.luminance(color) < 0.25) {

            selected_stop.setTextColor(Color.WHITE);
            time_field.setTextColor(Color.WHITE);

        } else {
            selected_stop.setTextColor(Color.BLACK);
            time_field.setTextColor(Color.BLACK);

        }

        selected_stop.setBackgroundColor(color);
        time_field.setBackgroundColor(color);

        preAdapter = new PreAdapter(this,predictionList,route);
        recyclerView.setAdapter(preAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setTitle("Route "+route.getRouteNumber()+" - "+route.getRouteName());

        adViewContainer.post(this::loadAdaptiveBanner);
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::doAction);






    }

    private void doRefresh() {
        PredictionsVolley.downloadPredictions(this,route,stop);
        String formattedDate = dateFormat.format(new Date());

        time_field.setText(formattedDate);


        swiper.setRefreshing(false);
//        Toast.makeText(this, "refreshing", Toast.LENGTH_SHORT).show();

    }

    private  void doAction(ActivityResult result) {
    }

    private void loadAdaptiveBanner() {

        AdRequest adRequest = new AdRequest.Builder().build();

        AdSize adSize = getAdSize();
        // Set the adaptive ad size on the ad view.
        adView3.setAdSize(adSize);

        adView3.setAdListener(new PredictionsActivity.BannerAdListener());

        // Start loading the ad in the background.
        adView3.loadAd(adRequest);
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

        float adWidthPixels = adView3.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels;
        }

        float density = getResources().getDisplayMetrics().density;
        int adWidth = (int) (adWidthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);

    }

    public void acceptStops(ArrayList<Prediction> predictionArrayList, Route route,Stops stops){


        predictionList.addAll(predictionArrayList);


        int size = predictionList.size();
        this.predictionList.clear();
        preAdapter.notifyItemRangeRemoved(0, size);
        this.predictionList.addAll(predictionArrayList);
        preAdapter.notifyItemRangeChanged(0, predictionList.size());


//        Toast.makeText(this,
//                route.getRouteName() + ": " + predictionArrayList.size() + ": ",
//                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {

    }
}