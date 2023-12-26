package com.srinivas.bustracker_cta;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class PreViewHolder extends RecyclerView.ViewHolder {
    TextView destination_pt;

    TextView rtdir_des;
    TextView prdtm;
    TextView prdctdn;
    ConstraintLayout constraintLayout;


    public PreViewHolder(@NonNull View itemView) {
        super(itemView);

        constraintLayout = itemView.findViewById(R.id.pre_entry);
        destination_pt = itemView.findViewById(R.id.destination_pt);
        rtdir_des = itemView.findViewById(R.id.direction_destination);
        prdtm = itemView.findViewById(R.id.pre_time);
        prdctdn = itemView.findViewById(R.id.due_time);
    }

    public ConstraintLayout getConstraintLayout() {
        return constraintLayout;
    }

}
