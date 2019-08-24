package com.example.mapstask;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_signup)
    Button btnSignup;
    @BindView(R.id.btn_signin)
    Button btnSignin;

    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


    }

    @OnClick(R.id.btn_signup)
    public void onViewClicked() {

        startActivity(new Intent(this, SignUpActivity.class));

    }

    @OnClick(R.id.btn_signin)
    public void btn_signin() {

//        ref = FirebaseDatabase.getInstance().getReference();
//
//
//        ref.child("User01").setValue(new User("lol","lol","lol","loo"));
//        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
    }
}
