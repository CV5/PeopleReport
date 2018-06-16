package com.peoplereport.peoplereport;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.peoplereport.peoplereport.Adapters.ReportsAdapter;
import com.peoplereport.peoplereport.ClassModels.LikesModels;
import com.peoplereport.peoplereport.ClassModels.MyUsers;
import com.peoplereport.peoplereport.ClassModels.Reports;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final int RC_SIGN_IN = 22;
    public DatabaseReference mDatabaseReference;
    public FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private RecyclerView.Adapter adapter;
    private ArrayList<Reports> reportsArrayList = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    int buttonDate = 0;
    long dia;
    TextView fecha;
    public FirebaseUser user;
    Gson gson = new Gson();
    TextView noitem;
    Intent intent;
    ImageButton derecha;
    ImageButton izquierda;
    SharedPreferences mPrefs;
    SharedPreferences.Editor prefsEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPrefs = getSharedPreferences("com.peoplereport.org", MODE_PRIVATE);
        prefsEditor = mPrefs.edit();
        if (mFirebaseDatabase == null) {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseDatabase.setPersistenceEnabled(true);
        }



        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();


        RecyclerView rv = findViewById(R.id.rv);
        noitem = findViewById(R.id.noItem);
        FloatingActionButton fab = findViewById(R.id.boton_publicar);
        fecha = findViewById(R.id.DateAppBarTextView);
        derecha = findViewById(R.id.forwardButton);
        izquierda = findViewById(R.id.backButton);
        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddReportActivity.class);
                startActivity(intent);
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        intent = new Intent(MainActivity.this, PeopleReportService.class);


        intent.setAction("Execute");
        startService(intent);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(PeopleReportService.MY_SERVICE));

        dia = calendar.getTime().getTime();
        onChildAdded(dateString(dia));
        fecha.setText(getDateString(dia));
        setTodayText();
        adapter = new ReportsAdapter(this, reportsArrayList, new ReportsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }
        });
        rv.setAdapter(adapter);

        derecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diaPaLante();
            }
        });
        izquierda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diaPatra();
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = mFirebaseAuth.getCurrentUser();
                if (user != null) {

                    mPrefs = getSharedPreferences("com.peoplereport.org", MODE_PRIVATE);
                    Query queryRef = mDatabaseReference.child("Users").orderByChild("id").equalTo(user.getUid());
                    queryRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot children : dataSnapshot.getChildren()) {
                                prefsEditor.clear().apply();
                                MyUsers myUsers = children.getValue(MyUsers.class);
                                String myLoginUsers = gson.toJson(myUsers);
                                prefsEditor.putString("MyLoginUser", myLoginUsers);
                                prefsEditor.apply();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });


                    FirebaseUserMetadata metadata = mFirebaseAuth.getCurrentUser().getMetadata();
                    assert metadata != null;
                    if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
                        // The user is new, show them a fancy intro screen!
                        String id = mFirebaseAuth.getCurrentUser().getUid();
                        if (user != null) {
                            mDatabaseReference.child("Users").child(id).setValue(new MyUsers(user.getDisplayName(), user.getEmail()
                                    , id, user.getPhotoUrl().toString(), "https://firebasestorage.googleapis.com/v0/b/people-report.appspot.com/o/covers%2Fcover.jpg?alt=media&token=a58fc79a-e9eb-42a9-a723-fc7ddf22b47f"));

                        }
                    }


                } else { // not signed in
                    prefsEditor = mPrefs.edit();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }

        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this,"Bienvenido",Toast.LENGTH_LONG).show();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "No Login", Toast.LENGTH_LONG).show();
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "Network Problems", Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(this, "Unknonw Error", Toast.LENGTH_LONG).show();
                Log.e("noticias", "Sign-in error: ", response.getError());
            }
        }


    }

    public void onChildAdded(String fecha) {
        mDatabaseReference.child("Reports/").child(fecha).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reportsArrayList.clear();
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    Reports reports = children.getValue(Reports.class);
                    reportsArrayList.add(reports);
                    Collections.reverse(reportsArrayList);
                    adapter.notifyDataSetChanged();

                }
                if (reportsArrayList.size() == 0) {
                    reportsArrayList.clear();
                    noitem.setVisibility(View.VISIBLE);

                } else {

                    noitem.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void diaPatra() {
        reportsArrayList.clear();
        adapter.notifyDataSetChanged();
        long dia = yesterday().getTime();
        getYesterdayDateString(dia);
        fecha.setText("" + getDateString(dia));
        onChildAdded(dateString(dia));
        setTodayText();
        setYesterdayText();
        derecha.setEnabled(true);


    }

    private void diaPaLante() {
        reportsArrayList.clear();
        adapter.notifyDataSetChanged();
        long dia = tomorrow().getTime();
        getYesterdayDateString(dia);
        fecha.setText("" + getDateString(dia));
        onChildAdded(dateString(dia));
        setTodayText();

        setYesterdayText();


    }


    private Date yesterday() {
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    private Date tomorrow() {
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }

    private void getYesterdayDateString(Long i) {
        DateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM , yyyy", new Locale("es", "ES"));
        dateFormat.format(i);
    }

    private String getDateString(Long i) {
        DateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM", new Locale("es", "ES"));
        return dateFormat.format(i);
    }

    public String dateString(Long i) {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dMyyyy");
        return dateFormat.format(i);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showNotification();
        }
    };

    private void showNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        int iconResId = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) ?
                R.drawable.ic_launcher_background : R.mipmap.ic_launcher;

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(iconResId)
                .setColor(getResources().getColor(R.color.cardview_dark_background))
                .setContentTitle("titulo")
                .setContentText("mensaje")
                //.setContent()
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(0, notificationBuilder.build());
    }

    public void showDatePickerDialog(View v) {
        DatePicker newFragment = new DatePicker();

        newFragment.show(getFragmentManager(), "DatePicker");
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year1, int month, int dayOfMonth) {

        calendar.set(Calendar.YEAR, year1);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        Calendar c = Calendar.getInstance();
        if (c.get(Calendar.YEAR) <= calendar.get(Calendar.YEAR) && c.get(Calendar.MONTH) <= calendar.get(Calendar.MONTH)
                && c.get(Calendar.DAY_OF_MONTH) <= calendar.get(Calendar.DAY_OF_MONTH)) {
            Toast.makeText(MainActivity.this, "Can't reach this date", Toast.LENGTH_LONG).show();

        } else {
            onChildAdded(dateString(calendar.getTime().getTime()));
            fecha.setText(getDateString(calendar.getTime().getTime()));
            derecha.setEnabled(true);
            setTodayText();
            setYesterdayText();
        }


    }

    public void setTodayText() {
        Calendar c = Calendar.getInstance();
        if (fecha.getText().toString().equals(getDateString(c.getTime().getTime()))) {
            fecha.setText(R.string.today_string);
            derecha.setEnabled(false);
        }
    }


    public void setYesterdayText() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        if (fecha.getText().toString().equals(getDateString(cal.getTime().getTime()))) {
            fecha.setText(R.string.yesterday_string);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.userProfile: {
                ShowLoginUI();
                return true;
            }

            case R.id.userLogout: {
                LogotUI();
                return true;
            }


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void LogotUI() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        Toast.makeText(MainActivity.this, "You're out", Toast.LENGTH_LONG).show();
                        prefsEditor.clear().apply();
                        reportsArrayList.clear();


                    }
                });
    }

    public void ShowLoginUI() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .build(),
                RC_SIGN_IN);
    }


    public void addViews(final String Postid, String date, DatabaseReference mDatabaseReference1, int count) {
        mDatabaseReference1.child("Reports")
                .child(date)
                .child(Postid)
                .child("views")
                .setValue(count + 1);
    }




    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }


    public void likeAction(String postId, String date, String userId, String PictureProfile,DatabaseReference mDatabase) {
        mDatabase.child("Reports")
                .child(date)
                .child(postId)
                .child("likes")
                .child(userId)
                .setValue(new LikesModels(userId, PictureProfile));
    }

    public void unLikeAction( String postId, String date, String userId, DatabaseReference mDatabase) {
        mDatabase.child("Reports")
                .child(date)
                .child(postId)
                .child("likes")
                .child(userId)
                .removeValue();
    }

    // call this method when required to show popup


}
