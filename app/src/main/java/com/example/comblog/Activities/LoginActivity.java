package com.example.comblog.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.comblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText userMail, userPassword;
    private Button loginButton;
    private ProgressBar loginProgressBar;
    private FirebaseAuth firebaseAuth;
    private Intent homeActivity;
    private ImageView userPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userMail = findViewById(R.id.login_mail);
        userPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        userPhoto = findViewById(R.id.login_photo);
        loginProgressBar = findViewById(R.id.login_progressBar);
        firebaseAuth = FirebaseAuth.getInstance();
        homeActivity = new Intent(this, Home.class);
        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerActivity = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(registerActivity);
                finish();
            }
        });


        loginProgressBar.setVisibility(View.INVISIBLE);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginProgressBar.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.INVISIBLE);

                final String email = userMail.getText().toString();
                final String password = userPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    showMessage("Please Verify all fields");
                    loginButton.setVisibility(View.VISIBLE);
                    loginProgressBar.setVisibility(View.INVISIBLE);


                }else {
                    signIn(email, password);
                }
            }
        });
    }

    private void signIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loginProgressBar.setVisibility(View.INVISIBLE);
                    loginButton.setVisibility(View.VISIBLE);
                    updateUI();
                }else {
                    showMessage(task.getException().getMessage());
                    loginButton.setVisibility(View.VISIBLE);
                    loginProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void updateUI() {
        startActivity(homeActivity);
        finish();

    }

    private void showMessage(String s) {

        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null) {
            updateUI();

        }
    }
}