package com.bitirme.gitbusters.borkinroads;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class RouteDetailsAdapter extends RecyclerView.Adapter<RouteDetailsAdapter.ViewHolder> {
    List<RouteDetails> routeDetailsList;

    public RouteDetailsAdapter(List<RouteDetails> list) {
        super();
        this.routeDetailsList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        new RouteDetailsAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.route_detail_row), view)

        View layout =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.route_detail_row, viewGroup, false);
        return new ViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        View layout = viewHolder.layout;
        RouteDetails routeDetails = routeDetailsList.get(i);

        TextView date = layout.findViewById(R.id.tv_date_data);
        TextView avgSpeed = layout.findViewById(R.id.tv_avg_speed_data);
        TextView avgMovingSpeed = layout.findViewById(R.id.tv_avg_mov_speed_data);
        TextView maxSpeed = layout.findViewById(R.id.tv_max_speed_data);
        TextView avgPace = layout.findViewById(R.id.tv_avg_pace_data);
        TextView avgMovingPace = layout.findViewById(R.id.tv_avg_mov_pace_data);
        TextView maxPace = layout.findViewById(R.id.tv_max_pace_data);
        TextView totalTime = layout.findViewById(R.id.tv_total_timing_data);
        TextView movingTime = layout.findViewById(R.id.tv_mov_timing_data);


        date.setText(routeDetails.getTime() + " " + routeDetails.getDate());
        avgSpeed.setText(String.valueOf(routeDetails.getAvgSpeed()));
        avgMovingSpeed.setText(String.valueOf(routeDetails.getMovingSpeed()));
        maxSpeed.setText(String.valueOf(routeDetails.getMaxSpeed()));
        avgPace.setText(String.valueOf(routeDetails.getAvgPace()));
        avgMovingPace.setText(String.valueOf(routeDetails.getMovingPace()));
        maxPace.setText(String.valueOf(routeDetails.getMaxPace()));
        totalTime.setText(String.valueOf(routeDetails.getTotalTime()));
        movingTime.setText(String.valueOf(routeDetails.getMovingTime()));


    }

    @Override
    public int getItemCount() {
        return routeDetailsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
        }

    }
}
