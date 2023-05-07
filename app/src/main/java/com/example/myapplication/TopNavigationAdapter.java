package com.example.myapplication;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TopNavigationAdapter extends RecyclerView.Adapter<TopNavigationAdapter.MyViewHolder> {
    private Context context;
    private List<String> manufactures;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onLongItemClick(View view, int position);
    }

    GestureDetector mGestureDetector;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView NameMan;
        ImageView imageMan;

        MyViewHolder(View view) {
            super(view);
            NameMan = view.findViewById(R.id.NameMan);
            imageMan = view.findViewById(R.id.imageMan);


        }
    }

    public TopNavigationAdapter(List<String> manufacturesList) {
        this.manufactures = manufacturesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.man_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String str = manufactures.get(position);
        holder.NameMan.setText(str);

        if (str == "BMW")
            holder.imageMan.setImageResource(R.drawable.bmw);
        else if (str == "Volvo")
            holder.imageMan.setImageResource(R.drawable.volvo);
        else if (str == "Nissan")
            holder.imageMan.setImageResource(R.drawable.nissan);
        else if (str == "Subaru")
            holder.imageMan.setImageResource(R.drawable.subaru);
        else if (str == "volkswagen")
            holder.imageMan.setImageResource(R.drawable.volkswagen);
        else if (str == "Toyota")
            holder.imageMan.setImageResource(R.drawable.toyota);
        else if (str == "Infinity")
            holder.imageMan.setImageResource(R.drawable.infiniti);
        else if (str == "Jeep")
            holder.imageMan.setImageResource(R.drawable.jeep);
        else if (str == "Mustang")
            holder.imageMan.setImageResource(R.drawable.mustang);
        else if (str == "Mercedes")
            holder.imageMan.setImageResource(R.drawable.mercedes);
        else if (str == "Honda")
            holder.imageMan.setImageResource(R.drawable.honda);
        else
            holder.imageMan.setImageResource(R.mipmap.ic_launcher);
    }

    @Override
    public int getItemCount() {
        return manufactures.size();
    }

    public String getName(int position) {
        return manufactures.get(position);
    }
}
