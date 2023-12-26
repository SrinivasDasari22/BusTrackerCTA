package com.srinivas.bustracker_cta;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class StopsViewHolder extends RecyclerView.ViewHolder{
    TextView stp_name;
    TextView distance;
    ConstraintLayout constraintLayout;



    public StopsViewHolder(@NonNull View itemView) {
        super(itemView);

        constraintLayout = itemView.findViewById(R.id.stop_entry);

        stp_name = itemView.findViewById(R.id.stp_name);
        distance = itemView.findViewById(R.id.distance);

    }

    public ConstraintLayout getConstraintLayout() {
        return constraintLayout;
    }
}
