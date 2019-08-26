package com.example.mapstask;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kusu.loadingbutton.LoadingButton;
import com.onurkaganaldemir.ktoastlib.KToast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ControlActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String LAN = "lan";
    public static final String LON = "lon";
    public static final String NICKNAME = "nickname";

    @BindView(R.id.switchLocation)
    Switch switchLocation;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;


    ArrayList<User> users;

    DatabaseReference databaseReference;
    @BindView(R.id.btn_mylocation)
    LoadingButton btnMylocation;

    private StorageReference mStorageRef;

    User currentUser;
    @BindView(R.id.btn_signout)
    LoadingButton btn_signout;

    Uri imgUri;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    boolean currentUserDefined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        ButterKnife.bind(this);

        currentUserDefined = false;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        databaseReference = FirebaseDatabase.getInstance().getReference();
        users = new ArrayList<>();

        currentUser = new User();
        currentUser.setEmail(getIntent().getStringExtra(LoginActivity.email));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                users.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                    User user = userSnapshot.getValue((User.class));

                    if (user.getEmail().toLowerCase().equals(currentUser.getEmail().toLowerCase())) {


                        if (!currentUserDefined) {
                            currentUser.setPhotoUrl(user.getPhotoUrl());
                            currentUser.setLan(user.getLan());
                            currentUser.setLon(user.getLon());
                            currentUser.setNickname(user.getNickname());
                            currentUser.setPassword(user.getPassword());
                            currentUser.setPhoneNumber(user.getPhoneNumber());
                            currentUserDefined = true;
                        }

                        Glide
                                .with(ControlActivity.this)
                                .load(currentUser.getPhotoUrl())
                                .centerCrop()
                                .placeholder(R.drawable.defaultprofimage)
                                .into(profileImage);


                    } else {
                        users.add(user);
                    }

                }

                recyclerView.setAdapter(new RecycleAdapterUser(ControlActivity.this, users));
                recyclerView.setLayoutManager(new LinearLayoutManager(ControlActivity.this));

            }

            @Override
            public void onCancelled(DatabaseError error) {

                Toast.makeText(ControlActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        Glide
                .with(this)
                .load(currentUser.getPhotoUrl())
                .centerCrop()
                .placeholder(R.drawable.defaultprofimage)
                .into(profileImage);


        //When the screen is opened it will ask for location and when the switch is opened
        statusCheck();
        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    statusCheck();
                    mGoogleApiClient.connect();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ControlActivity.this);
                    builder.setMessage("GPS must be disabled from operating system")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, final int id) {
                                    dialog.dismiss();
                                }
                            });
                    final AlertDialog alert = builder.create();
                    mGoogleApiClient.disconnect();
                    alert.show();
                }
            }
        });


    }


    @OnClick(R.id.profile_image)
    public void pickImage() {

        try {

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);


        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {

            imgUri = data.getData();
            profileImage.setImageURI(imgUri);
            upload();


        }
    }

    private void upload() {

        if (imgUri != null) {
            final StorageReference ref = mStorageRef.child("Photos/" + System.currentTimeMillis());

            ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {


                            //Current User image update
                            currentUser.setPhotoUrl(uri.toString());
                            databaseReference.child(cleanEmail(currentUser.getEmail())).setValue(currentUser);


                            Toast.makeText(ControlActivity.this, "Upload Success", Toast.LENGTH_SHORT).show();


                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ControlActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                }
            });


        } else {
            Toast.makeText(this, "Failed upload", Toast.LENGTH_SHORT).show();
        }

    }

    private String cleanEmail(String email) {
        return email.replace(".", "_")
                .replace("[", "_")
                .replace("$", "_")
                .replace("#", "_")
                .replace("]", "_");

    }

    @OnClick(R.id.btn_signout)
    public void btn_signout() {
        Intent intent = new Intent(ControlActivity.this, LoginActivity.class);
        mGoogleApiClient.disconnect();
        startActivity(intent);
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))//If the user agrees to open location
                switchLocation.setChecked(true);
            else
                switchLocation.setChecked(false);
        } else {
            switchLocation.setChecked(true);
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        switchLocation.setChecked(false);
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onLocationChanged(Location location) {


        if (currentUserDefined) {
            currentUser.setLan(location.getLatitude());
            currentUser.setLon(location.getLongitude());

            databaseReference.child(cleanEmail(currentUser.getEmail())).setValue(currentUser);
            currentUserDefined = false;
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10); // Update location every second


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionSuspended(int i) {
        KToast.warningToast(ControlActivity.this, "Connection Suspended", Gravity.BOTTOM, KToast.LENGTH_AUTO);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        KToast.errorToast(ControlActivity.this, "Connection Failed", Gravity.BOTTOM, KToast.LENGTH_AUTO);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();


    }

    @OnClick(R.id.btn_mylocation)
    public void btn_mylocation() {

        Intent intent = new Intent(ControlActivity.this,MapsActivity.class);
        intent.putExtra(ControlActivity.NICKNAME,currentUser.getNickname());
        intent.putExtra(ControlActivity.LAN,currentUser.getLan());
        intent.putExtra(ControlActivity.LON,currentUser.getLon());
        startActivity(intent);



    }
}
