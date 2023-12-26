package com.srinivas.bustracker_cta;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class RouteViewHolder extends RecyclerView.ViewHolder {

    ConstraintLayout constraintLayout;
    TextView routeNum;
    TextView routeName;

    public RouteViewHolder(@NonNull View itemView) {
        super(itemView);
        constraintLayout = itemView.findViewById(R.id.stopLayout);
        routeNum = itemView.findViewById(R.id.routeNumber);
        routeName = itemView.findViewById(R.id.routeName);
    }

    public ConstraintLayout getConstraintLayout() {
        return constraintLayout;
    }

    public TextView getRouteNum() {
        return routeNum;
    }

    public TextView getRouteName() {
        return routeName;
    }
}
