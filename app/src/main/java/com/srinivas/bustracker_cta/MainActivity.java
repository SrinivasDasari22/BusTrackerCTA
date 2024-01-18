package com.srinivas.bustracker_cta;
#DS

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.srinivas.bustracker_cta.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private ActivityMainBinding binding;
    private static final int LOCATION_REQUEST = 111;

    private  static  int nightFlag;
    public LatLng myLocation;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    private static FusedLocationProviderClient fusedLocationClient;

    private View selectedView;
    private final ArrayList<Route> allRouteList = new ArrayList<>();
    private final ArrayList<Route> routeList = new ArrayList<>();
    private final ArrayList<Route> stopsList = new ArrayList<>();


    private final ArrayList<String> directionsList = new ArrayList<>();
    private RouteAdapter routeAdapter;
    public static SimpleDateFormat timeFormat =
            new SimpleDateFormat("MM-dd-yyyy-HH:mm", Locale.getDefault());;
    private AdView adView;

    private final String adUnitId = "ca-app-pub-3940256099942544/6300978111";
    private static final String TAG = "MainActivityTag";

    //mine = ca-app-pub-9837773904596179/5474064201
    //ca-app-pub-3877624148995309/2572537268
    //ca-app-pub-3940256099942544/6300978111

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();


//        actionBar.setDisplayUseLogoEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setLogo(R.drawable.bus_icon);

        nightFlag =
                getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightFlag != Configuration.UI_MODE_NIGHT_YES) {
            actionBar.setHomeAsUpIndicator(R.drawable.bus_icon);
        } else {
            actionBar.setHomeAsUpIndicator(R.drawable.bus_icon_black);
        }


        if (hasNetworkConnection()) {
            fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(this);
            getLocation();


//            if(checkPermission()) {


            MobileAds.initialize(this, initializationStatus ->
                    Log.d(TAG, "onInitializationComplete:"));

            FrameLayout adViewContainer = findViewById(R.id.adView_container);
            adView = new AdView(this);
            adView.setAdUnitId(adUnitId);
            adViewContainer.addView(adView);


            routeList.clear();

//        RoutesVolley routesVolley = new RoutesVolley();
//
//        Thread thread = new Thread(routesVolley);
//        thread.start();
//        routesVolley.run(this);


            RoutesVolley.downloadRoutes(this);

//        try {
//            thread.join();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }


//        RoutesVolley.downloadRoutes(this);
            System.out.println("ds:" + routeList.size());

            binding.recycler.setLayoutManager(new LinearLayoutManager(this));

            routeAdapter = new RouteAdapter(this, routeList);
            binding.recycler.setAdapter(routeAdapter);

//        EditTextWithClear etwc = findViewById(R.id.edittextwithClear);
//        EditText et = findViewById(R.id.editText);


            binding.edittextwithClear.editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    ArrayList<Route> temp = new ArrayList<>();
                    for (Route r : allRouteList) {
                        if (r.getRouteName().toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                                r.getRouteNumber().toLowerCase().contains(charSequence.toString().toLowerCase()))
                            temp.add(r);
                    }
                    int size = routeList.size();
                    MainActivity.this.routeList.clear();
                    routeAdapter.notifyItemRangeRemoved(0, size);

                    MainActivity.this.routeList.addAll(temp);
                    routeAdapter.notifyItemRangeChanged(0, routeList.size());
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            adViewContainer.post(this::loadAdaptiveBanner);
            activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::doAction);
//            }
//        else{
//
//                    AlertDialog.Builder builderS2 = new AlertDialog.Builder(this);
//                    builderS2.setPositiveButton("ok", (dialog, which) -> {
//                        dialog.dismiss();
//                    });
//                    builderS2.setTitle("Bus Tracker - CTA");
//                    builderS2.setMessage("Unable to determine device location. Please allow this app to access device location.");
//                    builderS2.show();
//
//            }
        } else {


            AlertDialog.Builder builderS1 = new AlertDialog.Builder(this);
            builderS1.setTitle("Bus Tracker - CTA");
            builderS1.setMessage("Unable to contact Bus Tracker API due to network problem. Please check your network connection.");
            builderS1.setPositiveButton("OK", (dialog1, p) -> {
                dialog1.dismiss();
            });
            builderS1.show();


//        }


        }
    }

    private void getLocation() throws RuntimeException {

//        if (ContextCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
//            return;
//        }
        if (checkPermission()) {


        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations this can be null.
                        // Add a marker at current location
                    if (location != null) {
                        myLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    }

                })
                .addOnFailureListener(
                        e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }





    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        getLocation();
                    } else {


                            AlertDialog.Builder builderS2 = new AlertDialog.Builder(this);
                            builderS2.setPositiveButton("ok", (dialog, which) -> {
                                dialog.dismiss();
                                onBackPressed();
                            });
                            builderS2.setTitle("Bus Tracker - CTA");
                            builderS2.setMessage("Unable to determine device location. Please allow this app to access device location.");
                            builderS2.show();


                    }
                }
            }
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu){
        getMenuInflater().inflate(R.menu.app_info,menu);
        return true;

    }



    @SuppressLint("ResourceType")
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() ==R.id.app_info) {


            TextView info = new TextView(this);
            info.setText("https://www.transitchicago.com/developers/bustracker/");
            info.setGravity(Gravity.CENTER);
            Linkify.addLinks(info, Linkify.ALL);


            AlertDialog.Builder builders = new AlertDialog.Builder(this);
            builders.setView(info);
            builders.setTitle("Bus Tracker - CTA");
            builders.setIcon(R.drawable.logo);

            builders.setMessage("CTA Bus Tracker data provided by Chicago Transit Authority");
//            builders.setIconAttribute(R.drawable.bus_icon);
            builders.show();

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void doAction(ActivityResult result) {
    }

    private void loadAdaptiveBanner() {

        AdRequest adRequest = new AdRequest.Builder().build();

        AdSize adSize = getAdSize();
        // Set the adaptive ad size on the ad view.
        adView.setAdSize(adSize);

        adView.setAdListener(new BannerAdListener());

        // Start loading the ad in the background.
        adView.loadAd(adRequest);
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

        float adWidthPixels = adView.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels;
        }

        float density = getResources().getDisplayMetrics().density;
        int adWidth = (int) (adWidthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);

    }

    private boolean hasNetworkConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    public void acceptDirections(ArrayList<String> directionListIn,Route route) {

        PopupMenu popupMenu = new PopupMenu(this, selectedView);

        for (String dir : directionListIn){
            popupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, dir);
        }

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            CharSequence dir = menuItem.getTitle();
            String t = dir.toString();


            secondActivity(t,route);
//            Toast.makeText(this,
//                    route.getRouteName() + ": " + menuItem.getTitle() + ": " + dir,
//                    Toast.LENGTH_LONG).show();


            return true;
        });
        popupMenu.show();


    }

    public void secondActivity(String t, Route route) {

        Intent intent = new Intent(this,MainActivity2.class);

//        int position = binding.recycler.getChildLayoutPosition(v);

//        currentOfficial = officialArrayList.get(position);
        intent.putExtra("OPEN_STOPS",t);
        intent.putExtra("DIRECTION",route);
        intent.putExtra("LAT",myLocation.latitude);
        intent.putExtra("LON",myLocation.longitude);
        activityResultLauncher.launch(intent);
    }

    public void acceptRoutes(ArrayList<Route> routeListIn) {
        int size = routeList.size();
        this.routeList.clear();
        routeAdapter.notifyItemRangeRemoved(0, size);
        this.routeList.addAll(routeListIn);
        routeAdapter.notifyItemRangeChanged(0, routeList.size());

        this.allRouteList.clear();
        this.allRouteList.addAll(this.routeList);



        routeAdapter.notifyItemRangeChanged(0, routeList.size());
    }

    public void clearSearch(View v) {
        binding.edittextwithClear.editText.setText("");
    }

    @Override
    public void onClick(View view) {
        selectedView = view;
        int pos = binding.recycler.getChildLayoutPosition(view);
        Route selectedRoute = routeList.get(pos);
        DirectionsVolley.downloadDirections(this,selectedRoute.getRouteNumber(),selectedRoute);

    }




}
