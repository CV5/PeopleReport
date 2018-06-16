package com.peoplereport.peoplereport.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.peoplereport.peoplereport.ClassModels.LikesModels;
import com.peoplereport.peoplereport.R;

import java.util.ArrayList;

/**
 * creado por  Christian en la fecha 2018-06-11.
 */

public class LikesAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<LikesModels> likesArrayList;
    private OnItemClickListener mOnItemClickListener;



    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public LikesAdapter(Context context, ArrayList<LikesModels> likesArrayList, OnItemClickListener mOnItemClickListener) {
        this.context = context;
        this.likesArrayList = likesArrayList;
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.like_list_item, null);
        return new LikesHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LikesModels currentLike = likesArrayList.get(position);

        Glide.with(context).load(currentLike.getLikesPhotoUrl()).into(((LikesHolder) holder).image);


    }

    @Override
    public int getItemCount() {
        return likesArrayList.size();
    }


    public static class LikesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        private AdapterView.OnItemClickListener itemClickListener;


        LikesHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }


        @Override
        public void onClick(View v) {
        }

        public void setItemClickListener(AdapterView.OnItemClickListener ic) {
            this.itemClickListener = ic;

        }
    }
}
