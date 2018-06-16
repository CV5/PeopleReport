package com.peoplereport.peoplereport.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.peoplereport.peoplereport.ClassModels.LikesModels;
import com.peoplereport.peoplereport.ClassModels.MyUsers;
import com.peoplereport.peoplereport.ClassModels.Reports;
import com.peoplereport.peoplereport.MainActivity;
import com.peoplereport.peoplereport.R;
import com.peoplereport.peoplereport.ReportsDetailsActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import spencerstudios.com.bungeelib.Bungee;

/**
 * creado por  Christian en la fecha 2018-05-24.
 */

public class ReportsAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<Reports> reportsArrayList;
    private Gson gson = new Gson();
    private MainActivity mainActivity = new MainActivity();
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private LikesModels islike = null;


    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public ReportsAdapter(Context context, ArrayList<Reports> reportsArrayList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.reportsArrayList = reportsArrayList;
        OnItemClickListener onItemClickListener1 = onItemClickListener;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.reports_list_item, null);
        return new Holder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final Reports currentReports = reportsArrayList.get(position);
        int commentsCount = 0;
        int likesCount = 0;
        Calendar c = Calendar.getInstance();

        long diff = c.getTime().getTime() - currentReports.getPosttime();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        if (seconds >= 60) {
            if (minutes >= 60) {
                ((Holder) holder).posttime.setText(hours + " hours ago");
            } else {
                ((Holder) holder).posttime.setText(minutes + " minutes ago");
            }
        }else {
            ((Holder)holder).posttime.setText(seconds+" seconds ago");
        }


        if (currentReports.getComments() != null) {
            commentsCount = currentReports.getComments().size();
        }
        if (currentReports.getLikes() != null) {
            likesCount = currentReports.getLikes().size();
        }



        String myLogin = context.getSharedPreferences("com.peoplereport.org", 0).getString("MyLoginUser", "");
        final MyUsers myLoginUser = gson.fromJson(myLogin, MyUsers.class);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        if (currentReports.getLikes() != null) {
            islike = currentReports.getLikes().get(myLoginUser.getId());
            if (islike != null) {
                ((Holder) holder).likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_fill_red_vector, 0, 0, 0);

            } else {
                ((Holder) holder).likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_empty_vector, 0, 0, 0);
            }
        }

        ((Holder) holder).tituloTextView.setText(currentReports.getTitulo());
        ((Holder) holder).Comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowPopup(v);
            }
        });


        ((Holder) holder).foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReportsDetailsActivity.class);
                String reports = gson.toJson(currentReports);
                intent.putExtra("report", reports);
                mainActivity
                        .addViews(currentReports.getPostid()
                                , currentReports.getDate()
                                , mDatabaseReference
                                , currentReports.getViews());
                context.startActivity(intent);
                Bungee.slideUp(context);

            }
        });

        ((Holder) holder).Description.setText(currentReports.getDescripcion());
        Glide.with(context).load(currentReports.getPictureProfile()).into(((Holder) holder).Profilefoto);
        Glide.with(context).load(currentReports.getFoto()).into(((Holder) holder).foto);


        ((Holder) holder).likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentReports.getLikes() != null) {
                    islike = currentReports.getLikes().get(myLoginUser.getId());
                }
                if (islike != null) {
                    ((Holder) holder).likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_empty_vector, 0, 0, 0);
                    mainActivity.unLikeAction(currentReports.getPostid(), currentReports.getDate(), myLoginUser.getId(), mDatabaseReference);

                } else {
                    ((Holder) holder).likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_fill_red_vector, 0, 0, 0);
                    mainActivity.likeAction(currentReports.getPostid(), currentReports.getDate(), myLoginUser.getId(), myLoginUser.getPictureProfile(), mDatabaseReference);
                }


            }
        });

//
//        if () {
//            ((Holder) holder).likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_fill_red_vector, 0, 0, 0);
//        }else {
//            ((Holder) holder).likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_empty_vector, 0, 0, 0);
//
//
//        }


        ((Holder) holder).Comments.setText(commentsCount + " Comments");


        ((Holder) holder).mViews.setText(currentReports.getViews() + " Views");
        ((Holder) holder).likes.setText(likesCount + " Likes");


//        if (currentReports.getPostid() != null) {
//            mDatabaseReference.child("Reports")
//                    .child(dateString(mainActivity.calendar.getTime().getTime()))
//                    .child(currentReports.getPostid())
//                    .child("likes")
//                    .child(myLoginUser.getId()).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    islike = dataSnapshot.getChildrenCount();
//
//
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }


    }

    @Override
    public int getItemCount() {
        return reportsArrayList.size();
    }


    public static class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView foto;
        CircleImageView Profilefoto;
        TextView tituloTextView;
        TextView Comments;
        TextView mViews;
        TextView likes;
        TextView posttime;
        TextView Description;
        CardView cardview;
        private AdapterView.OnItemClickListener itemClickListener;


        Holder(View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.list_avatar);
            Profilefoto = itemView.findViewById(R.id.avatar_image);
            tituloTextView = itemView.findViewById(R.id.list_title);
            Comments = itemView.findViewById(R.id.comment_body);
            likes = itemView.findViewById(R.id.likes);
            posttime = itemView.findViewById(R.id.posttime);
            mViews = itemView.findViewById(R.id.mViews);
            Description = itemView.findViewById(R.id.descripcion);
            cardview = itemView.findViewById(R.id.cardview);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
        }

        public void setItemClickListener(AdapterView.OnItemClickListener ic) {
            this.itemClickListener = ic;

        }
    }

    private String dateString(Long i) {
        DateFormat dateFormat = new SimpleDateFormat("dMyyyy");
        return dateFormat.format(i);
    }


    public void onShowPopup(View v) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        final View inflatedView = layoutInflater.inflate(R.layout.comments_layout, null, false);
        // find the ListView in the popup layout
//        RecyclerView rvComments = inflatedView.findViewById(R.id.commentRecycleViewPop);
//        rvComments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//
//
//        rvComments.setAdapter(adapterComments);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        // get device size
        final Point size = new Point();
        display.getSize(size);
        // set height depends on the device size
        // set height depends on the device size
        PopupWindow popWindow = new PopupWindow(v, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
        // set a background drawable with rounders corners
        popWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popup_bg));
        // make it focusable to show the keyboard to enter in `EditText`
        popWindow.setFocusable(true);


        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        popWindow.update();
        // make it outside touchable to dismiss the popup window
        popWindow.setOutsideTouchable(true);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(v, Gravity.CENTER, 0, 100);
    }


}
