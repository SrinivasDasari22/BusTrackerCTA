package com.srinivas.bustracker_cta;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RouteAdapter extends RecyclerView.Adapter<RouteViewHolder>{

    private final ArrayList<Route> routeList;
    private final MainActivity mainActivity;

    public RouteAdapter(MainActivity mainActivity, ArrayList<Route> routeList) {
        this.mainActivity = mainActivity;
        this.routeList = routeList;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_entry, parent, false);
        view.setOnClickListener(mainActivity);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        Route route = routeList.get(position);
        holder.getRouteNum().setText(route.getRouteNumber());
        holder.getRouteName().setText(route.getRouteName());

        int color = Color.parseColor(route.getRouteColor());

        if (Color.luminance(color) < 0.25) {
            holder.getRouteNum().setTextColor(Color.WHITE);
            holder.getRouteName().setTextColor(Color.WHITE);
        } else {
            holder.getRouteNum().setTextColor(Color.BLACK);
            holder.getRouteName().setTextColor(Color.BLACK);
        }

        holder.getConstraintLayout().setBackgroundColor(color);

    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }
}
