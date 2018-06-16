package com.peoplereport.peoplereport;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.peoplereport.peoplereport.ClassModels.Reports;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * creado por  Christian en la fecha 2018-05-27.
 */

public class PeopleReportService extends Service {
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private ArrayList<Reports> reportsArrayList = new ArrayList<>();
    int size1;
    long chil = 0;
    long aux = 0;
    public static String MY_SERVICE = ".PeopleReportService.MY_SERVICE";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        size1 = intent.getIntExtra("key", 0);
//        if (size1 >= 1) {
        onChildAdded("2752018");


        return super.onStartCommand(intent, flags, startId);
    }

    private void onChildAdded(String fecha) {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Reports/");
        mDatabaseReference.child(fecha).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //                for (DataSnapshot children : dataSnapshot.getChildren()) {
//                    Reports reports = children.getValue(Reports.class);
//                    reportsArrayList.add(reports);
//                }
//                Log.d("Noticias", ""+reportsArrayList.size() );
//                Log.d("Noticias", ""+size1 );
//                Log.d("Noticias", ""+size );
//
//                if (size >=1 ) {
                aux = chil;
                chil = dataSnapshot.getChildrenCount();
                Log.d("Noticias", "auxiliar " + aux);
                Log.d("Noticias", "chil " + chil);


                if (aux < chil && aux != 0) {
                    Intent intent = new Intent(MY_SERVICE);
                    Log.d("Noticias", "Manda la notificacion ");
                    LocalBroadcastManager.getInstance(PeopleReportService.this).sendBroadcast(intent);



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private Timer timer;
    private TimerTask timerTask;
    private int counter;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("Noticias", "in timer ++++  " + (counter++));
            }
        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onDestroy() {
        stoptimertask();
        super.onDestroy();
    }




}
