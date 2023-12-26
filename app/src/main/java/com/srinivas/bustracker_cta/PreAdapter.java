package com.srinivas.bustracker_cta;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PreAdapter extends RecyclerView.Adapter<PreViewHolder>{

    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
    SimpleDateFormat outputFormat = new SimpleDateFormat("h.mm a");
    Date time = null; // Parse the time string to a Date object

    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");

    private PredictionsActivity predictionsActivity;
    private ArrayList<Prediction> predictionArrayList;

    private Route route;

    private Stops stops;

    public PreAdapter(PredictionsActivity predictionsActivity, ArrayList<Prediction> predictionArrayList,Route route) {
        this.predictionsActivity = predictionsActivity;
        this.predictionArrayList = predictionArrayList;
        this.route = route;
    }



    @NonNull
    @Override
    public PreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pre_entry,parent,false);

        itemView.setOnClickListener(predictionsActivity);

        return new PreViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull PreViewHolder holder, int position) {

        Prediction prediction = predictionArrayList.get(position);

        int color = Color.parseColor(route.getRouteColor());

        String formattedDate = dateFormat.format(new Date());
        System.out.println("Current time: " + formattedDate);


        try {
            time = inputFormat.parse(prediction.getPreTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String formattedTime = outputFormat.format(time);


        holder.destination_pt.setText("BUS #"+prediction.getDestinationPt());
        holder.rtdir_des.setText(prediction.getRouteDirection()+" to "+prediction.getDestination());
        holder.prdctdn.setText(formattedTime);
        holder.prdtm.setText("Due in "+prediction.getDueTime()+" mins at");

        if (Color.luminance(color) < 0.25) {



            holder.destination_pt.setTextColor(Color.WHITE);
            holder.rtdir_des.setTextColor(Color.WHITE);
            holder.rtdir_des.setTextColor(Color.WHITE);

            holder.rtdir_des.setTextColor(Color.WHITE);

        } else {

            holder.destination_pt.setTextColor(Color.BLACK);
            holder.rtdir_des.setTextColor(Color.BLACK);
            holder.rtdir_des.setTextColor(Color.BLACK);

            holder.rtdir_des.setTextColor(Color.BLACK);


        }

        holder.getConstraintLayout().setBackgroundColor(color);


    }

    @Override
    public int getItemCount() {
        return predictionArrayList.size();
    }
}
