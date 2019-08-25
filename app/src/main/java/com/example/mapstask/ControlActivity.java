package com.example.mapstask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ControlActivity extends AppCompatActivity{

    @BindView(R.id.switchLocation)
    Switch switchLocation;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;


    ArrayList<User> users;

    DatabaseReference databaseReference;

    private StorageReference mStorageRef;

    User currentUser;
    @BindView(R.id.btn_mylocation)
    LoadingButton btnMylocation;
    @BindView(R.id.btn_signout)
    LoadingButton btn_signout;

    Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        ButterKnife.bind(this);

        mStorageRef = FirebaseStorage.getInstance().getReference();


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


                        currentUser.setPhotoUrl(user.getPhotoUrl());
                        currentUser.setLan(user.getLan());
                        currentUser.setLon(user.getLon());
                        currentUser.setNickname(user.getNickname());
                        currentUser.setPassword(user.getPassword());
                        currentUser.setPhoneNumber(user.getPhoneNumber());


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
                Toast.makeText(ControlActivity.this, "List Updated", Toast.LENGTH_SHORT).show();

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
                if(b){
                    statusCheck();
                }
                else{
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ControlActivity.this);
                    builder.setMessage("GPS must be disabled from operating system")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, final int id) {
                                    dialog.dismiss();
                                }
                            });
                    final AlertDialog alert = builder.create();
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
        Intent intent = new Intent(ControlActivity.this,LoginActivity.class);
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
        }
        else{
            switchLocation.setChecked(true);
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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

}
