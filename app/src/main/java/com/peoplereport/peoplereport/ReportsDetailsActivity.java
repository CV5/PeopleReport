package com.peoplereport.peoplereport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.peoplereport.peoplereport.Adapters.CommentsAdapter;
import com.peoplereport.peoplereport.Adapters.LikesAdapter;
import com.peoplereport.peoplereport.ClassModels.CommentsModels;
import com.peoplereport.peoplereport.ClassModels.LikesModels;
import com.peoplereport.peoplereport.ClassModels.MyUsers;
import com.peoplereport.peoplereport.ClassModels.Reports;

import java.util.ArrayList;
import java.util.Collections;

import spencerstudios.com.bungeelib.Bungee;

public class ReportsDetailsActivity extends AppCompatActivity {
    Gson gson = new Gson();
    ImageView imageView;
    ImageView imageViewProfile;
    TextView Title;
    TextView username;
    public DatabaseReference mDatabaseReference;
    public FirebaseDatabase mFirebaseDatabase;
    TextView likes;
    TextView Commentscount;
    TextView clickToComment;
    private RecyclerView.Adapter adapterLiks;
    private ArrayList<LikesModels> likesArrayList = new ArrayList<>();
    private RecyclerView.Adapter adapterComments;
    private RecyclerView.Adapter adapterComments1;
    private ArrayList<CommentsModels> commentsArrayList = new ArrayList<>();
    private ArrayList<CommentsModels> commentsArrayList1 = new ArrayList<>();
    TextView Description;
    ImageButton shareButton;
    MainActivity mainActivity;
    SharedPreferences mPrefs;
    LikesModels islike = null;
    String comments;
    Reports reports;
    MyUsers myLoginUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = new MainActivity();
        setContentView(R.layout.activity_reports_details);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        imageView = findViewById(R.id.image);
        imageViewProfile = findViewById(R.id.details_userPerfil);
        Title = findViewById(R.id.textViewTitle);
        clickToComment = findViewById(R.id.clickToComment);
        Commentscount = findViewById(R.id.commentCount);
        username = findViewById(R.id.username);
        likes = findViewById(R.id.likes);
        shareButton = findViewById(R.id.shareButton);
        Description = findViewById(R.id.textViewDescription);
        RecyclerView rvLikes = findViewById(R.id.likesRecycleView);
        RecyclerView rvComments = findViewById(R.id.commentRecycleViewPop);

        adapterLiks = new LikesAdapter(this, likesArrayList, new LikesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
        adapterComments = new CommentsAdapter(this, commentsArrayList);


        adapterComments1 = new CommentsAdapter(this, commentsArrayList1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPrefs = getSharedPreferences("com.peoplereport.org", MODE_PRIVATE);
        String myLogin = mPrefs.getString("MyLoginUser", "");
        myLoginUser = gson.fromJson(myLogin, MyUsers.class);

        String sReport = getIntent().getStringExtra("report");
        reports = gson.fromJson(sReport, Reports.class);
        if (reports.getLikes() != null) {
            islike = reports.getLikes().get(myLoginUser.getId());
            if (islike != null) {
                likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_fill_red_vector, 0, 0, 0);
            } else {
                likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_empty_vector, 0, 0, 0);
            }
        }
        rvLikes.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));
        rvComments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        clickToComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShowPopup(v);
            }
        });


        onPostChildAdded(reports.getDate(), reports.getPostid());
        onLikeChildAdded(reports.getDate(), reports.getPostid());
        onCommentsChildAdded(reports.getDate(), reports.getPostid());


        rvLikes.setAdapter(adapterLiks);
        rvComments.setAdapter(adapterComments1);

        Glide.with(this).load(reports.getFoto()).into(imageView);
        Glide.with(this).load(reports.getPictureProfile()).into(imageViewProfile);
        Title.setText(reports.getTitulo());
        username.setText(reports.getUsuario());
        Description.setText(reports.getDescripcion());

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, reports.getTitulo());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, reports.getDescripcion());
                startActivity(Intent.createChooser(sharingIntent, "Share with"));
            }
        });

        if (reports.getLikes() != null) {
            likes.setText("" + reports.getLikes().size());
        } else {
            likes.setText("0");
        }

        if (reports.getComments() != null) {
            Commentscount.setText("" + reports.getComments().size());
        } else {
            likes.setText("0");
        }

        likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (reports.getLikes() != null) {
                    islike = reports.getLikes().get(myLoginUser.getId());
                    if (islike != null) {
                        likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_empty_vector, 0, 0, 0);
                        mainActivity.unLikeAction(reports.getPostid(), reports.getDate(), myLoginUser.getId(), mDatabaseReference);
                        likesArrayList.clear();

                    } else {
                        likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_fill_red_vector, 0, 0, 0);
                        mainActivity.likeAction(reports.getPostid(), reports.getDate(), myLoginUser.getId(), myLoginUser.getPictureProfile(), mDatabaseReference);
                    }
                } else {
                    likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart_fill_red_vector, 0, 0, 0);
                    mainActivity.likeAction(reports.getPostid(), reports.getDate(), myLoginUser.getId(), myLoginUser.getPictureProfile(), mDatabaseReference);

                }


            }
        });

    }

    public void onLikeChildAdded(String fecha, String posid) {
        mDatabaseReference.child("Reports/").child(fecha).child(posid).child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                likesArrayList.clear();
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    LikesModels likesModels = children.getValue(LikesModels.class);
                    likesArrayList.add(likesModels);
                    Collections.reverse(likesArrayList);

                    adapterLiks.notifyDataSetChanged();
                }
                likes.setText("" + likesArrayList.size());
                Log.d("saber", "" + likesArrayList.size());
//                if (likesArrayList.size() == 0) {
//                    likesArrayList.clear();
//                    noitem.setVisibility(View.VISIBLE);
//
//                } else {
//
//                    noitem.setVisibility(View.GONE);
//
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void onCommentsChildAdded(String fecha, String posid) {
        mDatabaseReference.child("Reports/").child(fecha).child(posid).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentsArrayList.clear();
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    CommentsModels commentsModels = children.getValue(CommentsModels.class);
                    commentsArrayList.add(commentsModels);
//                    Collections.reverse(commentsArrayList);
//                    Commentscount.setText("" + commentsArrayList.size());
                    Commentscount.setText(getString(R.string.comments_count,commentsArrayList.size()));
                    adapterComments.notifyDataSetChanged();
                }
                if (commentsArrayList.size() > 0) {
                    commentsArrayList1.add(commentsArrayList.get(commentsArrayList.size() - 1));
                    adapterComments1.notifyDataSetChanged();
//                if (likesArrayList.size() == 0) {
//                    likesArrayList.clear();
//                    noitem.setVisibility(View.VISIBLE);
//
//                } else {
//
//                    noitem.setVisibility(View.GONE);
//
//                }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void onPostChildAdded(String fecha, String posid) {

        Query queryRef = mDatabaseReference.child("Reports/").child(fecha).orderByChild("postid").equalTo(posid);

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    reports = children.getValue(Reports.class);


                }
//                if (likesArrayList.size() == 0) {
//                    likesArrayList.clear();
//                    noitem.setVisibility(View.VISIBLE);
//
//                } else {
//
//                    noitem.setVisibility(View.GONE);
//
//                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideDown(this); //fire the slide left animation
    }

    public void onShowPopup(View v) {

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout

        final View inflatedView = layoutInflater.inflate(R.layout.comments_layout, null, false);
        // find the ListView in the popup layout
        final ImageButton publishCommentButton;
        final EditText muCommentsBox;
        final RecyclerView rvComments = inflatedView.findViewById(R.id.commentRecycleViewPop);
        publishCommentButton = inflatedView.findViewById(R.id.publishCommentButton);
        muCommentsBox = inflatedView.findViewById(R.id.myCommentsBox);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        rvComments.setLayoutManager(layoutManager);
        rvComments.setAdapter(adapterComments);


        muCommentsBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    comments = s.toString();
                    publishCommentButton.setVisibility(View.VISIBLE);
                } else {
                    publishCommentButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        publishCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muCommentsBox.setText("");
                mDatabaseReference.child("Reports/")
                        .child(reports.getDate())
                        .child(reports.getPostid())
                        .child("comments")
                        .push().setValue(new CommentsModels(
                        myLoginUser.getId()
                        , myLoginUser.getUsername()
                        , comments
                        , myLoginUser.getPictureProfile()

                ));
            }
        });


        // get device size
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        // set height depends on the device size
        PopupWindow popWindow = new PopupWindow(inflatedView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
        // set a background drawable with rounders corners
        popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_bg));
        // make it focusable to show the keyboard to enter in `EditText`
        popWindow.setFocusable(true);


        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        popWindow.update();
        // make it outside touchable to dismiss the popup window
        popWindow.setOutsideTouchable(true);

        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        Bungee.slideDown(this); //fire the slide left animation

    }


}
