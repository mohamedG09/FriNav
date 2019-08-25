package com.example.mapstask;

import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ControlActivity extends AppCompatActivity {

    @BindView(R.id.switchLocation)
    Switch switchLocation;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        ButterKnife.bind(this);

        Glide
                .with(this)
                .load(R.drawable.defaultprofimage)
                .centerCrop()
                .placeholder(R.drawable.defaultprofimage)
                .into(profileImage);


    }
}
