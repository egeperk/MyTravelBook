package com.egeperk.mytravelbook.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.egeperk.mytravelbook.Location;
import com.egeperk.mytravelbook.databinding.RecyclerRowBinding;
import com.egeperk.mytravelbook.view.MapsActivity;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.LocationHolder> {

    List<Location> locationList;

    public Adapter(List<Location> locationList) {
        this.locationList = locationList;
    }

    @NonNull
    @Override
    public LocationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new LocationHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationHolder holder, int position) {

        holder.recyclerRowBinding.recyclerViewTextView.setText(locationList.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), MapsActivity.class);
                intent.putExtra("location",locationList.get(position));
                intent.putExtra("info","old");
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public class LocationHolder extends RecyclerView.ViewHolder {

        RecyclerRowBinding recyclerRowBinding;


        public LocationHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
        }
    }
    }


