package com.example.mapstask;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kusu.loadingbutton.LoadingButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    public static final String email = "email";

    @BindView(R.id.et_email_signin)
    MaterialEditText etEmailSignin;
    @BindView(R.id.et_password_signin)
    MaterialEditText etPasswordSignin;
    @BindView(R.id.btn_signin_act)
    LoadingButton btnSigninAct;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_signin_act)
    public void btn_signin_act() {


        try {
            btnSigninAct.showLoading();

            mAuth.signInWithEmailAndPassword(etEmailSignin.getText().toString().trim(), etPasswordSignin.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser userFB = mAuth.getCurrentUser();
                                updateUI(userFB);
                            } else {
                                // If sign in fails, display a message to the user.
                                updateUI(null);
                            }

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void updateUI(FirebaseUser user){
        if(user != null){
            btnSigninAct.hideLoading();
            Intent intent = new Intent(LoginActivity.this,ControlActivity.class);
            intent.putExtra(LoginActivity.email,etEmailSignin.getText().toString().trim());
            startActivity(intent);
        } else{
            etEmailSignin.setError("Invalid Credentials");
            etPasswordSignin.setError("");
            btnSigninAct.hideLoading();
        }
    }
}
