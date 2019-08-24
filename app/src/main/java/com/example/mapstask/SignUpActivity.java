package com.example.mapstask;


import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kusu.loadingbutton.LoadingButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {


    @BindView(R.id.btn_signup_act)
    LoadingButton btnSignupAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);




    }


    @OnClick(R.id.btn_signup_act)
    public void btn_signup_act() {

        btnSignupAct.showLoading();





        //TODO Firebase connection with account creation and intents






        //btnSignupAct.hideLoading();


    }
}
