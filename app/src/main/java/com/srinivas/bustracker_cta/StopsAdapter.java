package com.srinivas.bustracker_cta;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

public class StopsAdapter extends RecyclerView.Adapter<StopsViewHolder>{

    private MainActivity2 mainActivity2;
    private ArrayList<Stops> stopsArrayList;

    private Route route;



    public StopsAdapter(MainActivity2 mainActivity2, ArrayList<Stops> stopsArrayList,Route route,double lat,double lon) {
        this.mainActivity2 = mainActivity2;
        this.stopsArrayList = stopsArrayList;
        this.route = route;
    }

    @NonNull
    @Override
    public StopsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stop_entry,parent,false);

        itemView.setOnClickListener(mainActivity2);

        return new StopsViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull StopsViewHolder holder, int position) {


        Stops stops = stopsArrayList.get(position);

        holder.stp_name.setText(stops.getStpName());
        long distanceApp = Math.round(stops.getDistance());
        String dis= Long.toString(distanceApp);
        holder.distance.setText(dis+" m from our location");

        int color = Color.parseColor(route.getRouteColor());

        if (Color.luminance(color) < 0.25) {



            holder.stp_name.setTextColor(Color.WHITE);
            holder.distance.setTextColor(Color.WHITE);
        } else {

            holder.stp_name.setTextColor(Color.BLACK);
            holder.distance.setTextColor(Color.BLACK);


        }

        holder.getConstraintLayout().setBackgroundColor(color);

    }

    @Override
    public int getItemCount() {
        return stopsArrayList.size();
    }
}
