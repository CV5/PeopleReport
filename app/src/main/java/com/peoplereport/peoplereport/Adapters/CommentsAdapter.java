package com.peoplereport.peoplereport.Adapters;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.peoplereport.peoplereport.ClassModels.CommentsModels;
import com.peoplereport.peoplereport.R;
import com.peoplereport.peoplereport.ReportsDetailsActivity;

import java.util.ArrayList;

import spencerstudios.com.bungeelib.Bungee;

/**
 * creado por  Christian en la fecha 2018-06-12.
 */

public class CommentsAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<CommentsModels> commentsArrayList;
    ReportsDetailsActivity reportsDetailsActivity = new ReportsDetailsActivity();

    public CommentsAdapter(Context context, ArrayList<CommentsModels> commentsArrayList) {
        this.context = context;
        this.commentsArrayList = commentsArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comments_item_list,null);
        return new CommentsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommentsModels currentComment = commentsArrayList.get(position);

        ((CommentsHolder)holder).commentsUsername.setText(currentComment.getCommentsName());
        ((CommentsHolder)holder).commentsBody.setText(currentComment.getCommentsBody());
        Glide.with(context).load(currentComment.getCommentsPictureProfile()).into(((CommentsHolder)holder).commentsPicture);


    }

    @Override
    public int getItemCount() {
        return commentsArrayList.size();
    }
    public static class CommentsHolder extends RecyclerView.ViewHolder {
        TextView commentsUsername;
        TextView commentsBody;
        ImageView commentsPicture;


        CommentsHolder(View itemView) {
            super(itemView);
            commentsUsername = itemView.findViewById(R.id.comments_username);
            commentsBody = itemView.findViewById(R.id.comment_body);
            commentsPicture = itemView.findViewById(R.id.userPerfil_comments);

        }
    }



}
