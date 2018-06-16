package com.peoplereport.peoplereport;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.peoplereport.peoplereport.ClassModels.MyUsers;
import com.peoplereport.peoplereport.ClassModels.Reports;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddReportActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseReference;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;
    private ImageView btnChoose;
    private EditText mensajeAPublicar;
    private EditText tituloAPublicar;
    private String ubication;
    FusedLocationProviderClient mFusedLocationClient;
    StorageReference ref;
    private Calendar calendar;
    FirebaseUser user;
    Context context;
    Gson gson = new Gson();
    Geocoder geocoder;
    List<Address> addresses;
    private final String LOG_TAG = "CV5App";

    private final int PICK_IMAGE_REQUEST = 71;
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 71;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_report_activity);
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        context = this;


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            getLocation();
            return;
        } else {


            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {


                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Log.d(LOG_TAG,"to tell why this is importan");
                } else {


                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                }
            }
        }


        user = mFirebaseAuth.getCurrentUser();


        btnChoose = findViewById(R.id.btnChoose);
        TextView profileNametv = findViewById(R.id.nameAppBarLayout);
        CircleImageView userPerfil = findViewById(R.id.userProfile);
        ImageButton postButton = findViewById(R.id.postButton);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Reports");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        calendar = Calendar.getInstance();

        calendar.getTime().getTime();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.fui_ic_phone_white_24dp);
        requestOptions.error(R.drawable.fui_done_check_mark);


        profileNametv.setText(user.getDisplayName());
        Glide.with(this).setDefaultRequestOptions(requestOptions).load(user.getPhotoUrl()).into(userPerfil);

        tituloAPublicar = findViewById(R.id.tituloApublicar);
        mensajeAPublicar = findViewById(R.id.mensajeAPublicar);


        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mensajeAPublicar.length() != 0 && tituloAPublicar.length() != 0) {
                    uploadImage(tituloAPublicar.getText().toString(), mensajeAPublicar.getText().toString());
                } else {
                    Toast.makeText(AddReportActivity.this, "Completa los campos correctamente", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                btnChoose.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(final String titulo, final String mensaje) {
        final SharedPreferences mPrefs = getSharedPreferences("com.peoplereport.org", MODE_PRIVATE);

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            final String keyValue = mDatabaseReference.push().getKey();
                            String myLogin = mPrefs.getString("MyLoginUser", "");
                            final MyUsers myLoginUser = gson.fromJson(myLogin, MyUsers.class);


                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Calendar c = Calendar.getInstance();
                                    Reports reports = new Reports
                                            (uri.toString()
                                                    , myLoginUser.getPictureProfile()
                                                    , ubication
                                                    , mensaje
                                                    , myLoginUser.getUsername(), 0, null, null
                                                    , titulo
                                                    , keyValue
                                                    , dateString(c.getTime().getTime()), c.getTime().getTime());

                                    mDatabaseReference.child(dateString(c.getTime().getTime())).child(keyValue).setValue(reports);
                                }
                            });


                            tituloAPublicar.setText("");
                            mensajeAPublicar.setText("");

                            btnChoose.setImageResource(R.drawable.background_color_circle_selector);


//                            Toast.makeText(CrearArticulos.this, "Articulo Publicado", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
//                            Toast.makeText(CrearArticulos.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private String dateString(Long i) {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dMyyyy");
        return dateFormat.format(i);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();



                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(AddReportActivity.this, "You can't Post without your location", Toast.LENGTH_LONG).show();
        finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
@SuppressLint("MissingPermission")
public void getLocation(){
    mFusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations, this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        geocoder = new Geocoder(AddReportActivity.this, Locale.getDefault());


                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String ubication = addresses.get(0).getLocality();


                    }
                }
            });

}

}

