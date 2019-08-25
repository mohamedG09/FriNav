package com.example.mapstask;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kusu.loadingbutton.LoadingButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {


    @BindView(R.id.btn_signup_act)
    LoadingButton btnSignupAct;
    @BindView(R.id.et_email)
    MaterialEditText etEmail;
    @BindView(R.id.et_nickname)
    MaterialEditText etNickname;
    @BindView(R.id.et_phonenumber)
    MaterialEditText etPhonenumber;
    @BindView(R.id.et_password)
    MaterialEditText etPassword;

    DatabaseReference userDB;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        userDB = FirebaseDatabase.getInstance().getReference();


    }


    @OnClick(R.id.btn_signup_act)
    public void btn_signup_act() {
        btnSignupAct.showLoading();
        try {



            //User Declaration and validation
            User localuser = new User();
            localuser.setNickname(etNickname.getText().toString());
            boolean emailCheck = localuser.setEmail(etEmail.getText().toString());
            boolean phoneCheck = localuser.setPhoneNumber(etPhonenumber.getText().toString());
            boolean passwordCheck = localuser.setPassword(etPassword.getText().toString());

            if (!emailCheck) {
                etEmail.setError("Invalid Email");
                btnSignupAct.hideLoading();
                return;
            }

            if (!phoneCheck) {
                etPhonenumber.setError("Invalid Phone number");
                btnSignupAct.hideLoading();
                return;
            }


            if (!passwordCheck) {
                etPassword.setError("Password must be more than 6 characters");
                btnSignupAct.hideLoading();
                return;
            }




            mAuth.createUserWithEmailAndPassword(etEmail.getText().toString().trim(), etPassword.getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                            userDB.child(cleanEmail(localuser.getEmail())).setValue(localuser);
                                            btnSignupAct.hideLoading();

                                            startActivity(new Intent(SignUpActivity.this,MainActivity.class));

                                            Toast.makeText(SignUpActivity.this, "Account Made Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                makeFancyAlert();
                            }


                        }

                    });



        } catch (Exception e) {

            e.printStackTrace();

        } finally {
            btnSignupAct.showLoading();
        }


    }


    private void makeFancyAlert() {
        new FancyAlertDialog.Builder(this)
                .setTitle("Opps something went wrong :{")
                .setBackgroundColor(Color.parseColor("#424242"))  //Don't pass R.color.colorvalue
                .setMessage("We apologise for inconvenience")
                .setNegativeBtnText("RETRY")
                .setPositiveBtnBackground(Color.parseColor("#878F8A"))  //Don't pass R.color.colorvalue
                .setNegativeBtnBackground(Color.parseColor("#C6BD79"))
                .setPositiveBtnText("OK")
                .setAnimation(Animation.SLIDE)
                .isCancellable(true)
                .setIcon(R.drawable.crosslogo, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        Toast.makeText(getApplicationContext(), "We Stand By Your Side", Toast.LENGTH_SHORT).show();
                    }
                }).OnNegativeClicked(new FancyAlertDialogListener() {
            @Override
            public void OnClick() {
                Toast.makeText(getApplicationContext(), "Retrying to establish connection", Toast.LENGTH_SHORT).show();
            }
        })
                .build();
    }


    private String cleanEmail(String email){
       return email.replace(".","_")
               .replace("[","_")
               .replace("$","_")
               .replace("#","_")
               .replace("]","_");

    }


}
